package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Objects;

import Taschenrechner.controller.GraphController;
import Taschenrechner.controller.MatrixController;
import Taschenrechner.model.GraphModel;
import Taschenrechner.model.PolynomialFunction;

public class MainFrame extends JFrame {
    private final DisplayPanel displayPanel;
    private final ButtonPanel buttonPanel;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    static {
        // Nimbus Look & Feel aktivieren (Dark-Mode-Anpassung)
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
        setLayout(new BorderLayout());

        Color bgColor   = UIManager.getColor("control");
        Color sideBg    = UIManager.getColor("nimbusBase");
        Color textColor = UIManager.getColor("text");

        getContentPane().setBackground(bgColor);

        // Sidebar für Moduswechsel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sideBg);
        sidebar.setPreferredSize(new Dimension(60, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // Drei Buttons für die Modi
        JButton btnCalculator = createSidebarButton("/Taschenrechner/assets/standard.png", sideBg);
        JButton btnGraph      = createSidebarButton("/Taschenrechner/assets/graph.png",    sideBg);
        JButton btnMatrix     = createSidebarButton("/Taschenrechner/assets/matrix.png",  sideBg);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCalculator);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnGraph);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnMatrix);
        sidebar.add(Box.createVerticalGlue());

        // CardPanel für zentralen Bereich
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(bgColor);

        // --- Calculator Panel ---
        displayPanel = new DisplayPanel();
        displayPanel.setBackground(bgColor);
        displayPanel.setForeground(textColor);
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buttonPanel = new ButtonPanel();
        buttonPanel.setBackground(bgColor);

        JPanel calculatorPanel = new JPanel(new BorderLayout());
        calculatorPanel.setBackground(bgColor);
        calculatorPanel.add(displayPanel, BorderLayout.NORTH);
        calculatorPanel.add(buttonPanel, BorderLayout.CENTER);

        registerKeyBindings();

        // --- Graph View Panel ---
        GraphModel graphModel = new GraphModel(new PolynomialFunction(1, 0, 0)); // y = x
        GraphPanel graphPanel = new GraphPanel(graphModel);
        GraphViewPanel graphViewPanel = new GraphViewPanel(graphPanel);
        graphViewPanel.setBackground(bgColor);
        new GraphController(graphViewPanel, graphPanel);

        // --- Matrix Panel ---
        MatrixPanel matrixPanel = new MatrixPanel();
        matrixPanel.setBackground(bgColor);
        new MatrixController(matrixPanel);

        // Panels ins CardPanel einfügen
        cardPanel.add(calculatorPanel, "calculator");
        cardPanel.add(graphViewPanel,   "graph");
        cardPanel.add(matrixPanel,      "matrix");

        // Standardmäßig Rechner anzeigen
        setSize(400, 600);
        setLocationRelativeTo(null);
        cardLayout.show(cardPanel, "calculator");

        // Sidebar-Buttons Aktionen
        btnCalculator.addActionListener((ActionEvent e) -> {
            cardLayout.show(cardPanel, "calculator");
            setSize(400, 600);
            setLocationRelativeTo(null);
        });
        btnGraph.addActionListener((ActionEvent e) -> {
            cardLayout.show(cardPanel, "graph");
            setSize(800, 600);
            setLocationRelativeTo(null);
        });
        btnMatrix.addActionListener((ActionEvent e) -> {
            cardLayout.show(cardPanel, "matrix");
            setSize(600, 600);
            setLocationRelativeTo(null);
        });

        add(sidebar,  BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createSidebarButton(String resourcePath, Color bg) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(resourcePath)));
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

    private void registerKeyBindings() {
        java.util.List<JButton> allButtons = findAllButtons(buttonPanel);

        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();

        final JButton[] equalsButton = {null};
        final JButton[] clearButton  = {null};
        final JButton[] deleteButton = {null};

        for (JButton btn : allButtons) {
            String text = btn.getText();
            if (text == null || text.isEmpty()) continue;

            switch (text) {
                case "="  -> equalsButton[0] = btn;
                case "C"  -> clearButton[0]  = btn;
                case "CE" -> deleteButton[0] = btn;
            }

            if (text.length() == 1) {
                char keyChar = text.charAt(0);
                String actionKey = "key_" + keyChar;
                im.put(KeyStroke.getKeyStroke(keyChar), actionKey);
                am.put(actionKey, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btn.doClick();
                    }
                });
            }
        }

        if (equalsButton[0] != null) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "equals_key");
            am.put("equals_key", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    equalsButton[0].doClick();
                }
            });
        }

        if (deleteButton[0] != null) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete_key");
            am.put("delete_key", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteButton[0].doClick();
                }
            });
        }

        if (clearButton[0] != null) {
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear_key");
            am.put("clear_key", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearButton[0].doClick();
                }
            });
        }
    }

    private java.util.List<JButton> findAllButtons(Container container) {
        java.util.List<JButton> buttons = new java.util.ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton button) {
                buttons.add(button);
            } else if (comp instanceof Container child) {
                buttons.addAll(findAllButtons(child));
            }
        }
        return buttons;
    }

    public DisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }
}
