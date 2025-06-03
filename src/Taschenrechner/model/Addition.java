package Taschenrechner.model;

public class Addition implements Operator {
    @Override
    public double apply(double a, double b) {
        return a + b;
    }
}
