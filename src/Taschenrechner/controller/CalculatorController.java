package Taschenrechner.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import Taschenrechner.model.Expression;
import Taschenrechner.util.ExpressionParser;
import Taschenrechner.view.DisplayPanel;
import Taschenrechner.view.ButtonPanel;

/**
 * Controller für den Standard‐Taschenrechner.
 * Reagiert auf Button‐Klicks, baut den Ausdruck auf, parst und wertet ihn aus.
 * Fängt Division‐durch‐Null ab und zeigt stattdessen ein Bild‐Popup.
 */
public class CalculatorController implements ActionListener {
    private final DisplayPanel display;
    private final ExpressionParser parser;
    private final StringBuilder currentInput = new StringBuilder();

    public CalculatorController(DisplayPanel display, ButtonPanel buttons) {
        this.display = display;
        this.parser = new ExpressionParser();
        buttons.addButtonListener(this);
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
                if (!currentInput.isEmpty()) {
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
                    // Ergebnis als neuer Input verwenden
                    currentInput.setLength(0);
                    currentInput.append(result);
                } catch (ArithmeticException ae) {
                    // Division durch Null abgefangen → Bild‐Popup
                    showDivideByZeroPopup();
                    currentInput.setLength(0);
                } catch (ParseException pe) {
                    // Syntax‐Fehler
                    display.setText("Fehler");
                    currentInput.setLength(0);
                }
                break;

            // ---------- Trigonometrische Funktionen, √, Konstanten, ^ ----------
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

    /**
     * Zeigt ein Popup‐Dialogfenster mit dem Bild "div0.png", wenn durch 0 geteilt wurde.
     */
    private void showDivideByZeroPopup() {
        ImageIcon icon;
        try {
            icon = new ImageIcon(
                    Objects.requireNonNull(
                            getClass().getResource("/Taschenrechner/assets/div0.png")
                    )
            );
        } catch (Exception e) {
            // Falls das Bild nicht gefunden wird, zumindest eine Text‐Warnung
            JOptionPane.showMessageDialog(
                    null,
                    "Bild 'div0.png' nicht gefunden!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Zeige das Bild ohne zusätzlichen Text
        JOptionPane.showMessageDialog(
                null,
                "",
                "So nicht!",
                JOptionPane.INFORMATION_MESSAGE,
                icon
        );
    }
}
