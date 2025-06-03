package Taschenrechner.model;

public class Subtraction implements Operator {
    @Override
    public double apply(double a, double b) {
        return a - b;
    }
}
