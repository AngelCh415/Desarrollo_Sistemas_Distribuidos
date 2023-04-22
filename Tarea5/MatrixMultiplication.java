import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixMultiplication extends Remote {
    float[][] multiplica_Matrices(float[][] a, float[][] b) throws RemoteException;
}
