package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * GraphViewPanel mit schickem Input-Bereich, Zoom-Hinweis & Checkbox.
 */
public class GraphViewPanel extends JPanel {
    private final JTextField functionInput;
    private final JButton plotButton;
    private final JCheckBox derivativeCheckBox;
    private final GraphPanel graphPanel;

    public GraphViewPanel(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        setLayout(new BorderLayout());
        setBackground(UIManager.getColor("control"));

        // Input-Bereich oben
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.setBackground(UIManager.getColor("control"));

        JLabel label = new JLabel("Funktion f(x): ");
        label.setForeground(UIManager.getColor("text"));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        functionInput = new JTextField(20);
        functionInput.setBackground(UIManager.getColor("nimbusLightBackground"));
        functionInput.setForeground(UIManager.getColor("text"));
        functionInput.setCaretColor(UIManager.getColor("text"));
        functionInput.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        plotButton = new JButton("Plot");
        plotButton.setBackground(UIManager.getColor("nimbusLightBackground"));
        plotButton.setForeground(UIManager.getColor("text"));
        plotButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        derivativeCheckBox = new JCheckBox("Ableitung anzeigen");
        derivativeCheckBox.setBackground(UIManager.getColor("control"));
        derivativeCheckBox.setForeground(UIManager.getColor("text"));
        derivativeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel zoomHint = new JLabel("Scrollen zum Zoomen");
        zoomHint.setForeground(new Color(200, 200, 200));
        zoomHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        inputPanel.add(label);
        inputPanel.add(functionInput);
        inputPanel.add(plotButton);
        inputPanel.add(derivativeCheckBox);
        inputPanel.add(zoomHint);

        add(inputPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    public String getFunctionInput() {
        return functionInput.getText();
    }

    public void addPlotButtonListener(ActionListener listener) {
        plotButton.addActionListener(listener);
    }

    public boolean isDerivativeSelected() {
        return derivativeCheckBox.isSelected();
    }

    public void addDerivativeCheckboxListener(ActionListener listener) {
        derivativeCheckBox.addActionListener(listener);
    }
}
