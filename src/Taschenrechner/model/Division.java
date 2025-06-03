package Taschenrechner.model;

public class Division implements Operator{
    @Override
    public double apply(double a, double b) {
        return a / b;
    }
}
