package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final DisplayPanel displayPanel;
    private final ButtonPanel buttonPanel;

    public MainFrame() {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 450);
        setLocationRelativeTo(null); // zentriert

        setLayout(new BorderLayout());

        displayPanel = new DisplayPanel();
        buttonPanel = new ButtonPanel();

        add(displayPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public DisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}
