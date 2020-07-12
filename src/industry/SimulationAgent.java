package industry;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;

public class SimulationAgent extends Agent {
    Gson parser = new Gson();
    InformationCenter ic;
    Integer step = 0;
    Integer lastGenerated = 0;
    Integer lastGeneratedProductId = 0;
    Map<Integer, List<PlanElement>> PlanMap = new HashMap<Integer, List<PlanElement>>();
    List<DoneProduct> DoneProducts = new LinkedList<DoneProduct>();

    private String GenerateId(){
        ++lastGenerated;
        String id = Integer.toString(lastGenerated);
        for (int i = id.length(); i < 9; i++){
            id = "0" + id;
        }
        return "00" + id;
    }
    private Integer GenerateProductId(){
        return ++lastGeneratedProductId;
    }

    protected void setup() {
        ic = (InformationCenter) (getArguments()[0]);

        Behaviour RequestsBehaviour = new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                Simulation currentSimulation = ic.simulations.get(step);
                ic.guiFrame.setSimulationStep(step);

                for(int j = 0; j < currentSimulation.demandedProducts.size(); j++){
                    Integer lastStageId = Collections.max(ic.products.get(currentSimulation.demandedProducts.get(j).name).stages.keySet());
                    final DemandedProduct product = currentSimulation.demandedProducts.get(j);
                    for(int i = 0; i < product.amount; i++){
                        Integer productId = GenerateProductId();
                        LinkedList<PlanElement> PlanList = new LinkedList<PlanElement>();
                        ic.products.get(product.name).stages.get(lastStageId).forEach(action ->{
                            PlanMessage requestContent = new PlanMessage(product.name, action.actionName, lastStageId, product.priority,
                                    new HashMap<Integer, List<String>>(), getLocalName(), "dummyID", -1);
                            ic.machines.values().forEach(machine -> {
                                machine.actions.forEach(machineAction -> {
                                    if(machineAction.productName.equals(product.name)
                                            && machineAction.stageId == lastStageId
                                            && machineAction.actionName.equals(action.actionName)){
                                        ACLMessage msg = new ACLMessage();
                                        AID receiver = new AID("Machine" + Integer.toString(machine.machineId), AID.ISLOCALNAME);
                                        AID UpperSender = new AID("dummySender", AID.ISLOCALNAME);
                                        msg.addReceiver(receiver);
                                        requestContent.setId(GenerateId());
                                        msg.setProtocol("plan");
                                        PlanList.add(new PlanElement(new PlanMessage(requestContent), msg, receiver, UpperSender, null));
                                    }
                                });
                            });
                        });
                        PlanMap.put(productId, PlanList);
                        addBehaviour(PlanBehaviour(productId));
                    }
                }
                step++;

                if(ic.simulations.size() > step){
                    reset(currentSimulation.duration);
                } else {
                    stop();
                }
            }
        };
        addBehaviour(RequestsBehaviour);
    }
    protected void takeDown() {
        super.takeDown();
    }
    SimpleBehaviour PlanBehaviour(Integer productId){
        return new SimpleBehaviour() {
            boolean finished = false;
            @Override
            public void action() {
                PlanMap.get(productId).forEach(planElement ->{
                        addBehaviour(WaitForOffer(productId, planElement._messageContent.getId()));
                        planElement._aclMessage.setContent(parser.toJson(planElement._messageContent));
                        send(planElement._aclMessage);
                });
                finished = true;
            }
            @Override
            public boolean done() {
                return finished;
            }
        };
    }
    SimpleBehaviour WaitForOffer(Integer productId, String id){
        return new SimpleBehaviour() {
            boolean finished = false;
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchProtocol(id);
                ACLMessage rcv = receive(mt);
                if (rcv != null) {
                    OfferMessage offer = parser.fromJson(rcv.getContent(), OfferMessage.class);
                    boolean allOffersArrived = true;
                    for (int i = 0; i < PlanMap.get(productId).size(); i++){
                        if(PlanMap.get(productId).get(i)._messageContent.getId() == id){
                            PlanMap.get(productId).get(i)._productionEnd = offer.getProductionEnd();
                        }
                        else if(PlanMap.get(productId).get(i)._productionEnd == 0){
                            allOffersArrived = false;
                        }
                    }
                    if(allOffersArrived){
                        addBehaviour(ChooseOffer(productId));
                    }
                    finished = true;
                } else
                    block();
            }
            @Override
            public boolean done() {
                return finished;
            }
        };
    }
    SimpleBehaviour ChooseOffer(Integer productId){
        return new SimpleBehaviour() {
            boolean finished = false;
            @Override
            public void action() {
                long bestTime = Long.MAX_VALUE;
                int bestIdx = 0;
                for (int i = 0; i < PlanMap.get(productId).size(); i++){
                    if(PlanMap.get(productId).get(i)._productionEnd == -1) //when machine is broke
                        continue;
                    if(PlanMap.get(productId).get(i)._productionEnd < bestTime){
                        bestIdx = i;
                        bestTime = PlanMap.get(productId).get(i)._productionEnd;
                    }
                }
                PlanElement planElem = PlanMap.get(productId).get(bestIdx);
                addBehaviour(GetProduct(productId, planElem._messageContent.getId()));
                ACLMessage msg = new ACLMessage();
                msg.addReceiver(planElem._receiver);
                msg.setProtocol("acceptOffer");
                AcceptOfferMessage messageContent = new AcceptOfferMessage(planElem._messageContent.getId());
                msg.setContent(parser.toJson(messageContent));
                send(msg);
                finished = true;
            }
            @Override
            public boolean done() {
                return finished;
            }
        };
    }
    SimpleBehaviour GetProduct(Integer productId, String id){
        return new SimpleBehaviour() {
            boolean finished = false;
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchProtocol("product" + id);
                ACLMessage rcv = receive(mt);
                if (rcv != null) {
                    PlanMessage mes = PlanMap.get(productId).get(0)._messageContent;
                    String productName = mes.GetProductName();
                    DoneProducts.add(new DoneProduct(productName, productId, id));
                    System.out.println("Got finished product: " + productName + ". Finished products: " + DoneProducts.size());
                    ic.guiFrame.addFinishedProduct(productName);
                    finished = true;
                } else
                    block();
            }
            @Override
            public boolean done() {
                return finished;
            }
        };
    }
}