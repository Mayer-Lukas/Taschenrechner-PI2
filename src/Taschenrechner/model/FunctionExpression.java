package Taschenrechner.model;

/**
 * Repräsentiert einen Funktionsaufruf wie sin(arg), cos(arg), sqrt(arg) usw.
 */
public class FunctionExpression implements Expression {
    private final String name;
    private final Expression argument;

    public FunctionExpression(String name, Expression argument) {
        this.name = name;
        this.argument = argument;
    }

    @Override
    public double evaluate() {
        double x = argument.evaluate();
        return switch (name) {
            case "sin" -> Math.sin(x);
            case "cos" -> Math.cos(x);
            case "tan" -> Math.tan(x);
            case "sqrt" -> Math.sqrt(x);
            case "log" -> Math.log10(x);
            case "ln" -> Math.log(x);
            case "exp" -> Math.exp(x);
            default -> throw new IllegalArgumentException("Unbekannte Funktion: " + name);
        };
    }
}
