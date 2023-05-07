import java.rmi.Naming;


public class MatrixMultiplicationServer{

    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost:1099/ServicioMultiplicacionMatricesImpl";
        ServicioMultiplicacionMatricesImpl obj = new ServicioMultiplicacionMatricesImpl();
        Naming.rebind(url, obj);
    }
}
