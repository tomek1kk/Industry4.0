package industry;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BreakDownAgent extends Agent {

    InformationCenter ic;
    String productName;
    int stageId;
    int replacement = Integer.MIN_VALUE;

    @Override
    protected void setup() {
        BreakdownArgs args = (BreakdownArgs) (getArguments()[0]);
        productName = args.productName;
        stageId = args.stageId;
        addBehaviour(FindReplacement);
        addBehaviour(ReassignProduction);
    }

    OneShotBehaviour FindReplacement = new OneShotBehaviour() {
        @Override
        public void action() {
            int bestTime = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Machine> entry : ic.machines.entrySet()) {
                Integer key = entry.getKey();
                Machine value = entry.getValue();

                List<MachineAction> result = value.actions.stream()
                        .filter(item -> item.productName.equals(productName) && item.stageId == stageId)
                        .collect(Collectors.toList());
                if(result == null) //no other machines doing the same work
                    continue;

                if(result.get(0).productionTime < bestTime)
                {
                    bestTime = result.get(0).productionTime;
                    replacement = key;
                }
            }
        }
    };

    OneShotBehaviour ReassignProduction = new OneShotBehaviour() {
        @Override
        public void action() {
            if(replacement!= Integer.MIN_VALUE)
            {
            }
        }
    };


    @Override
    protected void takeDown() {
        super.takeDown();
    }
}
