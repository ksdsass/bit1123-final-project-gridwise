package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.City;
import model.EnergySource;
import model.HourlyRecord;

/**
 * GridSimulator - the heart of the application.
 *
 * For every hour of a simulated day it:
 *   1. asks the City how much power is needed,
 *   2. collects output from every weather-driven renewable FIRST
 *      (runtime polymorphism: one getOutputMW() call, each subclass
 *      answers with its own formula),
 *   3. releases hydro for the remaining gap (dispatchable AND clean),
 *   4. turns up gas plants only for what is still missing,
 *   5. records any remaining shortfall as a blackout risk,
 *   6. accumulates cost and CO2 emissions.
 *
 * The merit order (solar/wind -> hydro -> gas) is the SDG 7 idea in
 * code: the cleanest, cheapest energy is always used first.
 */
public class GridSimulator {

    private Random random = new Random();

    public SimulationReport run(City city, List<EnergySource> sources) {

        List<HourlyRecord> records = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {

            double demand = city.getDemandMW(hour);

            // Wind strength for this hour: a base breeze plus random gusts
            double windFactor = 0.25 + 0.5 * random.nextDouble();

            double solar = 0, wind = 0;
            double hydroAvailable = 0, gasAvailable = 0;
            double cost = 0, co2 = 0;

            // --- Step 1: weather-driven renewables generate first ---
            for (EnergySource source : sources) {
                if (source.isDispatchable()) {
                    if (source.getType().equals("HYDRO")) {
                        hydroAvailable += source.getCapacityMW();
                    } else {
                        gasAvailable += source.getCapacityMW();
                    }
                    continue;
                }
                // Polymorphic call - each subclass has its own formula
                double output = source.getOutputMW(hour, windFactor);
                if (source.getType().equals("SOLAR")) {
                    solar += output;
                } else {
                    wind += output;
                }
                cost += output * source.getCostPerMWh();
                co2 += output * source.getCo2PerMWh();
            }

            double renewable = solar + wind;

            // Renewables above demand are curtailed (not used, not paid)
            if (renewable > demand) {
                double scale = demand / renewable;
                solar *= scale;
                wind *= scale;
                cost = solar * 40 + wind * 60;
                renewable = demand;
            }

            // --- Step 2: hydro (clean + dispatchable) covers the gap ---
            double gap = demand - renewable;
            double hydroUsed = Math.min(gap, hydroAvailable);
            cost += dispatchCost(sources, "HYDRO", hydroUsed, hydroAvailable);
            co2 += dispatchCo2(sources, "HYDRO", hydroUsed, hydroAvailable);
            gap -= hydroUsed;

            // --- Step 3: gas is the last resort ---
            double gasUsed = Math.min(gap, gasAvailable);
            cost += dispatchCost(sources, "GAS", gasUsed, gasAvailable);
            co2 += dispatchCo2(sources, "GAS", gasUsed, gasAvailable);
            gap -= gasUsed;

            // --- Step 4: whatever is still missing is a shortfall ---
            double shortfall = Math.max(0, gap);

            records.add(new HourlyRecord(hour, demand, solar, wind,
                    hydroUsed, gasUsed, shortfall, cost, co2));
        }

        return new SimulationReport(city, records);
    }

    /** Cost of the used dispatchable energy, shared by capacity. */
    private double dispatchCost(List<EnergySource> sources, String type,
                                double used, double available) {
        if (used <= 0 || available <= 0) {
            return 0;
        }
        double cost = 0;
        for (EnergySource s : sources) {
            if (s.isDispatchable() && s.getType().equals(type)) {
                cost += used * (s.getCapacityMW() / available) * s.getCostPerMWh();
            }
        }
        return cost;
    }

    /** CO2 of the used dispatchable energy, shared by capacity. */
    private double dispatchCo2(List<EnergySource> sources, String type,
                               double used, double available) {
        if (used <= 0 || available <= 0) {
            return 0;
        }
        double co2 = 0;
        for (EnergySource s : sources) {
            if (s.isDispatchable() && s.getType().equals(type)) {
                co2 += used * (s.getCapacityMW() / available) * s.getCo2PerMWh();
            }
        }
        return co2;
    }
}
