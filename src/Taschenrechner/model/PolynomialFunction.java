package Taschenrechner.model;

public class PolynomialFunction implements Function {
    // Koeffizienten in absteigender Potenz, z.B. {1, 3, 2} für f(x)=x^2+3x+2
    private final double[] coefficients;

    public PolynomialFunction(double... coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public double evaluate(double x) {
        double result = 0;
        for (double coeff : coefficients) {
            result = result * x + coeff;
        }
        return result;
    }

    public PolynomialFunction derivative() {
        if (coefficients.length <= 1) {
            return new PolynomialFunction(0); // konstante Funktion
        }
        double[] derivCoeffs = new double[coefficients.length - 1];
        int degree = coefficients.length - 1;
        for (int i = 0; i < derivCoeffs.length; i++) {
            derivCoeffs[i] = coefficients[i] * (degree - i);
        }
        return new PolynomialFunction(derivCoeffs);
    }
}