package Taschenrechner.util;

import Taschenrechner.model.Function;
import Taschenrechner.model.PolynomialFunction;

public class FunctionParser {

    public static Function parse(String expression) throws IllegalArgumentException {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Leerer Ausdruck");
        }
        // Leerzeichen entfernen und implizite Multiplikation einfügen, wenn vor "("
        // eine Ziffer, ein 'x'/'X' oder ')' steht
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

        // Term -> Factor { ('*' | '/') Factor }
        public Function parseTerm() {
            Function result = parseFactor();
            while (pos < input.length()) {
                char op = input.charAt(pos);
                if (op == '*' || op == '/') {
                    pos++;
                    Function factor = parseFactor();
                    if (op == '*') {
                        if (result instanceof PolynomialFunction && factor instanceof PolynomialFunction) {
                            result = PolynomialFunction.multiply((PolynomialFunction) result, (PolynomialFunction) factor);
                        } else {
                            Function old = result;
                            result = (double x) -> old.evaluate(x) * factor.evaluate(x);
                        }
                    } else {
                        Function old = result;
                        result = (double x) -> old.evaluate(x) / factor.evaluate(x);
                    }
                } else {
                    break;
                }
            }
            return result;
        }

        // Factor -> Primary [ '^' Factor ]
        public Function parseFactor() {
            Function base = parsePrimary();
            while (pos < input.length() && input.charAt(pos) == '^') {
                pos++; // '^' überspringen
                // Prüfe auf optional negativen Exponenten
                boolean negativeExp = false;
                if (pos < input.length() && input.charAt(pos) == '-') {
                    negativeExp = true;
                    pos++;
                }
                Function exponent = parseFactor();

                // Versuche, Integer-Exponent aus konst. Polynom zu extrahieren
                Integer expInt = null;
                if (exponent instanceof PolynomialFunction) {
                    double[] coeffs = ((PolynomialFunction) exponent).getCoefficients();
                    if (coeffs.length == 1) {
                        expInt = (int) Math.round(coeffs[0]);
                        if (negativeExp) expInt = -expInt;
                    }
                }

                if (expInt != null) {
                    int e = expInt;
                    if (base instanceof PolynomialFunction && e >= 0) {
                        base = PolynomialFunction.pow((PolynomialFunction) base, e);
                    } else {
                        final int finalExp = e;
                        Function oldBase = base;
                        base = (double x) -> Math.pow(oldBase.evaluate(x), finalExp);
                    }
                } else {
                    // Allgemeiner Fall: Math.pow(base(x), exponent(x))
                    Function signedExp;
                    if (negativeExp) {
                        Function oldExp = exponent;
                        signedExp = (double x) -> -oldExp.evaluate(x);
                    } else {
                        signedExp = exponent;
                    }
                    Function oldBase = base;
                    base = (double x) -> {
                        double b = oldBase.evaluate(x);
                        double eVal = signedExp.evaluate(x);
                        return Math.pow(b, eVal);
                    };
                }
            }
            return base;
        }

        // Primary ->
        //   'ln' '(' Expr ')'
        // | 'lg' '(' Expr ')'
        // | 'log' '(' Expr ')'
        // | 'sin' '(' Expr ')'
        // | 'cos' '(' Expr ')'
        // | 'tan' '(' Expr ')'
        // | '(' Expr ')'
        // | Zahl
        // | 'x'
        // | 'e'
        public Function parsePrimary() {
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unerwartetes Ende des Ausdrucks");
            }

            // === ln(x) → natürlicher Log (Basis e) ===
            if (input.startsWith("ln", pos)
                    && pos + 2 < input.length()
                    && input.charAt(pos + 2) == '(')
            {
                pos += 2; // überspringe "ln"
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.log(inner.evaluate(x));
            }

            // === lg(x) → Logarithmus Basis 2 ===
            if (input.startsWith("lg", pos)
                    && pos + 2 < input.length()
                    && input.charAt(pos + 2) == '(')
            {
                pos += 2; // überspringe "lg"
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.log(inner.evaluate(x)) / Math.log(2);
            }

            // === log(x) → Logarithmus Basis 10 ===
            if (input.startsWith("log", pos)
                    && pos + 3 < input.length()
                    && input.charAt(pos + 3) == '(')
            {
                pos += 3; // überspringe "log"
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.log10(inner.evaluate(x));
            }

            // === sin(x) ===
            if (input.startsWith("sin", pos)) {
                pos += 3;
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.sin(inner.evaluate(x));
            }
            // === cos(x) ===
            if (input.startsWith("cos", pos)) {
                pos += 3;
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.cos(inner.evaluate(x));
            }
            // === tan(x) ===
            if (input.startsWith("tan", pos)) {
                pos += 3;
                expect('(');
                Function inner = parseExpression();
                expect(')');
                return (double x) -> Math.tan(inner.evaluate(x));
            }

            // === Klammerausdruck ===
            if (input.charAt(pos) == '(') {
                pos++;
                Function expr = parseExpression();
                expect(')');
                return expr;
            }

            // === Zahl (konstantes Polynom) ===
            if (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.') {
                double num = parseNumber();
                return new PolynomialFunction(num);
            }

            // === Variable 'x' ===
            if (input.charAt(pos) == 'x' || input.charAt(pos) == 'X') {
                pos++;
                return new PolynomialFunction(1, 0);
            }

            // === 'e' → Euler’sche Zahl ===
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
