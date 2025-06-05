package Taschenrechner.view;

import Taschenrechner.model.Complex;
import Taschenrechner.util.ComplexParser;

import javax.swing.*;
import java.awt.*;

/**
 * ComplexPanel für Grundoperationen mit komplexen Zahlen:
 * - Literale: a+bi, a-bi, 3i, -2i, 5
 * - Operatoren: +, -, *, /
 * - Funktionen: conj(z), abs(z)
 * <p>
 * Ergebnis wird immer mit drei Nachkommastellen formatiert, z.B. "4,000" statt "4".
 */
public class ComplexPanel extends JPanel {
    private final JTextField inputField;
    private final JTextArea outputArea;

    public ComplexPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIManager.getColor("control"));

        // Obere Eingabezeile
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(UIManager.getColor("control"));
        JLabel label = new JLabel("Ausdruck:");
        label.setForeground(UIManager.getColor("text"));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputField = new JTextField(25);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JButton calcButton = new JButton("Berechnen");
        calcButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        topPanel.add(label);
        topPanel.add(inputField);
        topPanel.add(calcButton);

        // Mittlerer Bereich: Ausgabe
        outputArea = new JTextArea(8, 40);
        outputArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(UIManager.getColor("nimbusLightBackground"));
        outputArea.setForeground(UIManager.getColor("text"));
        outputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIManager.getColor("nimbusBase"), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(outputArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        calcButton.addActionListener(e -> evaluateInput());
    }

    private void evaluateInput() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Bitte einen komplexen Ausdruck eingeben.",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Complex z = ComplexParser.parse(text);

            // Kartesische Darstellung (immer 3 Nachkommastellen)

            String sb = "Ergebnis (kartesisch):\n  " +
                    formatComplex(z) +
                    "\n\n" +

                    // Betrag |z| als reelle Zahl mit 3 Nachkommastellen
                    "Betrag |z|:    " +
                    String.format("%.3f", z.abs()) +
                    "\n" +

                    // Konjugierte
                    "Konjugierte:   " +
                    formatComplex(z.conj()) +
                    "\n";

            outputArea.setText(sb);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Ungültiger Ausdruck: " + ex.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Formatiert eine Complex-Zahl mit genau drei Nachkommastellen, z.B. "4,000 + 0,000i".
     */
    private String formatComplex(Complex z) {
        // Re- und Im-Teil mit drei Nachkommastellen
        String realStr = String.format("%.3f", z.re());
        String imagStr = String.format("%.3f", Math.abs(z.im()));

        if (Math.abs(z.im()) < 1e-12) {
            // nur reeller Anteil
            return realStr;
        }
        if (Math.abs(z.re()) < 1e-12) {
            // nur imaginärer Anteil („0 ± bi“)
            return (z.im() < 0 ? "-" : "") + imagStr + "i";
        }
        return realStr
                + (z.im() < 0 ? " - " : " + ")
                + imagStr + "i";
    }
}
