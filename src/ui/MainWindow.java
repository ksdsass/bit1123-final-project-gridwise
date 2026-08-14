package ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * MainWindow - the application frame holding the two tabs:
 * "Grid Setup" (build the city and its energy fleet) and
 * "Simulation" (run the 24-hour dispatch and view results).
 */
public class MainWindow extends JFrame {

    private SetupPanel setupPanel = new SetupPanel();
    private SimulationPanel simulationPanel = new SimulationPanel(setupPanel);
    private JTabbedPane tabs = new JTabbedPane();

    public MainWindow() {
        setTitle("GridWise - City Energy Grid Simulator (SDG 7)");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabs.addTab("Grid Setup", setupPanel);
        tabs.addTab("Simulation", simulationPanel);
        add(tabs);
    }

    public SetupPanel getSetupPanel() {
        return setupPanel;
    }

    public SimulationPanel getSimulationPanel() {
        return simulationPanel;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }
}
