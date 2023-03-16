package Tarea2;

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
    double[][][][] C = new double[3][3][N/3][N/3];    
      // Multiplicación de matrices
    int part = N / 3;
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            for (int x = i * part; x < (i + 1) * part; x++) {
               for (int y = j * part; y < (j + 1) * part; y++) {
                for (int z = 0; z < N; z++) {
                     C[i][j][x/3][y/3] += A[x][z] * Bt[y][z];
                }
            }
            }
        }
    }
      // Mostrar matrices y checksum de C
    if (N == 12 ) {
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

