package Taschenrechner.controller;

import Taschenrechner.model.Matrix;
import Taschenrechner.view.MatrixPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Controller für die Matrix-Berechnungen.
 */
public class MatrixController {
    private final MatrixPanel view;

    public MatrixController(MatrixPanel view) {
        this.view = view;
        view.addComputeListener(new ComputeListener());
    }

    /**
     * Listener für die Berechnungs-Buttons.
     * Führt die entsprechende Matrix-Operation aus.
     */
    private class ComputeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String op = view.getOperation();
                switch (op) {
                    case "A + B" -> computeAddSub(true);
                    case "A - B" -> computeAddSub(false);
                    case "A × B" -> computeMultiply();
                    case "Zeilen-Stufen-Form (A)" -> computeRowEchelon();
                    case "LGS lösen (A|b)"       -> computeSolveLGS();
                    case "Transponieren (A)"     -> computeTranspose();
                    default -> throw new IllegalStateException("Unbekannte Operation: " + op);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Eingabe-Fehler: " + ex.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (ArithmeticException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Berechnungs-Fehler: " + ex.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unbekannter Fehler: " + ex.getMessage(),
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        /** A + B oder A - B */
        private void computeAddSub(boolean isAdd) {
            double[][] a = view.getMatrixA();
            double[][] b = view.getMatrixB();
            if (a.length == 0 || b.length == 0) {
                throw new IllegalArgumentException("Beide Matrizen müssen existieren.");
            }
            if (a.length != b.length || a[0].length != b[0].length) {
                throw new IllegalArgumentException("Matrizen müssen die gleichen Dimensionen haben.");
            }
            Matrix mA = new Matrix(a.length, a[0].length, deepCopy(a));
            Matrix mB = new Matrix(b.length, b[0].length, deepCopy(b));
            Matrix res = isAdd ? mA.add(mB) : mA.sub(mB);
            view.showResult(res.getData());
        }

        /** A × B */
        private void computeMultiply() {
            double[][] a = view.getMatrixA();
            double[][] b = view.getMatrixB();
            if (a.length == 0 || b.length == 0) {
                throw new IllegalArgumentException("Beide Matrizen müssen existieren.");
            }
            if (a[0].length != b.length) {
                throw new IllegalArgumentException("Spalten von A müssen gleich Zeilen von B sein.");
            }
            Matrix mA = new Matrix(a.length, a[0].length, deepCopy(a));
            Matrix mB = new Matrix(b.length, b[0].length, deepCopy(b));
            Matrix res = mA.mult(mB);
            view.showResult(res.getData());
        }

        /** Zeilen‐Stufen‐Form von A */
        private void computeRowEchelon() {
            double[][] a = view.getMatrixA();
            if (a.length == 0) {
                throw new IllegalArgumentException("Matrix A darf nicht leer sein.");
            }
            Matrix mA = new Matrix(a.length, a[0].length, deepCopy(a));
            Matrix ref = mA.rowEchelonForm();
            view.showResult(ref.getData());
        }

        /** LGS lösen, A muss augmentierte Matrix (n×(n+1)) sein. */
        private void computeSolveLGS() {
            double[][] a = view.getMatrixA();
            int rows = a.length;
            int cols = a[0].length;
            if (cols != rows+1) {
                throw new IllegalArgumentException("LGS‐Matrix muss n x (n+1) sein.");
            }
            Matrix mA = new Matrix(rows, cols, deepCopy(a));
            double[] sol = mA.solve();
            view.showResultVector(sol);
        }

        /** Transponieren von A */
        private void computeTranspose() {
            double[][] a = view.getMatrixA();
            if (a.length == 0) {
                throw new IllegalArgumentException("Matrix A darf nicht leer sein.");
            }
            Matrix mA = new Matrix(a.length, a[0].length, deepCopy(a));
            Matrix t = mA.transpose();
            view.showResult(t.getData());
        }

        /** Hilfsmethode: Deep Copy von double[][] */
        private double[][] deepCopy(double[][] orig) {
            int r = orig.length;
            int c = orig[0].length;
            double[][] copy = new double[r][c];
            for (int i = 0; i < r; i++) {
                copy[i] = Arrays.copyOf(orig[i], c);
            }
            return copy;
        }
    }
}
