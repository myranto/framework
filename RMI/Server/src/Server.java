import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) {
        try {


            //set hostname
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            System.out.println("server was started..");
            Product p1 = new Product("Laptop", "description", 1212);
            Product p2 = new Product("MObile", "Mi", 202);
            Product p3 = new Product("Power charger", "lenovo", 240.1);
            Product p4 = new Product("MotorBike", "yamaha", 1212);
            ProductInterface stub1 =(ProductInterface) UnicastRemoteObject.exportObject(p1, 0);
            ProductInterface stub2 =(ProductInterface) UnicastRemoteObject.exportObject(p2, 0);
            ProductInterface stub3 =(ProductInterface) UnicastRemoteObject.exportObject(p3, 0);
            ProductInterface stub4 =(ProductInterface) UnicastRemoteObject.exportObject(p4, 0);
            //Register the exported class in rmi registry with some name

            //get the registry
            Registry reg = LocateRegistry.getRegistry("127.0.0.1",9100);

            //
            reg.rebind("j",stub1);
            reg.rebind("m",stub2);
            reg.rebind("c",stub3);
            reg.rebind("l",stub4);

        } catch (Exception e) {
            System.out.println("error occured");
            e.printStackTrace();
        }
    }
}
