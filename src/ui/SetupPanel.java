package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import model.HydroPlant;
import model.SolarFarm;
import model.WindTurbine;
import service.DataStore;
import service.PresetLibrary;

/**
 * SetupPanel - the "Grid Setup" screen. The user can load a real-world
 * example grid from the built-in library, adjust the city, and manage
 * the fleet of energy sources. The fleet can also be saved to and
 * loaded from grid_sources.csv.
 */
public class SetupPanel extends JPanel {

    private List<EnergySource> sources = new ArrayList<>();
    private DataStore dataStore = new DataStore();
    private PresetLibrary presets = new PresetLibrary();

    private JComboBox<String> presetBox = new JComboBox<>();
    private JLabel presetInfo = new JLabel(" ");

    private JTextField cityField = Theme.input(new JTextField("Cyberjaya", 12));
    private JTextField peakField = Theme.input(new JTextField("120", 6));

    private JComboBox<String> typeBox = new JComboBox<>(
            new String[] {"Solar Farm", "Wind Turbine", "Hydro Plant", "Gas Plant"});
    private JTextField nameField = Theme.input(new JTextField(11));
    private JTextField capacityField = Theme.input(new JTextField(5));
    private JTextField efficiencyField = Theme.input(new JTextField("0.85", 4));

    private DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Type", "Name", "Capacity (MW)"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private JTable table = new JTable(tableModel);

    public SetupPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.bg());
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // ---- Top row: example library + city settings ----
        JPanel top = new JPanel(new GridLayout(1, 2, 12, 0));
        top.setOpaque(false);

        JPanel presetCard = Theme.card("Real-world examples");
        for (String name : presets.getNames()) {
            presetBox.addItem(name);
        }
        JButton loadPresetBtn = Theme.primaryButton("Load Example");
        presetCard.add(row(presetBox, loadPresetBtn));
        presetInfo.putClientProperty(Theme.ROLE, Theme.ROLE_MUTED);
        presetInfo.setFont(Theme.SMALL);
        presetInfo.setForeground(Theme.muted());
        presetInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        presetCard.add(Box.createVerticalStrut(6));
        presetCard.add(presetInfo);
        top.add(presetCard);

        JPanel cityCard = Theme.card("City");
        cityCard.add(row(new JLabel("Name:"), cityField,
                new JLabel("  Peak demand (MW):"), peakField));
        top.add(cityCard);
        add(top, BorderLayout.NORTH);

        // ---- Center: fleet table ----
        JPanel tableCard = Theme.card("Energy sources");
        Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.border()));
        scroll.getViewport().setBackground(Theme.surface());
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableCard.add(scroll);
        add(tableCard, BorderLayout.CENTER);

        // ---- Bottom: add form + file buttons ----
        JPanel bottomCard = Theme.card("Add a source");
        bottomCard.add(row(new JLabel("Type:"), typeBox,
                new JLabel("  Name:"), nameField,
                new JLabel("  Capacity MW:"), capacityField,
                new JLabel("  Solar efficiency:"), efficiencyField));
        JButton addBtn = Theme.primaryButton("Add Source");
        JButton removeBtn = Theme.ghostButton("Remove Selected");
        JButton saveBtn = Theme.ghostButton("Save to File");
        JButton loadBtn = Theme.ghostButton("Load from File");
        bottomCard.add(Box.createVerticalStrut(8));
        bottomCard.add(row(addBtn, removeBtn, Box.createHorizontalStrut(18),
                saveBtn, loadBtn));
        add(bottomCard, BorderLayout.SOUTH);

        // ---- Actions ----
        loadPresetBtn.addActionListener(e -> loadSelectedPreset());
        presetBox.addActionListener(e -> updatePresetInfo());
        addBtn.addActionListener(e -> addSource());
        removeBtn.addActionListener(e -> removeSelected());
        saveBtn.addActionListener(e -> saveToFile());
        loadBtn.addActionListener(e -> loadFromFile());

        updatePresetInfo();
    }

    private static JPanel row(Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Component comp : comps) {
            p.add(comp);
        }
        return p;
    }

    private void updatePresetInfo() {
        PresetLibrary.Preset p = presets.get((String) presetBox.getSelectedItem());
        if (p != null) {
            presetInfo.setText(p.getDescription());
        }
    }

    /** Loads the currently selected real-world example into the grid. */
    private void loadSelectedPreset() {
        PresetLibrary.Preset p = presets.get((String) presetBox.getSelectedItem());
        if (p == null) {
            return;
        }
        cityField.setText(p.getCityName());
        peakField.setText(String.format("%.0f", p.getPeakDemandMW()));
        sources = p.createSources();
        refreshTable();
    }

    /** Kept for the demo flow: loads the first example (Cyberjaya). */
    public void loadDemoCity() {
        presetBox.setSelectedIndex(0);
        loadSelectedPreset();
    }

    /** Loads a named example, used by the screenshot tool and demos. */
    public void loadPresetAt(int index) {
        if (index >= 0 && index < presetBox.getItemCount()) {
            presetBox.setSelectedIndex(index);
            loadSelectedPreset();
        }
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
            case 2:
                source = new HydroPlant(name, capacity);
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
        int rowIndex = table.getSelectedRow();
        if (rowIndex >= 0) {
            sources.remove(rowIndex);
            refreshTable();
        }
    }

    private void saveToFile() {
        try {
            dataStore.saveSources(sources);
            JOptionPane.showMessageDialog(this, "Saved " + sources.size()
                    + " sources to " + DataStore.SOURCES_FILE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            sources = dataStore.loadSources();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Loaded " + sources.size() + " sources.");
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
