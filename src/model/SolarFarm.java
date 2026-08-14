package model;

/**
 * SolarFarm - produces power only during daylight, peaking at midday.
 * Zero fuel cost impact on climate: no CO2 emitted while generating.
 */
public class SolarFarm extends EnergySource {

    private double efficiency;   // 0..1, panel quality factor

    public SolarFarm(String name, double capacityMW, double efficiency) {
        super(name, capacityMW);
        this.efficiency = efficiency;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        if (efficiency > 0 && efficiency <= 1) {
            this.efficiency = efficiency;
        }
    }

    @Override
    public double getOutputMW(int hour, double windFactor) {
        // Sunlight follows a curve from 07:00 to 19:00, peak at 13:00
        if (hour < 7 || hour > 19) {
            return 0;
        }
        double daylight = Math.sin(Math.PI * (hour - 7) / 12.0);
        return getCapacityMW() * daylight * efficiency;
    }

    @Override
    public double getCostPerMWh() {
        return 40;    // maintenance only - sunlight is free
    }

    @Override
    public double getCo2PerMWh() {
        return 0;
    }

    @Override
    public String getType() {
        return "SOLAR";
    }

    @Override
    public boolean isDispatchable() {
        return false; // cannot order the sun to shine
    }
}
