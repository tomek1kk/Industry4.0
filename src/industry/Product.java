package industry;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Product {
    String name;
    List<String> blockedBy; // produkty wymagane do stworzenia produktu
    public Product(String name, ArrayList<String> blockedBy)
    {
        this.name = name;
        this.blockedBy = blockedBy;
    }
}
