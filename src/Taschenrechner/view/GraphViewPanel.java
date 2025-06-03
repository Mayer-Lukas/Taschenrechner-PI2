package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GraphViewPanel extends JPanel {
    private final JTextField functionInput;
    private final JButton plotButton;
    private final JCheckBox derivativeCheckBox;
    private final GraphPanel graphPanel;

    public GraphViewPanel(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Funktion f(x): ");
        functionInput = new JTextField(20);
        plotButton = new JButton("Plot");
        derivativeCheckBox = new JCheckBox("Ableitung anzeigen");
        // Styling: Bei Dark-Mode muss die Schriftfarbe angepasst werden
        derivativeCheckBox.setForeground(Color.WHITE);
        derivativeCheckBox.setOpaque(false);

        inputPanel.add(label);
        inputPanel.add(functionInput);
        inputPanel.add(plotButton);
        inputPanel.add(derivativeCheckBox);

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