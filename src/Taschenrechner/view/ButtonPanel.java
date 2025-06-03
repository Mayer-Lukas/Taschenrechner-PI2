package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Grid mit Ziffern-/Operator-Buttons.
 */
public class ButtonPanel extends JPanel {
    public ButtonPanel() {
        setLayout(new GridLayout(5, 4, 5, 5)); // 5 Zeilen, 4 Spalten, 5px Abstand

        String[] labels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "(", ")", "C", "CE"
        };
        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.setActionCommand(label);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            add(btn);
        }
    }

    /**
     * Registriert den ActionListener für alle Buttons.
     */
    public void addButtonListener(ActionListener listener) {
        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).addActionListener(listener);
            }
        }
    }
}
