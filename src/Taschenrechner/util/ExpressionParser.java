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

/**
 * Ein einfacher Ausdrucksparser, der die Operatoren +, -, *, / sowie
 * runde Klammern unterstützt. Er verwandelt einen String wie "3 + 4*(2-1)"
 * in einen Expression-Baum und ermöglicht das Auswerten per evaluate().
 */
public class ExpressionParser {
    /**
     * Map von Operator-Token ("+", "-", "*", "/") zu den entsprechenden Operator-Instanzen.
     */
    private static final Map<String, Operator> OPERATORS = new HashMap<>();

    static {
        OPERATORS.put("+", new Addition());
        OPERATORS.put("-", new Subtraction());
        OPERATORS.put("*", new Multiplication());
        OPERATORS.put("/", new Division());
    }

    /**
     * Parst den Eingabestring in einen Expression-Baum.
     *
     * @param input arithmetischer Ausdruck (z. B. "3 + 4 * (2 - 1)")
     * @return eine Expression, die evaluate() aufgerufen werden kann
     * @throws ParseException bei Syntaxfehlern (unbekanntes Zeichen, unbalancierte Klammern, etc.)
     */
    public Expression parse(String input) throws ParseException {
        // 1. Leerzeichen entfernen
        input = input.replaceAll("\\s+", "");

        // 2. Zwei Stacks: einer für Operatoren (Strings), einer für Operanden (Expression)
        Stack<String> operatorStack = new Stack<>();
        Stack<Expression> operandStack = new Stack<>();

        // 3. Zeichenweise durch den Input iterieren
        for (int i = 0; i < input.length(); ) {
            char ch = input.charAt(i);

            // 3.1 Zahl (inkl. Dezimalpunkt) erkennen
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
            // 3.2 Öffnende Klammer
            else if (ch == '(') {
                operatorStack.push("(");
                i++;
            }
            // 3.3 Schließende Klammer: bis zur passenden "(" abarbeiten
            else if (ch == ')') {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    applyOperator(operatorStack.pop(), operandStack);
                }
                if (operatorStack.isEmpty() || !operatorStack.peek().equals("(")) {
                    throw new ParseException("Ungepaarte Klammer", i);
                }
                operatorStack.pop(); // "(" entfernen
                i++;
            }
            // 3.4 Operator (+, -, *, /)
            else if (OPERATORS.containsKey(String.valueOf(ch))) {
                String opToken = String.valueOf(ch);
                // Operatoren mit höherer oder gleicher Priorität abarbeiten
                while (!operatorStack.isEmpty()
                        && !operatorStack.peek().equals("(")
                        && precedence(operatorStack.peek()) >= precedence(opToken)) {
                    applyOperator(operatorStack.pop(), operandStack);
                }
                operatorStack.push(opToken);
                i++;
            }
            // 3.5 Unbekanntes Zeichen
            else {
                throw new ParseException("Unbekanntes Zeichen: " + ch, i);
            }
        }

        // 4. Übrige Operatoren abarbeiten
        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new ParseException("Ungepaarte Klammer", -1);
            }
            applyOperator(op, operandStack);
        }

        // 5. Am Ende muss genau ein Operand auf dem Stack sein
        if (operandStack.size() != 1) {
            throw new ParseException("Ungültiger Ausdruck", -1);
        }

        return operandStack.pop();
    }

    /**
     * Wendet den Operator opToken auf die beiden obersten Werte im operandStack an
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
     * + und - → 1, * und / → 2, alles andere → 0.
     */
    private int precedence(String op) {
        if (op.equals("+") || op.equals("-")) return 1;
        if (op.equals("*") || op.equals("/")) return 2;
        return 0;
    }
}
