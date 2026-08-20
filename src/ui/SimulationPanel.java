package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.City;
import service.CurrencyConverter;
import service.DataStore;
import service.GridSimulator;
import service.SimulationReport;

/**
 * SimulationPanel - the "Simulation" screen: the run button, five stat
 * cards, the supply/demand chart and an advice banner that changes
 * colour depending on how healthy the simulated grid is.
 *
 * The cost is shown in whatever currency the user picked in the header;
 * changing it only reformats the stored result, it never re-runs the
 * simulation.
 */
public class SimulationPanel extends JPanel {

    private SetupPanel setupPanel;
    private CurrencyConverter currency;
    private GridSimulator simulator = new GridSimulator();
    private DataStore dataStore = new DataStore();
    private SimulationReport lastReport;

    private ChartPanel chart = new ChartPanel();
    private JLabel demandValue = statValue("–");
    private JLabel costValue = statValue("–");
    private JLabel co2Value = statValue("–");
    private JLabel renewableValue = statValue("–");
    private JLabel blackoutValue = statValue("–");
    private JLabel costTitle;
    private JLabel adviceLabel = new JLabel("Run a simulation to get advice.");
    private RoundedPanel adviceBanner = new RoundedPanel(14);

    public SimulationPanel(SetupPanel setupPanel, CurrencyConverter currency) {
        this.setupPanel = setupPanel;
        this.currency = currency;

        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.bg());
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // ---- Top: actions + stat cards ----
        JButton runBtn = Theme.primaryButton("Run 24h Simulation");
        JButton exportBtn = Theme.ghostButton("Export Report");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(runBtn);
        actions.add(exportBtn);

        JPanel stats = new JPanel(new GridLayout(1, 5, 10, 0));
        stats.setOpaque(false);
        stats.add(statCard("Total demand", demandValue));
        JPanel costCard = statCard("Cost (" + currency.getActiveCode() + ")", costValue);
        costTitle = (JLabel) costCard.getComponent(0);
        stats.add(costCard);
        stats.add(statCard("CO2", co2Value));
        stats.add(statCard("Renewable", renewableValue));
        stats.add(statCard("Blackout hours", blackoutValue));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(actions, BorderLayout.NORTH);
        top.add(stats, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // ---- Center: chart card ----
        JPanel chartCard = Theme.card("Hourly supply vs demand");
        chart.setAlignmentX(Component.LEFT_ALIGNMENT);
        chartCard.add(chart);
        add(chartCard, BorderLayout.CENTER);

        // ---- Bottom: advice banner ----
        adviceBanner.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 11));
        adviceLabel.setFont(Theme.BOLD);
        adviceBanner.add(adviceLabel);
        add(adviceBanner, BorderLayout.SOUTH);

        runBtn.addActionListener(e -> runSimulation());
        exportBtn.addActionListener(e -> exportReport());
    }

    private static JLabel statValue(String text) {
        JLabel label = new JLabel(text);
        label.putClientProperty(Theme.ROLE, Theme.ROLE_STAT);
        label.setFont(Theme.STAT);
        label.setForeground(Theme.text());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JPanel statCard(String titleText, JLabel value) {
        JPanel card = Theme.card(titleText);
        card.add(value);
        return card;
    }

    /** Runs the simulator on the current city + fleet and shows results. */
    public void runSimulation() {
        if (setupPanel.getSources().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one energy source in the Grid Setup screen.");
            return;
        }
        City city = new City(setupPanel.getCityName(), setupPanel.getPeakDemand());
        lastReport = simulator.run(city, setupPanel.getSources());
        refreshDisplay();
    }

    /**
     * Re-renders the results of the last run. Called after a simulation
     * and whenever the display currency changes.
     */
    public void refreshDisplay() {
        costTitle.setText(("Cost (" + currency.getActiveCode() + ")").toUpperCase());

        if (lastReport == null) {
            return;
        }
        chart.setRecords(lastReport.getRecords());
        demandValue.setText(String.format("%,.0f MWh", lastReport.getTotalDemandMWh()));
        costValue.setText(currency.format(lastReport.getTotalCostRM()));
        co2Value.setText(String.format("%.1f t", lastReport.getTotalCo2Tonnes()));
        renewableValue.setText(String.format("%.0f%%",
                lastReport.getRenewableShare() * 100));
        blackoutValue.setText(String.valueOf(lastReport.getBlackoutHours()));

        adviceLabel.setText(lastReport.getRecommendation());
        boolean trouble = lastReport.getBlackoutHours() > 0;
        adviceBanner.setFillColor(trouble ? Theme.dangerBg() : Theme.okBg());
        adviceLabel.setForeground(trouble ? Theme.danger() : Theme.accentDark());
    }

    private void exportReport() {
        if (lastReport == null) {
            JOptionPane.showMessageDialog(this, "Run a simulation first.");
            return;
        }
        try {
            dataStore.exportReport(lastReport, "simulation_report.txt", currency);
            JOptionPane.showMessageDialog(this, "Report saved to simulation_report.txt");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }
}
