package Taschenrechner;

import javax.swing.SwingUtilities;

import Taschenrechner.view.MainFrame;
import Taschenrechner.controller.CalculatorController;

/**
 * Hauptklasse für den Taschenrechner.
 * Startet die GUI und initialisiert den Controller.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            // Controller an DisplayPanel und ButtonPanel koppeln:
            new CalculatorController(
                    frame.getDisplayPanel(),
                    frame.getButtonPanel()
            );
        });
    }
}
