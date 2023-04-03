import java.util.Arrays;
import java.util.Scanner;

public class MatrixMultiplication {
    public static void main(String[] args) {
        String validar = "0";
        System.out.println("Validar: " + validar);
        if (validar.equals("0")) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Ingrese el valor de N: ");
            int N = sc.nextInt();
            // Matriz A
            double[][] A = new double[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = 2 * i + j;
                }
            }

            // Matriz B
            double[][] B = new double[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    B[i][j] = 3 * i - j;
                }
            }
            // Transpuesta de B
            double[][] Bt = new double[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    Bt[i][j] = B[j][i];
                }
            }
            int part = N / 3;
            double[][] A1 = Arrays.copyOfRange(A, 0, N/3);
            double[][] A2 = Arrays.copyOfRange(A, N/3, 2*N/3);
            double[][] A3 = Arrays.copyOfRange(A, 2*N/3, N);
            double[][] B1 = Arrays.copyOfRange(Bt, 0, N/3);
            double[][] B2 = Arrays.copyOfRange(Bt, N/3, 2*N/3);
            double[][] B3 = Arrays.copyOfRange(Bt, 2*N/3, N);
            //Mostrar A1, A2, A3, B1, B2 y B3
            System.out.println("Matriz A1:");
            printMatrix(A1);
            System.out.println("Matriz A2:");
            printMatrix(A2);
            System.out.println("Matriz A3:");
            printMatrix(A3);
            System.out.println("Matriz B1:");
            printMatrix(B1);
            System.out.println("Matriz B2:");
            printMatrix(B2);
            System.out.println("Matriz B3:");
            printMatrix(B3);

            // Matriz C
            double[][] C = new double[N][N];
            // Multiplicación de matrices
            /*for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    double[][] Ai = null;
                    double[][] Bj = null;
                    if (i == 0) {
                        Ai = A1;
                    } else if (i == 1) {
                        Ai = A2;
                    } else if (i == 2) {
                        Ai = A3;
                    }
                    if (j == 0) {
                        Bj = B1;
                    } else if (j == 1) {
                        Bj = B2;
                    } else if (j == 2) {
                        Bj = B3;
                    }
                    for (int x = 0; x < N/3; x++) {
                        for (int y = 0; y < N/3; y++) {
                            for (int k = 0; k < N; k++) {
                                C[x + (i * N/3)][y + (j * N/3)] += Ai[x][k] * Bj[y][k];
                            }
                        }
                    }
                }
            }*/

                double[][] SC1 = multiplyA1B123(A1, B1, B2, B3);
                double[][] SC2 = multiplyA2B123(A2, B1, B2, B3);
                double[][] SC3 = multiplyA3B123(A3, B1, B2, B3);
                //Imprimir SC1, SC2 y SC3
                System.out.println("Matriz SC1:");
                printMatrix(SC1);
                System.out.println("Matriz SC2:");
                printMatrix(SC2);
                System.out.println("Matriz SC3:");
                printMatrix(SC3);

           //375708 es checksum para 12
    // Mostrar matrices y checksum de C
      /*    if (N == 12 || N > 0) {
            System.out.println("Matriz A:");
            printMatrix(A);
            System.out.println("Matriz B:");
            printMatrix(B);
            System.out.println("Matrices C:");
            printMatrix(C);
            System.out.println("Checksum de C: " + getChecksum(C));
        } else if (N == 3000) {
            System.out.println("Checksum de C: " + getChecksum(C));
        } else {
            System.out.println("N no es igual a 12 ni a 3000.");
        }*/
        } else if (validar.equals("1")) {
            
        }
    }
    public static void printMatrix(double[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[0].length; j++) {
            System.out.print(matrix[i][j] + " ");
        }
        System.out.println();
    }
}


public static double[][] multiplyA1B123(double[][] A1, double[][] B1, double[][] B2, double[][] B3) {
    int N = A1[0].length;
    double[][] C = new double[N / 3][N];
        for (int j = 0; j < 3; j++) {
            double[][] Ai = null;
            double[][] Bj = null;
            Ai = A1;
            if (j == 0) {
                Bj = B1;
            } else if (j == 1) {
                Bj = B2;
            } else if (j == 2) {
                Bj = B3;
            }
            for (int x = 0; x < N / 3; x++) {
                for (int y = 0; y < N / 3; y++) {
                    for (int k = 0; k < N; k++) {
                        C[y][j] += Ai[x][k] * Bj[y][k];
                    }
                }
            }
        }
    return C;
}

public static double[][] multiplyA2B123(double[][] A2, double[][] B1, double[][] B2, double[][] B3) {
    int N = A2[0].length;
    double[][] C = new double[N / 3][N];
        for (int j = 0; j < 3; j++) {
            double[][] Ai = null;
            double[][] Bj = null;
            Ai = A2;
            if (j == 0) {
                Bj = B1;
            } else if (j == 1) {
                Bj = B2;
            } else if (j == 2) {
                Bj = B3;
            }
            for (int x = 0; x < N / 3; x++) {
                for (int y = 0; y < N / 3; y++) {
                    for (int k = 0; k < N; k++) {
                        C[y][j] += Ai[x][k] * Bj[y][k];
                    }
                }
            }
        
    }
    return C;
}

public static double[][] multiplyA3B123(double[][] A3, double[][] B1, double[][] B2, double[][] B3) {
    int N = A3[0].length;
    double[][] C = new double[N / 3][N];
        for (int j = 0; j < 3; j++) {
            double[][] Ai = null;
            double[][] Bj = null;
                Ai = A3;
            if (j == 0) {
                Bj = B1;
            } else if (j == 1) {
                Bj = B2;
            } else if (j == 2) {
                Bj = B3;
            }
            for (int x = 0; x < N / 3; x++) {
                for (int y = 0; y < N / 3; y++) {
                    for (int k = 0; k < N; k++) {
                        C[y][j] += Ai[x][k] * Bj[y][k];
                    }
                }
            }
    }
    return C;
}
    public static double getChecksum(double[][] matrix) {
        double checksum = 0.0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                checksum += matrix[i][j];
            }
        }
        return checksum;
    }
    
}
