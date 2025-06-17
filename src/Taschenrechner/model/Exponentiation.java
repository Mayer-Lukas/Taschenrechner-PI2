package Taschenrechner.model;


/**
 * Repräsentiert das Potenzieren im Taschenrechner.
 */
public class Exponentiation implements Operator {
    @Override
    public double apply(double a, double b) {
        return Math.pow(a, b);
    }
}
