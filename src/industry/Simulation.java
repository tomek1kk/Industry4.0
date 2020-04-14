package industry;

import jade.core.behaviours.SimpleBehaviour;

import java.util.ArrayList;

public class Simulation {
    int duration;
    ArrayList<DemandedProduct> demandedProducts;
    public Simulation(int duration, ArrayList<DemandedProduct> prods)
    {
        this.duration = duration;
        demandedProducts = prods;
    }
}

class DemandedProduct
{
    public String name;
    public int amount;
    public int priority;
    public DemandedProduct(String name, int amount, int priority)
    {
        this.name = name;
        this.amount = amount;
        this.priority = priority;
    }
};
