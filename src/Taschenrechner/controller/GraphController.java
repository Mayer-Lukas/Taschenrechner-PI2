package Taschenrechner.controller;

import Taschenrechner.model.Function;
import Taschenrechner.model.GraphModel;
import Taschenrechner.util.FunctionParser;
import Taschenrechner.view.GraphPanel;
import Taschenrechner.view.GraphViewPanel;

import javax.swing.*;

/**
 * Controller für die Graphen‐Ansicht.
 * Reagiert auf Button-Klicks, parst die eingegebene Funktion und aktualisiert das GraphPanel.
 * Enthält auch einen Easter-Egg-Modus, der aktiviert wird, wenn das Schlüsselwort "easteregg" eingegeben wird.
 */
public class GraphController {
    private final GraphViewPanel graphViewPanel;
    private GraphModel graphModel;
    private final GraphPanel graphPanel;

    public GraphController(GraphViewPanel graphViewPanel, GraphPanel graphPanel) {
        this.graphViewPanel = graphViewPanel;
        this.graphPanel = graphPanel;
        initializeListeners();
    }

    /**
     * Initialisiert die Event-Listener für die GraphViewPanel-Komponenten.
     * Reagiert auf Button-Klicks und Checkbox-Änderungen.
     * Aktualisiert das GraphPanel entsprechend der eingegebenen Funktion.
     */
    private void initializeListeners() {
        graphViewPanel.addPlotButtonListener(_event -> {
            String input = graphViewPanel.getFunctionInput().trim();

            // 1) Check auf Easter‐Egg‐Schlüsselwort
            if (input.equalsIgnoreCase("easteregg")) {
                // Easteregg‐Modus einschalten (Flag in GraphPanel setzen)
                graphPanel.setShowEasterEgg(true);
                graphPanel.repaint();
                return;
            }

            // 2) Ansonsten: normaler Modus
            graphPanel.setShowEasterEgg(false);

            try {
                Function f = FunctionParser.parse(input);
                graphModel = new GraphModel(f);
                // Setze den Ableitungsflag nur, wenn das Kontrollkästchen ausgewählt ist
                graphModel.setShowDerivative(graphViewPanel.isDerivativeSelected());
                graphPanel.updateGraphModel(graphModel);
            } catch (IllegalArgumentException ex) {
                graphPanel.setShowEasterEgg(false);
                JOptionPane.showMessageDialog(graphViewPanel,
                        "Ungültige Funktion: " + ex.getMessage(),
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Checkbox‐Listener bleibt unverändert
        graphViewPanel.addDerivativeCheckboxListener(_event -> {
            if (graphModel != null) {
                graphModel.setShowDerivative(graphViewPanel.isDerivativeSelected());
                graphPanel.repaint();
            }
        });
    }
}
