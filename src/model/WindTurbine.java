package model;

/**
 * WindTurbine - output depends on how strongly the wind blows in a
 * given hour (the simulator supplies a wind factor between 0 and 1).
 */
public class WindTurbine extends EnergySource {

    public WindTurbine(String name, double capacityMW) {
        super(name, capacityMW);
    }

    @Override
    public double getOutputMW(int hour, double windFactor) {
        return getCapacityMW() * windFactor;
    }

    @Override
    public double getCostPerMWh() {
        return 60;    // slightly higher maintenance than solar
    }

    @Override
    public double getCo2PerMWh() {
        return 0;
    }

    @Override
    public String getType() {
        return "WIND";
    }

    @Override
    public boolean isDispatchable() {
        return false; // depends on the weather
    }
}
