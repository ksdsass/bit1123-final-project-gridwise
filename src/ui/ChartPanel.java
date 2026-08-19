package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import model.HourlyRecord;

/**
 * ChartPanel - a custom Swing component that paints the 24-hour
 * simulation as stacked bars (solar + wind + hydro + gas) with the
 * demand curve drawn on top, so blackout gaps are visible immediately.
 */
public class ChartPanel extends JPanel {

    private static final Color SOLAR_COLOR  = new Color(0xF9, 0xC7, 0x4F);
    private static final Color WIND_COLOR   = new Color(0x4D, 0x96, 0xD9);
    private static final Color HYDRO_COLOR  = new Color(0x2A, 0x9D, 0x8F);
    private static final Color GAS_COLOR    = new Color(0x9B, 0xA4, 0xB4);
    private static final Color DEMAND_COLOR = new Color(0xE6, 0x39, 0x46);

    private List<HourlyRecord> records;

    public ChartPanel() {
        setBackground(Theme.surface());
    }

    public void setRecords(List<HourlyRecord> records) {
        this.records = records;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        g2.setColor(Theme.surface());
        g2.fillRect(0, 0, w, h);

        if (records == null || records.isEmpty()) {
            g2.setColor(Theme.muted());
            g2.setFont(Theme.BASE);
            g2.drawString("Run a simulation to see the supply chart.", 24, h / 2);
            return;
        }

        int left = 52, bottom = h - 28, top = 30;
        int plotW = w - left - 18, plotH = bottom - top;

        double max = 1;
        for (HourlyRecord r : records) {
            max = Math.max(max, Math.max(r.getDemandMW(), r.getSuppliedMW()));
        }

        // Horizontal gridlines at 25% steps
        g2.setFont(Theme.SMALL);
        for (int i = 0; i <= 4; i++) {
            int y = bottom - plotH * i / 4;
            g2.setColor(Theme.chartGrid());
            g2.drawLine(left, y, left + plotW, y);
            g2.setColor(Theme.muted());
            g2.drawString(String.format("%.0f", max * i / 4), 10, y + 4);
        }

        int barW = plotW / 24;

        for (HourlyRecord r : records) {
            int x = left + r.getHour() * barW;

            int hSolar = (int) (r.getSolarMW() / max * plotH);
            int hWind  = (int) (r.getWindMW()  / max * plotH);
            int hHydro = (int) (r.getHydroMW() / max * plotH);
            int hGas   = (int) (r.getGasMW()   / max * plotH);

            int y = bottom;
            g2.setColor(SOLAR_COLOR);
            g2.fillRect(x + 2, y - hSolar, barW - 4, hSolar);
            y -= hSolar;
            g2.setColor(WIND_COLOR);
            g2.fillRect(x + 2, y - hWind, barW - 4, hWind);
            y -= hWind;
            g2.setColor(HYDRO_COLOR);
            g2.fillRect(x + 2, y - hHydro, barW - 4, hHydro);
            y -= hHydro;
            g2.setColor(GAS_COLOR);
            g2.fillRect(x + 2, y - hGas, barW - 4, hGas);

            if (r.getHour() % 4 == 0) {
                g2.setColor(Theme.muted());
                g2.drawString(String.format("%02d", r.getHour()), x + 4, bottom + 15);
            }
        }

        // Demand line on top of the bars
        g2.setColor(DEMAND_COLOR);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        int prevX = -1, prevY = -1;
        for (HourlyRecord r : records) {
            int x = left + r.getHour() * barW + barW / 2;
            int y = bottom - (int) (r.getDemandMW() / max * plotH);
            if (prevX >= 0) {
                g2.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
        }

        // Legend
        int lx = left + 4;
        lx = legendItem(g2, lx, top - 16, SOLAR_COLOR, "Solar");
        lx = legendItem(g2, lx, top - 16, WIND_COLOR, "Wind");
        lx = legendItem(g2, lx, top - 16, HYDRO_COLOR, "Hydro");
        lx = legendItem(g2, lx, top - 16, GAS_COLOR, "Gas");
        legendItem(g2, lx, top - 16, DEMAND_COLOR, "Demand");
    }

    private int legendItem(Graphics2D g2, int x, int y, Color c, String label) {
        g2.setColor(c);
        g2.fillRoundRect(x, y, 11, 11, 3, 3);
        g2.setColor(Theme.muted());
        g2.setFont(Theme.SMALL);
        g2.drawString(label, x + 15, y + 10);
        return x + 15 + g2.getFontMetrics().stringWidth(label) + 14;
    }
}
