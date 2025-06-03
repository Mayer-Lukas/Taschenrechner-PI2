package Taschenrechner.util;

import Taschenrechner.model.Function;

public class DerivativeCalculator {
    public static double derivative(Function f, double x) {
        double h = 1e-5;
        return (f.evaluate(x + h) - f.evaluate(x - h)) / (2 * h);
    }
}