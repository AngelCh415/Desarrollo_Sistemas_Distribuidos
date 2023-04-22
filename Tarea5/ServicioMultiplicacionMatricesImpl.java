import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.zip.CRC32;


public class ServicioMultiplicacionMatricesImpl extends UnicastRemoteObject implements MatrixMultiplication{
    
    private static final int N = 9;
    private static final int M = 4;
    
    public ServicioMultiplicacionMatricesImpl() throws java.rmi.RemoteException {
        super();
    }
    public float[][] multiplica_Matrices(float[][] A, float[][] B) throws java.rmi.RemoteException {
        int n = A.length;
        int m = B.length;
        int l = A[0].length;
        
        if (l != m) {
            throw new RemoteException("El número de columnas de A debe ser igual al número de filas de B.");
        }
        
        float[][] C = new float[n][m];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < l; k++) {
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        
        // Calcular el checksum de la matriz C
        long checksum = calcularChecksum(C);
        
        System.out.println("Matriz C: ");
        mostrarMatriz(C);
        System.out.println("Checksum de la matriz C: " + checksum);
        System.out.println();
        
        return C;
    }
    
    private void mostrarMatriz(float[][] matriz) {
        int n = matriz.length;
        int m = matriz[0].length;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    private long calcularChecksum(float[][] matriz) {
        CRC32 crc = new CRC32();
        
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                crc.update(Float.floatToIntBits(matriz[i][j]));
            }
        }
        
        return crc.getValue();
    }
}
