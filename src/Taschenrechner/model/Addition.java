package Taschenrechner.model;

/**
 * Addition für den Taschenrechner.
 */
public class Addition implements Operator {
    @Override
    public double apply(double a, double b) {
        return a + b;
    }
}







