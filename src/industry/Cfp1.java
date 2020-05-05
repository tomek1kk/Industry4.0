package industry;

public class Cfp1 {
    private String productName;
    private Integer stageId;
    private String actionName;
    private Integer priority;
    public Cfp1(String productName, String actionName, Integer stageId, Integer priority){
        this.productName = productName;
        this.stageId = stageId;
        this.actionName = actionName;
        this.priority = priority;
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
}
