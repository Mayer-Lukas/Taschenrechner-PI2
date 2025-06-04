package Taschenrechner.model;

public class GraphModel {
    private final Function function;
    private final Function derivative;
    private boolean showDerivative = false; // standardmäßig nicht anzeigen

    // Wertebereich (default)
    private double xMin = -10, xMax = 10;
    private double yMin = -10, yMax = 10;

    public GraphModel(Function function) {
        this.function = function;

        if (function instanceof PolynomialFunction) {
            // exakte polynomiale Ableitung
            derivative = ((PolynomialFunction) function).derivative();
        } else {
            // numerische Ableitung für alle anderen (z.B. Trig-Lambdas, Kombinationen, …)
            derivative = (double x) -> {
                double h = 1e-5; // oder 1e-6, je nach gewünschter Genauigkeit
                return (function.evaluate(x + h) - function.evaluate(x - h)) / (2 * h);
            };
        }
    }

    public Function getFunction() {
        return function;
    }

    public Function getDerivative() {
        return derivative;
    }

    public boolean isShowDerivative() {
        return showDerivative;
    }

    public void setShowDerivative(boolean showDerivative) {
        this.showDerivative = showDerivative;
    }

    public double getxMin() {
        return xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setxRange(double xMin, double xMax) {
        this.xMin = xMin;
        this.xMax = xMax;
    }

    public void setyRange(double yMin, double yMax) {
        this.yMin = yMin;
        this.yMax = yMax;
    }
}
