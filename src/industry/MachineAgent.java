package industry;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;

public class MachineAgent extends Agent {
    Machine machine;
    HashMap<String, List<MachineReference>> subproductMachines;
    HashMap<String, List<MachineReference>> sameProductMachines;
    HashMap<String, Product> productsDefinitions;
    Gson parser = new Gson();
    int lastGenerated = 0;
    Map<String, Map<String, List<PlanElement>>> PlanMap = new HashMap<String, Map<String, List<PlanElement>>>();
    List<List<ProduceElement>> ProduceList = new LinkedList<List<ProduceElement>>();

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
                System.out.println("My AID: " + getAID().toString());
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
        InitProductionList();
        addBehaviour(initMachine);
        addBehaviour(GetPlan());
        addBehaviour(GetAcceptOffer());
    }
    private void InitProductionList(){
        for(int i = 0; i < 10; i++){
            ProduceList.add(new LinkedList<ProduceElement>());
        }
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    SimpleBehaviour GetPlan() {
        return new SimpleBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchProtocol("plan");
                ACLMessage rcv = receive(mt);
                if (rcv != null) {
                    PlanMessage message = parser.fromJson(rcv.getContent(), PlanMessage.class);
                    HandlePlan(message);
                } else
                    block();
            }

            @Override
            public boolean done() {
                return false;
            }
        };
    }
    SimpleBehaviour GetAcceptOffer() {
        return new SimpleBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchProtocol("acceptOffer");
                ACLMessage rcv = receive(mt);
                if (rcv != null) {
                    AcceptOfferMessage message = parser.fromJson(rcv.getContent(), AcceptOfferMessage.class);
                    HandleAcceptOffer(message);
                } else
                    block();
            }

            @Override
            public boolean done() {
                return false;
            }
        };
    }

    private void ReturnOffer(String id, Long productionEnd, String receiver) {
        OfferMessage content = new OfferMessage(productionEnd);
        ACLMessage msg = new ACLMessage();
        msg.setContent(parser.toJson(content));
        AID AIDreceiver = new AID(receiver, AID.ISLOCALNAME);
        msg.addReceiver(AIDreceiver);
        msg.setProtocol(id);
        send(msg);
    }

    private MachineAction FindAction(PlanMessage request) {
        for (int i = 0; i < machine.actions.size(); i++) {
            if (machine.actions.get(i).actionName.equals(request.GetActionName())
                    && machine.actions.get(i).stageId == request.GetStageId()
                    && machine.actions.get(i).productName.equals(request.GetProductName())) {
                return machine.actions.get(i);
            }
        }
        return null;
    }
    private MachineAction FindAction(String actionName, int stageId, String productName) {
        for (int i = 0; i < machine.actions.size(); i++) {
            if (machine.actions.get(i).actionName.equals(actionName)
                    && machine.actions.get(i).stageId == stageId
                    && machine.actions.get(i).productName.equals(productName)) {
                return machine.actions.get(i);
            }
        }
        return null;
    }

    private ProductAction GetProductAction(MachineAction machineAction) {
        InformationCenter ic = InformationCenter.getInstance();
        Product requestedProduct = ic.products.get(machineAction.productName);
        return requestedProduct.stages.get(machineAction.stageId)
                .stream().filter(a -> a.actionName.equals(machineAction.actionName))
                .findAny().get();
    }

    private boolean MachineActionMatch(MachineAction mAction, String pName, int pStage, String aName) {
        if (mAction.actionName.equals(aName) && mAction.stageId == pStage && mAction.productName.equals(pName))
            return true;
        return false;
    }

    private String GenerateId() {
        ++lastGenerated;
        String id = Integer.toString(lastGenerated);
        for (int i = id.length(); i < 9; i++) {
            id = "0" + id;
        }
        return Integer.toString(machine.machineId) + id;
    }
    private long GetFinalProductionEnd(Collection<Long> times){
        long lastSubProdTime = Collections.max(times);
        return lastSubProdTime;
        //TODO: dodać sprawdzenie czasu wolnego w liście produkcji (ewentualnie aktualizacje czasów)
    }
    private long GetFinalProductionEnd(){
        return System.currentTimeMillis();
        //TODO: dodać sprawdzenie czasu wolnego w liście produkcji (ewentualnie aktualizacje czasów)
    }

    private void HandleAcceptOffer(AcceptOfferMessage message){
        Map<String, List<PlanElement>> planMapNode = PlanMap.get(message.getId());
        List<PlanElement> tmpSubProductList = new LinkedList<PlanElement>();
        String[] keys = planMapNode.keySet().toArray(new String[planMapNode.keySet().size()]);
        for(int i = 0; i < keys.length; i++){
            for(int j = 0; j < planMapNode.get(keys[i]).size(); j++){
                if(planMapNode.get(keys[i]).get(j)._bestOffer){
                    tmpSubProductList.add(planMapNode.get(keys[i]).get(j));
                    break;
                }
            }
        }
        List<PlanElement> subProductList = new LinkedList<PlanElement>();
        if(!tmpSubProductList.get(0)._lastProcess){
            subProductList = tmpSubProductList;
        }
        ProduceElement prodElement = new ProduceElement(subProductList, tmpSubProductList.get(0)._messageContent);
        ProduceList.get(tmpSubProductList.get(0)._upperMessageContent.getPriority()).add(prodElement);
        for(int i = 0; i < subProductList.size(); i++){
            AcceptOffer(message.getId(), subProductList.get(i));
        }
    }

    private void AcceptOffer(String productId, PlanElement planElem){
        addBehaviour(GetProduct(productId, planElem._messageContent.getId()));
        ACLMessage msg = new ACLMessage();
        msg.addReceiver(planElem._receiver);
        msg.setProtocol("acceptOffer");
        AcceptOfferMessage messageContent = new AcceptOfferMessage(planElem._messageContent.getId());
        msg.setContent(parser.toJson(messageContent));
        send(msg);
    }
    SimpleBehaviour GetProduct(String productId, String id){
        return new SimpleBehaviour() {
            boolean finished = false;
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchProtocol("product" + id);
                ACLMessage rcv = receive(mt);
                if (rcv != null) {
                    ProduceElement tmpProduceElem = FindProduceElement(productId);
                    tmpProduceElem.Acquire(id);
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
    private ProduceElement FindProduceElement(String Id){
        for(int i = 0; i<10; i++){
            for(int j = 0; j<ProduceList.get(i).size(); j++){
                if(ProduceList.get(i).get(j)._planMessage.getId().equals(Id))
                    return ProduceList.get(i).get(j);
            }
        }
        return null;
    }
    private void HandlePlan(PlanMessage plan) {
        MachineAction requestedAction = FindAction(plan);
        if (requestedAction == null) {
            ReturnOffer(plan.getId(), Long.MAX_VALUE, plan.getRequestingAgent());
        }
        PlanMap.put(plan.getId(), new HashMap<String, List<PlanElement>>());
        Map<Integer, List<String>> bookedActions = plan.getBookedActions();
        ProductAction action = GetProductAction(requestedAction);
        if (bookedActions.get(plan.GetStageId()) != null)
            bookedActions.get(plan.GetStageId()).add(action.actionName);
        else
            bookedActions.put(plan.GetStageId(), new ArrayList<String>() {
                {
                    add(action.actionName);
                }
            });
        List<String> subproducts = new ArrayList<String>(action.subproducts);
        InformationCenter ic = InformationCenter.getInstance();
        HashMap<String, List<PlanElement>> tmpPlanMap = new HashMap<String, List<PlanElement>>();

        //---------------------------------------------------------- add subproducts to plan map:
        subproducts.forEach(p -> {
            final Product product = ic.products.get(p);
            List<PlanElement> planList = new LinkedList<PlanElement>();
            subproductMachines.get(p).forEach(subMachine -> {
                int stageId = Collections.max(product.stages.keySet());
                product.stages.get(stageId).forEach(productAction -> {
                    subMachine.machine.actions.forEach(machineAction -> {
                        if (MachineActionMatch(machineAction, product.name, stageId, productAction.actionName)) {
                            PlanMessage msg = new PlanMessage(product.name, productAction.actionName, stageId, plan.getPriority(),
                                    new HashMap<Integer, List<String>>(), getLocalName(), GenerateId());
                            ACLMessage aclMessage = new ACLMessage();
                            aclMessage.setProtocol("plan");
                            AID receiver = new AID("Machine" + Integer.toString(subMachine.machine.machineId), AID.ISLOCALNAME);
                            aclMessage.addReceiver(receiver);
                            planList.add(new PlanElement(msg, aclMessage, receiver, new AID(plan.getRequestingAgent(), AID.ISLOCALNAME), plan));
                        }
                    });
                });
            });
            tmpPlanMap.put(p, planList);
        });
        //---------------------------------------------------------- add lastProcess actions to plan map:
        Product requestedProduct = ic.products.get(plan.GetProductName());
        int tmpPrevStageId = plan.GetStageId();
        if (bookedActions.get(plan.GetStageId()).size() == requestedProduct.stages.get(plan.GetStageId()).size())
            --tmpPrevStageId;
        if (tmpPrevStageId == 0) {
            long productionTime = GetFinalProductionEnd();
            productionTime += FindAction(plan.GetActionName(), plan.GetStageId(), plan.GetProductName()).productionTime;
            List<PlanElement> lastActionPlanList = new LinkedList<PlanElement>();
            PlanElement lastActionPlanElement = new PlanElement(null, null, null, new AID(plan.getRequestingAgent(), AID.ISLOCALNAME), plan);
            lastActionPlanElement._lastProcess = true;
            lastActionPlanElement._bestOffer = true;
            lastActionPlanList.add(lastActionPlanElement);
            tmpPlanMap.put("lastAction", lastActionPlanList);
            PlanMap.put(plan.getId(), tmpPlanMap);
            ReturnOffer(plan.getId(), productionTime, plan.getRequestingAgent());
            return ;
        }
        //---------------------------------------------------------- add "prev" actions to plan map:
        List<PlanElement> prevActionPlanList = new LinkedList<PlanElement>();
        final int prevStageId = tmpPrevStageId;
        requestedProduct.stages.get(prevStageId).stream().filter(a ->
                (prevStageId != plan.GetStageId() || bookedActions.get(prevStageId).contains(a.actionName) == false)
        ).forEach(productAction -> {
            PlanMessage msg = new PlanMessage(requestedProduct.name, productAction.actionName, prevStageId, plan.getPriority(),
                    bookedActions, getLocalName(), "dummyID");
            if(sameProductMachines.get(plan.GetProductName()) == null){
                System.out.println("dupa");
            }
            sameProductMachines.get(plan.GetProductName()).forEach(subMachine -> {
                subMachine.machine.actions.forEach(machineAction -> {
                    if (MachineActionMatch(machineAction, requestedProduct.name, prevStageId, productAction.actionName)) {
                        ACLMessage aclMessage = new ACLMessage();
                        aclMessage.setProtocol("plan");
                        AID receiver = new AID("Machine" + Integer.toString(subMachine.machine.machineId), AID.ISLOCALNAME);
                        aclMessage.addReceiver(receiver);
                        msg.setId(GenerateId());
                        prevActionPlanList.add(new PlanElement(new PlanMessage(msg), aclMessage, receiver, new AID(plan.getRequestingAgent(), AID.ISLOCALNAME), plan));
                    }
                });
            });
        });
        tmpPlanMap.put("prevActions", prevActionPlanList);
        PlanMap.put(plan.getId(), tmpPlanMap);
        addBehaviour(PlanBehaviour(plan.getId()));
    }

    SimpleBehaviour PlanBehaviour(String productId) {
        return new SimpleBehaviour() {
            boolean finished = false;

            @Override
            public void action() {
                String[] keys = PlanMap.get(productId).keySet().toArray(new String[PlanMap.get(productId).keySet().size()]);
                for (int j = 0; j < keys.length; j++) {
                    List<PlanElement> refPlanList = PlanMap.get(productId).get(keys[j]);
                    for (int i = 0; i < refPlanList.size(); i++) {
                        addBehaviour(WaitForOffer(productId, refPlanList.get(i)._messageContent.getId()));
                        refPlanList.get(i)._aclMessage.setContent(parser.toJson(refPlanList.get(i)._messageContent));
                        send(refPlanList.get(i)._aclMessage);
                    }
                }
                finished = true;
            }

            @Override
            public boolean done() {
                return finished;
            }
        };
    }

    SimpleBehaviour WaitForOffer(String productId, String id) {
        return new SimpleBehaviour() {
            boolean finished = false;

            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchProtocol(id);
                ACLMessage rcv = receive(mt);
                if (rcv != null) {
                    OfferMessage offer = parser.fromJson(rcv.getContent(), OfferMessage.class);
                    boolean allOffersArrived = true;
                    String[] keys = PlanMap.get(productId).keySet().toArray(new String[PlanMap.get(productId).keySet().size()]);
                    for (int j = 0; j < keys.length; j++) {
                        List<PlanElement> refPlanList = PlanMap.get(productId).get(keys[j]);
                        for (int i = 0; i < refPlanList.size(); i++) {
                            if (refPlanList.get(i)._messageContent.getId().equals(id)) {
                                refPlanList.get(i)._productionEnd = offer.getProductionEnd();
                            } else if (refPlanList.get(i)._productionEnd == 0) {
                                allOffersArrived = false;
                            }
                        }
                    }
                    if (allOffersArrived) {
                        addBehaviour(ChooseOffers(productId));
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
    SimpleBehaviour ChooseOffers(String productId){
        return new SimpleBehaviour() {
            boolean finished = false;
            @Override
            public void action() {
                String[] keys = PlanMap.get(productId).keySet().toArray(new String[PlanMap.get(productId).keySet().size()]);
                Map<String, Long> offersTimes = new HashMap<String, Long>();
                Map<String, Integer> offersIdx = new HashMap<String, Integer>();
                for (int j = 0; j < keys.length; j++) {
                    offersTimes.put(keys[j], Long.MAX_VALUE);
                    offersIdx.put(keys[j], 0);
                    List<PlanElement> refPlanList = PlanMap.get(productId).get(keys[j]);
                    for (int i = 0; i < refPlanList.size(); i++) {
                        if(refPlanList.get(i)._productionEnd < offersTimes.get(keys[j])){
                            offersIdx.replace(keys[j], i);
                            offersTimes.replace(keys[j], refPlanList.get(i)._productionEnd);
                        }
                    }
                }
                for(int i = 0; i < keys.length; i++){
                    PlanMap.get(productId).get(keys[i]).get(offersIdx.get(keys[i]))._bestOffer = true;
                }
                long finalProductionEnd = GetFinalProductionEnd(offersTimes.values());
                ReturnOffer(productId, finalProductionEnd, PlanMap.get(productId).get(keys[0]).get(0)._requestingAgent.getLocalName());
                finished = true;
            }
            @Override
            public boolean done() {
                return finished;
            }
        };
    }
}
//        Behaviour Cfp1Responder = new ContractNetResponder(this, MessageTemplate.MatchProtocol("cfp1")) { //wykorzystanie pola protocol do filtrowania wiadomości
//            @Override
//            protected ACLMessage handleCfp(ACLMessage m) {
//
//                Cfp1 messageContent = parser.fromJson(m.getContent(), Cfp1.class);
//                System.out.println(machine.machineId + " got request: " + messageContent.GetActionName() + "from " + messageContent.getRequestingAgent());
//
//                MachineAction requestedAction = FindAction(messageContent);
//                if(requestedAction == null) {
//                    return new ACLMessage(ACLMessage.REFUSE);
//                }
//
//                InformationCenter ic = InformationCenter.getInstance();
//                Product requestedProduct = ic.products.get(requestedAction.productName);
//                Map<Integer, List<String>> bookedActions = messageContent.getBookedActions();
//                ProductAction action = requestedProduct.stages.get(requestedAction.stageId)
//                        .stream().filter(a -> a.actionName.equals(requestedAction.actionName))
//                        .findAny().get();
//                if (bookedActions.get(messageContent.GetStageId()) != null) {
//                    bookedActions.get(messageContent.GetStageId()).add(action.actionName);
//                }
//                else
//                    bookedActions.put(messageContent.GetStageId(), new ArrayList<String>() { {add(action.actionName);}});
//                List<String> subproducts = new ArrayList<String>(action.subproducts);
//                subproducts.forEach(p -> {
//                    final Product product = ic.products.get(p);
//                    subproductMachines.get(p).forEach(machine -> {
//                        int stageId = Collections.max(product.stages.keySet());
//                        product.stages.get(stageId).forEach(a -> {
//                            Cfp1 request = new Cfp1(p, a.actionName, stageId, messageContent.getPriority(),
//                                    messageContent.getProductId(), bookedActions, getLocalName());
//                            sendRequest(machine.name, request);
//                        });
//
//                    });
//                });
//
//                requestedProduct.stages.get(messageContent.GetStageId()).stream().filter(a ->
//                    bookedActions.get(messageContent.GetStageId()).contains(a.actionName) == false
//                ).forEach(productAction -> {
//                    Cfp1 request = new Cfp1(requestedProduct.name, productAction.actionName, messageContent.GetStageId(),
//                            messageContent.getPriority(),
//                            messageContent.getProductId(), bookedActions, getLocalName());
//                    if (sameProductMachines.containsKey(requestedProduct.name)) {
//                        sameProductMachines.get(requestedProduct.name).forEach(machine -> {
//                            if (machine.machine.actions.stream().map(a -> a.actionName).collect(Collectors.toList()).contains(productAction.actionName))
//                                sendRequest(machine.name, request);
//                        });
//                    }
//                });
//
//                Cfp2 responseContent = new Cfp2(productionQueue.size(), requestedAction.productionTime, null, 1);
//                ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
//                response.setContent(parser.toJson(responseContent));
//                return response;
//            }
//
//            @Override
//            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
//                Cfp1 messageContent = parser.fromJson(cfp.getContent(), Cfp1.class);
//                //TODO: umieszczenie w kolejce i produkcja
//                return new ACLMessage(ACLMessage.INFORM);
//            }
//        };
//        addBehaviour(Cfp1Responder);
//}


//    private Behaviour Cfp1Requester (ACLMessage messageRequest, ACLMessage messageAcceptProposal){
//        return new ContractNetInitiator(this, messageRequest) {
//            @Override
//            protected void handleAllResponses(Vector responses, Vector acceptances) {
//                Enumeration e = responses.elements();
//                ACLMessage bestProposeReply = null;
//                Integer metric = Integer.MAX_VALUE;
//                while (e.hasMoreElements()) {
//                    ACLMessage msg = (ACLMessage) e.nextElement();
//                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
//                        ACLMessage reply = msg.createReply();
//                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
//                        acceptances.addElement(reply);
//                        Cfp2 proposeMessage = parser.fromJson(msg.getContent(), Cfp2.class);
//                        if(CalculateMetric(proposeMessage) < metric){
//                            bestProposeReply = reply;
//                            metric = CalculateMetric(proposeMessage);
//                        }
//                    }
//                }
//                if (acceptances.size() > 0) {
//                    bestProposeReply = messageAcceptProposal;
//                } else {
//                    System.out.println("No machine that make this product");
//                }
//            }
////            @Override
////            protected void handleAllResultNotifications(Vector resultNotifications) { }
//        };
//    }
//    private Integer CalculateMetric(Cfp2 proposeMessage){
//        return proposeMessage.getAmountInQueue() * proposeMessage.getProductionTime(); //prosta heurystyka
//    }
//}

//    private void sendRequest(String machine, Cfp1 request) {
//        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
//        msg.addReceiver(new AID(machine, AID.ISLOCALNAME));
//        msg.setContent(parser.toJson(request));
//        msg.setProtocol("cfp1");
//        send(msg);
//    }