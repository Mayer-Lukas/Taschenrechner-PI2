package Taschenrechner.model;



public class Exponential implements Operator {
    @Override
    public double apply(double a, double b) {
        return Math.pow(a, b);
    }
}
