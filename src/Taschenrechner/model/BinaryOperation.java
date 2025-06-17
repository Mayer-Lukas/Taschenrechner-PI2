package Taschenrechner.model;

/**
 * Klasse für binäre Operationen im Taschenrechner.
 */
public class BinaryOperation implements Expression {
    private final Expression left;
    private final Expression right;
    private final Operator operator;

    public BinaryOperation(Expression left, Expression right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public double evaluate() {
        return operator.apply(left.evaluate(), right.evaluate());
    }
}
