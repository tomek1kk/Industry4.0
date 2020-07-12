package industry;

import com.google.gson.reflect.TypeToken;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;

import com.google.gson.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerAgent extends Agent {
    private List<MachineReference> machineAgents = new ArrayList<MachineReference>();
    private InformationCenter ic;

    protected void parseMachineObject(JsonObject machine, InformationCenter ic) {
        int machineId = machine.get("machineId").getAsInt();
        int socketId = machine.get("socketId").getAsInt();
        List<MachineAction> actions = new ArrayList<MachineAction>();
        JsonArray actionsJson = machine.get("actions").getAsJsonArray();
        actionsJson.forEach(p-> actions.add(new MachineAction(
                p.getAsJsonObject().get("productName").getAsString(),
                p.getAsJsonObject().get("stageId").getAsInt(),
                p.getAsJsonObject().get("actionName").getAsString(),
                p.getAsJsonObject().get("productionTime").getAsInt()
        )));
        Machine m = new Machine(machineId, socketId, actions);
        ic.addMachine(m);
    }

    protected Breakdown parseBreakDownObject(JsonObject breakdown, InformationCenter ic) {
        int machineId = breakdown.get("machineId").getAsInt();
        int breakTime = breakdown.get("breakTime").getAsInt();
        int duration = breakdown.get("duration").getAsInt();

        var br = new Breakdown(machineId, breakTime, duration);
        ic.addBreakdown(br);
        return br;
    }

    protected void parseProductObject(JsonObject product, InformationCenter ic) {
        String productName = product.get("name").getAsString();
        HashMap<Integer, List<ProductAction>> stages = new HashMap<Integer, List<ProductAction>>();
        JsonArray stagesJsonArray = product.get("stages").getAsJsonArray();
        for(int i = 0; i < stagesJsonArray.size(); i++){
            int stageId = stagesJsonArray.get(i).getAsJsonObject().get("stageId").getAsInt();
            List<ProductAction> actions = new ArrayList<ProductAction>();
            JsonArray actionJsonArray = stagesJsonArray.get(i).getAsJsonObject().get("actions").getAsJsonArray();
            for(int j = 0; j < actionJsonArray.size(); j++){
                List<String> subproducts = new ArrayList<String>();
                JsonArray subproductsJsonArray = actionJsonArray.get(j).getAsJsonObject().get("subproducts").getAsJsonArray();
                subproductsJsonArray.forEach(s -> subproducts.add(s.getAsString()));
                actions.add(new ProductAction(actionJsonArray.get(j).getAsJsonObject().get("actionName").getAsString(), subproducts));
            }
            stages.put(stageId, actions);
        }
        Product p = new Product(productName, stages);
        ic.addProduct(p);
    }

    protected void parseSimulationObject(JsonObject simulation, InformationCenter ic) {
        JsonArray simulations = simulation.get("demandedProducts").getAsJsonArray();
        ArrayList<DemandedProduct> users = new ArrayList<DemandedProduct>();
        simulations.forEach(p-> users.add( new DemandedProduct(p.getAsJsonObject().get("name").getAsString(),p.getAsJsonObject().get("amount").getAsInt(), p.getAsJsonObject().get("priority").getAsInt())));
        Simulation s = new Simulation(simulation.get("duration").getAsInt(), users);
        ic.addSimulation(s);
    }

    protected void addNewMachineAgent(Machine machine, Collection<Machine> machines, HashMap<String, Product> products) {
        ContainerController cc = getContainerController();
        AgentController ac = null;
        try {
            String agentName = "Machine" + machine.machineId;
            machineAgents.add(new MachineReference(agentName, machine));
            HashMap<String, List<MachineReference>> productsSubmachines = new HashMap<String, List<MachineReference>>();
            HashMap<String, List<MachineReference>> sameProdMachines = new HashMap<String, List<MachineReference>>();
            HashMap<String, Product> productsDefinitions = new HashMap<>();

            machine.actions.forEach((action)-> {
                // lista podproduktow dla danej akcji
                List<ProductAction> productAction = products.get(action.productName).stages.get(action.stageId)
                      .stream().filter(a -> a.actionName.equals(action.actionName)).collect(Collectors.toList());
                List<String> subproducts = new ArrayList<>();
                if (productAction.size() > 0)
                    subproducts = productAction.get(0).subproducts;
                // dla kazdego podproduktu generujemy liste maszyn go produkujacych
                subproducts.forEach(product -> {
                    int maxStage = Collections.max(products.get(product).stages.keySet());
                    productsSubmachines.put(product, machines.stream().filter(m -> m.actions.stream()
                            .anyMatch(a -> a.productName.equals(product) && a.stageId == maxStage))
                            .map(m -> new MachineReference(m)).collect(Collectors.toList()));
                });
                // dla kazdej akcji ustalamy liste maszyn ktore dzialaja na tym samym produkcie i tym samym lub o 1 nizszym stagu
                List<MachineReference> sameProductMachines = machines.stream().filter(m -> m.actions.stream()
                        .anyMatch(a -> a.productName.equals(action.productName) && !a.actionName.equals(action.actionName)
                                && (a.stageId == action.stageId || a.stageId == action.stageId - 1)))
                        .map(m -> new MachineReference(m)).collect(Collectors.toList());
                sameProdMachines.put(action.productName, sameProductMachines);
                //wybieramy definicje produktów, w produkcji których bierze udział maszyna
                if(!productsDefinitions.containsKey(action.productName)){
                    productsDefinitions.put(action.productName, products.get(action.productName));
                }
            });
            Object[] args = new Object[1];
            args[0] = new MachineAgentArguments(machine, productsSubmachines, sameProdMachines, productsDefinitions);
            ac = cc.createNewAgent(agentName, "industry.MachineAgent", args);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private boolean containsAny(HashMap<String, Integer> map, List<String> list) {
        for (String candidate : list) {
            if (map.containsKey(candidate))
                return true;
        }
        return false;
    }

    private void RunSimulation(){
        ContainerController cc = getContainerController();
        try {
            Object[] args = new Object[1];
            args[0] = InformationCenter.getInstance();
            AgentController ac = cc.createNewAgent("SimulationAgent", "industry.SimulationAgent", args);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    protected void setup() {
        Behaviour readConfiguration = new OneShotBehaviour() {
            @Override
            public void action() {
                Gson gson = new Gson();
                Reader reader = null;
                JsonParser parser = new JsonParser();
                try {
                        reader = Files.newBufferedReader(Paths.get("clothes-factory.JSON"));
                        JsonElement jsonTree = parser.parse(reader);
                        JsonObject jsonObject = jsonTree.getAsJsonObject();

                        JsonArray machines = jsonObject.get("machines").getAsJsonArray();
                        machines.forEach(m-> parseMachineObject(m.getAsJsonObject(), ic));

                        JsonArray products =  jsonObject.get("products").getAsJsonArray();
                        products.forEach(p-> parseProductObject(p.getAsJsonObject(), ic));

                        JsonArray simulations = jsonObject.get("simulation").getAsJsonArray();
                        simulations.forEach(s -> parseSimulationObject(s.getAsJsonObject(), ic));

                        int delay = jsonObject.get("socketDelay").getAsInt();
                        ic.addSocketDelay(delay);
                  
                        JsonArray breakdowns = jsonObject.get("breakdowns").getAsJsonArray();

                        for(var b :breakdowns)
                        {
                            var br = parseBreakDownObject(b.getAsJsonObject(), ic);
                            ContainerController cc = getContainerController();
                            for( var pr : ic.machines.get(br.MachineId).actions) {
                                try {
                                    Object[] args = new Object[1];
                                    args[0] = new BreakdownArgs(pr.productName, pr.stageId);
                                    AgentController ac = cc.createNewAgent("BreakdownAgent_" + br.MachineId, "industry.BreakDownAgent", args);
                                    ac.start();
                                } catch (StaleProxyException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        reader.close();
                    }
                catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };
        Behaviour generateMachines = new WakerBehaviour(this, 2000) {
            @Override
            protected void onWake() {
                System.out.println("Initializing machines...");
                ic.machines.forEach((id, machine)-> {
                    addNewMachineAgent(machine, ic.machines.values(), ic.products);
                });
            }
        };
        Behaviour startSimulation = new WakerBehaviour(this, 4000) {
            @Override
            protected void onWake() {
                System.out.println("Starting simulation...");
                RunSimulation();
            }
        };
        ic = InformationCenter.getInstance();
        addBehaviour(readConfiguration);
        addBehaviour(generateMachines);
        addBehaviour(startSimulation);

    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }
}