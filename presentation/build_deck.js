/*
 * build_deck.js - generates GridWise_Presentation.pptx
 *
 * Run:  cd presentation && npm install && node build_deck.js
 *
 * To put the real names on the slides, edit MEMBERS below and re-run.
 */

const pptxgen = require("pptxgenjs");

// ---------------------------------------------------------------------------
// EDIT THIS: replace the placeholders with the four group members' names.
// The order decides who is credited on which slides.
// ---------------------------------------------------------------------------
const MEMBERS = [
  "Member 1",   // problem, objectives, overview
  "Member 2",   // architecture, OOP, Grid Setup demo
  "Member 3",   // dispatch algorithm, simulation demo, results
  "Member 4",   // features demo, SDG 7, conclusions
];

// --- palette: night grid (navy) + renewables (teal) + sunlight (amber) ------
const NAVY  = "0B2545";
const TEAL  = "1B998B";
const AMBER = "F4A300";
const SLATE = "5A6B7C";
const TINT  = "EEF2F6";
const WHITE = "FFFFFF";
const FONT  = "Calibri";

const M = 0.6;              // page margin
const W = 13.333;           // slide width
const CW = W - 2 * M;       // content width

const pres = new pptxgen();
pres.layout = "LAYOUT_WIDE";
pres.author = "GridWise Group";
pres.title = "GridWise - City Energy Grid Simulator";

// ---------------------------------------------------------------------------
// helpers
// ---------------------------------------------------------------------------

/** White content slide with an action title and a presenter tag. */
function contentSlide(title, presenterIndex, timing) {
  const s = pres.addSlide();
  s.background = { color: WHITE };
  s.addText(title, {
    x: M, y: 0.38, w: CW - 2.5, h: 0.9,
    fontFace: FONT, fontSize: 26, bold: true, color: NAVY,
    align: "left", valign: "top", margin: 0,
  });
  s.addText(`${MEMBERS[presenterIndex]}  ·  ${timing}`, {
    x: W - M - 2.4, y: 0.42, w: 2.4, h: 0.3,
    fontFace: FONT, fontSize: 11, color: SLATE, align: "right", margin: 0,
  });
  return s;
}

/** Rounded tinted card. */
function card(s, x, y, w, h, fill) {
  s.addShape(pres.ShapeType.roundRect, {
    x, y, w, h, rectRadius: 0.08,
    fill: { color: fill || TINT }, line: { color: fill || TINT },
  });
}

/** Big number + caption, used for the result comparison. */
function stat(s, x, y, w, value, label, color) {
  s.addText(value, {
    x, y, w, h: 0.72,
    fontFace: FONT, fontSize: 40, bold: true, color, align: "left", margin: 0,
  });
  s.addText(label, {
    x, y: y + 0.7, w, h: 0.32,
    fontFace: FONT, fontSize: 13, color: SLATE, align: "left", margin: 0,
  });
}

/** Bulleted body text block. */
function bullets(s, x, y, w, h, items, size) {
  s.addText(
    items.map((t, i) => ({
      text: t,
      options: { bullet: true, breakLine: i !== items.length - 1 },
    })),
    {
      x, y, w, h,
      fontFace: FONT, fontSize: size || 17, color: "23303C",
      paraSpaceAfter: 10, valign: "top", margin: 0,
    }
  );
}

// ===========================================================================
// 1. Title
// ===========================================================================
{
  const s = pres.addSlide();
  s.background = { color: NAVY };

  s.addText("GridWise", {
    x: M, y: 1.55, w: CW, h: 1.1,
    fontFace: FONT, fontSize: 60, bold: true, color: WHITE, margin: 0,
  });
  s.addText("A city energy grid simulator for SDG 7: Affordable and Clean Energy", {
    x: M, y: 2.7, w: CW - 1.5, h: 0.5,
    fontFace: FONT, fontSize: 22, color: AMBER, margin: 0,
  });
  s.addText(
    "BIT1123 Object Oriented Programming  ·  Final Group Project (40%)\n" +
    "Faculty of Information Technology, City University Malaysia",
    {
      x: M, y: 3.45, w: CW, h: 0.8,
      fontFace: FONT, fontSize: 15, color: "C7D3DF", lineSpacing: 22, margin: 0,
    }
  );
  s.addText("Presented by " + MEMBERS.join("  ·  "), {
    x: M, y: 5.35, w: CW, h: 0.4,
    fontFace: FONT, fontSize: 15, bold: true, color: WHITE, margin: 0,
  });
  s.addText("10-minute presentation  ·  live demo  ·  Q&A", {
    x: M, y: 5.78, w: CW, h: 0.4,
    fontFace: FONT, fontSize: 13, color: "8FA3B5", margin: 0,
  });
  s.addNotes(
    "ALL FOUR MEMBERS on stage. " + MEMBERS[0] + " opens.\n" +
    "0:00-0:20 - Good morning. We are group [NAME]. Our project is GridWise, " +
    "a Java Swing simulator that lets a city test its electricity mix for one full day " +
    "before spending a ringgit on it. It supports UN Sustainable Development Goal 7.\n" +
    "Introduce the four members by name in one sentence, then go straight to the problem."
  );
}

