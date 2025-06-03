package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final DisplayPanel displayPanel;
    private final ButtonPanel buttonPanel;
    private final JPanel sidebar;

    public MainFrame() {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Farben für Dark Mode
        Color bgColor = new Color(45, 45, 45);
        Color textColor = new Color(230, 230, 230);

        getContentPane().setBackground(bgColor);

        // Display Panel (oben)
        displayPanel = new DisplayPanel();
        displayPanel.setBackground(bgColor);
        displayPanel.setForeground(textColor);

        // Button Panel (Zentral)
        buttonPanel = new ButtonPanel();
        buttonPanel.setBackground(bgColor);

        // Sidebar (Links) für Modus-Wechsel
        sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(3, 1, 5, 5));
        sidebar.setBackground(new Color(30, 30, 30));

        String basePath = "src/Taschenrechner/assets/"; // Pfad relativ zum Projekt

        String[] imageFiles = { "standard.png", "graph.png", "matrix.png" };

        for (String fileName : imageFiles) {
            String filePath = basePath + fileName;
            ImageIcon icon = new ImageIcon(filePath); // Direkt aus Dateipfad laden
            Image scaledImg = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JButton modeButton = new JButton(new ImageIcon(scaledImg));
            modeButton.setBackground(new Color(70, 70, 70));
            sidebar.add(modeButton);
        }


        add(sidebar, BorderLayout.WEST);
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
