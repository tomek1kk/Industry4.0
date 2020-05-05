package industry;

import com.google.gson.Gson;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.BitSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class SimulationAgent extends Agent {
    Gson parser = new Gson();
    InformationCenter ic;
    Integer step = 0;
    final Integer sizeOfGenerator = 99999;
    BitSet idGeneratorSet = new BitSet(sizeOfGenerator);    //all initially false

    private Behaviour Cfp1Requester (ACLMessage messageRequest, ACLMessage messageAcceptProposal){
        return new ContractNetInitiator(this, messageRequest) {
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
                    bestProposeReply = messageAcceptProposal;
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
        
    }

    protected void setup() {
        ic = (InformationCenter) (getArguments()[0]);

        Behaviour RequestsBehaviour = new TickerBehaviour(this, 0) {
            @Override
            protected void onTick() {
                Simulation currentSimulation = ic.simulations.get(step);
                currentSimulation.demandedProducts.forEach(product -> {
                    Integer lastStageId = Collections.max(ic.products.get(product.name).stages.keySet());
                    ic.products.get(product.name).stages.get(lastStageId).forEach(action ->{
                        Cfp1 requestContent = new Cfp1(product.name, action.actionName, lastStageId, product.priority,
                                GenerateProductId(), new Map<Integer, List<String>>())
                    });

                    Cfp1 requestContent = new Cfp1()
                });
            }
        };
    }
    protected void takeDown() {
        super.takeDown();
    }
}
