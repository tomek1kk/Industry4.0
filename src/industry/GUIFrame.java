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
    HashMap<String, Integer> ordered = new HashMap<>();
    HashMap<String, Integer> produced = new HashMap<>();
    HashMap<String, JLabel> orderedLabel = new HashMap<>();
    HashMap<String, JLabel> producedLabel = new HashMap<>();
    InformationCenter ic = InformationCenter.getInstance();

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

        ic.products.values().forEach(value -> {
            ordered.put(value.name, 0);
            produced.put(value.name, 0);
            orderedLabel.put(value.name, new JLabel("0", SwingConstants.CENTER));
            producedLabel.put(value.name, new JLabel("0", SwingConstants.CENTER));
            finishedProducts.add(new JLabel(value.name + " ", SwingConstants.CENTER));
            finishedProducts.add(producedLabel.get(value.name));
            finishedProducts.add(new JLabel("/", SwingConstants.CENTER));
            finishedProducts.add(orderedLabel.get(value.name));
        });

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
        produced.put(product, produced.get(product) + 1);
        producedLabel.get(product).setText(produced.get(product).toString());
    }

    public void setSimulationStep(int step) {
        Simulation simulation = ic.simulations.get(step);
        for (int i = 0; i < simulation.demandedProducts.size(); i++) {
            DemandedProduct product = simulation.demandedProducts.get(i);
            int prevValue = ordered.get(product.name);
            ordered.replace(product.name, prevValue, prevValue + product.amount);
            orderedLabel.get(product.name).setText(ordered.get(product.name).toString());

        }
    }

}