import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceRMI extends Remote {
    float[][] multiplica_matrices(float[][] A, float[][] B) throws RemoteException;
}