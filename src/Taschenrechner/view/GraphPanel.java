package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import Taschenrechner.model.Function;
import Taschenrechner.model.GraphModel;

/**
 * Moderner GraphPanel mit Zoom (Mausrad), transparenten Gitterlinien, dynamischer Skalierung
 * und lokalem Flag für das Anzeigen der Ableitung. Zusätzliche Easter‐Egg‐Funktion.
 */
public class GraphPanel extends JPanel {
    private GraphModel graphModel;
    private double xMin, xMax, yMin, yMax;
    private boolean showDerivativeFlag = false; // lokal gesteuert durch Checkbox
    private boolean showEasterEgg = false;      // Easter‐Egg‐Flag

    private static final double ZOOM_FACTOR = 1.2;

    public GraphPanel(GraphModel graphModel) {
        this.graphModel = graphModel;
        this.xMin = graphModel.getxMin();
        this.xMax = graphModel.getxMax();
        this.yMin = graphModel.getyMin();
        this.yMax = graphModel.getyMax();
        setBackground(new Color(30, 30, 30));

        addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) zoom(1.0 / ZOOM_FACTOR);
            else zoom(ZOOM_FACTOR);
        });
    }

    public void setShowDerivative(boolean flag) {
        this.showDerivativeFlag = flag;
        repaint();
    }

    /** Setter für den Easter‐Egg‐Modus */
    public void setShowEasterEgg(boolean show) {
        this.showEasterEgg = show;
        repaint();
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

        int w = getWidth(), h = getHeight();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, w, h);

        // 1) Gitter & Achsen zeichnen
        drawGridAndAxes(g2, w, h);

        // 2) Easter‐Egg? Falls ja, Texte unten rechts und ggf. oben links malen, dann return
        if (showEasterEgg) {
            String eggText = "PI2";
            String profText = "Professor Lensch";
            int margin = 20;

            // --- PI2 unten rechts ---
            g2.setColor(new Color(68, 175, 240));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            FontMetrics fmEgg = g2.getFontMetrics();
            int eggWidth  = fmEgg.stringWidth(eggText);
            int eggAscent = fmEgg.getAscent();
            int eggX = w - margin - eggWidth;
            int eggY = h - margin;
            g2.drawString(eggText, eggX, eggY);

            // --- Professor Lensch oben links, nur wenn Ableitung angezeigt wird ---
            if (showDerivativeFlag) {
                g2.setColor(new Color(240, 65, 65));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 24));
                FontMetrics fmProf = g2.getFontMetrics();
                int profAscent = fmProf.getAscent();
                int profY = margin + profAscent;
                g2.drawString(profText, margin, profY);
            }
            return;
        }

        // 3) Funktion plotten (Standardfarbe) und evtl. Ableitung
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(68, 175, 240)); // Hellblau
        plotFunction(g2, graphModel.getFunction(), w, h);

        if (showDerivativeFlag && graphModel.getDerivative() != null) {
            g2.setColor(new Color(240, 65, 65)); // Rot für Ableitung
            plotFunction(g2, graphModel.getDerivative(), w, h);
        }
    }

    private void drawGridAndAxes(Graphics2D g2, int w, int h) {
        Color gridColor = new Color(80, 80, 80, 80);
        Color axisColor = new Color(200, 200, 200);
        Color labelColor = new Color(230, 230, 230);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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
        int xAxisPx = mapY(0, h);
        int yAxisPx = mapX(0, w);
        g2.setColor(axisColor);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new Line2D.Double(0, xAxisPx, w, xAxisPx));
        g2.draw(new Line2D.Double(yAxisPx, 0, yAxisPx, h));

        // Pfeilspitzen
        int arrowSize = 6;
        g2.draw(new Line2D.Double(w - arrowSize, xAxisPx - arrowSize, w, xAxisPx));
        g2.draw(new Line2D.Double(w - arrowSize, xAxisPx + arrowSize, w, xAxisPx));
        g2.draw(new Line2D.Double(yAxisPx - arrowSize, arrowSize, yAxisPx, 0));
        g2.draw(new Line2D.Double(yAxisPx + arrowSize, arrowSize, yAxisPx, 0));

        // Achsenbeschriftungen „x“ und „y“
        g2.setColor(labelColor);
        g2.drawString("x", w - 15, xAxisPx - 10);
        g2.drawString("y", yAxisPx + 10, 15);

        // Zahlenbeschriftungen unten (x) und links (y)
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            int px = mapX(x, w);
            g2.drawLine(px, xAxisPx - 3, px, xAxisPx + 3);
            String label = formatLabel(x);
            g2.drawString(label, px - g2.getFontMetrics().stringWidth(label) / 2, xAxisPx + 15);
        }
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            int py = mapY(y, h);
            g2.drawLine(yAxisPx - 3, py, yAxisPx + 3, py);
            if (Math.abs(y) > 1e-6) {
                String label = formatLabel(y);
                g2.drawString(label, yAxisPx + 5, py + g2.getFontMetrics().getAscent() / 2);
            }
        }
    }

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
            int px = mapX(x, w);
            int py = mapY(y, h);

            if (!first) {
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
        this.xMin = newModel.getxMin();
        this.xMax = newModel.getxMax();
        this.yMin = newModel.getyMin();
        this.yMax = newModel.getyMax();
        repaint();
    }
}
