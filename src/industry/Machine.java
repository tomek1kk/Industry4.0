package industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Machine {
    public int machineId;
    public int socketId;
    public HashMap<String, MachineAction> actions;
    public Machine(int machineId, int socketId,  HashMap<String, MachineAction> actions)
    {
        this.machineId = machineId;
        this.socketId = socketId;
        this.actions = actions;
    }
}
