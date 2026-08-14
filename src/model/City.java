package model;

/**
 * City - the demand side of the simulation. Electricity demand follows
 * a realistic daily curve: low at night, a morning rise, and the
 * highest peak in the evening when everyone is at home.
 */
public class City {

    private String name;
    private double peakDemandMW;

    /** Demand multiplier for each hour 0-23, as a share of the peak. */
    private static final double[] DAILY_PROFILE = {
        0.55, 0.50, 0.48, 0.47, 0.48, 0.52,   // 00-05 night
        0.62, 0.75, 0.85, 0.90, 0.92, 0.95,   // 06-11 morning
        0.97, 0.95, 0.93, 0.90, 0.92, 0.96,   // 12-17 afternoon
        1.00, 0.98, 0.93, 0.85, 0.72, 0.62    // 18-23 evening peak
    };

    public City(String name, double peakDemandMW) {
        this.name = name;
        this.peakDemandMW = peakDemandMW;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPeakDemandMW() {
        return peakDemandMW;
    }

    public void setPeakDemandMW(double peakDemandMW) {
        if (peakDemandMW > 0) {
            this.peakDemandMW = peakDemandMW;
        }
    }

    /** Demand in MW for the given hour of the day (0-23). */
    public double getDemandMW(int hour) {
        return peakDemandMW * DAILY_PROFILE[hour];
    }
}
