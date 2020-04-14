package industry;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class MachineAgentArguments {
    Machine machine;
    HashMap<String, List<Pair<String, Machine>>> productMachines;

    public MachineAgentArguments(Machine machine, HashMap<String, List<Pair<String, Machine>>> productMachines) {
        this.machine = machine;
        this.productMachines = productMachines;
    }
}
