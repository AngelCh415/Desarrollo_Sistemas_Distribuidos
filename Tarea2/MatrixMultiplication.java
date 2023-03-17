package Tarea2;

import java.util.Arrays;
import java.util.Scanner;
public class MatrixMultiplication {
public static void main(String[] args) {
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
    
      // Matriz C
    //double[][][][] C = new double[3][3][N/3][N/3];    
      // Multiplicación de matrices
    int part = N / 3;
    /*for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            for (int x = i * part; x < (i + 1) * part; x++) {
               for (int y = j * part; y < (j + 1) * part; y++) {
                for (int z = 0; z < N; z++) {
                     C[i][j][x/3][y/3] += A[x][z] * Bt[y][z];
                }
            }
            }
        }
    }*/
    // Dividir la matriz A en 3 partes iguales
    double[][] A1 = Arrays.copyOfRange(A, 0, N/3);
    double[][] A2 = Arrays.copyOfRange(A, N/3, 2*N/3);
    double[][] A3 = Arrays.copyOfRange(A, 2*N/3, N);
    // Dividir la matriz transpuesta B en 3 partes iguales
    double[][] B1 = Arrays.copyOfRange(Bt, 0, N/3);
    double[][] B2 = Arrays.copyOfRange(Bt, N/3, 2*N/3);
    double[][] B3 = Arrays.copyOfRange(Bt, 2*N/3, N);
    // Matriz C
double[][][][] C = new double[3][3][N/3][N/3];
//Mostrar valores de A1, A2, A3, B1, B2, B3
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

// Multiplicación de matrices
for (int i = 0; i < 3; i++) {
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
                for (int z = 0; z < N; z++) {
                    C[i][j][x][y] += Ai[x][z] * Bj[y][z];
                }
            }
        }
    }
}
//2105136 es checksum para 12
// Mostrar matrices y checksum de C
if (N == 12 || N > 0) {
    System.out.println("Matriz A:");
    printMatrix(A);
    System.out.println("Matriz B:");
    printMatrix(B);
    System.out.println("Matrices C:");
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            System.out.println("C" + (i*3+j+1) + ":");
            printMatrix(C[i][j]);
        }
    }
    System.out.println("Checksum de C: " + getChecksum(C));
    } else if (N == 3000) {
    System.out.println("Checksum de C: " + getChecksum(C));
    } else {
    System.out.println("N no es igual a 12 ni a 3000.");
    }

      // Mostrar matrices y checksum de C
}
   // Función para imprimir una matriz
public static void printMatrix(double[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
        for (int j = 0; j < matrix[0].length; j++) {
            System.out.print(matrix[i][j] + " ");
        }
        System.out.println();
    }
}
      // Función para calcular el checksum de una matriz
    public static double getChecksum(double[][][][] matrix) {
        double checksum = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                double sum = 0;
                double c = 0;
                for (int x = 0; x < matrix[i][j].length; x++) {
                    for (int y = 0; y < matrix[i][j][0].length; y++) {
                        double y1 = matrix[i][j][x][y] - c;
                        double t = sum + y1;
                        c = (t - sum) - y1;
                        sum = t;
                    }
                }
                checksum += sum * (i*3+j+1);
            }
        }
        return checksum;
    }
    
}
