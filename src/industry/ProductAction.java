package industry;

import java.util.List;

public class ProductAction {
    public String actionName;
    public List<String> subproducts;
    public ProductAction(String actionName, List<String> subproducts){
        this.actionName = actionName;
        this.subproducts = subproducts;
    }
}
