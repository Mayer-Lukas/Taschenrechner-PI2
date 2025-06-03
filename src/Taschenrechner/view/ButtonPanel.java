package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Panel mit Ziffern, Punkt, Grund-Operatoren und allen Funktionen:
 * ^, sin, cos, tan, sqrt (√), log, ln, exp, pi, e, (, ), C, CE
 */
public class ButtonPanel extends JPanel {
    public ButtonPanel() {
        // 6 Zeilen × 5 Spalten, Abstand 5px
        setLayout(new GridLayout(6, 5, 5, 5));

        String[] labels = {
                "sin", "cos", "tan", "log", "√",
                "7",   "8",   "9",   "/",    "^",
                "4",   "5",   "6",   "*",    "(",
                "1",   "2",   "3",   "-",    ")",
                "0",   ".",   "=",   "+",    "C",
                "CE",  "pi",  "e",   "exp",  "ln"
        };

        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.setActionCommand(label);
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
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
