import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClaseRMI extends UnicastRemoteObject implements InterfaceRMI {
    public ClaseRMI() throws RemoteException {
        super();
    }

    public float[][] multiplica_matrices(float[][] A, float[][] B) throws RemoteException {
        float[][] C = new float[A.length][A.length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                for (int k = 0; k < A[i].length; k++) {
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }

        return C;
    }
}