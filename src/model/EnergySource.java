package model;

/**
 * EnergySource is the abstract superclass of every power source in the
 * grid (solar farm, wind turbine, gas plant).
 *
 * OOP concepts:
 * - Abstraction  : abstract class + abstract methods define WHAT every
 *                  source must report, never HOW.
 * - Encapsulation: all attributes are private with getters/setters.
 */
public abstract class EnergySource {

    private String name;
    private double capacityMW;   // maximum possible output in megawatts

    public EnergySource(String name, double capacityMW) {
        this.name = name;
        this.capacityMW = capacityMW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCapacityMW() {
        return capacityMW;
    }

    public void setCapacityMW(double capacityMW) {
        // A capacity below zero makes no physical sense
        if (capacityMW >= 0) {
            this.capacityMW = capacityMW;
        }
    }

    /**
     * How many MW this source actually produces at the given hour
     * (0-23). Weather-dependent sources return less than capacity.
     * windFactor (0..1) is the wind strength for that hour.
     */
    public abstract double getOutputMW(int hour, double windFactor);

    /** Generation cost in RM per MWh produced. */
    public abstract double getCostPerMWh();

    /** Emissions in tonnes of CO2 per MWh produced. */
    public abstract double getCo2PerMWh();

    /** Short type label used by the UI, files and reports. */
    public abstract String getType();

    /** True if the source can be turned up on demand (dispatchable). */
    public abstract boolean isDispatchable();

    @Override
    public String toString() {
        return String.format("%s '%s' (%.1f MW)", getType(), name, capacityMW);
    }
}
