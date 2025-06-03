package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import Taschenrechner.model.Function;
import Taschenrechner.model.GraphModel;

/**
 * Moderner GraphPanel mit Zoom (Mausrad), transparenten Gitterlinien und dynamischer Skalierung.
 */
public class GraphPanel extends JPanel {
    private GraphModel graphModel;
    private double xMin, xMax, yMin, yMax;

    // Zoom-Faktor pro Mausrad-„Klick“
    private static final double ZOOM_FACTOR = 1.2;

    public GraphPanel(GraphModel graphModel) {
        this.graphModel = graphModel;
        // Initiale Ansicht aus dem Model
        this.xMin = graphModel.getxMin();
        this.xMax = graphModel.getxMax();
        this.yMin = graphModel.getyMin();
        this.yMax = graphModel.getyMax();

        setBackground(new Color(30, 30, 30)); // Dunkler Hintergrund

        // Mausrad-Zoom
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                zoom(1.0 / ZOOM_FACTOR);
            } else {
                zoom(ZOOM_FACTOR);
            }
        });
    }

    private void zoom(double factor) {
        double centerX = (xMin + xMax) / 2.0;
        double centerY = (yMin + yMax) / 2.0;
        double halfWidth = (xMax - xMin) / 2.0 * factor;
        double halfHeight = (yMax - yMin) / 2.0 * factor;
        xMin = centerX - halfWidth;
        xMax = centerX + halfWidth;
        yMin = centerY - halfHeight;
        yMax = centerY + halfHeight;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphModel == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Hintergrund
        g2.setColor(getBackground());
        g2.fillRect(0, 0, w, h);

        // Zeichne Gitter und Achsen
        drawGridAndAxes(g2, w, h);

        // Zeichne Funktion (blau)
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(68, 175, 240));
        plotFunction(g2, graphModel.getFunction(), w, h);

        // Ableitung (rot), falls aktiviert
        if (graphModel.isShowDerivative() && graphModel.getDerivative() != null) {
            g2.setColor(new Color(240, 65, 65));
            plotFunction(g2, graphModel.getDerivative(), w, h);
        }
    }

    private void drawGridAndAxes(Graphics2D g2, int w, int h) {
        // Farben (transparente Gitterlinien)
        Color gridColor = new Color(80, 80, 80, 80);        // Alpha ~30%
        Color axisColor = new Color(200, 200, 200);
        Color labelColor = new Color(230, 230, 230);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Dynamische Schrittweite: ermittele „schönen“ Abstand basierend auf aktuellem Bereich
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        double xStep = niceStep(xRange / 10.0);
        double yStep = niceStep(yRange / 10.0);

        // Vertikale Gitterlinien
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            int px = mapX(x, w);
            g2.setColor(gridColor);
            g2.drawLine(px, 0, px, h);
        }

        // Horizontale Gitterlinien
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            int py = mapY(y, h);
            g2.setColor(gridColor);
            g2.drawLine(0, py, w, py);
        }

        // Achsen
        int xAxisPx = mapY(0, h); // y=0 in Pixel
        int yAxisPx = mapX(0, w); // x=0 in Pixel
        g2.setColor(axisColor);
        g2.setStroke(new BasicStroke(2f));
        // X-Achse
        g2.draw(new Line2D.Double(0, xAxisPx, w, xAxisPx));
        // Y-Achse
        g2.draw(new Line2D.Double(yAxisPx, 0, yAxisPx, h));

        // Pfeilspitzen an Achsenenden
        int arrowSize = 6;
        // X-Achse Pfeil rechts
        g2.draw(new Line2D.Double(w - arrowSize, xAxisPx - arrowSize, w, xAxisPx));
        g2.draw(new Line2D.Double(w - arrowSize, xAxisPx + arrowSize, w, xAxisPx));
        // Y-Achse Pfeil oben
        g2.draw(new Line2D.Double(yAxisPx - arrowSize, arrowSize, yAxisPx, 0));
        g2.draw(new Line2D.Double(yAxisPx + arrowSize, arrowSize, yAxisPx, 0));

        // Achsenbeschriftung
        g2.setColor(labelColor);
        g2.drawString("x", w - 15, xAxisPx - 10);
        g2.drawString("y", yAxisPx + 10, 15);

        // Skalierungsbeschriftung (Ticks)
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            int px = mapX(x, w);
            int py = xAxisPx;
            // Tick-Mark
            g2.drawLine(px, py - 3, px, py + 3);
            // Zahlenlabel
            String label = formatLabel(x);
            g2.drawString(label, px - g2.getFontMetrics().stringWidth(label) / 2, py + 15);
        }
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            int px = yAxisPx;
            int py = mapY(y, h);
            // Tick-Mark
            g2.drawLine(px - 3, py, px + 3, py);
            // Zahlenlabel (außer y=0)
            if (Math.abs(y) > 1e-6) {
                String label = formatLabel(y);
                g2.drawString(label, px + 5, py + g2.getFontMetrics().getAscent() / 2);
            }
        }
    }

    // Rundet Schrittweite auf „schöne“ Werte: 1, 2, 5 × 10^n
    private double niceStep(double rawStep) {
        double exponent = Math.floor(Math.log10(rawStep));
        double mantissa = rawStep / Math.pow(10, exponent);
        double niceMantissa;
        if (mantissa <= 1) niceMantissa = 1;
        else if (mantissa <= 2) niceMantissa = 2;
        else if (mantissa <= 5) niceMantissa = 5;
        else niceMantissa = 10;
        return niceMantissa * Math.pow(10, exponent);
    }

    private String formatLabel(double value) {
        if (Math.abs(value - Math.round(value)) < 1e-6) {
            return String.format("%.0f", value);
        } else {
            return String.format("%.2f", value);
        }
    }

    private void plotFunction(Graphics2D g2, Function func, int w, int h) {
        boolean first = true;
        int prevX = 0, prevY = 0;

        for (int i = 0; i < w; i++) {
            double x = xMin + i * (xMax - xMin) / (w - 1);
            double y = func.evaluate(x);
            int px = i;
            int py = mapY(y, h);

            if (!first && !Double.isNaN(prevY) && !Double.isNaN(py)) {
                g2.drawLine(prevX, prevY, px, py);
            }
            first = false;
            prevX = px;
            prevY = py;
        }
    }

    private int mapX(double x, int width) {
        return (int) ((x - xMin) / (xMax - xMin) * width);
    }

    private int mapY(double y, int height) {
        return (int) ((yMax - y) / (yMax - yMin) * height);
    }

    public void updateGraphModel(GraphModel newModel) {
        this.graphModel = newModel;
        // Bounds zurücksetzen
        this.xMin = newModel.getxMin();
        this.xMax = newModel.getxMax();
        this.yMin = newModel.getyMin();
        this.yMax = newModel.getyMax();
        repaint();
    }
}
