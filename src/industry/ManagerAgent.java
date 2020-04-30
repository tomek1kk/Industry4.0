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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerAgent extends Agent {
    private List<MachineReference> machineAgents = new ArrayList<MachineReference>();

    protected void parseMachineObject(JsonObject machine, InformationCenter ic) {
        int machineId = machine.get("machineId").getAsInt();
        int socketId = machine.get("socketId").getAsInt();
        HashMap<String, MachineAction> actions = new HashMap<String, MachineAction>();
        JsonArray actionsJson = machine.get("actions").getAsJsonArray();
        actionsJson.forEach(p-> actions.put(p.getAsJsonObject().get("productName").getAsString(), new MachineAction(
                p.getAsJsonObject().get("productName").getAsString(),
                p.getAsJsonObject().get("stageId").getAsInt(),
                p.getAsJsonObject().get("actionName").getAsString(),
                p.getAsJsonObject().get("productionTime").getAsInt()
        )));
        Machine m = new Machine(machineId, socketId, actions);
        ic.addMachine(m);
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
//        ContainerController cc = getContainerController();
//        AgentController ac = null;
//        try {
//            String agentName = "Machine" + machine.machineId;
//            machineAgents.add(new Pair<>(agentName, machine));
//            HashMap<String, List<Pair<String, Machine>>> productsSubmachines = new HashMap<String, List<Pair<String, Machine>>>();
//            machine.actions.forEach((name, product)-> {
//                List<String> blockedBy = products.get(name).blockedBy;
//                productsSubmachines.put(name, machines.stream().filter(m -> containsAny(m.products, blockedBy)).map(m-> new Pair<>("Machine" + m.id, m)).collect(Collectors.toList()));
//            });
//            Object[] args = new Object[1];
//            args[0] = new MachineAgentArguments(machine, productsSubmachines);
//            ac = cc.createNewAgent(agentName, "industry.MachineAgent", args);
//
//            ac.start();
//        } catch (StaleProxyException e) {
//            e.printStackTrace();
//        }
    }

    private boolean containsAny(HashMap<String, Integer> map, List<String> list) {
        for (String candidate : list) {
            if (map.containsKey(candidate))
                return true;
        }
        return false;
    }

    protected void setup() {
        Behaviour readConfiguration = new OneShotBehaviour() {
            @Override
            public void action() {
                InformationCenter ic = new InformationCenter();
                System.out.println("dupa");
                Gson gson = new Gson();
                Reader reader = null;
                JsonParser parser = new JsonParser();
                try {
                        reader = Files.newBufferedReader(Paths.get("simple_confiq.JSON"));
                        JsonElement jsonTree = parser.parse(reader);
                        JsonObject jsonObject = jsonTree.getAsJsonObject();

                        JsonArray machines = jsonObject.get("machines").getAsJsonArray();
                        machines.forEach(m-> parseMachineObject(m.getAsJsonObject(), ic));

                        JsonArray products =  jsonObject.get("products").getAsJsonArray();
                        products.forEach(p-> parseProductObject(p.getAsJsonObject(), ic));

                        JsonArray simulations = jsonObject.get("simulation").getAsJsonArray();
                        simulations.forEach(s -> parseSimulationObject(s.getAsJsonObject(), ic));

                        ic.machines.forEach((id, machine)-> {
                            addNewMachineAgent(machine, ic.machines.values(), ic.products);
                        });
                        reader.close();

                    }
                catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        };
        addBehaviour(readConfiguration);
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }
}