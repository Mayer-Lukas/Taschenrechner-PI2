package Taschenrechner.model;

/**
 * Modell für Matrizen im Taschenrechner.
 */
public class Matrix {
    private final double[][] data;
    private final int rows;
    private final int cols;

    public Matrix(int rows, int cols, double[][] data) {
        this.rows = rows;
        this.cols = cols;
        this.data = data;
    }

    public double get(int row, int col) {
        return data[row][col];
    }

    public double[][] getData() {
        return data;
    }

    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    // Weitere Methoden wie Addition

    /**
     * Addiert zwei Matrizen.
     * @param matrix2 Die zweite Matrix, die addiert werden soll.
     * @return Die resultierende Matrix nach der Addition.
     */
    public Matrix add(Matrix matrix2) {
        if(this.getRows() != matrix2.getRows() || this.getCols() != matrix2.getCols()) {
            throw new IllegalArgumentException("Die Matrizen sind nicht kompatibel für die Addition.");
        }
        double[][] mat1 = this.getData();
        double[][] mat2 = matrix2.getData();
        double[][] d = new double[this.getRows()][this.getCols()];
        Matrix result = new Matrix(this.getRows(), this.getCols(), d);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(i, j, mat1[i][j] + mat2[i][j]);
            }
        }
        return result;
    }

    /**
     * Subtrahiert zwei Matrizen.
     * @param matrix2 Die zweite Matrix, die subtrahiert werden soll.
     * @return Die resultierende Matrix nach der Subtraktion.
     */
    public Matrix sub(Matrix matrix2) {
        if(this.getRows() != matrix2.getRows() || this.getCols() != matrix2.getCols()) {
            throw new IllegalArgumentException("Die Matrizen sind nicht kompatibel für die Addition.");
        }
        double[][] mat1 = this.getData();
        double[][] mat2 = matrix2.getData();
        double[][] d = new double[this.getRows()][this.getCols()];
        Matrix result = new Matrix(this.getRows(), this.getCols(), d);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(i, j, mat1[i][j] - mat2[i][j]);
            }
        }
        return result;
    }

    /**
     * Multipliziert zwei Matrizen.
     * @param m2 Die zweite Matrix, die multipliziert werden soll.
     * @return Die resultierende Matrix nach der Multiplikation.
     */
    public Matrix mult(Matrix m2) {
        if(this.getRows() != m2.getCols()){
            throw new IllegalArgumentException("Die Matrizen sind nicht kompatibel für die Multiplikation.");
        }
        double[][] mat1 = this.getData();
        double[][] mat2 = m2.getData();
        double[][] d = new double[this.getRows()][m2.getCols()];
        Matrix result = new Matrix(this.getRows(), m2.getCols(), d);
        for(int i=0; i<this.getRows(); i++){
            for(int j=0; j<m2.getCols(); j++){
                double sum = 0;
                for(int k=0; k<this.getCols(); k++){
                    sum += mat1[i][k] * mat2[k][j];
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }

    /**
     * Transponiert die Matrix.
     * @return Die transponierte Matrix.
     */
    public Matrix transpose() {
        // Für die transponierte Matrix brauchen wir cols Zeilen und rows Spalten
        double[][] d = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                d[j][i] = this.get(i, j);
            }
        }
        return new Matrix(cols, rows, d);
    }

    /**
     * Wandelt die Matrix in Zeilen-Stufen-Form (Row Echelon Form) mithilfe des GaussianSolver um.
     * @return Die Matrix in Zeilen-Stufen-Form.
     */
    public Matrix rowEchelonForm() {
        double[][] mat1 = this.getData();
        GaussianSolver solver = new GaussianSolver(mat1);
        return new Matrix(this.getRows(), this.getCols(), solver.toRowEchelon());
    }

    /**
     * Löst das Gleichungssystem Ax = b, wobei A die Matrix ist und b der rechte Vektor.
     * Dies geschieht mittels des Gauß-Algorithmus mit partieller Pivotisierung im GaussianSolver.
     * @return Der Lösungsvektor x.
     * @throws ArithmeticException falls die Matrix singulär oder nahe singulär ist
     */
    public double[] solve() {
        GaussianSolver solver = new GaussianSolver(this.getData());
        return solver.solve();
    }

    /**
     * Gibt die Matrix in einem lesbaren Format aus (Diese war nur für Testzwecke, sie wird nicht für Funktionalitäten benötigt).
     */
    public void printMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%8.2f ", data[i][j]);
            }
            System.out.println();
        }
    }
}

