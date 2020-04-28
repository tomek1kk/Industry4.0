package industry;

public class MachineAction {
    public String productName;
    public int stageId;
    public String actionName;
    public int productionTime;
    public MachineAction (String productName, int stageId, String actionName, int productionTime){
        this.productName = productName;
        this.stageId = stageId;
        this.actionName = actionName;
        this.productionTime = productionTime;
    }
}
