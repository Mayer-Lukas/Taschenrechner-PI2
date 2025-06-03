package Taschenrechner.util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import Taschenrechner.model.Expression;
import Taschenrechner.model.Constant;
import Taschenrechner.model.BinaryOperation;
import Taschenrechner.model.Operator;
import Taschenrechner.model.Addition;
import Taschenrechner.model.Subtraction;
import Taschenrechner.model.Multiplication;
import Taschenrechner.model.Division;
import Taschenrechner.model.Exponentiation;
import Taschenrechner.model.FunctionExpression;

/**
 * Ein Ausdrucksparser, der die Operatoren +, -, *, /, ^ sowie
 * Funktionen sin, cos, tan, sqrt, log, ln, exp unterstützt und
 * runde Klammern korrekt auswertet.
 */
public class ExpressionParser {
    /**
     * Map von Operator-Token ("+", "-", "*", "/", "^") zu den entsprechenden Operator-Instanzen.
     */
    private static final Map<String, Operator> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("+", new Addition());
        OPERATORS.put("-", new Subtraction());
        OPERATORS.put("*", new Multiplication());
        OPERATORS.put("/", new Division());
        OPERATORS.put("^", new Exponentiation());
    }

    /**
     * Parst den Eingabestring in einen Expression-Baum.
     *
     * @param input arithmetischer Ausdruck (z. B. "3 + 4 * (2 - 1)")
     * @return eine Expression, auf der evaluate() aufgerufen werden kann
     * @throws ParseException bei Syntaxfehlern (unbekanntes Zeichen, unbalancierte Klammern, etc.)
     */
    public Expression parse(String input) throws ParseException {
        // 1. Leerzeichen entfernen
        input = input.replaceAll("\\s+", "");

        // 2. Zwei Stacks: einer für Operatoren/Funktionsnamen, einer für Operanden (Expression)
        Stack<String> operatorStack = new Stack<>();
        Stack<Expression> operandStack = new Stack<>();

        // 3. Zeichenweise durch den Input iterieren
        for (int i = 0; i < input.length(); ) {
            char ch = input.charAt(i);

            // 3.1 Zahl (inklusive Dezimalpunkt) erkennen
            if (Character.isDigit(ch) || ch == '.') {
                int j = i;
                while (j < input.length() && (Character.isDigit(input.charAt(j)) || input.charAt(j) == '.')) {
                    j++;
                }
                String numberToken = input.substring(i, j);
                double value;
                try {
                    value = Double.parseDouble(numberToken);
                } catch (NumberFormatException e) {
                    throw new ParseException("Ungültige Zahl: " + numberToken, i);
                }
                operandStack.push(new Constant(value));
                i = j;
            }
            // 3.2 Funktionsname (Buchstaben) erkennen
            else if (Character.isLetter(ch)) {
                int j = i;
                while (j < input.length() && Character.isLetter(input.charAt(j))) {
                    j++;
                }
                String funcName = input.substring(i, j); // z. B. "sin", "log"
                // Nun muss zwingend eine "(" folgen
                if (j >= input.length() || input.charAt(j) != '(') {
                    throw new ParseException("Funktion erwartet '(': " + funcName, i);
                }
                // Funktionsname auf den Operator-Stack schieben
                int parenCount = 1;
                int k = j + 1;
                while (k < input.length() && parenCount > 0) {
                    if (input.charAt(k) == '(') parenCount++;
                    else if (input.charAt(k) == ')') parenCount--;
                    k++;
                }
                if (parenCount != 0) {
                    throw new ParseException("Ungepaarte Klammer in Funktionsaufruf: " + funcName, j);
                }
                String argStr = input.substring(j + 1, k - 1);
                Expression argExpr = parse(argStr);
                operandStack.push(new FunctionExpression(funcName, argExpr));
                i = k; // i zeigt jetzt auf '('; die nächste Iteration behandelt '('
            }
            // 3.3 Öffnende Klammer
            else if (ch == '(') {
                operatorStack.push("(");
                i++;
            }
            // 3.4 Schließende Klammer: bis zur passenden "(" abarbeiten
            else if (ch == ')') {
                // 3.4.1 Alle Operatoren bis "(" abarbeiten
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    String top = operatorStack.pop();
                    if (isFunctionName(top)) {
                        // Funktionsaufruf: nimm das oberste Argument
                        if (operandStack.isEmpty()) {
                            throw new ParseException("Fehlendes Argument für Funktion: " + top, i);
                        }
                        Expression arg = operandStack.pop();
                        operandStack.push(new FunctionExpression(top, arg));
                    } else {
                        // normaler Operator (+, -, *, /, ^)
                        applyOperator(top, operandStack);
                    }
                }
                // 3.4.2 Prüfen, ob "(" da ist
                if (operatorStack.isEmpty() || !operatorStack.peek().equals("(")) {
                    throw new ParseException("Ungepaarte Klammer", i);
                }
                operatorStack.pop(); // "(" entfernen
                i++;
            }
            // 3.5 Operatoren +, -, *, /, ^
            else if (OPERATORS.containsKey(String.valueOf(ch))) {
                String opToken = String.valueOf(ch);
                // Shunting-Yard: Operatoren mit höherer oder gleicher Priorität abarbeiten
                while (!operatorStack.isEmpty()
                        && !operatorStack.peek().equals("(")
                        && (
                        // rechts-assoziativ für "^": nur strikt > statt >=
                        (opToken.equals("^")
                                ? precedence(operatorStack.peek()) > precedence(opToken)
                                : precedence(operatorStack.peek()) >= precedence(opToken))
                )
                ) {
                    String top = operatorStack.pop();
                    applyOperator(top, operandStack);
                }
                operatorStack.push(opToken);
                i++;
            }
            // 3.6 Unbekanntes Zeichen
            else {
                throw new ParseException("Unbekanntes Zeichen: " + ch, i);
            }
        }

        // 4. Übrige Operatoren/Funktionsnamen abarbeiten
        while (!operatorStack.isEmpty()) {
            String top = operatorStack.pop();
            if (top.equals("(") || top.equals(")")) {
                throw new ParseException("Ungepaarte Klammer", -1);
            }
            if (isFunctionName(top)) {
                // Fehlende schließende Klammer für Funktion
                throw new ParseException("Fehlende schließende Klammer für Funktion: " + top, -1);
            } else {
                applyOperator(top, operandStack);
            }
        }

        // 5. Am Ende muss genau ein Operand auf dem Stack sein
        if (operandStack.size() != 1) {
            throw new ParseException("Ungültiger Ausdruck", -1);
        }
        return operandStack.pop();
    }

    /**
     * Wendet den Operator opToken auf die beiden obersten Values im operandStack an
     * und schiebt das Ergebnis zurück auf den Stack.
     */
    private void applyOperator(String opToken, Stack<Expression> operandStack) throws ParseException {
        if (operandStack.size() < 2) {
            throw new ParseException("Nicht genügend Operanden für Operator: " + opToken, -1);
        }
        Expression right = operandStack.pop();
        Expression left = operandStack.pop();
        Operator op = OPERATORS.get(opToken);
        if (op == null) {
            throw new ParseException("Unbekannter Operator: " + opToken, -1);
        }
        operandStack.push(new BinaryOperation(left, right, op));
    }

    /**
     * Gibt die Priorität des Operators zurück:
     * ^ → 4, * und / → 3, + und - → 2, alles andere → 0.
     */
    private int precedence(String op) {
        switch (op) {
            case "^": return 4;
            case "*":
            case "/": return 3;
            case "+":
            case "-": return 2;
            default:    return 0;
        }
    }

    /**
     * Prüft, ob der gegebene String ein Funktionsname ist.
     * Wir nehmen an, dass alles, was nicht "(" und nicht in OPERATORS ist,
     * eine Funktion (z. B. "sin", "cos", "tan", "sqrt", "log", "ln", "exp") darstellt.
     */
    private boolean isFunctionName(String s) {
        return !s.equals("(") && !OPERATORS.containsKey(s);
    }
}
