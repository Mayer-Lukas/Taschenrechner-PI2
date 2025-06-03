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

        // Listener für alle Buttons registrieren:
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
                } catch (ParseException ex) {
                    display.setText("Fehler");
                    currentInput.setLength(0);
                } catch (ArithmeticException ex) {
                    // z. B. Division durch 0
                    display.setText("Fehler");
                    currentInput.setLength(0);
                }
                break;

            default:
                // Ziffer, Punkt, Operator, Klammer → anhängen
                currentInput.append(cmd);
                display.setText(currentInput.toString());
        }
    }
}
