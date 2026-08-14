package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.City;
import service.DataStore;
import service.GridSimulator;
import service.SimulationReport;

/**
 * SimulationPanel - the "Simulation" tab. Runs a 24-hour simulation on
 * the fleet built in the SetupPanel, shows the daily statistics, the
 * supply/demand chart, and can export the full report to a text file.
 */
public class SimulationPanel extends JPanel {

    private SetupPanel setupPanel;                 // where the data lives
    private GridSimulator simulator = new GridSimulator();
    private DataStore dataStore = new DataStore();
    private SimulationReport lastReport;

    private ChartPanel chart = new ChartPanel();
    private JLabel demandLabel = stat("Total demand: -");
    private JLabel costLabel = stat("Total cost: -");
    private JLabel co2Label = stat("CO2 emitted: -");
    private JLabel renewableLabel = stat("Renewable share: -");
    private JLabel blackoutLabel = stat("Blackout hours: -");
    private JLabel adviceLabel = new JLabel("Press 'Run 24h Simulation' to begin.");

    public SimulationPanel(SetupPanel setupPanel) {
        this.setupPanel = setupPanel;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton runBtn = new JButton("Run 24h Simulation");
        JButton exportBtn = new JButton("Export Report to File");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(runBtn);
        top.add(exportBtn);
        add(top, BorderLayout.NORTH);

        chart.setBorder(BorderFactory.createTitledBorder(
                "Hourly supply vs demand"));
        add(chart, BorderLayout.CENTER);

        JPanel stats = new JPanel(new GridLayout(2, 3, 8, 4));
        stats.setBorder(BorderFactory.createTitledBorder("Daily results"));
        stats.add(demandLabel);
        stats.add(costLabel);
        stats.add(co2Label);
        stats.add(renewableLabel);
        stats.add(blackoutLabel);
        stats.add(new JLabel());

        adviceLabel.setFont(adviceLabel.getFont().deriveFont(Font.BOLD));
        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.add(stats, BorderLayout.CENTER);
        south.add(adviceLabel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        runBtn.addActionListener(e -> runSimulation());
        exportBtn.addActionListener(e -> exportReport());
    }

    private static JLabel stat(String text) {
        return new JLabel(text);
    }

    /** Runs the simulator on the current city + fleet and shows results. */
    public void runSimulation() {
        if (setupPanel.getSources().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one energy source in the Grid Setup tab.");
            return;
        }
        City city = new City(setupPanel.getCityName(),
                setupPanel.getPeakDemand());

        lastReport = simulator.run(city, setupPanel.getSources());

        chart.setRecords(lastReport.getRecords());
        demandLabel.setText(String.format("Total demand: %,.0f MWh",
                lastReport.getTotalDemandMWh()));
        costLabel.setText(String.format("Total cost: RM %,.0f",
                lastReport.getTotalCostRM()));
        co2Label.setText(String.format("CO2 emitted: %.1f tonnes",
                lastReport.getTotalCo2Tonnes()));
        renewableLabel.setText(String.format("Renewable share: %.0f%%",
                lastReport.getRenewableShare() * 100));
        blackoutLabel.setText("Blackout hours: "
                + lastReport.getBlackoutHours());
        adviceLabel.setText("Advice: " + lastReport.getRecommendation());
    }

    private void exportReport() {
        if (lastReport == null) {
            JOptionPane.showMessageDialog(this, "Run a simulation first.");
            return;
        }
        String fileName = "simulation_report.txt";
        try {
            dataStore.exportReport(lastReport, fileName);
            JOptionPane.showMessageDialog(this, "Report saved to " + fileName);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }
}
