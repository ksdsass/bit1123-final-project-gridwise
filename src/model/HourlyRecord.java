package model;

/**
 * HourlyRecord - one row of simulation results: what happened in the
 * grid during a single hour of the simulated day.
 */
public class HourlyRecord {

    private int hour;
    private double demandMW;
    private double solarMW;
    private double windMW;
    private double gasMW;
    private double shortfallMW;   // demand that nobody could supply
    private double costRM;
    private double co2Tonnes;

    public HourlyRecord(int hour, double demandMW, double solarMW,
                        double windMW, double gasMW, double shortfallMW,
                        double costRM, double co2Tonnes) {
        this.hour = hour;
        this.demandMW = demandMW;
        this.solarMW = solarMW;
        this.windMW = windMW;
        this.gasMW = gasMW;
        this.shortfallMW = shortfallMW;
        this.costRM = costRM;
        this.co2Tonnes = co2Tonnes;
    }

    public int getHour()            { return hour; }
    public double getDemandMW()     { return demandMW; }
    public double getSolarMW()      { return solarMW; }
    public double getWindMW()       { return windMW; }
    public double getGasMW()        { return gasMW; }
    public double getShortfallMW()  { return shortfallMW; }
    public double getCostRM()       { return costRM; }
    public double getCo2Tonnes()    { return co2Tonnes; }

    /** Total MW actually supplied this hour. */
    public double getSuppliedMW() {
        return solarMW + windMW + gasMW;
    }

    public boolean isBlackoutHour() {
        return shortfallMW > 0.01;
    }
}
