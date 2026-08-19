package model;

/**
 * HydroPlant - a dam with a reservoir. Like gas it is dispatchable
 * (water can be released when needed), but it is renewable: cheap to
 * run and emits no CO2. This is why hydro-rich regions such as
 * Sarawak have both clean and reliable grids.
 */
public class HydroPlant extends EnergySource {

    public HydroPlant(String name, double capacityMW) {
        super(name, capacityMW);
    }

    @Override
    public double getOutputMW(int hour, double windFactor) {
        // The reservoir lets a dam run whenever the grid needs it
        return getCapacityMW();
    }

    @Override
    public double getCostPerMWh() {
        return 90;    // dam maintenance; no fuel cost
    }

    @Override
    public double getCo2PerMWh() {
        return 0;
    }

    @Override
    public String getType() {
        return "HYDRO";
    }

    @Override
    public boolean isDispatchable() {
        return true;
    }
}
