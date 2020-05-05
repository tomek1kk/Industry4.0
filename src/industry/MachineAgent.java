package industry;

import com.google.gson.Gson;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;

import java.util.*;

import static jade.lang.acl.MessageTemplate.and;

public class MachineAgent extends Agent {
    Machine machine;
    HashMap<String, List<MachineReference>> subproductMachines;
    HashMap<String, List<MachineReference>> sameProductMachines;
    HashMap<String, Product> productsDefinitions;
    Gson parser = new Gson();
    List<QueueProduct> productionQueue = new ArrayList<QueueProduct>();

    @Override
    protected void setup() {
        MachineAgentArguments args = (MachineAgentArguments) (getArguments()[0]);
        machine = args.machine;
        subproductMachines = args.subproductMachines;
        sameProductMachines = args.sameProductMachines;
        productsDefinitions = args.productsDefinitions;


        Behaviour initMachine = new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getAID().getLocalName() + " started");
                System.out.println("My actions:");
                machine.actions.forEach((action) -> {
                    System.out.println("Product: " + action.productName + ", action: " + action.actionName);
                });
                subproductMachines.forEach((product, machines) -> {
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

        Behaviour Cfp1Responder = new ContractNetResponder(this, MessageTemplate.MatchProtocol("cfp1")) { //wykorzystanie pola protocol do filtrowania wiadomości
            @Override
            protected ACLMessage handleCfp(ACLMessage m) {
                Cfp1 messageContent = parser.fromJson(m.getContent(), Cfp1.class);
                if(FindAction(messageContent) == null) {
                    return new ACLMessage(ACLMessage.REFUSE);
                }
                Cfp2 responseContent = new Cfp2(productionQueue.size(), FindAction(messageContent).productionTime, null, 1);
                ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                response.setContent(parser.toJson(responseContent));
                return response;
            }

            @Override
            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
                Cfp3 messageContent = parser.fromJson(accept.getContent(), Cfp3.class);
                //TODO: umieszczenie w kolejce i produkcja
                return new ACLMessage(ACLMessage.INFORM);
            }
        };
        addBehaviour(Cfp1Responder);
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }


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
//            @Override
//            protected void handleAllResultNotifications(Vector resultNotifications) { }
        };
    }

    private MachineAction FindAction(Cfp1 request){
        for(int i = 0; i < machine.actions.size(); i++){
            if(machine.actions.get(i).actionName.equals(request.GetActionName())
                    && machine.actions.get(i).stageId == request.GetStageId()
                    && machine.actions.get(i).productName.equals(request.GetProductName())) {
                return machine.actions.get(i);
            }
        }
        return null;
    }
    private Integer CalculateMetric(Cfp2 proposeMessage){
        return proposeMessage.getAmountInQueue() * proposeMessage.getProductionTime(); //prosta heurystyka
    }
}
