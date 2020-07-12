package industry;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GUIFrame extends JFrame {

    List<MachineReference> machines;
    HashMap<MachineReference, JPanel> machinePanels = new HashMap<>();
    HashMap<JPanel, List<JLabel>> labels = new HashMap<>();
    JPanel finishedProducts;

    public GUIFrame(List<MachineReference> machines) {
        super("Industry 4.0");
        this.machines = machines;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocation(50,50);
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(800, 400);
        for (int i = 0; i < machines.size(); i++) {
            JPanel machine = new MachineGUI();
            JLabel label = new JLabel("  " + machines.get(i).name + "  ", SwingConstants.CENTER);
            machine.add(label);
            machinePanels.put(machines.get(i), machine);
            labels.put(machine, new ArrayList<>());
            mainPanel.add(machine);
        }

        finishedProducts = new JPanel();
        finishedProducts.setSize(800, 200);
        finishedProducts.add(new JLabel("Finished products: ", SwingConstants.CENTER));
        finishedProducts.setLocation(0, 400);
        add(finishedProducts);
        add(mainPanel);

        setVisible(true);
    }

    public void addProduct(String machine, String product) {
        MachineReference machineRef = machines.stream().filter(m -> m.name.equals(machine)).findFirst().get();
        JPanel panel = machinePanels.get(machineRef);
        JLabel label = new JLabel(product, SwingConstants.CENTER);
        panel.add(label);
        panel.revalidate();
        panel.repaint();
        labels.get(panel).add(label);
    }

    public void removeProduct(String machine, String product) {
        MachineReference machineRef = machines.stream().filter(m -> m.name.equals(machine)).findFirst().get();
        JPanel panel = machinePanels.get(machineRef);
        JLabel label = labels.get(panel).stream().filter(l -> l.getText().equals(product)).findFirst().get();
        panel.remove(label);
        labels.get(panel).remove(label);
        panel.revalidate();
        panel.repaint();
    }

    public void addFinishedProduct(String product) {
        finishedProducts.add(new JLabel(product, SwingConstants.CENTER));
    }
}