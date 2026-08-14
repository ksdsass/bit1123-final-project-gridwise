package app;

import javax.swing.SwingUtilities;

import ui.MainWindow;

/**
 * Main - entry point of GridWise.
 *
 * GridWise is a city energy grid simulator supporting SDG 7
 * (Affordable and Clean Energy): it shows how a mix of solar, wind
 * and gas keeps a city powered, and what each mix costs in money
 * and CO2 emissions.
 */
public class Main {

    public static void main(String[] args) {
        // Swing components must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