// ===========================================================================
// 2. Problem statement
// ===========================================================================
{
  const s = contentSlide(
    "Cities are told to go renewable - not what it costs, or when the lights go out.",
    0, "0:20 - 1:00"
  );

  bullets(s, M, 2.0, 6.9, 3.6, [
    "Electricity generation is the largest single source of a city's carbon emissions.",
    "Solar and wind are not dispatchable - output depends on the hour and the weather.",
    "The cruel timing: the sun sets at 19:00, exactly as demand hits its evening peak.",
    "Planners must weigh cost, CO₂ and blackout risk together, not one at a time.",
  ]);

  card(s, 7.9, 2.0, 4.85, 2.7, TINT);
  s.addText("The gap", {
    x: 8.2, y: 2.2, w: 4.3, h: 0.35,
    fontFace: FONT, fontSize: 14, bold: true, color: TEAL, margin: 0,
  });
  s.addText(
    "There is no simple, hands-on tool for a student or a planner to build an " +
    "energy mix and watch it succeed or fail hour by hour.",
    {
      x: 8.2, y: 2.6, w: 4.3, h: 1.9,
      fontFace: FONT, fontSize: 17, color: NAVY, lineSpacing: 24, margin: 0,
    }
  );

  s.addText(
    "SDG 7 target 7.2: substantially increase the share of renewable energy in the global energy mix.",
    {
      x: M, y: 6.2, w: CW, h: 0.35,
      fontFace: FONT, fontSize: 12, color: SLATE, italic: true, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[0] + " - 40 seconds. Do NOT read the bullets.\n" +
    "Say: every city is under pressure to add renewables. The problem is that solar " +
    "and wind cannot be switched on when you need them. Point at bullet 3 - that is the " +
    "heart of it: the sun sets at 7pm, and 7pm is exactly when everybody gets home and " +
    "switches everything on. So a plan that looks green on paper can still black out.\n" +
    "Land on the grey box: nobody has a cheap way to test that. That is the gap GridWise fills."
  );
}

// ===========================================================================
// 3. Objectives
// ===========================================================================
{
  const s = contentSlide("We set five objectives - four for the city, one for the syllabus.", 0, "1:00 - 1:35");

  const objectives = [
    ["1", "Model reality", "A 24-hour demand curve and four source types that each behave differently."],
    ["2", "Dispatch cleanly", "Renewables first, hydro next, gas only as the last resort."],
    ["3", "Measure it", "Report cost, CO₂, renewable share and blackout hours for every run."],
    ["4", "Advise, not store", "Tell the user how to improve the mix - decision logic, not data entry."],
    ["5", "Prove the syllabus", "Abstraction, inheritance, polymorphism, encapsulation, collections, files, GUI."],
  ];

  let y = 1.9;
  objectives.forEach(([n, head, body]) => {
    s.addText(n, {
      x: M, y, w: 0.45, h: 0.42,
      fontFace: FONT, fontSize: 22, bold: true, color: AMBER, align: "left", margin: 0,
    });
    s.addText(head, {
      x: M + 0.5, y, w: 2.9, h: 0.42,
      fontFace: FONT, fontSize: 18, bold: true, color: NAVY, valign: "top", margin: 0,
    });
    s.addText(body, {
      x: M + 3.6, y, w: CW - 3.6, h: 0.5,
      fontFace: FONT, fontSize: 17, color: "23303C", valign: "top", margin: 0,
    });
    y += 0.95;
  });

  s.addNotes(
    MEMBERS[0] + " - 35 seconds. Read only the five bold headings, then add one clause each.\n" +
    "Emphasise objective 4: this is what makes GridWise a simulation and not a CRUD app - " +
    "the system makes a judgement about your grid and tells you what to change.\n" +
    "Hand over: '" + MEMBERS[1] + " will show you how we built it.'"
  );
}

// ===========================================================================
// 4. How it works
// ===========================================================================
{
  const s = contentSlide("The user builds a fleet, GridWise runs 24 hours, the report gives a verdict.", 0, "1:35 - 2:10");

  const steps = [
    ["BUILD", TEAL, [
      "Name the city, set its peak demand",
      "Add solar, wind, hydro or gas units",
      "Or load one of six real-world example grids",
    ]],
    ["SIMULATE", AMBER, [
      "24 hourly steps, one full day",
      "Demand curve + random hourly wind",
      "Merit order: clean and cheap runs first",
    ]],
    ["DECIDE", NAVY, [
      "Cost, CO₂, renewable %, blackout hours",
      "Hour-by-hour supply vs demand chart",
      "A written recommendation, and export",
    ]],
  ];

  let x = M;
  const cw = 3.98, gap = 0.4;
  steps.forEach(([title, color, items], i) => {
    card(s, x, 1.8, cw, 3.55, TINT);
    s.addText(String(i + 1), {
      x: x + 0.3, y: 2.0, w: 0.6, h: 0.5,
      fontFace: FONT, fontSize: 26, bold: true, color, margin: 0,
    });
    s.addText(title, {
      x: x + 0.85, y: 2.06, w: cw - 1.1, h: 0.45,
      fontFace: FONT, fontSize: 20, bold: true, color: NAVY, valign: "middle", margin: 0,
    });
    bullets(s, x + 0.3, 2.75, cw - 0.6, 2.4, items, 15);
    x += cw + gap;
  });

  s.addText(
    "Nothing is stored and echoed back: every number on the report is computed by the simulator from the fleet you built.",
    {
      x: M, y: 5.6, w: CW, h: 0.4,
      fontFace: FONT, fontSize: 15, color: NAVY, italic: true, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[0] + " - 35 seconds. Walk the three cards left to right in one sentence each.\n" +
    "Finish on the italic line - if the lecturer asks 'is this just CRUD?', the answer is " +
    "already on this slide and " + MEMBERS[2] + " will prove it with the algorithm.\n" +
    "Hand over to " + MEMBERS[1] + "."
  );
}

// ===========================================================================
// 5. Architecture
// ===========================================================================
{
  const s = contentSlide("Four packages keep the model, the logic and the screen independent.", 1, "2:10 - 2:50");

  const layers = [
    ["app", "1 class", "Main - starts the Swing event thread", NAVY],
    ["ui", "8 classes", "MainWindow, SetupPanel, SimulationPanel, ChartPanel, Theme, AppLogo, RoundedPanel, CenteredPanel", TEAL],
    ["service", "7 classes", "GridSimulator, SimulationReport, PresetLibrary, CurrencyConverter, DataStore, plus the nested Preset and Currency", AMBER],
    ["model", "7 classes", "EnergySource, SolarFarm, WindTurbine, HydroPlant, GasPlant, City, HourlyRecord", NAVY],
  ];

  let y = 1.8;
  layers.forEach(([name, count, contents, color]) => {
    card(s, M, y, 8.6, 0.98, TINT);
    s.addText(name, {
      x: M + 0.3, y: y + 0.1, w: 1.5, h: 0.4,
      fontFace: FONT, fontSize: 19, bold: true, color, margin: 0,
    });
    s.addText(count, {
      x: M + 0.3, y: y + 0.52, w: 1.5, h: 0.3,
      fontFace: FONT, fontSize: 12, color: SLATE, margin: 0,
    });
    s.addText(contents, {
      x: M + 1.9, y: y + 0.14, w: 6.5, h: 0.7,
      fontFace: FONT, fontSize: 13, color: "23303C", valign: "middle", lineSpacing: 16, margin: 0,
    });
    y += 1.14;
  });

  s.addText("23", {
    x: 9.55, y: 1.9, w: 3.2, h: 0.9,
    fontFace: FONT, fontSize: 54, bold: true, color: TEAL, margin: 0,
  });
  s.addText("classes in 21 source files\n(the syllabus asks for 6-8)", {
    x: 9.55, y: 2.8, w: 3.2, h: 0.7,
    fontFace: FONT, fontSize: 14, color: SLATE, lineSpacing: 18, margin: 0,
  });
  s.addText(
    "Dependencies only point one way: ui and service depend on model, and model " +
    "depends on nothing. That is why we can run the whole simulation from a plain " +
    "main method, with no window open, to check our numbers.",
    {
      x: 9.55, y: 3.7, w: 3.2, h: 2.2,
      fontFace: FONT, fontSize: 14, color: NAVY, lineSpacing: 19, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[1] + " - 40 seconds.\n" +
    "Say: read the stack bottom-up. model knows physics and nothing else. service holds " +
    "the algorithms. ui only paints. Because model has zero dependencies we could run the " +
    "whole 24-hour simulation from a plain main method with no GUI - and we did, to check our numbers.\n" +
    "Do not read the class lists aloud, just point at them."
  );
}

// ===========================================================================
// 6. OOP requirements
// ===========================================================================
{
  const s = contentSlide("Every OOP requirement is met by code we can point at.", 1, "2:50 - 3:30");

  const rows = [
    ["Abstraction", "EnergySource is abstract with 5 abstract methods - it says what every source must report, never how."],
    ["Inheritance", "SolarFarm, WindTurbine, HydroPlant, GasPlant extend EnergySource; panels extend JPanel."],
    ["Polymorphism", "GridSimulator calls getOutputMW() on an EnergySource reference - each subclass answers with its own physics."],
    ["Encapsulation", "Private fields with validated setters: a capacity below zero or an efficiency above 1 is rejected."],
    ["Collections", "ArrayList<EnergySource>, List<HourlyRecord>, HashMap for per-type totals, LinkedHashMap for menu order."],
    ["File handling", "DataStore writes grid_sources.csv and rebuilds the correct subclass on load; exports the report as text."],
    ["GUI", "Java Swing: CardLayout navigation, JTable fleet, forms, and a chart painted with Java 2D."],
  ];

  let y = 1.72;
  rows.forEach(([k, v], i) => {
    if (i % 2 === 0) card(s, M, y - 0.06, CW, 0.68, "F6F9FB");
    s.addText(k, {
      x: M + 0.2, y, w: 2.0, h: 0.56,
      fontFace: FONT, fontSize: 15, bold: true, color: NAVY, valign: "middle", margin: 0,
    });
    s.addText(v, {
      x: M + 2.3, y, w: CW - 2.5, h: 0.56,
      fontFace: FONT, fontSize: 14, color: "23303C", valign: "middle", margin: 0,
    });
    y += 0.68;
  });

  s.addText(
    "Polymorphism is not decoration here: one getOutputMW() call inside one loop produces four completely different output curves.",
    {
      x: M, y: 6.62, w: CW, h: 0.35,
      fontFace: FONT, fontSize: 13, color: TEAL, italic: true, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[1] + " - 40 seconds. This is the marking-scheme slide, but do not read all seven rows.\n" +
    "Pick three and say them properly: abstraction (EnergySource), polymorphism (one call, four answers), " +
    "encapsulation (setters reject impossible values). Say the other four are on the slide and in the report.\n" +
    "Finish on the teal line, then switch to the running app."
  );
}

// ===========================================================================
// 7. Demo 1 - Grid Setup
// ===========================================================================
{
  const s = contentSlide("Demo 1: building a city's fleet takes about fifteen seconds.", 1, "3:30 - 4:20");

  card(s, M, 1.8, 7.4, 4.0, TINT);
  s.addText("On screen", {
    x: M + 0.35, y: 2.0, w: 3.0, h: 0.35,
    fontFace: FONT, fontSize: 14, bold: true, color: TEAL, margin: 0,
  });
  bullets(s, M + 0.35, 2.45, 6.7, 3.1, [
    "Pick \"Cyberjaya, Malaysia - Solar Pilot City\" and click Load Example.",
    "The JTable fills with four sources; the city and peak demand fields update.",
    "Add a source with the form: type, name, capacity in MW, solar efficiency.",
    "Remove Selected, Save to File and Load from File all act on the same list.",
  ], 16);

  card(s, 8.35, 1.8, 4.4, 4.0, "F6F9FB");
  s.addText("Say while clicking", {
    x: 8.65, y: 2.0, w: 3.8, h: 0.35,
    fontFace: FONT, fontSize: 14, bold: true, color: AMBER, margin: 0,
  });
  s.addText(
    "“These six examples are modelled on real energy mixes - Kuching runs on Sarawak " +
    "hydro, Copenhagen on offshore wind, Tokyo still on gas. Every row you see is an " +
    "EnergySource object; the table is just a view of the ArrayList.”",
    {
      x: 8.65, y: 2.45, w: 3.8, h: 3.1,
      fontFace: FONT, fontSize: 15, color: NAVY, lineSpacing: 21, valign: "top", margin: 0,
    }
  );

  s.addText(
    "Rehearsed path only - do not improvise new clicks on stage.",
    {
      x: M, y: 6.05, w: CW, h: 0.35,
      fontFace: FONT, fontSize: 13, color: SLATE, italic: true, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[1] + " - 50 seconds of LIVE DEMO. The app must already be running and " +
    "on the Grid Setup screen before the presentation starts - never compile on stage.\n" +
    "Order of clicks: 1) preset dropdown -> Cyberjaya, 2) Load Example, 3) point at the JTable, " +
    "4) type a source into the form and click Add Source, 5) select it and Remove Selected.\n" +
    "Leave the fleet as the clean Cyberjaya preset before handing to " + MEMBERS[2] + " - " +
    "the numbers on the results slide depend on it."
  );
}

// ===========================================================================
// 8. The algorithm + 24h chart
// ===========================================================================
{
  const s = contentSlide("Merit order is SDG 7 written as code: the cleanest energy always runs first.", 2, "4:20 - 5:10");

  const hours = Array.from({ length: 24 }, (_, h) => String(h).padStart(2, "0"));
  const solar = [0,0,0,0,0,0,0,0,21,40,56,69,77,80,77,69,56,40,21,0,0,0,0,0];
  const wind  = [22,26,15,16,14,12,20,23,16,27,23,13,10,23,11,26,14,29,12,14,14,13,21,27];
  const gas   = [44,34,43,40,44,51,55,60,60,41,31,32,29,12,24,13,40,47,60,60,60,60,60,47];
  const short = [0,0,0,0,0,0,0,7,5,0,0,0,0,0,0,0,0,0,27,43,37,29,6,0];
  const demand= [66,60,58,56,58,62,74,90,102,108,110,114,116,114,112,108,110,115,120,118,112,102,86,74];

  s.addChart(
    [
      { type: pres.ChartType.bar,
        data: [
          { name: "Solar", labels: hours, values: solar },
          { name: "Wind",  labels: hours, values: wind },
          { name: "Gas",   labels: hours, values: gas },
          { name: "Shortfall", labels: hours, values: short },
        ],
        options: { barGrouping: "stacked", chartColors: [AMBER, TEAL, SLATE, "C1272D"] },
      },
      { type: pres.ChartType.line,
        data: [{ name: "Demand", labels: hours, values: demand }],
        options: { chartColors: [NAVY], lineSize: 3, lineSmooth: true, lineDataSymbol: "none" },
      },
    ],
    {
      x: M, y: 1.75, w: 8.1, h: 4.25,
      showTitle: true, title: "Cyberjaya baseline: supply stack vs demand (MW)",
      titleFontFace: FONT, titleFontSize: 14, titleColor: NAVY,
      showLegend: true, legendPos: "b", legendFontSize: 11, legendColor: SLATE,
      catAxisLabelFontSize: 10, catAxisLabelColor: SLATE, catAxisLabelFontFace: FONT,
      valAxisLabelFontSize: 10, valAxisLabelColor: SLATE, valAxisLabelFontFace: FONT,
      valGridLine: { color: "E3E9EE", size: 1 },
      catGridLine: { style: "none" },
    }
  );

  s.addText("Four steps, every hour", {
    x: 8.95, y: 1.85, w: 3.8, h: 0.35,
    fontFace: FONT, fontSize: 15, bold: true, color: NAVY, margin: 0,
  });
  bullets(s, 8.95, 2.3, 3.8, 2.5, [
    "Solar and wind generate whatever the weather allows.",
    "Hydro releases water to cover the gap - clean and controllable.",
    "Gas is turned up only for what is still missing.",
    "Anything still short is recorded as a blackout hour.",
  ], 14);

  card(s, 8.95, 4.9, 3.8, 1.35, "FDEEEE");
  s.addText(
    "The red block is the whole problem: the sun is gone by 19:00, and demand peaks from 18:00 to 21:00.",
    {
      x: 9.25, y: 5.0, w: 3.2, h: 1.15,
      fontFace: FONT, fontSize: 14, bold: true, color: "8C1B20", lineSpacing: 18, valign: "middle", margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[2] + " - 50 seconds. This is the most important slide in the deck.\n" +
    "Say: this chart is one real run of our simulator. The navy line is what the city needs. " +
    "The stack underneath is what we produced, in merit order - amber solar, teal wind, grey gas.\n" +
    "Then point at the red: after 6pm the sun is gone and the gas plant is already flat out, " +
    "so the city is short. Seven hours of blackout risk out of 24.\n" +
    "Say the merit order out loud - solar and wind, then hydro, then gas, then shortfall - " +
    "because that is our polymorphic loop and it is exactly what SDG 7 asks a grid to do."
  );
}

// ===========================================================================
// 9. Demo 2 - run the simulation
// ===========================================================================
{
  const s = contentSlide("Demo 2: one click turns that fleet into a costed, hour-by-hour verdict.", 2, "5:10 - 6:00");

  card(s, M, 1.8, 7.4, 4.0, TINT);
  s.addText("On screen", {
    x: M + 0.35, y: 2.0, w: 3.0, h: 0.35,
    fontFace: FONT, fontSize: 14, bold: true, color: TEAL, margin: 0,
  });
  bullets(s, M + 0.35, 2.45, 6.7, 3.1, [
    "Open Simulation and click Run 24h Simulation.",
    "Five stat cards fill in: demand, cost, CO₂, renewable share, blackout hours.",
    "The chart underneath is painted by our own Java 2D code, not a library.",
    "The advice line reads the result and tells the user what to change.",
  ], 16);

  card(s, 8.35, 1.8, 4.4, 4.0, "F6F9FB");
  s.addText("Say while clicking", {
    x: 8.65, y: 2.0, w: 3.8, h: 0.35,
    fontFace: FONT, fontSize: 14, bold: true, color: AMBER, margin: 0,
  });
  s.addText(
    "“Run it twice. The numbers move slightly because wind strength is drawn fresh " +
    "every hour - that is deliberate. A real grid plan has to survive a bad wind day, " +
    "not just an average one.”",
    {
      x: 8.65, y: 2.45, w: 3.8, h: 3.1,
      fontFace: FONT, fontSize: 15, color: NAVY, lineSpacing: 21, valign: "top", margin: 0,
    }
  );

  s.addText(
    "Then stay on this screen - the next slide is the fix, performed live.",
    {
      x: M, y: 6.05, w: CW, h: 0.35,
      fontFace: FONT, fontSize: 13, color: SLATE, italic: true, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[2] + " - 50 seconds of LIVE DEMO.\n" +
    "Click Run 24h Simulation. Read the blackout card out loud - it will say roughly 6 to 9 hours. " +
    "Read the advice line out loud, word for word: that sentence is generated by our " +
    "SimulationReport class, not typed by us.\n" +
    "Run it a second time so the panel sees the numbers shift, and explain the random wind in one sentence."
  );
}

// ===========================================================================
// 10. Result - before / after
// ===========================================================================
{
  const s = contentSlide("Taking the app's own advice removes every blackout hour - and halves the bill.", 2, "6:00 - 6:50");

  // before
  card(s, M, 1.75, 5.85, 3.5, "F6F9FB");
  s.addText("Before", {
    x: M + 0.35, y: 1.95, w: 3.0, h: 0.35,
    fontFace: FONT, fontSize: 16, bold: true, color: SLATE, margin: 0,
  });
  s.addText("Cyberjaya preset as loaded", {
    x: M + 0.35, y: 2.28, w: 4.5, h: 0.3,
    fontFace: FONT, fontSize: 13, color: SLATE, margin: 0,
  });
  stat(s, M + 0.35, 2.7, 2.6, "7 h", "blackout risk", "C1272D");
  stat(s, M + 3.1, 2.7, 2.6, "50%", "renewable", SLATE);
  stat(s, M + 0.35, 4.1, 2.6, "365k", "cost, RM per day", SLATE);
  stat(s, M + 3.1, 4.1, 2.6, "524 t", "CO₂ per day", SLATE);

  // after
  card(s, 6.9, 1.75, 5.85, 3.5, "EAF6F3");
  s.addText("After", {
    x: 7.25, y: 1.95, w: 3.0, h: 0.35,
    fontFace: FONT, fontSize: 16, bold: true, color: TEAL, margin: 0,
  });
  s.addText("+ one 50 MW hydro plant", {
    x: 7.25, y: 2.28, w: 4.5, h: 0.3,
    fontFace: FONT, fontSize: 13, color: TEAL, margin: 0,
  });
  stat(s, 7.25, 2.7, 2.6, "0 h", "blackout risk", TEAL);
  stat(s, 10.0, 2.7, 2.6, "91%", "renewable", TEAL);
  stat(s, 7.25, 4.1, 2.6, "199k", "cost, RM per day", TEAL);
  stat(s, 10.0, 4.1, 2.6, "99 t", "CO₂ per day", TEAL);

  s.addText(
    "Clean and dispatchable beats clean alone: adding more solar never fixed the evening peak, " +
    "because the shortfall happens after sunset. The simulator made that obvious in one run.",
    {
      x: M, y: 5.5, w: CW, h: 0.7,
      fontFace: FONT, fontSize: 16, color: NAVY, lineSpacing: 22, margin: 0,
    }
  );
  s.addText(
    "Figures from GridWise runs on the Cyberjaya preset (peak demand 120 MW). Wind is random, so values vary by a few percent between runs.",
    {
      x: M, y: 6.5, w: CW, h: 0.35,
      fontFace: FONT, fontSize: 11, color: SLATE, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[2] + " - 50 seconds, and DO THIS LIVE if time allows.\n" +
    "Go back to Grid Setup, add HYDRO / 'Demo Hydro' / 50 MW, return to Simulation, run again. " +
    "Blackout hours drop to 0, renewable jumps to about 90 percent, cost roughly halves.\n" +
    "If you are behind schedule, skip the clicking and just present these numbers.\n" +
    "The closing point is the italic line: we also tried adding 60 MW of extra solar and it " +
    "barely helped, because the shortfall is after sunset. That is the insight the tool exists to produce.\n" +
    "Hand over to " + MEMBERS[3] + "."
  );
}

// ===========================================================================
// 11. Demo 3 - the rest of the app
// ===========================================================================
{
  const s = contentSlide("Demo 3: the report survives the session - and speaks the audience's currency.", 3, "6:50 - 7:40");

  const items = [
    ["Save & load", "The fleet is written to grid_sources.csv and the correct subclass is rebuilt on load.", TEAL],
    ["Export Report", "The full 24-hour breakdown and the advice go to simulation_report.txt.", TEAL],
    ["Nine currencies", "The same run read in MYR, USD, EUR, GBP, SGD, AUD, CNY, JPY or INR - display only.", AMBER],
    ["Light and dark", "One Theme class repaints every panel at runtime; the logo is drawn in Java 2D.", NAVY],
  ];

  let y = 1.8;
  items.forEach(([head, body, color]) => {
    card(s, M, y, CW, 1.05, TINT);
    s.addText(head, {
      x: M + 0.3, y: y + 0.16, w: 2.9, h: 0.7,
      fontFace: FONT, fontSize: 18, bold: true, color, valign: "middle", margin: 0,
    });
    s.addText(body, {
      x: M + 3.3, y: y + 0.16, w: CW - 3.6, h: 0.7,
      fontFace: FONT, fontSize: 16, color: "23303C", valign: "middle", margin: 0,
    });
    y += 1.2;
  });

  s.addText(
    "Currency is a display conversion, never a change to the simulation - the tariffs behind the model stay in ringgit.",
    {
      x: M, y: 6.55, w: CW, h: 0.35,
      fontFace: FONT, fontSize: 13, color: SLATE, italic: true, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[3] + " - 50 seconds of LIVE DEMO. Keep it moving, this is the fastest section.\n" +
    "1) Change the currency dropdown to USD and point at the cost card changing. " +
    "2) Click the theme toggle - dark mode. 3) Click Export Report, then open " +
    "simulation_report.txt so the panel sees the file exists. 4) Mention Save to File in one sentence.\n" +
    "If the panel looks impatient, cut the theme toggle - the export is the one that earns marks (file handling)."
  );
}

// ===========================================================================
// 12. SDG 7
// ===========================================================================
{
  const s = contentSlide("GridWise makes the SDG 7 trade-off visible instead of theoretical.", 3, "7:40 - 8:15");

  const cols = [
    ["What SDG 7 asks", [
      "Affordable energy",
      "Reliable, modern supply",
      "A rising share of renewables",
    ], NAVY],
    ["What GridWise shows", [
      "Cost per day, in the user's currency",
      "Blackout hours, counted honestly",
      "Renewable share of energy actually consumed",
    ], TEAL],
    ["What the user learns", [
      "Gas is the expensive way to stay safe",
      "Clean plus dispatchable beats clean alone",
      "Storage or hydro is what unlocks solar",
    ], AMBER],
  ];

  let x = M;
  const cw = 3.98, gap = 0.4;
  cols.forEach(([head, list, color]) => {
    card(s, x, 1.85, cw, 3.4, TINT);
    s.addText(head, {
      x: x + 0.3, y: 2.05, w: cw - 0.6, h: 0.45,
      fontFace: FONT, fontSize: 17, bold: true, color, margin: 0,
    });
    bullets(s, x + 0.3, 2.6, cw - 0.6, 2.5, list, 15);
    x += cw + gap;
  });

  s.addText(
    "A student can test an energy policy in ten seconds, for free, before anyone pours concrete.",
    {
      x: M, y: 5.55, w: CW, h: 0.45,
      fontFace: FONT, fontSize: 18, bold: true, color: NAVY, margin: 0,
    }
  );

  s.addNotes(
    MEMBERS[3] + " - 35 seconds. Read the three column headings, then one example from each.\n" +
    "The line at the bottom is your closing argument for the SDG 7 marks - say it slowly."
  );
}

// ===========================================================================
// 13. Conclusions
// ===========================================================================
{
  const s = pres.addSlide();
  s.background = { color: NAVY };

  s.addText("What we are taking away from GridWise", {
    x: M, y: 0.55, w: CW, h: 0.7,
    fontFace: FONT, fontSize: 30, bold: true, color: WHITE, margin: 0,
  });

  s.addText("It works", {
    x: M, y: 1.6, w: 5.9, h: 0.4,
    fontFace: FONT, fontSize: 17, bold: true, color: AMBER, margin: 0,
  });
  s.addText(
    [
      { text: "23 classes, four packages, compiles clean on JDK 21.", options: { bullet: true, breakLine: true } },
      { text: "Renewables-first dispatch over a 24-hour demand curve.", options: { bullet: true, breakLine: true } },
      { text: "Cost, CO₂, renewable share, blackout risk and written advice.", options: { bullet: true, breakLine: true } },
      { text: "CSV persistence, report export, nine currencies, light and dark mode.", options: { bullet: true } },
    ],
    {
      x: M, y: 2.05, w: 5.9, h: 2.4,
      fontFace: FONT, fontSize: 16, color: "DCE6EF", paraSpaceAfter: 10, valign: "top", margin: 0,
    }
  );

  s.addText("What we would build next", {
    x: 6.9, y: 1.6, w: 5.85, h: 0.4,
    fontFace: FONT, fontSize: 17, bold: true, color: AMBER, margin: 0,
  });
  s.addText(
    [
      { text: "Battery storage - the missing piece for the evening peak.", options: { bullet: true, breakLine: true } },
      { text: "Real weather data instead of a random wind factor.", options: { bullet: true, breakLine: true } },
      { text: "A full year rather than a single representative day.", options: { bullet: true, breakLine: true } },
      { text: "Automated tests over the dispatch logic.", options: { bullet: true } },
    ],
    {
      x: 6.9, y: 2.05, w: 5.85, h: 2.4,
      fontFace: FONT, fontSize: 16, color: "DCE6EF", paraSpaceAfter: 10, valign: "top", margin: 0,
    }
  );

  s.addText(
    "The biggest lesson: the abstraction earned its keep. Every source answers the same five " +
    "questions, which is why HydroPlant is only 40 lines. The dispatch loop still names some " +
    "types by string - that is the first thing we would refactor.",
    {
      x: M, y: 4.85, w: CW, h: 0.9,
      fontFace: FONT, fontSize: 17, color: WHITE, lineSpacing: 24, margin: 0,
    }
  );

  s.addText("Questions - all four of us are happy to take them.", {
    x: M, y: 6.2, w: 7.5, h: 0.4,
    fontFace: FONT, fontSize: 16, bold: true, color: AMBER, margin: 0,
  });
  s.addText("github.com/ksdsass/bit1123-final-project-gridwise", {
    x: 6.9, y: 6.2, w: 5.85, h: 0.4,
    fontFace: FONT, fontSize: 13, color: "8FA3B5", align: "right", margin: 0,
  });

  s.addNotes(
    MEMBERS[3] + " - 35 seconds, then STOP and leave this slide up for the whole Q&A.\n" +
    "Do not read both columns. Say: it works, here is what we would build next, and here is " +
    "the one thing we actually learned - the abstraction line.\n" +
    "Then: 'That is GridWise. We are happy to take your questions.'\n" +
    "During Q&A: whoever owns the section answers. If a question is about the algorithm it is " +
    MEMBERS[2] + "; about the code structure, " + MEMBERS[1] + "; about scope and SDG 7, " +
    MEMBERS[0] + " or " + MEMBERS[3] + ". Never let one person answer everything."
  );
}

// ===========================================================================
// 14. Appendix / references
// ===========================================================================
{
  const s = contentSlide("Appendix: sources and honest limitations", 3, "Q&A backup");

  s.addText("References", {
    x: M, y: 1.7, w: 6.0, h: 0.35,
    fontFace: FONT, fontSize: 16, bold: true, color: NAVY, margin: 0,
  });
  s.addText(
    [
      { text: "United Nations, Sustainable Development Goal 7: Affordable and Clean Energy. sdgs.un.org/goals/goal7", options: { bullet: true, breakLine: true } },
      { text: "Oracle, Java Platform SE 21 API documentation (Swing, Java 2D, Collections, java.io).", options: { bullet: true, breakLine: true } },
      { text: "Project source code: github.com/ksdsass/bit1123-final-project-gridwise", options: { bullet: true } },
    ],
    {
      x: M, y: 2.1, w: CW, h: 1.4,
      fontFace: FONT, fontSize: 14, color: "23303C", paraSpaceAfter: 8, valign: "top", margin: 0,
    }
  );

  s.addText("Limitations we will state before we are asked", {
    x: M, y: 3.6, w: 8.0, h: 0.35,
    fontFace: FONT, fontSize: 16, bold: true, color: NAVY, margin: 0,
  });
  s.addText(
    [
      { text: "The six example grids are inspired by real energy mixes but the megawatt figures are simplified and scaled to city size - they are illustrative, not measured data.", options: { bullet: true, breakLine: true } },
      { text: "Costs and emission factors are fixed representative values in ringgit per MWh, not published tariffs.", options: { bullet: true, breakLine: true } },
      { text: "Currency rates are fixed constants; the application is fully offline and calls no exchange-rate service.", options: { bullet: true, breakLine: true } },
      { text: "Wind is a random factor between 0.25 and 0.75 of capacity, not a weather forecast, and the model has no storage and no transmission losses.", options: { bullet: true } },
    ],
    {
      x: M, y: 4.0, w: CW, h: 2.5,
      fontFace: FONT, fontSize: 14, color: "23303C", paraSpaceAfter: 8, valign: "top", margin: 0,
    }
  );

  s.addNotes(
    "NOT PRESENTED - this slide exists for Q&A.\n" +
    "If a panel member challenges the realism of the numbers, jump here. Owning the " +
    "limitation before it is pointed out is worth more marks than defending it.\n" +
    "See PRESENTATION_GUIDE.md in the repository for the full list of anticipated questions."
  );
}

pres.writeFile({ fileName: "GridWise_Presentation.pptx" })
  .then(f => console.log("Created " + f));
