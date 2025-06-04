package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MatrixPanel extends JPanel {
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JButton btnRowEchelon;
    private final JButton btnSolve;
    private final JButton btnTranspose;

    public MatrixPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInput = new JLabel("Matrix eingeben (Zeilen durch Neue Zeile, Werte durch Leerzeichen):");
        inputArea = new JTextArea(8, 40);
        JScrollPane scrollInput = new JScrollPane(inputArea);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(lblInput, BorderLayout.NORTH);
        topPanel.add(scrollInput, BorderLayout.CENTER);

        btnRowEchelon = new JButton("Zeilen-Stufen-Form");
        btnSolve = new JButton("Lösen");
        btnTranspose = new JButton("Transponieren");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRowEchelon);
        buttonPanel.add(btnSolve);
        buttonPanel.add(btnTranspose);

        JLabel lblOutput = new JLabel("Ergebnis:");
        outputArea = new JTextArea(8, 40);
        outputArea.setEditable(false);
        JScrollPane scrollOutput = new JScrollPane(outputArea);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(lblOutput, BorderLayout.NORTH);
        bottomPanel.add(scrollOutput, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public String getInputText() {
        return inputArea.getText();
    }

    public void setOutputText(String text) {
        outputArea.setText(text);
    }

    public void addRowEchelonListener(ActionListener listener) {
        btnRowEchelon.addActionListener(listener);
    }

    public void addSolveListener(ActionListener listener) {
        btnSolve.addActionListener(listener);
    }

    public void addTransposeListener(ActionListener listener) {
        btnTranspose.addActionListener(listener);
    }
}
