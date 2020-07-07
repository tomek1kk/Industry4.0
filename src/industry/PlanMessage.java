package industry;

import jade.core.AID;

import java.util.List;
import java.util.Map;

public class PlanMessage {
    private String productName;
    private int socketId;
    private Integer stageId;
    private String actionName;
    private Integer priority;
    private String id; //11 cyfr, 2 pierwsze to numer maszyny która zleciła wykonanie produktu
    private Map<Integer, List<String>> bookedActions; //zawiera akcje które już zostały zarezerwowane do wykonania
    private String requestingAgent;
    public PlanMessage(String productName, String actionName, Integer stageId, Integer priority,
                       Map<Integer, List<String>> bookedActions, String requestingAgent, String id, int socketId){
        this.productName = productName;
        this.stageId = stageId;
        this.actionName = actionName;
        this.priority = priority;
        this.bookedActions = bookedActions;
        this.requestingAgent = requestingAgent;
        this.id = id;
        this.socketId = socketId;
    }
    public PlanMessage(PlanMessage that){
        this (that.productName, that.actionName, that.stageId, that.priority, that.bookedActions, that.requestingAgent, that.id, that.socketId);
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

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRequestingAgent() { return requestingAgent; }
    public int getSocketId() { return socketId; }
    public Map<Integer, List<String>> getBookedActions() {
        return bookedActions;
    }

}
