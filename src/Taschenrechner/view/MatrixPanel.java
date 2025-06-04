package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Haupt-Panel für den Matrix-Modus:
 * - Dropdown-Auswahl: A+B, A-B, A×B, Zeilen-Stufen-Form(A), LGS lösen(A|b), Transponieren(A)
 * - Je nach Auswahl erscheint 1 oder 2 EditableMatrixPanel
 * - Ausgabe erfolgt in MatrixDisplayPanel (LaTeX-ähnliche große Klammern)
 */
public class MatrixPanel extends JPanel {
    private final JComboBox<String> comboOperation;
    private final EditableMatrixPanel panA;
    private final EditableMatrixPanel panB;
    private final JButton btnCompute;
    private final MatrixDisplayPanel outputDisplay;

    public MatrixPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Color bg = UIManager.getColor("control");
        setBackground(bg);

        // 1) Oben: Operation auswählen + Berechnen-Button
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.add(new JLabel("Operation:"));
        comboOperation = new JComboBox<>(new String[]{
                "A + B",
                "A - B",
                "A × B",
                "Zeilen-Stufen-Form (A)",
                "LGS lösen (A|b)",
                "Transponieren (A)"
        });
        top.add(comboOperation);
        btnCompute = new JButton("Berechnen");
        top.add(btnCompute);
        add(top, BorderLayout.NORTH);

        // 2) Mitte: Zwei EditableMatrixPanel nebeneinander
        JPanel mid = new JPanel(new GridBagLayout());
        mid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        panA = new EditableMatrixPanel(3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mid.add(panA, gbc);

        panB = new EditableMatrixPanel(3, 3);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mid.add(panB, gbc);

        add(mid, BorderLayout.CENTER);

        // 3) Unten: MatrixDisplayPanel für das Ergebnis (in Scrollpane)
        outputDisplay = new MatrixDisplayPanel();
        JScrollPane outScroll = new JScrollPane(outputDisplay);
        outScroll.setBorder(BorderFactory.createTitledBorder("Ergebnis"));
        outScroll.setPreferredSize(new Dimension(400, 200));
        add(outScroll, BorderLayout.SOUTH);

        // PanB nur sichtbar, wenn eine binäre Operation ausgewählt ist
        updateMatrixVisibility();
        comboOperation.addActionListener(e -> updateMatrixVisibility());
    }

    /** Schaltet panB (Matrix B) ein/aus je nach Operation. */
    private void updateMatrixVisibility() {
        String op = (String) comboOperation.getSelectedItem();
        boolean zweimatr = op.equals("A + B")
                || op.equals("A - B")
                || op.equals("A × B");
        panB.setVisible(zweimatr);
        revalidate();
        repaint();
    }

    /** Ermöglicht dem Controller, den Berechnen‐Button anzuhängen. */
    public void addComputeListener(ActionListener listener) {
        btnCompute.addActionListener(listener);
    }

    /** Liefert Matrix A als double[][]. */
    public double[][] getMatrixA() {
        return panA.getMatrixData();
    }

    /** Liefert Matrix B als double[][]. */
    public double[][] getMatrixB() {
        return panB.getMatrixData();
    }

    /** Welche Operation gerade gewählt ist („A + B“, „A - B“, …). */
    public String getOperation() {
        return (String) comboOperation.getSelectedItem();
    }

    /** Zeigt das Ergebnis‐Array als schön gerenderte Matrix an. */
    public void showResult(double[][] matrix) {
        outputDisplay.setMatrix(matrix);
    }

    /** Wenn LGS gelöst wird, zeigt den Lösungsvektor (n×1) an. */
    public void showResultVector(double[] vec) {
        double[][] mat = new double[vec.length][1];
        for (int i = 0; i < vec.length; i++) {
            mat[i][0] = vec[i];
        }
        outputDisplay.setMatrix(mat);
    }
}
