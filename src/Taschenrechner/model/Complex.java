package Taschenrechner.model;

/**
 * Repräsentiert eine komplexe Zahl a + b i.
 * Unterstützt nur: Addition, Subtraktion, Multiplikation,
 * Division, Konjugation und Betrag
 */
public class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double re() {
        return re;
    }

    public double im() {
        return im;
    }

    /** (a+bi) + (c+di) = (a+c) + (b+d)i */
    public Complex add(Complex other) {
        return new Complex(this.re + other.re, this.im + other.im);
    }

    /** (a+bi) - (c+di) = (a-c) + (b-d)i */
    public Complex sub(Complex other) {
        return new Complex(this.re - other.re, this.im - other.im);
    }

    /** (a+bi)*(c+di) = (ac - bd) + (ad + bc)i */
    public Complex mul(Complex other) {
        double real = this.re * other.re - this.im * other.im;
        double imag = this.re * other.im + this.im * other.re;
        return new Complex(real, imag);
    }

    /** (a+bi)/(c+di) = [(a+bi)*(c-di)]/(c^2 + d^2) */
    public Complex div(Complex other) {
        double denom = other.re * other.re + other.im * other.im;
        if (denom == 0) {
            throw new ArithmeticException("Division durch Null in Complex.div()");
        }
        double real = (this.re * other.re + this.im * other.im) / denom;
        double imag = (this.im * other.re - this.re * other.im) / denom;
        return new Complex(real, imag);
    }

    /** Konjugation: conj(a+bi) = a - bi */
    public Complex conj() {
        return new Complex(this.re, -this.im);
    }

    /** Betrag (Modul): |a+bi| = sqrt(a^2 + b^2) */
    public double abs() {
        return Math.hypot(re, im);
    }

    /**
     * String-Repräsentation der komplexen Zahl.
     * Die Ausgabe erfolgt im Format "a + b i" oder "a - b i",
     */
    @Override
    public String toString() {
        // Ausgabe: "a + b i" oder "a - b i" (jeweils mit 3 Nachkommastellen)
        String realPart = String.format("%.3f", re);
        String imagPart = String.format("%.3f", Math.abs(im));
        if (Math.abs(im) < 1e-12) {
            // nur reeller Anteil
            return realPart;
        }
        if (Math.abs(re) < 1e-12) {
            // nur imaginärer Anteil
            return (im < 0 ? "-" : "") + imagPart + "i";
        }
        return realPart
                + (im < 0 ? " - " : " + ")
                + imagPart + "i";
    }
}
