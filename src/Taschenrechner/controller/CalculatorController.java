package Taschenrechner.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import Taschenrechner.model.Expression;
import Taschenrechner.util.ExpressionParser;
import Taschenrechner.view.DisplayPanel;
import Taschenrechner.view.ButtonPanel;

public class CalculatorController implements ActionListener {
    private final DisplayPanel display;
    private final ButtonPanel buttons;
    private final ExpressionParser parser;
    private final StringBuilder currentInput = new StringBuilder();

    public CalculatorController(DisplayPanel display, ButtonPanel buttons) {
        this.display = display;
        this.buttons = buttons;
        this.parser = new ExpressionParser();
        this.buttons.addButtonListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "C":
                // Alles löschen
                currentInput.setLength(0);
                display.setText("");
                break;

            case "CE":
                // Letztes Zeichen löschen
                if (currentInput.length() > 0) {
                    currentInput.deleteCharAt(currentInput.length() - 1);
                    display.setText(currentInput.toString());
                }
                break;

            case "=":
                // Ausdruck parsen und auswerten
                try {
                    Expression expr = parser.parse(currentInput.toString());
                    double result = expr.evaluate();
                    display.setText(Double.toString(result));
                    // Optional: Ergebnis als neuer Input
                    currentInput.setLength(0);
                    currentInput.append(result);
                } catch (ParseException | ArithmeticException ex) {
                    display.setText("Fehler");
                    currentInput.setLength(0);
                }
                break;

            // ---------- Funktionen, √, Konstanten, ^ ----------
            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "ln":
            case "exp":
                // Hänge "funkName(" an, z. B. "sin(" oder "log("
                currentInput.append(cmd).append("(");
                display.setText(currentInput.toString());
                break;

            case "√":
                // Wir übersetzen "√" in "sqrt("
                currentInput.append("sqrt(");
                display.setText(currentInput.toString());
                break;

            case "^":
                currentInput.append("^");
                display.setText(currentInput.toString());
                break;

            case "pi":
                // Füge π ein (Java-Präzision)
                currentInput.append(Math.PI);
                display.setText(currentInput.toString());
                break;

            case "e":
                // Füge e ein
                currentInput.append(Math.E);
                display.setText(currentInput.toString());
                break;

            // ---------- Standard: Ziffern, Punkt, einfache Operatoren, Klammern ----------
            default:
                // Lande hier, wenn cmd z. B. "0".."9", ".", "+", "-", "*", "/", "(", ")"
                currentInput.append(cmd);
                display.setText(currentInput.toString());
        }
    }
}
