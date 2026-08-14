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
 *   2. collects output from every renewable source FIRST
 *      (runtime polymorphism: one getOutputMW() call, each subclass
 *      answers with its own formula),
 *   3. turns up dispatchable (gas) plants only for what is missing,
 *   4. records any remaining shortfall as a blackout risk,
 *   5. accumulates cost and CO2 emissions.
 *
 * Renewables-first dispatch is the SDG 7 idea in code: the cheapest,
 * cleanest energy is always used before fossil fuel.
 */
public class GridSimulator {

    private Random random = new Random();

    public SimulationReport run(City city, List<EnergySource> sources) {

        List<HourlyRecord> records = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {

            double demand = city.getDemandMW(hour);

            // Wind strength for this hour: a base breeze plus random gusts
            double windFactor = 0.25 + 0.5 * random.nextDouble();

            double solar = 0, wind = 0, gasAvailable = 0;
            double cost = 0, co2 = 0;

            // --- Step 1: renewables always generate first ---
            for (EnergySource source : sources) {
                if (source.isDispatchable()) {
                    gasAvailable += source.getCapacityMW();
                    continue;
                }
                // Polymorphic call - SolarFarm and WindTurbine each
                // calculate output their own way
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

            // --- Step 2: gas covers only the remaining gap ---
            double gap = demand - renewable;
            double gasUsed = Math.min(gap, gasAvailable);
            for (EnergySource source : sources) {
                if (source.isDispatchable() && gasAvailable > 0) {
                    double share = gasUsed * (source.getCapacityMW() / gasAvailable);
                    cost += share * source.getCostPerMWh();
                    co2 += share * source.getCo2PerMWh();
                }
            }

            // --- Step 3: whatever is still missing is a shortfall ---
            double shortfall = Math.max(0, gap - gasUsed);

            records.add(new HourlyRecord(hour, demand, solar, wind,
                    gasUsed, shortfall, cost, co2));
        }

        return new SimulationReport(city, records);
    }
}
