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

        // Farben im Dark Mode
        Color bgColor = new Color(45, 45, 45);
        Color accentColor = new Color(70, 70, 70);
        Color sideBg = new Color(30, 30, 30);
        Color textColor = new Color(230, 230, 230);

        getContentPane().setBackground(bgColor);

        /*
         * Sidebar für Modus-Wechsel:
         * Mit BoxLayout verhindern wir, dass die einzelnen Buttons die ganze vertikale Breite füllen.
         * Außerdem bekommt die Sidebar eine feste Breite (hier 60px).
         */
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sideBg);
        sidebar.setPreferredSize(new Dimension(60, 0)); // fixe Breite

        String basePath = "src/Taschenrechner/assets/"; // Pfad relativ zum Projekt
        String[] imageFiles = {"standard.png", "graph.png", "matrix.png"};

        sidebar.add(Box.createVerticalStrut(10)); // Abstand von oben
        for (String fileName : imageFiles) {
            String filePath = basePath + fileName;
            ImageIcon icon = new ImageIcon(filePath);
            Image scaledImg = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            JButton modeButton = new JButton(new ImageIcon(scaledImg));
            modeButton.setBackground(accentColor);
            modeButton.setMaximumSize(new Dimension(50, 50));
            modeButton.setPreferredSize(new Dimension(50, 50));
            modeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(modeButton);
            sidebar.add(Box.createVerticalStrut(10)); // Abstand zwischen Buttons
        }
        sidebar.add(Box.createVerticalGlue()); // flexible Lücke am unteren Rand

        /*
         * Erstelle ein zentrales Content-Panel, das das DisplayPanel (oben) und das ButtonPanel (zentral) enthält.
         * Dadurch weist das DisplayPanel nicht mehr über die Sidebar.
         */
        displayPanel = new DisplayPanel();
        displayPanel.setBackground(bgColor);
        displayPanel.setForeground(textColor);
        // Optional: moderner Rahmen im DisplayPanel
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel = new ButtonPanel();
        buttonPanel.setBackground(bgColor);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(bgColor);
        contentPanel.add(displayPanel, BorderLayout.NORTH);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public DisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}