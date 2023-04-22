import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class ClienteRMI {
    
    private static final int N = 9;
    private static final int M = 4;
    private static final int PARTES = 81;
    private static final String RMI_URL = "rmi://localhost/";
    private static final String RMI_NOMBRE_SERVICIO = "ServicioMultiplicacionMatricesImpl";
    private static final String RMI_NOMBRE_OBJETO = "objetoMultiplicacionMatrices";
    
    public static void main(String[] args) {
        
        try {
            // Inicializar A y B
            float[][] A = new float[N][M];
            float[][] B = new float[M][N];
            
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    A[i][j] = 2 * i + 3 * j;
                }
            }
            
            for (int i = 0; i < M; i++) {
                for (int j = 0; j < N; j++) {
                    B[i][j] = 3 * i - 2 * j;
                }
            }
            
            // Obtener Bt
            float[][] Bt = new float[N][M];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    Bt[i][j] = B[j][i];
                }
            }
            
            // Obtener las matrices Ai y Bti
            List<float[][]> AiList = partirMatriz(A);
            List<float[][]> BtiList = partirMatriz(Bt);
            // Declarar arreglo para almacenar checksums de cada parte
        long[] checksums = new long[PARTES];
            // Inicializar el arreglo de objetos remotos
            ServicioMultiplicacionMatricesImpl[] objetosRemotos = new ServicioMultiplicacionMatricesImpl[PARTES];
            
            // Registrar el objeto remoto en los servidores RMI
            Registry registry0 = LocateRegistry.getRegistry("localhost", 1099);
            Registry registry1 = LocateRegistry.getRegistry("localhost", 1100);
            Registry registry2 = LocateRegistry.getRegistry("localhost", 1101);
            
            for (int i = 0; i < PARTES; i++) {
                int nodo = i % 3;
                switch (nodo) {
                    case 0:
                        objetosRemotos[i] = (ServicioMultiplicacionMatricesImpl) registry0.lookup(RMI_URL + RMI_NOMBRE_SERVICIO);
                        break;
                    case 1:
                        objetosRemotos[i] = (ServicioMultiplicacionMatricesImpl) registry1.lookup(RMI_URL + RMI_NOMBRE_SERVICIO);
                        break;
                    case 2:
                        objetosRemotos[i] = (ServicioMultiplicacionMatricesImpl) registry2.lookup(RMI_URL + RMI_NOMBRE_SERVICIO);
                        break;
                }
            }
            // Multiplicar las matrices Ai y Bti en paralelo utilizando threads
                List<Thread> threads = new ArrayList<>();
                float [][] C = new float [N][M];
                for (int i = 0; i < PARTES; i++) {
                    int finalI = i;
                    Thread thread = new Thread(() -> {
                        try {
                            float[][] result = objetosRemotos[finalI].multiplica_Matrices(AiList.get(finalI % 9), BtiList.get(finalI / 9));
                            checksums[finalI] = calcularChecksum(result);
                            // Agregar resultado de la multiplicación a la matriz C
                            int filaInicial = finalI % 9 * M;
                            int columnaInicial = finalI / 9 * M;
                            for (int fila = 0; fila < M; fila++) {
                                for (int columna = 0; columna < M; columna++) {
                                    C[filaInicial + fila][columnaInicial + columna] = result[fila][columna];
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                    threads.add(thread);
                    thread.start();
                }
                // Esperar a que terminen todos los threads
                for (Thread thread : threads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int fila = 0; fila < N; fila++) {
                    for (int columna = 0; columna < M; columna++) {
                        System.out.print(C[fila][columna] + " ");
                    }
                    System.out.println();
                }
                // Calcular el checksum global
                float checksumGlobal = calcularChecksumGlobal(checksums);
                System.out.println("Checksum global: " + checksumGlobal);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public static List<float[][]> partirMatriz(float[][] matriz) {
            List<float[][]> submatrices = new ArrayList<>();
        
            int filaSubmatriz = 0;
            int columnaSubmatriz = 0;
        
            while (filaSubmatriz < matriz.length) {
                float[][] submatriz = new float[1][1];
                submatriz[0][0] = matriz[filaSubmatriz][columnaSubmatriz];
                submatrices.add(submatriz);
        
                columnaSubmatriz++;
                if (columnaSubmatriz == matriz[filaSubmatriz].length) {
                    columnaSubmatriz = 0;
                    filaSubmatriz++;
                }
            }
        
            return submatrices;
        }
        // Método para calcular checksum de una matriz
private static long calcularChecksum(float[][] matriz) {
    CRC32 crc = new CRC32();
    for (int i = 0; i < matriz.length; i++) {
        for (int j = 0; j < matriz[i].length; j++) {
            crc.update(Float.floatToIntBits(matriz[i][j]));
        }
    }
    return crc.getValue();
}

// Método para calcular checksum global
private static long calcularChecksumGlobal(long[] checksums) {
    CRC32 crc = new CRC32();
    for (long checksum : checksums) {
        crc.update((int) checksum);
    }
    return crc.getValue();
}
    }
        