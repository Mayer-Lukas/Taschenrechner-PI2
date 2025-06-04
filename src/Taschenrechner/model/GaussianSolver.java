package Taschenrechner.model;

public class GaussianSolver {
    private static final double EPSILON = 1e-10;
    private final int n;              // Anzahl der Gleichungen
    private final double[][] aug;     // Augmentierte Matrix (n x (n+1))

    /**
     * Konstruktor: erwartet eine augmentierte Matrix m mit n Zeilen und n+1 Spalten.
     * Die letzte Spalte ist der Lösungsvektor.
     *
     * @param m augmentierte Matrix (n x (n+1))
     * @throws IllegalArgumentException falls m keine n x (n+1)-Matrix ist
     */
    public GaussianSolver(double[][] m) {
        if (m == null) {
            throw new IllegalArgumentException("Matrix darf nicht null sein");
        }
        n = m.length;
        if (n == 0) {
            throw new IllegalArgumentException("Matrix darf nicht leer sein");
        }
        // Prüfe, ob jede Zeile exactly n+1 Spalten hat
        for (int i = 0; i < n; i++) {
            if (m[i] == null || m[i].length != n + 1) {
                throw new IllegalArgumentException("Die augmentierte Matrix muss Größe n x (n+1) haben");
            }
        }
        // Kopiere Matrix, um das Original nicht zu verändern
        aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(m[i], 0, aug[i], 0, n + 1);
        }
    }

    /**
     * Liefert die Zeilen-Stufen-Form (Row Echelon Form) der augmentierten Matrix.
     * Das Original bleibt unverändert; zurückgegeben wird eine neue Matrix in Stufenform.
     *
     * @return eine neue double[n][n+1]-Matrix in Zeilen-Stufen-Form
     * @throws ArithmeticException falls die Matrix singulär oder nahe singulär ist
     */
    public double[][] toRowEchelon() {
        // Kopie erstellen
        double[][] mat = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aug[i], 0, mat[i], 0, n + 1);
        }
        // Vorwärtselimination
        forwardElimination(mat);
        return mat;
    }

    /**
     * Löst das Gleichungssystem Ax = b mittels Gauß-Algorithmus mit partieller Pivotisierung.
     * Dabei wird zuerst Vorwärtselfimination durchgeführt, anschließend Rückwärtseinsetzen.
     *
     * @return double[] x mit Länge n, die eindeutige Lösung des Systems
     * @throws ArithmeticException falls die Matrix singulär oder nahe singulär ist
     */
    public double[] solve() {
        // Kopie erstellen
        double[][] mat = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aug[i], 0, mat[i], 0, n + 1);
        }
        // Vorwärtselfimination
        forwardElimination(mat);

        // Rückwärtseinsetzen
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += mat[i][j] * x[j];
            }
            if (Math.abs(mat[i][i]) <= EPSILON) {
                throw new ArithmeticException("Matrix ist singulär oder nahe singulär");
            }
            x[i] = (mat[i][n] - sum) / mat[i][i];
        }
        return x;
    }

    /**
     * Führt Vorwärtselfimination auf der augmentierten Matrix mat durch.
     * Am Ende ist mat in Zeilen-Stufen-Form (Row Echelon Form).
     *
     * @param mat augmentierte Matrix (n x (n+1)), wird in-place umgeformt
     * @throws ArithmeticException falls ein Pivot-Element ≈ 0 ist
     */
    private void forwardElimination(double[][] mat) {
        for (int p = 0; p < n; p++) {
            // Pivot-Zeile suchen (maximales abs. Element in Spalte p ab Zeile p)
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(mat[i][p]) > Math.abs(mat[max][p])) {
                    max = i;
                }
            }
            // Zeilen tauschen
            double[] tempRow = mat[p];
            mat[p] = mat[max];
            mat[max] = tempRow;

            // Pivot-Element prüfen
            if (Math.abs(mat[p][p]) <= EPSILON) {
                throw new ArithmeticException("Matrix ist singulär oder nahe singulär");
            }

            // Elimination für Zeilen unterhalb p
            for (int i = p + 1; i < n; i++) {
                double alpha = mat[i][p] / mat[p][p];
                mat[i][p] = 0.0; // sicherstellen, dass 0 wird
                // restliche Spalten (p+1 ... n) und RHS (Spalte n) anpassen
                for (int j = p + 1; j <= n; j++) {
                    mat[i][j] -= alpha * mat[p][j];
                }
            }
        }
    }
}
