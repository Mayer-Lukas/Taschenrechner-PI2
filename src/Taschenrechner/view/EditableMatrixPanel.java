package Taschenrechner.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Panel für die interaktive Matrizen-Eingabe:
 * - Zeichnet außen runde Klammern.
 * - Im Inneren sitzt ein Grid aus JTextFields, dessen Größe über Spinner eingestellt wird.
 * - Hat ein Mindestmaß, damit Spinner/Spalten‐Auswahl niemals „verschwinden“.
 */
public class EditableMatrixPanel extends JPanel {
    private final JSpinner spinnerRows;
    private final JSpinner spinnerCols;
    private JPanel gridPanel;          // Container für die TextFields
    private JTextField[][] fields;     // Die eigentlichen Eingabefelder

    private final int MIN_SIZE = 1;
    private final int MAX_SIZE = 20;

    // Mindestgröße, damit die Spinner immer sichtbar bleiben
    private final Dimension MINIMUM_DIMENSION = new Dimension(250, 150);

    public EditableMatrixPanel(int initialRows, int initialCols) {
        setLayout(new BorderLayout(5, 5));
        setOpaque(false);

        // Oben: Spinner für Zeilen & Spalten
        JPanel dimensionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        dimensionPanel.setOpaque(false);
        dimensionPanel.add(new JLabel("Zeilen:"));
        spinnerRows = new JSpinner(new SpinnerNumberModel(initialRows, MIN_SIZE, MAX_SIZE, 1));
        dimensionPanel.add(spinnerRows);
        dimensionPanel.add(new JLabel("Spalten:"));
        spinnerCols = new JSpinner(new SpinnerNumberModel(initialCols, MIN_SIZE, MAX_SIZE, 1));
        dimensionPanel.add(spinnerCols);

        add(dimensionPanel, BorderLayout.NORTH);

        // Center: Platz für die Gitter-Eingabefelder (in einem Unterpanel)
        gridPanel = new JPanel();
        gridPanel.setOpaque(false);
        add(gridPanel, BorderLayout.CENTER);

        // Reagiere auf Änderung der Spinner
        ChangeListener sizeListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                rebuildGrid();
            }
        };
        spinnerRows.addChangeListener(sizeListener);
        spinnerCols.addChangeListener(sizeListener);

        // Zu Beginn das Raster einmal bauen
        rebuildGrid();
    }

    /** Baut das Grid neu auf, basierend auf spinnerRows/spinnerCols. */
    private void rebuildGrid() {
        int rows = (int) spinnerRows.getValue();
        int cols = (int) spinnerCols.getValue();

        fields = new JTextField[rows][cols];
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(rows, cols, 2, 2));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JTextField tf = new JTextField(4);
                tf.setHorizontalAlignment(SwingConstants.CENTER);
                fields[i][j] = tf;
                gridPanel.add(tf);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Liest die aktuellen Werte aus den JTextFields und gibt sie als double[][] zurück.
     * Leere oder ungültige Einträge werden als 0.0 interpretiert.
     */
    public double[][] getMatrixData() {
        int rows = fields.length;
        int cols = fields[0].length;
        double[][] data = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String txt = fields[i][j].getText().trim();
                if (txt.isEmpty()) {
                    data[i][j] = 0.0;
                } else {
                    try {
                        data[i][j] = Double.parseDouble(txt);
                    } catch (NumberFormatException ex) {
                        data[i][j] = 0.0;
                    }
                }
            }
        }
        return data;
    }

    /** Zeichnet große runde Klammern um die Eingabe‐Matrix herum. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fields == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(UIManager.getColor("text"));
        g2.setStroke(new BasicStroke(2));

        int rows = fields.length;
        int cols = fields[0].length;

        // Position und Größe des gridPanel (Layout-Bounds)
        Rectangle bounds = gridPanel.getBounds();
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;

        int archWidth  = 20;
        int halfArch   = 10;

        // Linke Klammer (oben)
        g2.drawArc(x - archWidth,      y, archWidth, halfArch * 2,  90, 180);
        // Linke Klammer (Mitte)
        g2.drawLine(x - archWidth/2,   y + halfArch, x - archWidth/2, y + h - halfArch);
        // Linke Klammer (unten)
        g2.drawArc(x - archWidth, y + h - halfArch * 2, archWidth, halfArch * 2, 90, -180);

        // Rechte Klammer (oben)
        int rx = x + w - archWidth/2;
        g2.drawArc(rx,      y, archWidth, halfArch * 2, 270, 180);
        // Rechte Klammer (Mitte)
        g2.drawLine(rx + archWidth/2, y + halfArch, rx + archWidth/2, y + h - halfArch);
        // Rechte Klammer (unten)
        g2.drawArc(rx, y + h - 2 * halfArch, archWidth, 2 * halfArch, 270, -180);
    }

    /** Setzt ein Mindestmaß, damit Spinner/Spalten‐Auswahl nicht verschwindet. */
    @Override
    public Dimension getMinimumSize() {
        return MINIMUM_DIMENSION;
    }

    @Override
    public Dimension getPreferredSize() {
        int rows = (fields != null ? fields.length : 1);
        int cols = (fields != null ? fields[0].length : 1);
        int cellW = 50;
        int cellH = 30;
        int totalW = cols * cellW + 80;  // Einschließlich Klammern‐Rand
        int totalH = rows * cellH + 100; // inkl. Platz für Spinner-Panel
        return new Dimension(totalW, totalH);
    }
}
