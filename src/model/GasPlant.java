package model;

/**
 * GasPlant - a dispatchable (controllable) source. The simulator turns
 * it up only to cover whatever demand the renewables cannot supply,
 * because it burns fuel and emits CO2.
 */
public class GasPlant extends EnergySource {

    public GasPlant(String name, double capacityMW) {
        super(name, capacityMW);
    }

    @Override
    public double getOutputMW(int hour, double windFactor) {
        // A gas plant CAN run at full capacity at any hour;
        // the simulator decides how much of it is actually used.
        return getCapacityMW();
    }

    @Override
    public double getCostPerMWh() {
        return 300;   // fuel makes it the most expensive source
    }

    @Override
    public double getCo2PerMWh() {
        return 0.5;   // tonnes of CO2 per MWh generated
    }

    @Override
    public String getType() {
        return "GAS";
    }

    @Override
    public boolean isDispatchable() {
        return true;
    }
}
