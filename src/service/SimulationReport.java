package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.City;
import model.HourlyRecord;

/**
 * SimulationReport - aggregates the 24 hourly records into the daily
 * statistics shown in the GUI and written to the report file.
 *
 * Uses a HashMap to total the energy supplied by each source type.
 */
public class SimulationReport {

    private City city;
    private List<HourlyRecord> records;
    private Map<String, Double> energyByType = new HashMap<>();

    private double totalDemand = 0;
    private double totalCost = 0;
    private double totalCo2 = 0;
    private int blackoutHours = 0;

    public SimulationReport(City city, List<HourlyRecord> records) {
        this.city = city;
        this.records = records;

        for (HourlyRecord r : records) {
            totalDemand += r.getDemandMW();
            totalCost += r.getCostRM();
            totalCo2 += r.getCo2Tonnes();
            if (r.isBlackoutHour()) {
                blackoutHours++;
            }
            // merge() adds to the existing total for each type
            energyByType.merge("SOLAR", r.getSolarMW(), Double::sum);
            energyByType.merge("WIND", r.getWindMW(), Double::sum);
            energyByType.merge("GAS", r.getGasMW(), Double::sum);
        }
    }

    public City getCity()                       { return city; }
    public List<HourlyRecord> getRecords()      { return records; }
    public Map<String, Double> getEnergyByType(){ return energyByType; }
    public double getTotalDemandMWh()           { return totalDemand; }
    public double getTotalCostRM()              { return totalCost; }
    public double getTotalCo2Tonnes()           { return totalCo2; }
    public int getBlackoutHours()               { return blackoutHours; }

    /** Share of consumed energy that came from renewable sources. */
    public double getRenewableShare() {
        double renewable = energyByType.getOrDefault("SOLAR", 0.0)
                         + energyByType.getOrDefault("WIND", 0.0);
        double supplied = renewable + energyByType.getOrDefault("GAS", 0.0);
        return supplied == 0 ? 0 : renewable / supplied;
    }

    /** A simple advisory message - the "decision making" of the system. */
    public String getRecommendation() {
        if (blackoutHours > 0) {
            return "WARNING: " + blackoutHours + " hour(s) of supply shortfall. "
                 + "Add more capacity (solar + storage or gas backup).";
        }
        double share = getRenewableShare();
        if (share < 0.4) {
            return "Renewable share is only " + Math.round(share * 100)
                 + "%. Consider adding solar or wind capacity (SDG 7 target).";
        }
        if (share < 0.7) {
            return "Good mix (" + Math.round(share * 100)
                 + "% renewable). Adding solar would cut costs and CO2 further.";
        }
        return "Excellent! " + Math.round(share * 100)
             + "% of the city's energy is renewable and supply is secure.";
    }
}
