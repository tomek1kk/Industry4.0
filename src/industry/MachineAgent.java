package industry;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.HashMap;
import java.util.List;

public class MachineAgent extends Agent {
    Machine machine;
    HashMap<String, List<MachineReference>> productMachines;

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
                    machines.forEach(m -> {
                        System.out.println("I'm " + getAID().getLocalName() + " and I produce " + product + " Machine that can help me: " + m.name);
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
