package Taschenrechner.model;

public class Division implements Operator {
    @Override
    public double apply(double left, double right) {
        if (right == 0) {
            throw new ArithmeticException("Division durch 0");
        }
        return left / right;
    }
}
