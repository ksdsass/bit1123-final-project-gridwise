package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.EnergySource;
import model.GasPlant;
import model.SolarFarm;
import model.WindTurbine;
import service.DataStore;

/**
 * SetupPanel - the "Grid Setup" tab. The user names the city, sets its
 * peak demand, and builds the fleet of energy sources shown in a table.
 * The fleet can be saved to / loaded from grid_sources.csv.
 */
public class SetupPanel extends JPanel {

    private List<EnergySource> sources = new ArrayList<>();
    private DataStore dataStore = new DataStore();

    private JTextField cityField = new JTextField("Cyberjaya", 10);
    private JTextField peakField = new JTextField("120", 6);

    private JComboBox<String> typeBox =
            new JComboBox<>(new String[] {"Solar Farm", "Wind Turbine", "Gas Plant"});
    private JTextField nameField = new JTextField(10);
    private JTextField capacityField = new JTextField(5);
    private JTextField efficiencyField = new JTextField("0.85", 4);

    private DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Type", "Name", "Capacity (MW)"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private JTable table = new JTable(tableModel);

    public SetupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- City settings (top) ---
        JPanel cityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cityPanel.setBorder(BorderFactory.createTitledBorder("City"));
        cityPanel.add(new JLabel("City name:"));
        cityPanel.add(cityField);
        cityPanel.add(new JLabel("Peak demand (MW):"));
        cityPanel.add(peakField);
        add(cityPanel, BorderLayout.NORTH);

        // --- Source table (center) ---
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Energy Sources"));
        add(scroll, BorderLayout.CENTER);

        // --- Add form + buttons (bottom) ---
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Type:"));
        form.add(typeBox);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Capacity MW:"));
        form.add(capacityField);
        form.add(new JLabel("Solar efficiency:"));
        form.add(efficiencyField);

        JButton addBtn = new JButton("Add Source");
        JButton removeBtn = new JButton("Remove Selected");
        JButton demoBtn = new JButton("Load Demo City");
        JButton saveBtn = new JButton("Save to File");
        JButton loadBtn = new JButton("Load from File");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addBtn);
        buttons.add(removeBtn);
        buttons.add(demoBtn);
        buttons.add(saveBtn);
        buttons.add(loadBtn);

        JPanel south = new JPanel(new GridLayout(2, 1));
        south.add(form);
        south.add(buttons);
        add(south, BorderLayout.SOUTH);

        // --- Button actions (lambdas implement ActionListener) ---
        addBtn.addActionListener(e -> addSource());
        removeBtn.addActionListener(e -> removeSelected());
        demoBtn.addActionListener(e -> loadDemoCity());
        saveBtn.addActionListener(e -> saveToFile());
        loadBtn.addActionListener(e -> loadFromFile());
    }

    private void addSource() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a source name.");
            return;
        }
        double capacity;
        try {
            capacity = Double.parseDouble(capacityField.getText().trim());
            if (capacity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity must be a positive number.");
            return;
        }

        EnergySource source;
        switch (typeBox.getSelectedIndex()) {
            case 0:
                double eff;
                try {
                    eff = Double.parseDouble(efficiencyField.getText().trim());
                } catch (NumberFormatException ex) {
                    eff = 0.85;
                }
                source = new SolarFarm(name, capacity, eff);
                break;
            case 1:
                source = new WindTurbine(name, capacity);
                break;
            default:
                source = new GasPlant(name, capacity);
        }
        sources.add(source);
        refreshTable();
        nameField.setText("");
        capacityField.setText("");
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            sources.remove(row);
            refreshTable();
        }
    }

    /** Fills the grid with a ready-made example - handy for the demo. */
    public void loadDemoCity() {
        cityField.setText("Cyberjaya");
        peakField.setText("120");
        sources.clear();
        sources.add(new SolarFarm("Putra Solar Park", 70, 0.85));
        sources.add(new SolarFarm("Rooftop Program", 25, 0.80));
        sources.add(new WindTurbine("Sepang Ridge Wind", 40));
        sources.add(new GasPlant("Serdang Backup Gas", 60));
        refreshTable();
    }

    private void saveToFile() {
        try {
            dataStore.saveSources(sources);
            JOptionPane.showMessageDialog(this,
                    "Saved " + sources.size() + " sources to "
                    + DataStore.SOURCES_FILE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            sources = dataStore.loadSources();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                    "Loaded " + sources.size() + " sources.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (EnergySource s : sources) {
            tableModel.addRow(new Object[] {
                s.getType(), s.getName(),
                String.format("%.1f", s.getCapacityMW())
            });
        }
    }

    // Used by the simulation tab
    public List<EnergySource> getSources() {
        return sources;
    }

    public String getCityName() {
        return cityField.getText().trim();
    }

    public double getPeakDemand() {
        try {
            return Double.parseDouble(peakField.getText().trim());
        } catch (NumberFormatException ex) {
            return 120;
        }
    }
}
