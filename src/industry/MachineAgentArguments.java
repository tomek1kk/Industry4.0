package industry;

import java.util.HashMap;
import java.util.List;

public class MachineAgentArguments {
    public Machine machine;
    public HashMap<String, List<MachineReference>> subproductMachines;
    public HashMap<String, List<MachineReference>> sameProductMachines;
    public HashMap<String, Product> productsDefinitions;

    public MachineAgentArguments(
            Machine machine, HashMap<String,
            List<MachineReference>> subproductMachines,
            HashMap<String, List<MachineReference>> sameProductMachines,
            HashMap<String, Product> productsDefinitions
                                 ) {
        this.machine = machine;
        this.subproductMachines = subproductMachines;
        this.sameProductMachines = sameProductMachines;
        this.productsDefinitions = productsDefinitions;
    }
}
