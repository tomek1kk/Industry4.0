package industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Machine {
    int id;
    HashMap<String,Integer> products;
    public Machine(int id, HashMap<String,Integer> products)
    {
        this.id = id;
        this.products = products;
    }
}
