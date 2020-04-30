package industry;

import java.util.HashMap;
import java.util.List;

public class MachineAgentArguments {
    Machine machine;
    HashMap<String, List<MachineReference>> productMachines;

    public MachineAgentArguments(Machine machine, HashMap<String, List<MachineReference>> productMachines) {
        this.machine = machine;
        this.productMachines = productMachines;
    }
}
