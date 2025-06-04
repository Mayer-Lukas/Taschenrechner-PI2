package Taschenrechner.controller;

import Taschenrechner.model.Function;
import Taschenrechner.model.GraphModel;
import Taschenrechner.util.FunctionParser;
import Taschenrechner.view.GraphPanel;
import Taschenrechner.view.GraphViewPanel;

import javax.swing.*;

public class GraphController {
    private final GraphViewPanel graphViewPanel;
    private GraphModel graphModel;
    private final GraphPanel graphPanel;

    public GraphController(GraphViewPanel graphViewPanel, GraphPanel graphPanel) {
        this.graphViewPanel = graphViewPanel;
        this.graphPanel = graphPanel;
        initializeListeners();
    }

    private void initializeListeners() {
        graphViewPanel.addPlotButtonListener(_ -> {
            String input = graphViewPanel.getFunctionInput();
            try {
                Function f = FunctionParser.parse(input);
                graphModel = new GraphModel(f);
                // Setze den Ableitungsflag nur, wenn das Kontrollkästchen ausgewählt ist
                graphModel.setShowDerivative(graphViewPanel.isDerivativeSelected());
                graphPanel.updateGraphModel(graphModel);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(graphViewPanel,
                        "Ungültige Funktion: " + ex.getMessage(),
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Option: Wenn der Benutzer das Kontrollkästchen ändert, aktualisiere die Anzeige (falls bereits ein Graph vorhanden ist)
        graphViewPanel.addDerivativeCheckboxListener(_ -> {
            if (graphModel != null) {
                graphModel.setShowDerivative(graphViewPanel.isDerivativeSelected());
                graphPanel.repaint();
            }
        });
    }
}