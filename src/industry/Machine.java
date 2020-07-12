package industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

enum MachineState
{
    free,
    busy
}
public class Machine {
    public int machineId;
    public int socketId;
    public List<MachineAction> actions;
    public Breakdown breakdown;
    public Boolean active = true;
    public MachineState state;
    public Machine(int machineId, int socketId, List<MachineAction> actions)
    {
        this.machineId = machineId;
        this.socketId = socketId;
        this.actions = actions;
        state = MachineState.free;
        breakdown = null;
    }

}

