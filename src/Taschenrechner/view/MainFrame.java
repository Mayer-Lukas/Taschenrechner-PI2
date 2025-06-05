package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

import Taschenrechner.controller.GraphController;
import Taschenrechner.controller.MatrixController;
import Taschenrechner.model.GraphModel;
import Taschenrechner.model.PolynomialFunction;

/**
 * MainFrame mit benutzerdefinierter, dunkler Titelleiste und integriertem ComplexPanel.
 */
public class MainFrame extends JFrame {
    private final DisplayPanel displayPanel;
    private final ButtonPanel buttonPanel;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    // Für Fenster-Verschiebdarstellung
    private int mouseX, mouseY;

    static {
        // Nimbus Look & Feel aktivieren (Dark-Mode)
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
        super(); // Kein Standard-Titel, da wir eigene Titelleiste zeichnen

        // 1) Keine native Dekoration (eigene Titelleiste)
        setUndecorated(true);

        // 2) Icon für Titelleiste und Taskleiste
        Image icon = new ImageIcon(Objects.requireNonNull(
                getClass().getResource("/Taschenrechner/assets/app_icon.png")
        )).getImage();
        setIconImage(icon);

        // 3) Eigene dunkle Titelleiste
        JPanel titleBar = createTitleBar(icon);

        // 4) Restliche Einrichtung
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

        // Vier Buttons für die Modi
        JButton btnCalculator = createSidebarButton("/Taschenrechner/assets/standard.png", sideBg);
        JButton btnGraph      = createSidebarButton("/Taschenrechner/assets/graph.png",    sideBg);
        JButton btnMatrix     = createSidebarButton("/Taschenrechner/assets/matrix.png",  sideBg);
        JButton btnComplex    = createSidebarButton("/Taschenrechner/assets/complex.png", sideBg);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCalculator);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnGraph);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnMatrix);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnComplex);
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
        displayPanel.setFocusable(true);

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

        // --- Complex Panel ---
        ComplexPanel complexPanel = new ComplexPanel();
        complexPanel.setBackground(bgColor);

        // Panels ins CardPanel einfügen
        cardPanel.add(calculatorPanel, "calculator");
        cardPanel.add(graphViewPanel,   "graph");
        cardPanel.add(matrixPanel,      "matrix");
        cardPanel.add(complexPanel,     "complex");

        // 5) Layout zusammenbauen
        getContentPane().add(titleBar,   BorderLayout.NORTH);
        getContentPane().add(sidebar,    BorderLayout.WEST);
        getContentPane().add(cardPanel,  BorderLayout.CENTER);

        // 6) Standard-Tab und Größe
        cardLayout.show(cardPanel, "calculator");
        setSize(400, 600);
        setLocationRelativeTo(null);

        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                // Sobald das Fenster den Fokus erhält (z.B. nach Schließen des Bilder-Popups),
                // holen wir uns den Fokus zurück auf das DisplayPanel:
                displayPanel.requestFocusInWindow();
            }
        });

        setVisible(true);

        // 7) Aktionen für Sidebar-Buttons
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
        btnComplex.addActionListener((ActionEvent e) -> {
            cardLayout.show(cardPanel, "complex");
            setSize(650, 500); // Fenstergröße so, dass "Berechnen" sichtbar bleibt
            setLocationRelativeTo(null);
        });
    }

    /**
     * Erstellt die benutzerdefinierte, dunkle Titelleiste mit Icon, Titel und transparentem Close-Button.
     */
    private JPanel createTitleBar(Image icon) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(UIManager.getColor("nimbusBase"));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        // Icon (20×20) und Titeltext
        JLabel lblIcon = new JLabel(new ImageIcon(icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JLabel lblTitle = new JLabel("  Taschenrechner");
        lblTitle.setForeground(UIManager.getColor("text"));
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        leftPanel.setOpaque(false);
        leftPanel.add(lblIcon);
        leftPanel.add(lblTitle);

        // Schließen-Button ("X"), transparent
        JButton btnClose = new JButton("X");
        btnClose.setForeground(UIManager.getColor("text"));
        btnClose.setBackground(new Color(0, 0, 0, 0)); // komplett transparent
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));

        // Hover-Effekt: Button-Hintergrund bei Maus darüber
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClose.setBackground(new Color(200, 50, 50));
                btnClose.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnClose.setBackground(new Color(0, 0, 0, 0));
                btnClose.setOpaque(false);
            }
        });

        // Fenster verschiebbar machen
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen() - mouseX;
                int y = e.getYOnScreen() - mouseY;
                setLocation(x, y);
            }
        });

        titleBar.add(leftPanel, BorderLayout.WEST);
        titleBar.add(btnClose, BorderLayout.EAST);
        return titleBar;
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
