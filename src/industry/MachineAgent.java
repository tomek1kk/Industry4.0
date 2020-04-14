package industry;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class MachineAgent extends Agent {
    Machine machine;
    HashMap<String, List<Pair<String, Machine>>> productMachines;

    @Override
    protected void setup() {
        MachineAgentArguments args = (MachineAgentArguments)(getArguments()[0]);
        machine = args.machine;
        productMachines = args.productMachines;
        Behaviour initMachine = new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getLocalName() + " started");
                productMachines.forEach((product, machines)-> {
                    System.out.println("Product: " + product + ". Available machines:");
                    machines.forEach(m -> {
                        System.out.println("Machine agent name: " + m.getKey());
                    });
                });
            }
        };
        addBehaviour(initMachine);
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }
}
