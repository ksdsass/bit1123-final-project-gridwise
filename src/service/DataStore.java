package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.EnergySource;
import model.GasPlant;
import model.HourlyRecord;
import model.HydroPlant;
import model.SolarFarm;
import model.WindTurbine;

/**
 * DataStore - file handling for the application.
 *
 * 1. Saves/loads the list of energy sources to grid_sources.csv so the
 *    grid setup survives after the program is closed (persistence).
 * 2. Exports a full simulation report to a readable text file.
 */
public class DataStore {

    public static final String SOURCES_FILE = "grid_sources.csv";

    /** One line per source: TYPE,name,capacity,efficiency */
    public void saveSources(List<EnergySource> sources) throws IOException {
        try (BufferedWriter writer =
                new BufferedWriter(new FileWriter(SOURCES_FILE))) {
            for (EnergySource s : sources) {
                double eff = (s instanceof SolarFarm)
                        ? ((SolarFarm) s).getEfficiency() : 0;
                writer.write(s.getType() + "," + s.getName() + ","
                        + s.getCapacityMW() + "," + eff);
                writer.newLine();
            }
        }
    }

    /** Rebuilds the correct subclass for every line of the file. */
    public List<EnergySource> loadSources() throws IOException {
        List<EnergySource> sources = new ArrayList<>();
        try (BufferedReader reader =
                new BufferedReader(new FileReader(SOURCES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                String type = parts[0];
                String name = parts[1];
                double capacity = Double.parseDouble(parts[2]);
                double efficiency = Double.parseDouble(parts[3]);

                switch (type) {
                    case "SOLAR":
                        sources.add(new SolarFarm(name, capacity, efficiency));
                        break;
                    case "WIND":
                        sources.add(new WindTurbine(name, capacity));
                        break;
                    case "HYDRO":
                        sources.add(new HydroPlant(name, capacity));
                        break;
                    case "GAS":
                        sources.add(new GasPlant(name, capacity));
                        break;
                    default:
                        // ignore unknown lines instead of crashing
                }
            }
        }
        return sources;
    }

    /**
     * Writes the full hourly table + summary to a text file, showing
     * the cost in the currency the user selected.
     */
    public void exportReport(SimulationReport report, String fileName,
                             CurrencyConverter currency) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName))) {
            w.write("GridWise Simulation Report - " + report.getCity().getName());
            w.newLine();
            w.write(String.format("Peak demand: %.1f MW",
                    report.getCity().getPeakDemandMW()));
            w.newLine();
            w.newLine();
            w.write("Hour |  Demand |  Solar |   Wind |  Hydro |    Gas | Shortfall");
            w.newLine();
            w.write("-----+---------+--------+--------+--------+--------+----------");
            w.newLine();
            for (HourlyRecord r : report.getRecords()) {
                w.write(String.format(
                        "%02d:00| %7.1f | %6.1f | %6.1f | %6.1f | %6.1f | %8.1f",
                        r.getHour(), r.getDemandMW(), r.getSolarMW(),
                        r.getWindMW(), r.getHydroMW(), r.getGasMW(),
                        r.getShortfallMW()));
                w.newLine();
            }
            w.newLine();
            w.write("Total cost   : " + currency.formatExact(report.getTotalCostRM())
                    + "  (" + currency.getActiveCode() + ")");
            w.newLine();
            w.write(String.format("Total CO2    : %.1f tonnes", report.getTotalCo2Tonnes()));
            w.newLine();
            w.write(String.format("Renewable    : %.0f%%", report.getRenewableShare() * 100));
            w.newLine();
            w.write("Blackout hrs : " + report.getBlackoutHours());
            w.newLine();
            w.write("Advice       : " + report.getRecommendation());
            w.newLine();
        }
    }
}
