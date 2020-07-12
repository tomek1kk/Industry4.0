package industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InformationCenter {
    public HashMap<Integer, Machine> machines;
    public HashMap<String, Product> products;
    public ArrayList<Simulation> simulations;
    public int socketDelay;
    public ArrayList<Breakdown> breakdowns;
    public GUIFrame guiFrame;

    private static InformationCenter instance;

    public static InformationCenter getInstance() {
        if (instance == null)
            instance = new InformationCenter();
        return instance;
    }

    public InformationCenter()
    {
        machines = new HashMap<Integer, Machine>();
        products = new HashMap<String, Product>();
        simulations = new ArrayList<Simulation>();
        breakdowns = new ArrayList<Breakdown>();
    }
    public void addMachine(Machine m)
    {
        machines.put(m.machineId, m);
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
    public void addSocketDelay(int d)
    {
        socketDelay = d;
        System.out.println("Socket delay added");
    }
    public void addBreakdown(Breakdown br)
    {
        breakdowns.add(br);
        machines.get(br.MachineId).breakdown = br;
        System.out.println("Breakdown added");
    }
    public void addGUIFrame(GUIFrame gui)
    {
        guiFrame = gui;
    }
}
