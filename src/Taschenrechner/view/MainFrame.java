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

    public MainFrame() {
        setTitle("Taschenrechner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Gesamtgröße ggf. anpassen, da wir mehr Inhalte haben (Sidebar + Content)
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Farben im Dark Mode
        Color bgColor = new Color(45, 45, 45);
        Color accentColor = new Color(70, 70, 70);
        Color sideBg = new Color(30, 30, 30);
        Color textColor = new Color(230, 230, 230);

        getContentPane().setBackground(bgColor);

        ////////////////////////////
        // Sidebar für Moduswechsel
        ////////////////////////////
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sideBg);
        sidebar.setPreferredSize(new Dimension(60, 0)); // feste Breite

        String basePath = "src/Taschenrechner/assets/";
        // Erstelle drei Buttons für die Modi
        JButton btnCalculator = createSidebarButton(basePath + "standard.png", accentColor);
        JButton btnGraph = createSidebarButton(basePath + "graph.png", accentColor);
        JButton btnMatrix = createSidebarButton(basePath + "matrix.png", accentColor);

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
        // Hier wird ein Standard-Graph erstellt – zum Beispiel mit einer linearen Funktion als Platzhalter.
        GraphModel graphModel = new GraphModel(new PolynomialFunction(1, 0, 0)); // y = x
        GraphPanel graphPanel = new GraphPanel(graphModel);
        graphViewPanel = new GraphViewPanel(graphPanel);
        graphViewPanel.setBackground(bgColor);
        // Verbinde die Logik (GraphController) mit dem GraphViewPanel
        new GraphController(graphViewPanel, graphPanel);

        // --- Matrix Panel ---
        // Platzhalter-Panel
        matrixPanel = new JPanel(new BorderLayout());
        matrixPanel.setBackground(bgColor);
        JLabel matrixLabel = new JLabel("Matrizenmodus (in Arbeit)", SwingConstants.CENTER);
        matrixLabel.setForeground(textColor);
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

    private JButton createSidebarButton(String filePath, Color accentColor) {
        ImageIcon icon = new ImageIcon(filePath);
        Image scaledImg = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton btn = new JButton(new ImageIcon(scaledImg));
        btn.setBackground(accentColor);
        btn.setMaximumSize(new Dimension(50, 50));
        btn.setPreferredSize(new Dimension(50, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    public DisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}