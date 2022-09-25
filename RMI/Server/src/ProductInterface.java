import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProductInterface extends Remote {
    public String getname() throws RemoteException;
    public String getDescription() throws RemoteException;
    public double getprice() throws RemoteException;
}
