package industry;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.HashMap;
import java.util.List;

public class MachineAgent extends Agent {
    Machine machine;
    HashMap<String, List<MachineReference>> subproductMachines;
    HashMap<String, List<MachineReference>> sameProductMachines;

    @Override
    protected void setup() {
        MachineAgentArguments args = (MachineAgentArguments)(getArguments()[0]);
        machine = args.machine;
        subproductMachines = args.subproductMachines;
        sameProductMachines = args.sameProductMachines;

        Behaviour initMachine = new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getLocalName() + " started");
                System.out.println("My actions:");
                machine.actions.forEach((name, action)-> {
                    System.out.println("Product: " + name + ", action: " + action.actionName);
                });
                subproductMachines.forEach((product, machines)-> {
                    machines.forEach(m -> {
                        System.out.println("I'm " + getAID().getLocalName() + " and I produce " + product + " Machine that produce subproducts: " + m.name);
                    });
                });
                sameProductMachines.forEach((product, machines) -> {
                    machines.forEach(m -> {
                        System.out.println("I'm " + getAID().getLocalName() + " and I produce " + product + " Machine that also produces this: " + m.name);
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
