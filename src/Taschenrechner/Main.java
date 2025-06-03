package Taschenrechner;

import javax.swing.SwingUtilities;

import Taschenrechner.view.MainFrame;
import Taschenrechner.controller.CalculatorController;

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
