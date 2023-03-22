import java.util.Arrays;
import java.util.Scanner;
public class MatrixMultiplication {
public static void main(String[] args) {
    String validar = args[0];
    System.out.println("Validar: " + validar);
    if(validar.equals("0")){
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
        // Matriz C
        double[][][][] C = new double[3][3][N/3][N/3];
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
            //Multiplicar Ai y Bj con la funcion multi
            /*for (int x = 0; x < N/3; x++) {
                for (int y = 0; y < N/3; y++) {
                    C[i][j][x][y] = multi(Ai, Bj);
                }
            }*/
            for (int x = 0; x < N/3; x++) {
                for (int y = 0; y < N/3; y++) {
                    C[i][j][x][y] = 0.0;
                    for (int k = 0; k < N; k++) {
                        C[i][j][x][y] += Ai[x][k] * Bj[y][k];
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
    }
    else if(validar.equals("1")){
        
    }
    else if(validar.equals("2")){

    }else{

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
    
    public static double multi(double[][] A, double[][]B){
        double C = 0.0;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                C += A[i][j] * B[i][j];
            }
        }
        return C;
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
