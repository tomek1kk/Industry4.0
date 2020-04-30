package industry;

public class MachineReference {
    public String name;
    public Machine machine;

    public MachineReference(String name, Machine machine) {
        this.name = name;
        this.machine = machine;
    }
    public MachineReference(Machine machine) {
        this.name = "Machine" + machine.machineId;
        this.machine = machine;
    }
}
