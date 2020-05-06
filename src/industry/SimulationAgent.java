package industry;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.*;

public class SimulationAgent extends Agent {
    Gson parser = new Gson();
    InformationCenter ic;
    Integer step = 0;
    Integer lastGenerated = -1;
    final Integer sizeOfGenerator = 99999;
    BitSet generatorSet = new BitSet(sizeOfGenerator);    //all initially false

    private Behaviour Cfp1Requester (ACLMessage messageRequest, Vector<ACLMessage> messageRequests){
        return new ContractNetInitiator(this, messageRequest) {
            @Override
            protected java.util.Vector prepareCfps(ACLMessage cfp){
                return messageRequests;
            }
            @Override
            protected void handleAllResponses(Vector responses, Vector acceptances) {
                Enumeration e = responses.elements();
                ACLMessage bestProposeReply = null;
                Integer metric = Integer.MAX_VALUE;
                while (e.hasMoreElements()) {
                    ACLMessage msg = (ACLMessage) e.nextElement();
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        acceptances.addElement(reply);
                        Cfp2 proposeMessage = parser.fromJson(msg.getContent(), Cfp2.class);
                        if(CalculateMetric(proposeMessage) < metric){
                            bestProposeReply = reply;
                            metric = CalculateMetric(proposeMessage);
                        }
                    }
                }
                if (acceptances.size() > 0) {
                    bestProposeReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    System.out.println("No machine that make this product");
                }
            }
        };
    }

    private Integer CalculateMetric(Cfp2 proposeMessage){
        return proposeMessage.getAmountInQueue() * proposeMessage.getProductionTime(); //prosta heurystyka
    }

    private String GenerateProductId(){
        for(int i = 1; i < sizeOfGenerator; i++){
            if(generatorSet.get((lastGenerated + i) % sizeOfGenerator) == false){
                lastGenerated += i;
                generatorSet.set(lastGenerated);
                String res = Integer.toString(lastGenerated);
                while(res.length() < 5){
                    res = "0" + res;
                }
                return "00" + res;
            }
        }
        return "noFreeIdError";
    }

    protected void setup() {
        ic = (InformationCenter) (getArguments()[0]);

        Behaviour RequestsBehaviour = new TickerBehaviour(this, 1) {
            @Override
            protected void onTick() {
                Simulation currentSimulation = ic.simulations.get(step);
                Vector<ACLMessage> messageRequests = new Vector<ACLMessage>();
                currentSimulation.demandedProducts.forEach(product -> {
                    Integer lastStageId = Collections.max(ic.products.get(product.name).stages.keySet());
                    ic.products.get(product.name).stages.get(lastStageId).forEach(action ->{
                        Cfp1 requestContent = new Cfp1(product.name, action.actionName, lastStageId, product.priority,
                                GenerateProductId(), new HashMap<Integer, List<String>>());
                        ic.machines.values().forEach(machine -> {
                            machine.actions.forEach(machineAction -> {
                                if(machineAction.productName.equals(product.name)
                                        && machineAction.stageId == lastStageId
                                        && machineAction.actionName.equals(action.actionName)){
                                    ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                                    msg.addReceiver(new AID("Machine" + Integer.toString(machine.machineId), AID.ISLOCALNAME));
                                    msg.setContent(parser.toJson(requestContent));
                                    messageRequests.add(msg);
                                }
                            });
                        });
                    });
                    for(int i = 0; i < product.amount; i++){
                        addBehaviour(Cfp1Requester(messageRequests.get(0), messageRequests));
                    }
                    messageRequests.clear();
                });
                step++;
                if(ic.simulations.size() > step){
                    reset(currentSimulation.duration);
                }
            }
        };
        addBehaviour(RequestsBehaviour);
    }
    protected void takeDown() {
        super.takeDown();
    }
}
