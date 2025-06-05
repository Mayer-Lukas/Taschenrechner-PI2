package Taschenrechner.view;

import javax.swing.*;

import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

/**
 * Statt einer handgezeichneten Klammer benutzen wir hier JLaTeXMath,
 * um die Matrixausgabe exakt wie in LaTeX aussehen zu lassen.
 */
public class MatrixDisplayPanel extends JLabel {
    public MatrixDisplayPanel() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setBackground(UIManager.getColor("control"));
        setOpaque(true);
    }

    /**
     * Baut einen LaTeX‐String \\begin{pmatrix}…\\end{pmatrix} aus dem double[][]-Array
     * und setzt ihn als Icon dieser JLabel.
     */
    public void setMatrix(double[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            setIcon(null);
            setText("Keine Matrix");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{pmatrix}");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                double val = matrix[i][j];
                // Ganzzahlig oder mit 2 Nachkommastellen
                if (val == (long) val) {
                    sb.append((long) val);
                } else {
                    sb.append(String.format("%.2f", val));
                }
                if (j < matrix[i].length - 1) {
                    sb.append(" & ");
                }
            }
            if (i < matrix.length - 1) {
                sb.append(" \\\\ ");
            }
        }
        sb.append("\\end{pmatrix}");

        String latex = "$$" + sb + "$$"; // doppelte Dollar für display style
        TeXFormula formula = new TeXFormula(latex);
        TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, 20);

        setIcon(icon);
        setText(null);
    }
}
