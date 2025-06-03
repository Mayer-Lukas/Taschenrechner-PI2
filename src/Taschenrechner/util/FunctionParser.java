package Taschenrechner.util;

import Taschenrechner.model.Function;

/**
 * Erweiterter FunctionParser, der nun auch trigonometrische Funktionen
 * unterstützt und implizite Multiplikation (z.B. x(x+1)(x-1)) korrekt einfügt.
 * Dieser Parser verarbeitet Ausdrücke der Form:
 *
 *   sin(x) + cos(2*x) - tan(x*(x+1))
 *
 * sowie polynomiale Teile, Zahlen, Variablen (x), Klammerausdrücke und
 * Operatoren (+, -, *, ^).
 */
public class FunctionParser {

    public static Function parse(String expression) throws IllegalArgumentException {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Leerer Ausdruck");
        }
        // Entferne Leerzeichen und füge implizite Multiplikation ein, z.B. aus "x(" wird "x*("
        expression = expression.replaceAll("\\s+", "");
        expression = preprocess(expression);

        Parser parser = new Parser(expression);
        Function f = parser.parseExpression();
        if (parser.pos != expression.length()) {
            throw new IllegalArgumentException("Unerwartete Eingabe an Position " + parser.pos);
        }
        return f;
    }

    /**
     * Fügt implizit Multiplikationszeichen ein, wenn z. B. eine Ziffer, ein Buchstabe
     * oder eine schließende Klammer unmittelbar vor einer öffnenden Klammer steht.
     */
    private static String preprocess(String expr) {
        return expr.replaceAll("(?<=[0-9a-zA-Z)])\\(", "*(");
    }

    /**
     * Ein rekursiver Parser, der nun allgemeine Funktionsausdrücke
     * (mittels Lambda-Ausdrücken) verarbeitet.
     */
    private static class Parser {
        private final String input;
        private int pos;

        public Parser(String input) {
            this.input = input;
            this.pos = 0;
        }

        // Expression -> Term { ('+' | '-') Term }
        public Function parseExpression() {
            Function result = parseTerm();
            while (pos < input.length()) {
                char ch = input.charAt(pos);
                if (ch == '+') {
                    pos++;
                    Function term = parseTerm();
                    Function oldResult = result;
                    result = (double x) -> oldResult.evaluate(x) + term.evaluate(x);
                } else if (ch == '-') {
                    pos++;
                    Function term = parseTerm();
                    Function oldResult = result;
                    result = (double x) -> oldResult.evaluate(x) - term.evaluate(x);
                } else {
                    break;
                }
            }
            return result;
        }

        // Term -> Factor { '*' Factor }
        public Function parseTerm() {
            Function result = parseFactor();
            while (pos < input.length() && input.charAt(pos) == '*') {
                pos++; // '*' überspringen
                Function factor = parseFactor();
                Function oldResult = result;
                result = (double x) -> oldResult.evaluate(x) * factor.evaluate(x);
            }
            return result;
        }

        // Factor -> Primary [ '^' Zahl ]
        public Function parseFactor() {
            Function base = parsePrimary();
            while (pos < input.length() && input.charAt(pos) == '^') {
                pos++; // '^' überspringen
                int exponent = parseNumberInt(); // Exponent als ganze Zahl
                Function oldBase = base;
                base = (double x) -> Math.pow(oldBase.evaluate(x), exponent);
            }
            return base;
        }

        // Primary ->
        //    trig_function | '(' Expression ')' | Zahl | 'x'
        public Function parsePrimary() {
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unerwartetes Ende des Ausdrucks");
            }
            // Prüfe auf trigonometrische Funktionen:
            if (input.startsWith("sin", pos)) {
                pos += 3;
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.sin(inner.evaluate(x));
            } else if (input.startsWith("cos", pos)) {
                pos += 3;
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.cos(inner.evaluate(x));
            } else if (input.startsWith("tan", pos)) {
                pos += 3;
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.tan(inner.evaluate(x));
            }
            // Klammerausdruck
            else if (input.charAt(pos) == '(') {
                pos++; // '(' überspringen
                Function expr = parseExpression();
                expect(')');
                return expr;
            }
            // Zahl
            else if (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.') {
                double num = parseNumber();
                return (double x) -> num;
            }
            // Variable x
            else if (input.charAt(pos) == 'x' || input.charAt(pos) == 'X') {
                pos++; // 'x' verarbeiten
                return (double x) -> x;
            }
            throw new IllegalArgumentException("Unerwartetes Zeichen '" + input.charAt(pos) + "' an Position " + pos);
        }

        /**
         * Erwartet, dass an der aktuellen Position das Zeichen ch steht.
         * Falls ja, wird die Position um eins erhöht, sonst wird eine Exception geworfen.
         */
        private void expect(char ch) {
            if (pos >= input.length() || input.charAt(pos) != ch) {
                throw new IllegalArgumentException("Erwartetes '" + ch + "' an Position " + pos);
            }
            pos++;
        }

        // Liest eine Gleitkommazahl ein und gibt sie zurück.
        private double parseNumber() {
            int start = pos;
            while (pos < input.length() &&
                    (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                pos++;
            }
            String token = input.substring(start, pos);
            try {
                return Double.parseDouble(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ungültige Zahl: " + token);
            }
        }

        // Liest eine ganze Zahl ein.
        private int parseNumberInt() {
            int start = pos;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
            String token = input.substring(start, pos);
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ungültige Zahl: " + token);
            }
        }
    }
}