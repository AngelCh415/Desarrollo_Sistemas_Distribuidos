import java.rmi.Naming;

public class ClienteRMI {
    public static void main(String[] args) throws Exception {
        int N = Integer.parseInt(args[0]);
        int M = Integer.parseInt(args[1]);

        float[][] A = new float[N][M];
        float[][] B = new float[M][N];

        // Inicializar las matrices A y B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                A[i][j] = 2*i + 3*j;
            }
        }
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                B[i][j] = 3*i - 2*j;
            }
        }

        // Calcular la transpuesta de la matriz B
        float[][] BT = transponerMatriz(B);

        // Dividir matrices A y B en 9 partes iguales por renglones
        int numPartes = 9;
        int tamParte = N / numPartes;
        float[][][] A_partes = new float[numPartes][tamParte][M];
        float[][][] B_partes = new float[numPartes][tamParte][M];
        for (int k = 0; k < numPartes; k++) {
            int inicio = k * tamParte;
            int fin = (k + 1) * tamParte;
            for (int i = inicio; i < fin; i++) {
                for (int j = 0; j < M; j++) {
                    A_partes[k][i - inicio][j] = A[i][j];
                    B_partes[k][i - inicio][j] = BT[i][j];
                }
            }
        }

        // Conectar con los servidores RMI
        String url0 = "rmi://10.0.0.4/prueba";
        String url1 = "rmi://10.0.0.5/prueba";
        String url2 = "rmi://10.0.0.6/prueba";
        
        InterfaceRMI r0 = (InterfaceRMI) Naming.lookup(url0);
        InterfaceRMI r1 = (InterfaceRMI) Naming.lookup(url1);
        InterfaceRMI r2 = (InterfaceRMI) Naming.lookup(url2);
                
        float[][][] matricesNodo0 = new float[27][tamParte][tamParte];
        float[][][] matricesNodo1 = new float[27][tamParte][tamParte];
        float[][][] matricesNodo2 = new float[27][tamParte][tamParte];

        // Crear threads para invocar el metodo remoto
        Thread thread0 = new Thread(() -> {
            try {
                int r = 0;
                for (int k = 0; k < 3; k++) {
                    for (int j = 0; j < numPartes; j++) {
                        matricesNodo0[r] = r0.multiplica_matrices(A_partes[k], B_partes[j]);
                        r++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thread1 = new Thread(() -> {
            try {
                int r = 0;
                for (int k = 3; k < 6; k++) {
                    for (int j = 0; j < numPartes; j++) {
                        matricesNodo1[r] = r1.multiplica_matrices(A_partes[k], B_partes[j]);
                        r++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                int r = 0;
                for (int k = 6; k < 9; k++) {
                    for (int j = 0; j < numPartes; j++) {
                        matricesNodo2[r] = r2.multiplica_matrices(A_partes[k], B_partes[j]);
                        r++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Ejecutar los threads
        thread0.start();
        thread1.start();
        thread2.start();

        // Esperar a que los threads terminen
        thread0.join();
        thread1.join();
        thread2.join();

        // Obtener matriz C
        float[][] C = juntar_matrices(matricesNodo0, matricesNodo1, matricesNodo2, N);
        
        // Imprimir la matriz C si N = 9 y M = 4
        if(N == 9 && M == 4){
            System.out.println("Imprimiendo la matriz C");
            imprimir_matriz(C);
        }
        
        // Calcular el checksum de la matriz C
        double checksum = calcular_checksum(C);
        System.out.println("El checksum de la matriz C es: " + checksum);
    }
    
    public static float[][] juntar_matrices(float[][][] arreglo1, float[][][] arreglo2, float[][][] arreglo3, int N) {
        int numPartes = 9;
        int tamParte = N/numPartes;
        float[][] matrizFinal = new float[N][N];
        int i_offset = 0;
        int j_offset = 0;
        // Juntar las matrices del primer arreglo
        for (int k = 0; k < numPartes*numPartes/3; k++) {
            float[][] C = arreglo1[k];
            for (int i = 0; i < tamParte; i++) {
                for (int j = 0; j < tamParte; j++) {
                    matrizFinal[i+i_offset][j+j_offset] = C[i][j];
                }
            }
            j_offset += tamParte;
            if (j_offset == N) {
                j_offset = 0;
                i_offset += tamParte;
            }
        }
        // Juntar las matrices del segundo arreglo
        j_offset = 0;
        for (int k = 0; k < numPartes*numPartes/3; k++) {
            float[][] C = arreglo2[k];
            for (int i = 0; i < tamParte; i++) {
                for (int j = 0; j < tamParte; j++) {
                    matrizFinal[i+i_offset][j+j_offset] = C[i][j];
                }
            }
            j_offset += tamParte;
            if (j_offset == N) {
                j_offset = 0;
                i_offset += tamParte;
            }
        }
        // Juntar las matrices del tercer arreglo
        j_offset = 0;
        for (int k = 0; k < numPartes*numPartes/3; k++) {
            float[][] C = arreglo3[k];
            for (int i = 0; i < tamParte; i++) {
                for (int j = 0; j < tamParte; j++) {
                    matrizFinal[i+i_offset][j+j_offset] = C[i][j];
                }
            }
            j_offset += tamParte;
            if (j_offset == N) {
                j_offset = 0;
                i_offset += tamParte;
            }
        }
        return matrizFinal;
    }

    
    public static void imprimir_matriz(float[][] matriz) {
        int filas = matriz.length;
        int columnas = matriz[0].length;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public static double calcular_checksum(float[][] matriz) {
        double checksum = 0.0;
        int filas = matriz.length;
        int columnas = matriz[0].length;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                checksum += matriz[i][j];
            }
        }
        return checksum;
    }

    public static float[][] transponerMatriz(float[][] matriz) {
        int filas = matriz.length;
        int columnas = matriz[0].length;
        float[][] transpuesta = new float[columnas][filas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                transpuesta[j][i] = matriz[i][j];
            }
        }
        return transpuesta;
    }
}