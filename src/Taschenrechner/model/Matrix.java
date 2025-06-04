package Taschenrechner.model;

import java.util.Arrays;

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

    public Matrix rowEchelonForm() {
        double[][] mat1 = this.getData();
        GaussianSolver solver = new GaussianSolver(mat1);
        return new Matrix(this.getRows(), this.getCols(), solver.toRowEchelon());
    }

    public double[] solve() {
        GaussianSolver solver = new GaussianSolver(this.getData());
        return solver.solve();
    }

    public void printMatrix() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%8.2f ", data[i][j]);
            }
            System.out.println();
        }
    }
}

/*
class Main {
    public static void main(String[] args) {
        double[][] data1 = {
                { 0, 1,  1,  4 },
                { 2, 4, -2,  2 },
                { 0, 3, 15, 36 }
        };
        double[][] data2 = {
                {7, 8, 9, 2},
                {10, 11, 12, 4},
                {13, 14, 15, 3}
        };

        Matrix m1 = new Matrix(3, 4, data1);
        Matrix m2 = new Matrix(3, 4, data2);

        Matrix sum = m1.add(m2);
        System.out.println("Summe:");
        sum.printMatrix();

        Matrix diff = m1.sub(m2);
        System.out.println("Differenz:");
        diff.printMatrix();
        System.out.println("Zeilen-Stufen-Form:");
        Matrix rowEchelon = m1.rowEchelonForm();
        rowEchelon.printMatrix();
        System.out.println("Lösung:");
        System.out.println(Arrays.toString(m1.solve()));
        System.out.println("Transponierte Matrix:");
        m1.transpose().printMatrix();
    }
}

 */