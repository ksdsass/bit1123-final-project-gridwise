package service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.EnergySource;
import model.GasPlant;
import model.HydroPlant;
import model.SolarFarm;
import model.WindTurbine;

/**
 * PresetLibrary - the built-in "database" of example grids inspired by
 * real energy mixes around the world (values are simplified and scaled
 * to city size for the simulation).
 *
 * Each preset stores its source specifications as data rows and builds
 * fresh EnergySource objects on demand, so loading a preset twice never
 * shares mutable objects.
 */
public class PresetLibrary {

    /** One ready-made example grid. */
    public static class Preset {

        private String cityName;
        private double peakDemandMW;
        private String description;
        private List<String[]> sourceSpecs;  // {type, name, capacity, efficiency}

        public Preset(String cityName, double peakDemandMW,
                      String description, List<String[]> sourceSpecs) {
            this.cityName = cityName;
            this.peakDemandMW = peakDemandMW;
            this.description = description;
            this.sourceSpecs = sourceSpecs;
        }

        public String getCityName()      { return cityName; }
        public double getPeakDemandMW()  { return peakDemandMW; }
        public String getDescription()   { return description; }

        /** Builds a brand-new fleet from the stored specifications. */
        public List<EnergySource> createSources() {
            List<EnergySource> sources = new ArrayList<>();
            for (String[] spec : sourceSpecs) {
                String type = spec[0];
                String name = spec[1];
                double capacity = Double.parseDouble(spec[2]);
                double efficiency = Double.parseDouble(spec[3]);
                switch (type) {
                    case "SOLAR":
                        sources.add(new SolarFarm(name, capacity, efficiency));
                        break;
                    case "WIND":
                        sources.add(new WindTurbine(name, capacity));
                        break;
                    case "HYDRO":
                        sources.add(new HydroPlant(name, capacity));
                        break;
                    default:
                        sources.add(new GasPlant(name, capacity));
                }
            }
            return sources;
        }
    }

    // LinkedHashMap keeps the menu order stable
    private Map<String, Preset> presets = new LinkedHashMap<>();

    public PresetLibrary() {

        add("Cyberjaya, Malaysia — Solar Pilot City", new Preset(
            "Cyberjaya", 120,
            "Malaysia's tech city with a growing solar program backed by gas.",
            rows(new String[] {"SOLAR", "Putra Solar Park", "70", "0.85"},
                 new String[] {"SOLAR", "Rooftop Program", "25", "0.80"},
                 new String[] {"WIND", "Sepang Ridge Wind", "40", "0"},
                 new String[] {"GAS", "Serdang Backup Gas", "60", "0"})));

        add("Kuching, Malaysia — Hydro Powerhouse", new Preset(
            "Kuching", 150,
            "Sarawak's grid runs mainly on large dams like Bakun - clean AND reliable.",
            rows(new String[] {"HYDRO", "Bakun Dam", "130", "0"},
                 new String[] {"HYDRO", "Batang Ai Dam", "40", "0"},
                 new String[] {"SOLAR", "Rooftop Solar", "15", "0.80"},
                 new String[] {"GAS", "Peaking Gas", "25", "0"})));

        add("Copenhagen, Denmark — Wind Capital", new Preset(
            "Copenhagen", 180,
            "Denmark gets over half its power from wind, led by offshore farms.",
            rows(new String[] {"WIND", "Middelgrunden Offshore", "120", "0"},
                 new String[] {"WIND", "Amager Turbines", "60", "0"},
                 new String[] {"SOLAR", "Rooftop Solar", "20", "0.75"},
                 new String[] {"GAS", "Avedore Backup", "80", "0"})));

        add("Dubai, UAE — Desert Solar Giant", new Preset(
            "Dubai", 200,
            "Home of the huge Al Maktoum solar park, with gas covering the night.",
            rows(new String[] {"SOLAR", "Al Maktoum Solar Park", "160", "0.90"},
                 new String[] {"GAS", "Jebel Ali Gas", "130", "0"})));

        add("Tokyo District, Japan — Gas Reliant", new Preset(
            "Tokyo Bay District", 220,
            "A dense district still dominated by gas, with rooftop solar growing.",
            rows(new String[] {"GAS", "Bayside Gas Complex", "180", "0"},
                 new String[] {"SOLAR", "Rooftop Program", "40", "0.80"},
                 new String[] {"WIND", "Bay Wind Pilot", "20", "0"})));

        add("Munich, Germany — Balanced Energiewende", new Preset(
            "Munich", 160,
            "Germany's energy transition: solar, wind, hydro and gas in balance.",
            rows(new String[] {"SOLAR", "Bavarian Solar Fields", "60", "0.80"},
                 new String[] {"WIND", "Northern Wind Link", "70", "0"},
                 new String[] {"HYDRO", "Isar River Hydro", "20", "0"},
                 new String[] {"GAS", "City Gas Plant", "70", "0"})));
    }

    private void add(String label, Preset preset) {
        presets.put(label, preset);
    }

    private static List<String[]> rows(String[]... specs) {
        List<String[]> list = new ArrayList<>();
        for (String[] spec : specs) {
            list.add(spec);
        }
        return list;
    }

    public String[] getNames() {
        return presets.keySet().toArray(new String[0]);
    }

    public Preset get(String name) {
        return presets.get(name);
    }
}
