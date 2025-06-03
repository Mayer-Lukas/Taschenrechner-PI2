package Taschenrechner.model;

public class Multiplication implements Operator {
    @Override
    public double apply(double a, double b) {
        return a * b;
    }
}
