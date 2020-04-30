package industry;

import java.util.HashMap;
import java.util.List;

public class MachineAgentArguments {
    Machine machine;
    HashMap<String, List<MachineReference>> productMachines;
    HashMap<String, List<MachineReference>> sameProductMachines;

    public MachineAgentArguments(Machine machine, HashMap<String, List<MachineReference>> subproductMachines,
                                 HashMap<String, List<MachineReference>> sameProductMachines) {
        this.machine = machine;
        this.productMachines = productMachines;
        this.sameProductMachines = sameProductMachines;
    }
}
