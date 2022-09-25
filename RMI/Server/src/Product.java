import java.rmi.RemoteException;

public class Product  implements ProductInterface{
    String name;
    String description;
    double price;

    public Product(String name, String description, double price) throws RemoteException{
        this.name = name;
        this.description = description;
        this.price = price;
    }
    @Override
    public String getname() throws RemoteException {
        return this.name;
    }

    @Override
    public String getDescription() throws RemoteException {
        return this.description;
    }

    @Override
    public double getprice() throws RemoteException {
        return this.price;
    }
}
