import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        //get the ref of rmi registry

        //locate the registry
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1",9100);
            //ref of object
            ProductInterface pr1 = (ProductInterface) reg.lookup("j");
            ProductInterface pr2 = (ProductInterface) reg.lookup("m");
            ProductInterface pr3 = (ProductInterface) reg.lookup("c");
            ProductInterface pr4 = (ProductInterface) reg.lookup("l");
            //now invoke the method
            System.out.println("the name of pr1="+pr1.getname());
            System.out.println("the name of pr2="+pr2.getname());
            System.out.println("the name of pr3="+pr3.getname());
            System.out.println("the name of pr4="+pr4.getname());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
