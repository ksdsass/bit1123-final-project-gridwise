# GridWise — City Energy Grid Simulator

**BIT1123 Object Oriented Programming — Final Project (Group, 40%)**
Faculty of Information Technology, City University Malaysia

GridWise is a Java Swing application supporting **SDG 7: Affordable and
Clean Energy**. It simulates 24 hours of a city's electricity grid: the
user builds a fleet of energy sources (solar farms, wind turbines, gas
plants), and the simulator dispatches them **renewables-first** against a
realistic daily demand curve, reporting cost, CO₂ emissions, renewable
share, and blackout risk — with advice on how to improve the energy mix.

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
| 6–8+ classes | 12 classes across 4 packages |
| Encapsulation | Private fields + validated setters throughout `model` |
| Inheritance | `EnergySource` → `SolarFarm`, `WindTurbine`, `GasPlant` |
| Polymorphism | `GridSimulator` calls `getOutputMW()` / `getCostPerMWh()` on `EnergySource` references; each subclass answers differently at runtime |
| Abstraction | `EnergySource` is abstract with 5 abstract methods |
| Collections | `ArrayList<EnergySource>`, `List<HourlyRecord>`, `HashMap<String, Double>` for per-type energy totals |
| File handling | `DataStore` saves/loads the fleet (`grid_sources.csv`) and exports `simulation_report.txt` |
| GUI | Java Swing: tabbed window, forms, `JTable`, custom-painted chart |

## Package structure

```
src/
├── app/
│   └── Main.java             Entry point (starts the Swing UI)
├── model/
│   ├── EnergySource.java     Abstract superclass of all sources
│   ├── SolarFarm.java        Daylight-dependent output, 0 CO2
│   ├── WindTurbine.java      Wind-dependent output, 0 CO2
│   ├── GasPlant.java         Dispatchable backup, costly + CO2
│   ├── City.java             Hourly demand curve
│   └── HourlyRecord.java     One hour of simulation results
├── service/
│   ├── GridSimulator.java    24-hour renewables-first dispatch
│   ├── SimulationReport.java Daily totals, renewable share, advice
│   └── DataStore.java        CSV persistence + report export
└── ui/
    ├── MainWindow.java       Application frame with two tabs
    ├── SetupPanel.java       City + fleet management (JTable, forms)
    ├── SimulationPanel.java  Run button, statistics, export
    └── ChartPanel.java       Custom-painted supply vs demand chart
```

## How to run

```
javac -d out src/app/*.java src/model/*.java src/service/*.java src/ui/*.java
java -cp out app.Main
```

In the app: click **Load Demo City** on the Grid Setup tab, then open the
Simulation tab and click **Run 24h Simulation**. Add or remove sources and
re-run to see how the mix changes cost, emissions and blackout risk.

## Group members

Listed in the project report submitted via CityU LMS.
