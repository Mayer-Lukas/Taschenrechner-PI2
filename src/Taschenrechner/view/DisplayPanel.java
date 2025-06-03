package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;

/**
 * Oberes Panel: Zeigt den aktuellen Eingabetext oder das Ergebnis an.
 */
public class DisplayPanel extends JPanel {
    private final JTextField textField;

    public DisplayPanel() {
        setLayout(new BorderLayout());
        textField = new JTextField();
        textField.setEditable(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 24));
        // Moderner Border für das Textfeld:
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(textField, BorderLayout.CENTER);
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }
}