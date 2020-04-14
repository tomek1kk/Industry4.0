package industry;

import java.util.HashMap;
import java.util.Map;

public class InformationCenter {
    static HashMap<Integer, Machine> machines;
    static HashMap<String, Product> products;

    public InformationCenter()
    {
        machines = new HashMap<Integer, Machine>();
        products = new HashMap<String, Product>();
    }
    public void addMachine(Machine m)
    {
        machines.put(m.id, m);
    }
    public void addProduct(Product p)
    {
        products.put(p.name, p);
    }
}
