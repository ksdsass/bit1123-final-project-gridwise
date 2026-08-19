# GridWise — City Energy Grid Simulator

**BIT1123 Object Oriented Programming — Final Project (Group, 40%)**
Faculty of Information Technology, City University Malaysia

GridWise is a Java Swing application supporting **SDG 7: Affordable and
Clean Energy**. It simulates 24 hours of a city's electricity grid: the
user builds a fleet of energy sources (solar farms, wind turbines, gas
plants), and the simulator dispatches them **renewables-first** against a
realistic daily demand curve, reporting cost, CO₂ emissions, renewable
share, and blackout risk — with advice on how to improve the energy mix.

## Features

* **Built-in library of real-world examples** — six grids inspired by
  actual energy mixes: Cyberjaya (solar pilot), Kuching (Sarawak hydro),
  Copenhagen (wind), Dubai (desert solar), Tokyo (gas-reliant) and
  Munich (balanced Energiewende).
* **Four source types** — solar, wind, hydro and gas, each with its own
  output behaviour, cost and emissions.
* **Light and dark mode** — switchable at any time from the header.
* **Multi-currency display** — read the generation cost in MYR, USD,
  EUR, GBP, SGD, AUD, CNY, JPY or INR.
* **Custom-drawn logo and chart** — pure Java 2D, no image assets.
* **File persistence** — save/load the fleet, export the full report.

## Why it is a simulation, not CRUD

Every run models real grid behaviour:

* Demand follows a daily curve (night low, morning ramp, evening peak).
* Solar output follows the sun (zero at night, peak at 13:00, scaled by
  panel efficiency); wind varies randomly hour to hour.
* Renewables are dispatched first; gas plants only fill the remaining gap;
  anything still missing is recorded as a blackout hour.
* The system then advises the user (e.g. "add solar capacity") based on
  renewable share and shortfalls — decision-making logic, not data entry.

## OOP requirements map

| Requirement | Where |
| ----------- | ----- |
| 6–8+ classes | 16 classes across 4 packages |
| Encapsulation | Private fields + validated setters throughout `model` |
| Inheritance | `EnergySource` → `SolarFarm`, `WindTurbine`, `HydroPlant`, `GasPlant`; UI classes extend `JFrame` / `JPanel` |
| Polymorphism | `GridSimulator` calls `getOutputMW()` / `getCostPerMWh()` on `EnergySource` references; each subclass answers differently at runtime |
| Abstraction | `EnergySource` is abstract with 5 abstract methods |
| Collections | `ArrayList<EnergySource>`, `List<HourlyRecord>`, `HashMap<String, Double>` for per-type totals, `LinkedHashMap` for the example library and currency table |
| File handling | `DataStore` saves/loads the fleet (`grid_sources.csv`) and exports `simulation_report.txt` |
| GUI | Java Swing: card navigation, forms, `JTable`, custom-painted chart and logo, light/dark theming |

## Package structure

```
src/
├── app/
│   └── Main.java             Entry point (starts the Swing UI)
├── model/
│   ├── EnergySource.java     Abstract superclass of all sources
│   ├── SolarFarm.java        Daylight-dependent output, 0 CO2
│   ├── WindTurbine.java      Wind-dependent output, 0 CO2
│   ├── HydroPlant.java       Dispatchable AND clean (reservoir)
│   ├── GasPlant.java         Dispatchable backup, costly + CO2
│   ├── City.java             Hourly demand curve
│   └── HourlyRecord.java     One hour of simulation results
├── service/
│   ├── GridSimulator.java    24-hour merit-order dispatch
│   ├── SimulationReport.java Daily totals, renewable share, advice
│   ├── PresetLibrary.java    Built-in real-world example grids
│   ├── CurrencyConverter.java Multi-currency cost display
│   └── DataStore.java        CSV persistence + report export
└── ui/
    ├── MainWindow.java       Frame, header, navigation, settings
    ├── Theme.java            Light/dark palettes and components
    ├── AppLogo.java          Java 2D logo mark and window icon
    ├── SetupPanel.java       City + fleet management (JTable, forms)
    ├── SimulationPanel.java  Run button, stat cards, export
    └── ChartPanel.java       Custom-painted supply vs demand chart
```

## How to run

```
javac -d out src/app/*.java src/model/*.java src/service/*.java src/ui/*.java
java -cp out app.Main
```

In the app: pick a real-world example on the **Grid Setup** screen and
click **Load Example**, then open **Simulation** and click **Run 24h
Simulation**. Add or remove sources and re-run to see how the mix changes
cost, emissions and blackout risk. Use the header controls to switch
currency or toggle dark mode at any time.

## Group members

Listed in the project report submitted via CityU LMS.
