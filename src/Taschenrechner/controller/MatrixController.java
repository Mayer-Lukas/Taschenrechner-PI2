package Taschenrechner.controller;

import Taschenrechner.model.Matrix;
import Taschenrechner.view.MatrixPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MatrixController {
    private final MatrixPanel view;

    public MatrixController(MatrixPanel view) {
        this.view = view;
        view.addRowEchelonListener(new RowEchelonListener());
        view.addSolveListener(new SolveListener());
        view.addTransposeListener(new TransposeListener());
    }

    private double[][] parseInput(String text) throws NumberFormatException {
        String[] lines = text.trim().split("\\n");
        int rows = lines.length;
        if (rows == 0) {
            throw new NumberFormatException("Keine Eingabe");
        }
        String[][] tokens = new String[rows][];
        int cols = -1;
        for (int i = 0; i < rows; i++) {
            tokens[i] = lines[i].trim().split("\\s+");
            if (cols < 0) {
                cols = tokens[i].length;
            } else if (tokens[i].length != cols) {
                throw new NumberFormatException("Ungleiche Spaltenanzahl in Zeile " + (i + 1));
            }
        }
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = Double.parseDouble(tokens[i][j]);
            }
        }
        return data;
    }

    class RowEchelonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double[][] data = parseInput(view.getInputText());
                Matrix m = new Matrix(data.length, data[0].length, data);
                Matrix ref = m.rowEchelonForm();
                view.setOutputText(formatMatrix(ref));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ungültige Eingabe: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            } catch (ArithmeticException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei Berechnung: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class SolveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double[][] data = parseInput(view.getInputText());
                Matrix m = new Matrix(data.length, data[0].length, data);
                double[] sol = m.solve();
                view.setOutputText(vectorToString(sol));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ungültige Eingabe: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            } catch (ArithmeticException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei Berechnung: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class TransposeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double[][] data = parseInput(view.getInputText());
                Matrix m = new Matrix(data.length, data[0].length, data);
                Matrix t = m.transpose();
                view.setOutputText(matrixToString(t));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ungültige Eingabe: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String matrixToString(Matrix m) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getCols(); j++) {
                sb.append(String.format("%.2f", m.get(i, j)));
                if (j < m.getCols() - 1) sb.append(" ");
            }
            if (i < m.getRows() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    private String vectorToString(double[] v) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < v.length; i++) {
            sb.append(String.format("%.2f", v[i]));
            if (i < v.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String formatMatrix(Matrix mat) {
        StringBuilder sb = new StringBuilder();
        double[][] matrix = mat.getData();
        for (double[] row : matrix) {
            sb.append("[ ");
            for (double val : row) {
                if (val == (long) val) {
                    sb.append(String.format("%6d", (long) val));  // Ganze Zahl
                } else {
                    sb.append(String.format("%6.2f", val));        // Mit 2 Nachkommastellen
                }
            }
            sb.append(" ]\n");
        }
        return sb.toString();
    }
}
