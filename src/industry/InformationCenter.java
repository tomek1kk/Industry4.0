package industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InformationCenter {
    public HashMap<Integer, Machine> machines;
    public HashMap<String, Product> products;
    public ArrayList<Simulation> simulations;

    public InformationCenter()
    {
        machines = new HashMap<Integer, Machine>();
        products = new HashMap<String, Product>();
        simulations = new ArrayList<Simulation>();
    }
    public void addMachine(Machine m)
    {
        machines.put(m.id, m);
        System.out.println("Machine added");
    }
    public void addProduct(Product p)
    {
        products.put(p.name, p);
        System.out.println("Product added");
    }
    public void addSimulation(Simulation s)
    {
        simulations.add(s);
        System.out.println("Simulation added");
    }
}
