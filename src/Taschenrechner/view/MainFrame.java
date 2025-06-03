package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Taschenrechner.controller.GraphController;
import Taschenrechner.model.GraphModel;
import Taschenrechner.model.PolynomialFunction;

public class MainFrame extends JFrame {
    private final DisplayPanel displayPanel;
    private final ButtonPanel buttonPanel;
    private final JPanel sidebar;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    // Panels für die einzelnen Modi
    private final JPanel calculatorPanel;
    private final GraphViewPanel graphViewPanel;
    private final JPanel matrixPanel;

    static {
        // Nimbus Look & Feel aktivieren (Dark-Mode-Anpassung via UIManager)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Dark-Mode-Farben
        UIManager.put("control", new Color(45, 45, 45));
        UIManager.put("nimbusBase", new Color(30, 30, 30));
        UIManager.put("nimbusLightBackground", new Color(60, 60, 60));
        UIManager.put("text", new Color(230, 230, 230));
        UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));

        // Schriftarten
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 20));

        // Ränder
        UIManager.put("TextField.border",
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIManager.getColor("nimbusBase"), 2),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
        UIManager.put("Button.border",
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIManager.getColor("nimbusBase"), 1),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
    }

    public MainFrame() {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color bgColor = UIManager.getColor("control");
        Color sideBg = UIManager.getColor("nimbusBase");
        Color textColor = UIManager.getColor("text");

        getContentPane().setBackground(bgColor);

        ////////////////////////////
        // Sidebar für Moduswechsel
        ////////////////////////////
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sideBg);
        sidebar.setPreferredSize(new Dimension(60, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        String basePath = "src/Taschenrechner/assets/";
        // Erstelle drei Buttons für die Modi
        JButton btnCalculator = createSidebarButton(basePath + "standard.png", sideBg);
        JButton btnGraph = createSidebarButton(basePath + "graph.png", sideBg);
        JButton btnMatrix = createSidebarButton(basePath + "matrix.png", sideBg);

        // Aufbau mit Abständen
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCalculator);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnGraph);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnMatrix);
        sidebar.add(Box.createVerticalGlue());

        ////////////////////////////
        // CardPanel für den zentralen Bereich
        ////////////////////////////
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(bgColor);

        // --- Calculator Panel ---
        displayPanel = new DisplayPanel();
        displayPanel.setBackground(bgColor);
        displayPanel.setForeground(textColor);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel = new ButtonPanel();
        buttonPanel.setBackground(bgColor);

        calculatorPanel = new JPanel(new BorderLayout());
        calculatorPanel.setBackground(bgColor);
        calculatorPanel.add(displayPanel, BorderLayout.NORTH);
        calculatorPanel.add(buttonPanel, BorderLayout.CENTER);

        // --- Graph View Panel ---
        GraphModel graphModel = new GraphModel(new PolynomialFunction(1, 0, 0)); // y = x
        GraphPanel graphPanel = new GraphPanel(graphModel);
        graphViewPanel = new GraphViewPanel(graphPanel);
        graphViewPanel.setBackground(bgColor);
        // Verbinde die Logik (GraphController) mit dem GraphViewPanel
        new GraphController(graphViewPanel, graphPanel);

        // --- Matrix Panel ---
        matrixPanel = new JPanel(new BorderLayout());
        matrixPanel.setBackground(bgColor);
        JLabel matrixLabel = new JLabel("Matrizenmodus (in Arbeit)", SwingConstants.CENTER);
        matrixLabel.setForeground(textColor);
        matrixLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        matrixPanel.add(matrixLabel, BorderLayout.CENTER);

        // Füge die Panels dem CardPanel hinzu
        cardPanel.add(calculatorPanel, "calculator");
        cardPanel.add(graphViewPanel, "graph");
        cardPanel.add(matrixPanel, "matrix");

        // Standardmäßig den Calculator-Modus anzeigen
        cardLayout.show(cardPanel, "calculator");

        ////////////////////////////
        // Sidebar Button Aktionen
        ////////////////////////////
        btnCalculator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "calculator");
            }
        });

        btnGraph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "graph");
            }
        });

        btnMatrix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "matrix");
            }
        });

        ////////////////////////////
        // Komponenten hinzufügen
        ////////////////////////////
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createSidebarButton(String filePath, Color bg) {
        ImageIcon icon = new ImageIcon(filePath);
        Image scaledImg = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(scaledImg));
        btn.setBackground(bg);
        btn.setMaximumSize(new Dimension(50, 50));
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return btn;
    }

    public DisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}
