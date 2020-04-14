package industry;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.List;

public class MachineAgent extends Agent {
    Machine machine;
    List<Machine> productMachines;

    @Override
    protected void setup() {
        Behaviour initMachine = new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getLocalName() + " started");
            }
        };
        addBehaviour(initMachine);
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }
}
