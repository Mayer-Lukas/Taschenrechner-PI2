package Taschenrechner.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import Taschenrechner.model.Function;
import Taschenrechner.model.GraphModel;

public class GraphPanel extends JPanel {
    private GraphModel graphModel;

    public GraphPanel(GraphModel graphModel) {
        this.graphModel = graphModel;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawAxes(g2);
        // Zeichne die Funktion in Blau
        plotFunction(g2, graphModel.getFunction(), Color.BLUE);
        // Ableitung nur zeichnen, wenn der Flag aktiviert ist
        if (graphModel.isShowDerivative() && graphModel.getDerivative() != null) {
            plotFunction(g2, graphModel.getDerivative(), Color.RED);
        }
    }

    private void drawAxes(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();
        double xMin = graphModel.getxMin(), xMax = graphModel.getxMax();
        double yMin = graphModel.getyMin(), yMax = graphModel.getyMax();

        // Position der Achsen berechnen
        int xAxisPos = (int) ((0 - xMin) / (xMax - xMin) * w);
        int yAxisPos = (int) ((yMax - 0) / (yMax - yMin) * h);

        g2.setColor(Color.GRAY);
        g2.draw(new Line2D.Double(xAxisPos, 0, xAxisPos, h)); // y-Achse
        g2.draw(new Line2D.Double(0, yAxisPos, w, yAxisPos)); // x-Achse
    }

    private void plotFunction(Graphics2D g2, Function func, Color color) {
        int w = getWidth(), h = getHeight();
        double xMin = graphModel.getxMin(), xMax = graphModel.getxMax();
        double yMin = graphModel.getyMin(), yMax = graphModel.getyMax();

        g2.setColor(color);
        int prevX = 0, prevY = 0;
        boolean first = true;
        for (int i = 0; i < w; i++) {
            double x = xMin + i * (xMax - xMin) / w;
            double y = func.evaluate(x);
            int panelX = i;
            int panelY = (int) ((yMax - y) / (yMax - yMin) * h);
            if (!first) {
                g2.draw(new Line2D.Double(prevX, prevY, panelX, panelY));
            }
            first = false;
            prevX = panelX;
            prevY = panelY;
        }
    }

    public void updateGraphModel(GraphModel newModel) {
        this.graphModel = newModel;
        repaint();
    }
}