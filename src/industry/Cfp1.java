package industry;

import java.util.List;
import java.util.Map;

public class Cfp1 {
    private String productName;
    private Integer stageId;
    private String actionName;
    private Integer priority;
    private String productId; //7 cyfr, 2 pierwsze to numer maszyny która zleciła wykonanie produktu, 5 kolejnych to reużywalne kolejne numery
    private Map<Integer, List<String>> bookedActions; //zawiera akcje które już zostały zarezerwowane do wykonania
    private String requestingAgent;
    public Cfp1(String productName, String actionName, Integer stageId, Integer priority,
                String productId, Map<Integer, List<String>> bookedActions, String requestingAgent){
        this.productName = productName;
        this.stageId = stageId;
        this.actionName = actionName;
        this.priority = priority;
        this.productId = productId;
        this.bookedActions = bookedActions;
        this.requestingAgent = requestingAgent;
    }
    public String GetProductName() {
        return productName;
    }
    public Integer GetStageId(){
        return stageId;
    }
    public String GetActionName(){
        return actionName;
    }
    public Integer getPriority() {
        return priority;
    }

    public String getProductId() {
        return productId;
    }
    public String getRequestingAgent() { return requestingAgent; }

    public Map<Integer, List<String>> getBookedActions() {
        return bookedActions;
    }
}
