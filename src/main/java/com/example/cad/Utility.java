package com.example.cad;

import javafx.scene.control.TextField;

public class Utility {
    public static final String MESSAGE_INPUT_CHECK_FAILED = "Matrix must contain 0, 1 or -1 only!";
    public static final String NOT_VALID_INCIDENCE_MATRIX = "Please Enter A valid Incidence Matrix!";
    public static final String NOT_VALID_CURRENT_SOURCE_MATRIX = "Please Enter A valid Current Source Matrix!";
    public static final String NOT_VALID_VOLTAGE_SOURCE_MATRIX = "Please Enter A valid Voltage Source Matrix!";
    public static final String NOT_VALID_RESISTORS_SOURCE_MATRIX = "Please Enter A valid Resistors Matrix!";
    public static final String VALID = "Valid";

    public static double[][] toIncidenceMatrix(TextField[][] matrixFields) throws Exception {
        int n = matrixFields.length, m = matrixFields[0].length;
        double[][] incidenceMatrix = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                try {
                    incidenceMatrix[i][j] = Integer.parseInt(matrixFields[i][j].getText().trim());
                    if(incidenceMatrix[i][j] != 0 && incidenceMatrix[i][j] != 1 && incidenceMatrix[i][j] != -1)
                        throw new Exception();
                }catch (Exception ex){
                    throw new Exception(MESSAGE_INPUT_CHECK_FAILED);
                }
            }
        }

        for (int column = 0; column < m; column++) {
            int ones = 0, negativeOnes = 0;
            for (int row = 0; row < n; row++) {
                if(incidenceMatrix[row][column] == 1) ones++;
                else if(incidenceMatrix[row][column] == -1) negativeOnes++;
            }
            if(ones > 1 || negativeOnes > 1 || ones + negativeOnes == 0)
                throw new Exception(NOT_VALID_INCIDENCE_MATRIX);
        }

        return incidenceMatrix;
    }

    public static double[][] getNormalizedMatrix(double[][] incidenceMatrix){
        int n = incidenceMatrix.length, m = incidenceMatrix[0].length;

        boolean needExtraRow = false;
        for (int column = 0; column < m; column++) {
            int ones = 0, negativeOnes = 0;
            for (int row = 0; row < n; row++) {
                if(incidenceMatrix[row][column] == 1) ones++;
                else if(incidenceMatrix[row][column] == -1) negativeOnes++;
            }
            needExtraRow |= (ones != negativeOnes);
        }
        if(!needExtraRow) return incidenceMatrix;

        double[][] normalizedMatrix = new double[n + 1][m];
        for (int column = 0; column < m; column++) {
            int ones = 0, negativeOnes = 0;
            for (int row = 0; row < n; row++) {
                normalizedMatrix[row][column] = incidenceMatrix[row][column];
                if(incidenceMatrix[row][column] == 1) ones++;
                else if(incidenceMatrix[row][column] == -1) negativeOnes++;
            }
            normalizedMatrix[n][column] = 0;
            if(ones > negativeOnes) normalizedMatrix[n][column] = -1;
            else if(ones < negativeOnes) normalizedMatrix[n][column] = 1;
        }
        return normalizedMatrix;
    }

    public static double[][] toCurrentSourceMatrix(TextField[] currentSourceInput) throws Exception {
        int n = currentSourceInput.length;
        double[][] currentSourceMatrix = new double[n][1];
        for (int i = 0; i < n; i++) {
            try {
                String cell = currentSourceInput[i].getText().trim();
                if(cell.isEmpty()) cell = "0";
                currentSourceMatrix[i][0] = Double.parseDouble(cell);
            }catch (Exception ex){
                throw new Exception(NOT_VALID_CURRENT_SOURCE_MATRIX);
            }
        }
        return currentSourceMatrix;
    }
    public static double[][] toVoltageSourceMatrix(TextField[] voltageSourceInput) throws Exception {
        int n = voltageSourceInput.length;
        double[][] voltageSourceMatrix = new double[n][1];
        for (int i = 0; i < n; i++) {
            try {
                String cell = voltageSourceInput[i].getText().trim();
                if(cell.isEmpty()) cell = "0";
                voltageSourceMatrix[i][0] = Double.parseDouble(cell);
            }catch (Exception ex){
                throw new Exception(NOT_VALID_VOLTAGE_SOURCE_MATRIX);
            }
        }
        return voltageSourceMatrix;
    }
    public static double[][] toResistorsMatrix(TextField[][] resistorsMatrixInput) throws Exception {
        int n = resistorsMatrixInput.length;
        double[][] resistorsMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                try {
                    String cell = resistorsMatrixInput[i][j].getText().trim();
                    if (cell.isEmpty()) cell = "0";
                    resistorsMatrix[i][j] = Double.parseDouble(cell);
                    if (resistorsMatrix[i][j] < 0)
                        throw new Exception();
                } catch (Exception ex) {
                    throw new Exception(NOT_VALID_RESISTORS_SOURCE_MATRIX);
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(i == j) continue;
                if(resistorsMatrix[i][j] != 0)
                    throw new Exception(NOT_VALID_RESISTORS_SOURCE_MATRIX);
            }
        }
        return resistorsMatrix;
    }

    private static double[][] getTreePartFromA(double[][] incidenceMatrix){
        int nodes = incidenceMatrix.length, branches = incidenceMatrix[0].length;
        int links = branches - nodes + 1;
        int n = nodes, m = branches - links;

        n = Math.min(n, m);
        m = n;

        double[][] treePart = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                treePart[i][j] = incidenceMatrix[i][j];
            }
        }
        System.out.println("Tree part:");
        print(treePart);
        return treePart;
    }
    private static double[][] getLinksPartFromA(double[][] incidenceMatrix, int n){
        int nodes = incidenceMatrix.length, branches = incidenceMatrix[0].length;
        int links = branches - nodes + 1;
        int tree = nodes - 1;
        int m = links;

//        n = Math.min(n, m);
//        m = n;

        double[][] linksPart = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j + tree < branches; j++) {
                linksPart[i][j] = incidenceMatrix[i][j + tree];
            }
        }
        System.out.println("links part");
        print(linksPart);
        return linksPart;
    }

    public static double[][] calculateCMatrixFromA(double[][] incidenceMatrix) throws Exception {
        double[][] at = getTreePartFromA(incidenceMatrix);
        double[][] cl =  multiply(getInverse(at), getLinksPartFromA(incidenceMatrix, at.length));

        System.out.println("CL");
        print(cl);
        int nodes = incidenceMatrix.length, branches = incidenceMatrix[0].length;
        int n = cl.length, tree = branches - cl[0].length;
        double[][] c = new double[n][branches];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = (i == j ? 1 : 0);
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j + tree < branches; j++) {
                c[i][j + tree] = cl[i][j];
            }
        }
        System.out.println("C 11:");
        print(c);
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[0].length; j++) {
                if(Math.abs(c[i][j]) == 0)
                    c[i][j] = 0;
            }
        }
        System.out.println("C 22:");
        print(c);
        return c;
    }

    public static double[][] calculateBMatrixFromC(double[][] incidenceMatrix, double[][] cutSetMatrix){
        int nodes = incidenceMatrix.length, branches = incidenceMatrix[0].length;
        int n = cutSetMatrix.length, links = branches - nodes + 1;
        double[][] cl = new double[cutSetMatrix.length][links];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j + nodes - 1 < branches; j++) {
                cl[i][j] = cutSetMatrix[i][j + nodes - 1];
            }
        }
        double[][] bt = transpose(cl);
        for (int i = 0; i < bt.length; i++) {
            for (int j = 0; j < bt[0].length; j++) {
                bt[i][j] *= -1;
            }
        }
        double[][] b = new double[bt.length][branches];
        for (int i = 0; i < bt.length; i++) {
            for (int j = 0; j < bt[0].length; j++) {
                b[i][j] = bt[i][j];
            }
        }
        for (int i = 0, j = nodes - 1; i < bt.length; i++, j++) {
            b[i][j] = 1;
        }
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                if(Math.abs(b[i][j]) == 0)
                    b[i][j] = 0;
            }
        }
        return b;
    }

    public static double[][] calculateBranchesCurrent(double[][] tieSetMatrix, double[][] resistorsMatrix,
                                                    double[][] voltageSources, double[][] currentSources) throws Exception {

        double[][] LHS = multiply(tieSetMatrix, multiply(resistorsMatrix, transpose(tieSetMatrix)));
        double[][] RHS = subtract(multiply(tieSetMatrix, voltageSources),
                        multiply(tieSetMatrix, multiply(resistorsMatrix, currentSources)));

        return multiply(transpose(tieSetMatrix), multiply(getInverse(LHS), RHS));
    }

    public static double[][] calculateBranchesVoltage(double[][] resistorsMatrix, double[][] currentSources,
                                                      double[][] branchesCurrent, double[][] voltageSources) throws Exception {

        return subtract(multiply(resistorsMatrix, add(currentSources, branchesCurrent)), voltageSources);
    }
    private static void print(double[][] a){
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static double[][] getInverse(double[][] matrix) throws Exception {
        int size = matrix.length;

        double det = calculateDeterminant(matrix);
        if (det == 0) {
            throw new Exception("determinant = 0, This matrix has no Inverse!");
        }
        double[][] bigMat = getAugmentedMatrix(matrix);
        double pivot = 0;
        int counter = 0;
        while (counter < size) {
            pivot = bigMat[counter][counter];
            if(pivot == 0){
                addTwoRows(bigMat, counter);
                pivot = bigMat[counter][counter];
            }
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length * 2; j++) {
                    if (i != counter && j != counter){
                        double d1 = bigMat[counter][counter] * bigMat[i][j];
                        double d2 = bigMat[counter][j] * bigMat[i][counter];
                        double d3 = d1 - d2;
                        bigMat[i][j] = d3 / pivot;
                    }
                }
            }
            for (int i = 0; i < matrix.length * 2; i++) {
                if (i != counter) {
                    bigMat[counter][i] = (bigMat[counter][i] / pivot);
                }
            }
            for (int i = 0; i < matrix.length; i++) {
                if (i != counter) {
                    bigMat[i][counter] = 0;
                } else {
                    bigMat[i][counter] = 1;
                }
            }
            counter++;
        }

        double[][] inverse = new double[size][size];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j + size < matrix.length * 2; j++) {
               inverse[i][j] = bigMat[i][j + size];
            }
        }
        return inverse;
    }

    private static double[][] getAugmentedMatrix(double[][] matrix) {
        double[][] bigMat = new double[matrix.length][matrix.length * 2];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                bigMat[i][j] = matrix[i][j];
            }
        }
        double[][] identityMatrix = new double[matrix.length][matrix.length];
        for (int i = 0; i < identityMatrix.length; i++) {
            for (int j = 0; j < identityMatrix.length; j++) {
                identityMatrix[i][j] = 0;
            }
        }
        for (int i = 0; i < identityMatrix.length; i++) {
            identityMatrix[i][i] = 1;
        }
        for (int i = 0; i < identityMatrix.length; i++) {
            for (int j = matrix.length, k = 0; j < identityMatrix.length * 2; j++, k++) {
                bigMat[i][j] = identityMatrix[i][k];
            }
        }
        return bigMat;
    }

    private static double calculateDeterminant(double[][] matrix) {
        int size = matrix.length;
        double[][] temp = new double[size - 1][size - 1];
        double[] pivot = new double[size - 2];
        int pCounter = 0;
        double pMul = 1.0;
        int negCounter = 0;

        while (size > 2) {
            pivot[pCounter] = matrix[0][0];
            int checkCounter = 1;
            while (pivot[pCounter] == 0) {
                double swap;
                if (checkCounter < matrix.length && matrix[checkCounter][0] != 0) {
                    negCounter++;
                    pivot[pCounter] = matrix[checkCounter][0];
                    for (int i = 0; i < matrix.length; i++) {
                        swap = matrix[0][i];
                        matrix[0][i] = matrix[checkCounter][i];
                        matrix[checkCounter][i] = swap;
                    }
                }
                if (checkCounter == matrix.length) {
                    return 0;
                }
                checkCounter++;
            }
            for (int i = 0; i < temp.length; i++) {
                for (int j = 0; j < temp.length; j++) {
                    temp[i][j] = (pivot[pCounter] * matrix[i + 1][j + 1] - (matrix[0][j + 1] * matrix[i + 1][0]))
                                    / pivot[pCounter];
                }
            }
            size--;
            matrix = new double[size][size];
            for (int i = 0; i < temp.length; i++) {
                for (int j = 0; j < temp.length; j++) {
                    matrix[i][j] = temp[i][j];
                }
            }

            temp = new double[size - 1][size - 1];
            pCounter++;
        }

        for (int i = 0; i < pivot.length; i++) {
            if (negCounter > 0) {
                pMul *= -1 * pivot[i];
                negCounter--;
            } else {
                pMul *= pivot[i];
            }
        }
        double result = (matrix[0][0] * matrix[1][1] - (matrix[0][1] * matrix[1][0]));
        result *= (pMul);
        return result;
    }

    private static void addTwoRows(double[][] a, int index){
        double[] b = new double[a.length * 2];
        double[] c = new double[a.length * 2];
        for (int i = 0; i < b.length; i++) {
            b[i] = a[index][i];
        }
        boolean foundRow = false;
        for (int i = index + 1; !foundRow && i < a.length; i++) {
            if(a[i][index] != 0){
                for (int j = 0; j < c.length; j++) {
                    c[j] = a[i][j];
                }
                foundRow = true;
            }
        }
        if(foundRow){
            for (int i = 0; i < c.length; i++) {
                a[index][i] = c[i] + b[i];
            }
        }
    }
    
    private static double[][] multiply(double[][] a, double[][] b) {
        int n = a.length, m = b[0].length;
        double[][] result = new double[n][m];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < m; col++) {
                for (int i = 0; i < a[0].length; i++) {
                    result[row][col] += a[row][i] * b[i][col];
                }
            }
        }
        return result;
    }

    private static double[][] subtract(double[][] a, double[][] b) throws Exception {
        if(a.length != b.length || a[0].length != b[0].length)
            throw new Exception("Matrix A and B must have same size n*m");
        int n = a.length, m = a[0].length;
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }
    private static double[][] add(double[][] a, double[][] b) throws Exception {
        if(a.length != b.length || a[0].length != b[0].length)
            throw new Exception("Matrix A and B must have same size n*m");
        int n = a.length, m = a[0].length;
        double[][] result = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    private static double[][] transpose(double[][] a){
        int n = a.length, m = a[0].length;
        double[][] result = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = a[j][i];
            }
        }
        return result;
    }
}
