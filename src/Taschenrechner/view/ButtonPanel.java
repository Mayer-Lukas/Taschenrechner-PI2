package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel mit Ziffern, Punkt, Grund-Operatoren und Funktionen:
 * sin, cos, tan, log, √, etc.
 */
public class ButtonPanel extends JPanel {
    public ButtonPanel() {
        // 6 Zeilen × 5 Spalten, Abstand 5px
        setLayout(new GridLayout(6, 5, 5, 5));

        String[] labels = {
                "sin", "cos", "tan", "log", "√",
                "7",   "8",   "9",   "/",   "^",
                "4",   "5",   "6",   "*",   "(",
                "1",   "2",   "3",   "-",   ")",
                "0",   ".",   "=",   "+",   "C",
                "CE",  "pi",  "e",   "exp", "ln"
        };

        Color btnBg = new Color(60, 60, 60);
        Color btnText = new Color(230, 230, 230);

        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.setActionCommand(label);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            btn.setFocusPainted(false);
            btn.setBackground(btnBg);
            btn.setForeground(btnText);
            add(btn);
        }
    }

    public void addButtonListener(ActionListener listener) {
        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).addActionListener(listener);
            }
        }
    }
}