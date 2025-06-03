package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;

/**
 * Oberes Panel: Zeigt aktuellen Eingabetext oder Ergebnis an.
 */
public class DisplayPanel extends JPanel {
    private final JTextField textField;

    public DisplayPanel() {
        setLayout(new BorderLayout());
        textField = new JTextField();
        textField.setEditable(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 24));
        add(textField, BorderLayout.CENTER);
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }
}
