//package industry;
//
//import jade.core.Agent;
//import jade.core.behaviours.*;
//
//import com.google.gson.*;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public class ManagerAgent extends Agent {
//    protected void parseMachineObject(JSONObject machine, InformationCenter ic) throws JSONException {
//        int id = machine.getInt("id");
//        HashMap<String, Integer> prods = new HashMap<String, Integer>();
//        JSONArray products = machine.getJSONArray("products");
//        for (int i=0; i < products.length(); i++) {
//            JSONObject obj = products.getJSONObject(i);
//            prods.put(obj.getString("name"), obj.getInt("productionTime"));
//        }
//        Machine m = new Machine(id, prods);
//        ic.addMachine(m);
//    }
//
//    protected void parseProductObject(JSONObject product, InformationCenter ic) throws JSONException {
//        ArrayList<String> subproducts = new ArrayList<String>();
//        JSONArray subprods = product.getJSONArray("subproducts");
//        for (int i=0; i < subprods.length(); i++) {
//            JSONObject obj = subprods.getJSONObject(i);
//            //subproducts.add((String)obj);
//        }
//        Product p = new Product(product.getString("name"), subproducts);
//        ic.addProduct(p);
//    }
//
//    protected void parseSimulationObject(JSONObject simulation, InformationCenter ic)
//    {
//        return;
//    }
//    protected void setup() {
//        Behaviour readConfiguration = new OneShotBehaviour() {
//            @Override
//            public void action() {
//                InformationCenter ic = new InformationCenter();
//                Gson gson = new Gson();
//                try {
//
//                    Object object = gson.fromJson(new FileReader("simple_config.json"), Object.class);
//
//
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
////
////
////                    JSONArray machines = obj.getJSONArray("machines");
////                    JSONArray products = obj.getJSONArray("products");
////                    JSONArray simulation = obj.getJSONArray("simulation");
////                    System.out.println("READ JSON");
////
////                    for (int i=0; i < machines.length(); i++) {
////                        parseMachineObject(machines.getJSONObject(i), ic);
////                    }
////                    for (int i=0; i < products.length(); i++) {
////                        parseProductObject(products.getJSONObject(i), ic);
////                    }
////                    for (int i=0; i < simulation.length(); i++) {
////                        parseSimulationObject(simulation.getJSONObject(i), ic);
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//            }
//        };
//        addBehaviour(readConfiguration);
//    }
//
//    @Override
//    protected void takeDown() {
//        super.takeDown();
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//

package industry;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import org.json.*;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ManagerAgent extends Agent {
    protected void parseMachineObject(JSONObject machine, InformationCenter ic) throws JSONException {
        int id = machine.getInt("id");
        HashMap<String, Integer> prods = new HashMap<String, Integer>();
        JSONArray products = machine.getJSONArray("products");
        for (int i=0; i < products.length(); i++) {
            JSONObject obj = products.getJSONObject(i);
            prods.put(obj.getString("name"), obj.getInt("productionTime"));
        }
        Machine m = new Machine(id, prods);
        ic.addMachine(m);
    }

    protected void parseProductObject(JSONObject product, InformationCenter ic) throws JSONException {
        ArrayList<String> subproducts = new ArrayList<String>();
        JSONArray subprods = product.getJSONArray("subproducts");
        for (int i=0; i < subprods.length(); i++) {
            JSONObject obj = subprods.getJSONObject(i);
            //subproducts.add((String)obj);
        }
        Product p = new Product(product.getString("name"), subproducts);
        ic.addProduct(p);
    }

    protected void parseSimulationObject(JSONObject simulation, InformationCenter ic)
    {
        return;
    }
    protected void setup() {
        Behaviour readConfiguration = new OneShotBehaviour() {
            @Override
            public void action() {
                InformationCenter ic = new InformationCenter();
                JSONParser jsonParser = new JSONParser();
                try (FileReader reader = new FileReader("simple_config.json"))
                {
                    //Read JSON file
                    JSONObject obj = (JSONObject) jsonParser.parse(reader);

                    JSONArray machines = obj.getJSONArray("machines");
                    JSONArray products = obj.getJSONArray("products");
                    JSONArray simulation = obj.getJSONArray("simulation");
                    System.out.println("READ JSON");

                    for (int i=0; i < machines.length(); i++) {
                        parseMachineObject(machines.getJSONObject(i), ic);
                    }
                    for (int i=0; i < products.length(); i++) {
                        parseProductObject(products.getJSONObject(i), ic);
                    }
                    for (int i=0; i < simulation.length(); i++) {
                        parseSimulationObject(simulation.getJSONObject(i), ic);
                    }
                } catch (Exception e) {
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
