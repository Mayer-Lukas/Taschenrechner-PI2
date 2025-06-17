package Taschenrechner.util;

import Taschenrechner.model.Complex;

/**
 * Ein einfacher recursive‐descent‐Parser für komplexe Ausdrücke.
 * Unterstützt:
 *   - Literale:    a+bi, a-bi, 3i, -2i, 5        (ohne Leerzeichen oder mit Leerzeichen)
 *   - Klammerausdrücke: (… )
 *   - Operatoren:  +, -, *, /
 *   - Funktionen:  conj(z), abs(z)
 */
public class ComplexParser {
    private final String input;
    private int pos;

    private ComplexParser(String input) {
        // Entferne Leerzeichen
        this.input = input.replaceAll("\\s+", "");
        this.pos = 0;
    }

    /**
     * Parst den gesamten Ausdruck und liefert das Complex-Ergebnis.
     */
    public static Complex parse(String s) {
        ComplexParser cp = new ComplexParser(s);
        Complex result = cp.parseExpression();
        if (cp.pos != cp.input.length()) {
            throw new IllegalArgumentException(
                    "Unerwartete Eingabe an Position " + cp.pos
            );
        }
        return result;
    }

    // Expression → Term { ('+' | '-') Term }
    private Complex parseExpression() {
        Complex result = parseTerm();
        while (pos < input.length()) {
            char ch = input.charAt(pos);
            if (ch == '+') {
                pos++;
                Complex term = parseTerm();
                result = result.add(term);
            }
            else if (ch == '-') {
                pos++;
                Complex term = parseTerm();
                result = result.sub(term);
            }
            else {
                break;
            }
        }
        return result;
    }

    // Term → Factor { ('*' | '/') Factor }
    private Complex parseTerm() {
        Complex result = parseFactor();
        while (pos < input.length()) {
            char ch = input.charAt(pos);
            if (ch == '*') {
                pos++;
                Complex fac = parseFactor();
                result = result.mul(fac);
            }
            else if (ch == '/') {
                pos++;
                Complex fac = parseFactor();
                result = result.div(fac);
            }
            else {
                break;
            }
        }
        return result;
    }

    // Factor → Function | Literal | '(' Expression ')'
    private Complex parseFactor() {
        if (pos >= input.length()) {
            throw new IllegalArgumentException("Unerwartetes Ende des Ausdrucks");
        }

        // 1) Funktion: conj(z) oder abs(z)
        if (input.startsWith("conj", pos)) {
            pos += 4;
            expect('(');
            Complex inner = parseExpression();
            expect(')');
            return inner.conj();
        }
        if (input.startsWith("abs", pos)) {
            pos += 3;
            expect('(');
            Complex inner = parseExpression();
            expect(')');
            // abs liefert eine reelle Zahl, als Complex mit im=0
            return new Complex(inner.abs(), 0);
        }

        // 2) Klammerausdruck
        if (input.charAt(pos) == '(') {
            pos++;
            Complex expr = parseExpression();
            expect(')');
            return expr;
        }

        // 3) Literal: a+bi, a-bi, bi, a
        Complex lit = parseComplexLiteral();
        if (lit != null) {
            return lit;
        }

        throw new IllegalArgumentException(
                "Ungültiges Token an Position " + pos + ": '" + input.charAt(pos) + "'"
        );
    }

    /**
     * Versucht, am aktuellen pos ein komplexes Literal zu lesen:
     *   - a+bi  (z.B. "3+4i", "2-5i")
     *   - bi    (z.B. "3i", "-2i", "+i")
     *   - a     (reine reelle Zahl, z.B. "5", "-3.2")
     * liefert ein Complex-Objekt oder null, wenn kein Literal passt.
     */
    private Complex parseComplexLiteral() {
        int start = pos;

        // 1) Lese optionales Vorzeichen
        boolean neg = false;
        if (match('+')) {
        } else if (match('-')) {
            neg = true;
        }

        // 2) Lese reellen Teil (Zahl ohne 'i')
        Double realPart = null;
        int numStart = pos;
        while (pos < input.length() &&
                (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            pos++;
        }
        if (pos > numStart) {
            String numStr = input.substring(numStart, pos);
            try {
                realPart = Double.parseDouble(numStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Ungültige Zahl: " + numStr);
            }
            if (neg) {
                realPart = -realPart;
                neg = false; // Vorzeichen verarbeitet
            }
        }

        // 3) Prüfe, ob direkt ein 'i' folgt → dann ist das der Imaginärteil
        if (pos < input.length() && input.charAt(pos) == 'i') {
            pos++;
            double imag = (realPart != null ? realPart : 1.0);
            // Fall „i“ bzw. „-i“: realPart == null, neg == false→1i;
            // wenn vorzeichen war, dann realPart erst Null, neg==true, dann imag = 1, mit negativ?
            if (neg) {
                imag = -1.0;
            }
            return new Complex(0.0, imag);
        }

        // 4) Falls wir eine Zahl gelesen haben und jetzt nicht 'i' folgt, ist es reine reelle Zahl
        if (realPart != null) {
            return new Complex(realPart, 0.0);
        }

        // 5) Versuche Form „a+bi“ oder „a-bi“
        pos = start;
        // Lese (ggf. Vorzeichen), dann eine Zahl
        boolean firstNeg = match('-');
        match('+'); // falls '+' direkt vor der Zahl, einfach überspringen

        int idx = pos;
        while (idx < input.length() && (Character.isDigit(input.charAt(idx)) || input.charAt(idx) == '.')) {
            idx++;
        }
        if (idx == pos) {
            // Keine Ziffer, kein Literal
            pos = start;
            return null;
        }
        String realStr = input.substring(pos, idx);
        double a;
        try {
            a = Double.parseDouble(realStr);
        } catch (NumberFormatException e) {
            pos = start;
            return null;
        }
        if (firstNeg) a = -a;
        pos = idx;

        // Lese '+' oder '-'
        boolean plus = false, minus = false;
        if (pos < input.length() && input.charAt(pos) == '+') {
            pos++;
        } else if (pos < input.length() && input.charAt(pos) == '-') {
            minus = true; pos++;
        } else {
            pos = start;
            return null;
        }

        // Lese Imaginärteil‐Zahl (möglicherweise leer → implizit „1“)
        int imagStart = pos;
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            pos++;
        }
        double b;
        if (imagStart == pos) {
            // kein numerischer Teil, also „+i“ bzw. „-i“
            b = 1.0;
        } else {
            String imagStr = input.substring(imagStart, pos);
            try {
                b = Double.parseDouble(imagStr);
            } catch (NumberFormatException e) {
                pos = start;
                return null;
            }
        }
        if (minus) b = -b;

        // Jetzt muss ein 'i' folgen
        if (pos < input.length() && input.charAt(pos) == 'i') {
            pos++;
            return new Complex(a, b);
        }

        // Falls kein 'i', kein gültiges a+bi‐Literal
        pos = start;
        return null;
    }

    private boolean match(char c) {
        if (pos < input.length() && input.charAt(pos) == c) {
            pos++;
            return true;
        }
        return false;
    }

    private void expect(char c) {
        if (pos >= input.length() || input.charAt(pos) != c) {
            throw new IllegalArgumentException("Erwartetes '" + c + "' an Position " + pos);
        }
        pos++;
    }
}
