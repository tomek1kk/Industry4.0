package industry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Machine {
    public int machineId;
    public int socketId;
    public List<MachineAction> actions;
    public Breakdown breakdown;
    public Boolean active = true;
    public Machine(int machineId, int socketId, List<MachineAction> actions)
    {
        this.machineId = machineId;
        this.socketId = socketId;
        this.actions = actions;
        breakdown = null;
    }
    public void HandleBreakDown()
    {
        if(breakdown != null)
        {
            Timer workTimer = new Timer();
            workTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    active = false;
                }
            }, breakdown.breakTime);
            Timer breakdownTimer = new Timer();
            breakdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    active = true;
                }
            }, breakdown.durationOfBreak);

        }
    }
}

