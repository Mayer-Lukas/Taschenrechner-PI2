package Taschenrechner.model;

import java.util.Arrays;

public class PolynomialFunction implements Function {
    // coefficients[0] * x^(n-1) + coefficients[1] * x^(n-2) + ... + coefficients[n-1] * x^0
    private final double[] coefficients;

    public PolynomialFunction(double... coefficients) {
        // Beispiel: new PolynomialFunction(1, 3, 2) repräsentiert x^2 + 3x + 2
        // Oder: new PolynomialFunction(5) repräsentiert die konstante Funktion 5
        this.coefficients = coefficients;
    }

    @Override
    public double evaluate(double x) {
        // Horner-Schema für absteigende Koeffizienten:
        double result = 0;
        for (double coeff : coefficients) {
            result = result * x + coeff;
        }
        return result;
    }

    /** Exakte Ableitung des Polynoms. */
    public PolynomialFunction derivative() {
        int n = coefficients.length;
        if (n <= 1) {
            // konstante Funktion -> Ableitung = 0
            return new PolynomialFunction(0);
        }
        // Wenn originaler Grad = n-1, dann hat Ableitung Grad (n-2).
        // Die Ableitungskoeffizienten lauten:
        //    orig[0] * (n-1), orig[1] * (n-2), ..., orig[n-2] * 1
        double[] derivCoeffs = new double[n - 1];
        int degree = n - 1; // Grad des Originals
        for (int i = 0; i < n - 1; i++) {
            derivCoeffs[i] = coefficients[i] * (degree - i);
        }
        return new PolynomialFunction(derivCoeffs);
    }

    /** Addiert zwei Polynome (absteigende Koeffizienten). */
    public static PolynomialFunction add(PolynomialFunction p, PolynomialFunction q) {
        double[] a = p.coefficients;
        double[] b = q.coefficients;
        // Welches Polynom hat den höheren Grad?
        if (a.length < b.length) {
            // b ist höhergradiger – wir tauschen, damit a.length >= b.length ist
            double[] tmp = a; a = b; b = tmp;
        }
        // Erstelle Array für das Ergebnis mit der Länge von a (der größere Grad)
        int len = a.length;
        double[] sum = new double[len];
        int diff = a.length - b.length;
        // Kopiere die ersten diff Koeffizienten von a (weil b dort keine Entsprechung hat)
        System.arraycopy(a, 0, sum, 0, diff);
        // Nun Koeffizienten von a und b elementweise addieren
        for (int i = diff; i < len; i++) {
            sum[i] = a[i] + b[i - diff];
        }
        return new PolynomialFunction(sum);
    }

    /** Subtrahiert q von p (also p - q). */
    public static PolynomialFunction subtract(PolynomialFunction p, PolynomialFunction q) {
        double[] a = p.coefficients;
        double[] b = q.coefficients;
        if (a.length < b.length) {
            // Wenn q höheren Grad hat, erweitern wir a vorne mit Nullen
            double[] newA = new double[b.length];
            int diff = b.length - a.length;
            // führende Koeffizienten von newA sind 0 (bereits Nullinit),
            // dann kopieren wir a:
            System.arraycopy(a, 0, newA, diff, a.length);
            a = newA;
        }
        // jetzt gilt a.length >= b.length
        int len = a.length;
        double[] diffCoeffs = new double[len];
        int offset = len - b.length;
        // Kopiere vorne a (bis zum Part, der zu b korrespondiert)
        System.arraycopy(a, 0, diffCoeffs, 0, offset);
        // Ab dort: a[i] - b[i-offset]
        for (int i = offset; i < len; i++) {
            diffCoeffs[i] = a[i] - b[i - offset];
        }
        return new PolynomialFunction(diffCoeffs);
    }

    /** Multipliziert zwei Polynome (konvolution der Koeffizienten). */
    public static PolynomialFunction multiply(PolynomialFunction p, PolynomialFunction q) {
        double[] a = p.coefficients;
        double[] b = q.coefficients;
        int degA = a.length - 1; // Grad von p
        int degB = b.length - 1; // Grad von q
        int degR = degA + degB; // Grad des Produkts
        // Wir müssen ein Array der Länge degR + 1 füllen (absteigend)
        double[] prod = new double[degR + 1];
        // Indizierung: a[i] ist Koeffizient zu x^(degA - i)
        //           b[j] ist Koeffizient zu x^(degB - j)
        // dann trägt a[i]*b[j] zur Koeffizientenstelle für x^(degA - i + degB - j) bei.
        // Die Position in prod (für x^(degR - k)) liegt bei k = (degA - i) + (degB - j),
        // also (degA + degB) - (i + j). Damit füllen wir prod[i+j] (weil i+j von 0 bis degR).
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                prod[i + j] += a[i] * b[j];
            }
        }
        return new PolynomialFunction(prod);
    }

    /** Potenziert ein Polynom zu einem nicht-negativen ganzzahligen Exponenten. */
    public static PolynomialFunction pow(PolynomialFunction base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Negativer Exponent bei Polynom-Potenzierung nicht unterstützt.");
        }
        // Start mit Polynom „1“
        PolynomialFunction result = new PolynomialFunction(1.0);
        for (int i = 0; i < exponent; i++) {
            result = multiply(result, base);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = coefficients.length;
        for (int i = 0; i < n; i++) {
            double c = coefficients[i];
            int power = n - 1 - i;
            if (c == 0) continue;
            if (sb.length() > 0) {
                sb.append(c > 0 ? " + " : " - ");
            } else if (c < 0) {
                sb.append("-");
            }
            double abs = Math.abs(c);
            // Bei abs == 1 und power > 0 nur das x-Teil anzeigen
            if (!(abs == 1 && power > 0)) {
                sb.append(abs);
            }
            if (power > 0) {
                sb.append("x");
                if (power > 1) sb.append("^").append(power);
            }
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }
}
