package industry;

import com.google.gson.reflect.TypeToken;
import jade.core.Agent;
import jade.core.behaviours.*;

import com.google.gson.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerAgent extends Agent {
    protected void parseMachineObject(JsonObject machine, InformationCenter ic) {
        int id = machine.get("id").getAsInt();
        HashMap<String, Integer> prods = new HashMap<String, Integer>();
        JsonArray products = machine.get("products").getAsJsonArray();
        products.forEach(p-> prods.put(p.getAsJsonObject().get("name").getAsString(), p.getAsJsonObject().get("productionTime").getAsInt()));
        Machine m = new Machine(id, prods);
        ic.addMachine(m);
    }

    protected void parseProductObject(JsonObject product, InformationCenter ic) {
        ArrayList<String> subproducts = new ArrayList<String>();
        JsonArray subprods = product.get("subproducts").getAsJsonArray();
        subprods.forEach(p -> subproducts.add(p.getAsString()));
        Product p = new Product(product.get("name").getAsString(), subproducts);
        ic.addProduct(p);
    }

    protected void parseSimulationObject(JsonObject simulation, InformationCenter ic) {
        JsonArray simulations = simulation.get("demandedProducts").getAsJsonArray();
        ArrayList<DemandedProduct> users = new ArrayList<DemandedProduct>();
        simulations.forEach(p-> users.add( new DemandedProduct(p.getAsJsonObject().get("name").getAsString(),p.getAsJsonObject().get("amount").getAsInt(), p.getAsJsonObject().get("priority").getAsInt())));
        Simulation s = new Simulation(simulation.get("duration").getAsInt(), users);
        ic.addSimulation(s);
    }
    protected void setup() {
        Behaviour readConfiguration = new OneShotBehaviour() {
            @Override
            public void action() {
                InformationCenter ic = new InformationCenter();
                Gson gson = new Gson();
                Reader reader = null;
                JsonParser parser = new JsonParser();
                try {
                        reader = Files.newBufferedReader(Paths.get("Y:\\Desktop\\industry4.0\\simple_confiq.JSON"));
                        JsonElement jsonTree = parser.parse(reader);
                        JsonObject jsonObject = jsonTree.getAsJsonObject();

                        JsonArray machines = jsonObject.get("machines").getAsJsonArray();
                        machines.forEach(m-> parseMachineObject(m.getAsJsonObject(), ic));

                        JsonArray products =  jsonObject.get("products").getAsJsonArray();
                        products.forEach(p-> parseProductObject(p.getAsJsonObject(), ic));

                        JsonArray simulations = jsonObject.get("simulation").getAsJsonArray();
                        simulations.forEach(s -> parseSimulationObject(s.getAsJsonObject(), ic));

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