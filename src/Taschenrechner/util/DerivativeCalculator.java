package Taschenrechner.util;

import Taschenrechner.model.Function;

/**
 * Berechnet die Ableitung von Polynomfunktionen. Diese wurde bereits optimiert in der Klasse PolynomialFunction implementiert.
 * Wir haben diese mal drin gelassen, falls sie doch benötigt wird.
 */
public class DerivativeCalculator {
    public static double derivative(Function f, double x) {
        double h = 1e-5;
        return (f.evaluate(x + h) - f.evaluate(x - h)) / (2 * h);
    }
}