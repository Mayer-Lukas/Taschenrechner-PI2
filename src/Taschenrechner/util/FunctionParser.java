package Taschenrechner.util;

import Taschenrechner.model.Function;
import Taschenrechner.model.PolynomialFunction;

/**
 * Ein Parser für mathematische Funktionen, der die Operatoren +, -, *, /, ^ sowie
 * Funktionen wie sin, cos, tan, sqrt, log, ln unterstützt und runde Klammern korrekt auswertet.
 * Dieser Parser verwendet einen rekursiven Abstieg-Algorithmus.
 * Dieser Parser wird für die Darstellung von Graphen in der GUI benötigt.
 */
public class FunctionParser {

    public static Function parse(String expression) throws IllegalArgumentException {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Leerer Ausdruck");
        }
        // Leerzeichen entfernen und implizite Multiplikation einfügen
        expression = expression.replaceAll("\\s+", "");
        expression = expression.replaceAll("(?<=[0-9xX)])\\(", "*(");

        Parser parser = new Parser(expression);
        Function f = parser.parseExpression();
        if (parser.pos != expression.length()) {
            throw new IllegalArgumentException("Unerwartete Eingabe an Position " + parser.pos);
        }
        return f;
    }

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
                    if (result instanceof PolynomialFunction && term instanceof PolynomialFunction) {
                        result = PolynomialFunction.add((PolynomialFunction) result, (PolynomialFunction) term);
                    } else {
                        Function old = result;
                        result = (double x) -> old.evaluate(x) + term.evaluate(x);
                    }
                }
                else if (ch == '-') {
                    pos++;
                    Function term = parseTerm();
                    if (result instanceof PolynomialFunction && term instanceof PolynomialFunction) {
                        result = PolynomialFunction.subtract((PolynomialFunction) result, (PolynomialFunction) term);
                    } else {
                        Function old = result;
                        result = (double x) -> old.evaluate(x) - term.evaluate(x);
                    }
                }
                else {
                    break;
                }
            }
            return result;
        }

        // Term -> Unary { ('*' | '/') Unary }
        private Function parseTerm() {
            Function result = parseUnary();
            while (pos < input.length()) {
                char op = input.charAt(pos);
                if (op == '*' || op == '/') {
                    pos++;
                    Function right = parseUnary();
                    if (op == '*') {
                        if (result instanceof PolynomialFunction && right instanceof PolynomialFunction) {
                            result = PolynomialFunction.multiply((PolynomialFunction) result, (PolynomialFunction) right);
                        } else {
                            Function old = result;
                            result = (double x) -> old.evaluate(x) * right.evaluate(x);
                        }
                    } else {
                        Function old = result;
                        result = (double x) -> old.evaluate(x) / right.evaluate(x);
                    }
                } else {
                    break;
                }
            }
            return result;
        }

        // Unary -> ('+' | '-') Unary | Power
        private Function parseUnary() {
            if (pos < input.length() && input.charAt(pos) == '+') {
                pos++;
                return parseUnary();
            }
            if (pos < input.length() && input.charAt(pos) == '-') {
                pos++;
                Function inner = parseUnary();
                return (double x) -> -inner.evaluate(x);
            }
            return parsePower();
        }

        // Power -> Primary [ '^' Unary ]
        private Function parsePower() {
            Function base = parsePrimary();
            while (pos < input.length() && input.charAt(pos) == '^') {
                pos++; // '^' überspringen
                Function exponent = parseUnary();
                Function oldBase = base;
                base = (double x) -> Math.pow(oldBase.evaluate(x), exponent.evaluate(x));
            }
            return base;
        }

        // Primary →
        //   'sqrt' '(' Expr ')'
        // | 'ln' '(' Expr ')'
        // | 'lg' '(' Expr ')'
        // | 'log' '(' Expr ')'
        // | 'sin' '(' Expr ')'
        // | 'cos' '(' Expr ')'
        // | 'tan' '(' Expr ')'
        // | 'arcsin' '(' Expr ')'
        // | 'arccos' '(' Expr ')'
        // | 'arctan' '(' Expr ')'
        // | '(' Expr ')'
        // | '{' Expr '}'
        // | Zahl
        // | 'x'
        // | 'e'
        public Function parsePrimary() {
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unerwartetes Ende des Ausdrucks");
            }

            // sqrt(x)
            if (input.startsWith("sqrt", pos)) {
                pos += 4; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.sqrt(inner.evaluate(x));
            }
            // ln(x)
            if (input.startsWith("ln", pos)
                    && pos + 2 < input.length()
                    && input.charAt(pos + 2) == '(') {
                pos += 2; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.log(inner.evaluate(x));
            }
            // lg(x)
            if (input.startsWith("lg", pos)
                    && pos + 2 < input.length()
                    && input.charAt(pos + 2) == '(') {
                pos += 2; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.log(inner.evaluate(x)) / Math.log(2);
            }
            // log(x)
            if (input.startsWith("log", pos)
                    && pos + 3 < input.length()
                    && input.charAt(pos + 3) == '(') {
                pos += 3; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.log10(inner.evaluate(x));
            }
            // sin(x)
            if (input.startsWith("sin", pos)) {
                pos += 3; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.sin(inner.evaluate(x));
            }
            // cos(x)
            if (input.startsWith("cos", pos)) {
                pos += 3; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.cos(inner.evaluate(x));
            }
            // tan(x)
            if (input.startsWith("tan", pos)) {
                pos += 3; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.tan(inner.evaluate(x));
            }
            // arcsin(x)
            if (input.startsWith("arcsin", pos)) {
                pos += 6; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.asin(inner.evaluate(x));
            }
            // arccos(x)
            if (input.startsWith("arccos", pos)) {
                pos += 6; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.acos(inner.evaluate(x));
            }
            // arctan(x)
            if (input.startsWith("arctan", pos)) {
                pos += 6; expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.atan(inner.evaluate(x));
            }

            // Klammerausdruck rund
            if (input.charAt(pos) == '(') {
                pos++;
                Function expr = parseExpression();
                expect(')');
                return expr;
            }
            // Klammerausdruck geschweift
            if (input.charAt(pos) == '{') {
                pos++;
                Function expr = parseExpression();
                expect('}');
                return expr;
            }

            // Zahl (konstantes Polynom)
            if (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.') {
                double num = parseNumber();
                return new PolynomialFunction(num);
            }

            // Variable 'x'
            if (input.charAt(pos) == 'x' || input.charAt(pos) == 'X') {
                pos++;
                return new PolynomialFunction(1, 0);
            }

            // Euler'sche Zahl 'e'
            if (input.charAt(pos) == 'e') {
                pos++;
                return new PolynomialFunction(Math.E);
            }

            throw new IllegalArgumentException(
                    "Unerwartetes Zeichen '" + input.charAt(pos) + "' an Position " + pos
            );
        }

        private void expect(char c) {
            if (pos >= input.length() || input.charAt(pos) != c) {
                throw new IllegalArgumentException("Erwartetes '" + c + "' an Position " + pos);
            }
            pos++;
        }

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

        private int parseNumberInt() {
            int start = pos;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                pos++;
            }
            String token = input.substring(start, pos);
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ungültige ganze Zahl: " + token);
            }
        }
    }
}
