package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.JPanel;

import model.HourlyRecord;

/**
 * ChartPanel - a custom Swing component that paints the 24-hour
 * simulation as stacked bars (solar + wind + gas) with the demand
 * curve drawn on top, so blackout gaps are visible immediately.
 */
public class ChartPanel extends JPanel {

    private static final Color SOLAR_COLOR = new Color(0xF6, 0xB9, 0x2E);
    private static final Color WIND_COLOR  = new Color(0x3E, 0x9E, 0xD1);
    private static final Color GAS_COLOR   = new Color(0x8D, 0x6E, 0x63);
    private static final Color DEMAND_COLOR = new Color(0xC6, 0x28, 0x28);

    private List<HourlyRecord> records;

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
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        if (records == null || records.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("Run a simulation to see the supply chart.", 20, h / 2);
            return;
        }

        int left = 50, bottom = h - 30, top = 25;
        int plotW = w - left - 15, plotH = bottom - top;

        // Scale: highest of demand or supply in any hour
        double max = 1;
        for (HourlyRecord r : records) {
            max = Math.max(max, Math.max(r.getDemandMW(), r.getSuppliedMW()));
        }

        int barW = plotW / 24;

        // Axis labels
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g2.drawString(String.format("%.0f MW", max), 5, top + 5);
        g2.drawString("0", 30, bottom + 4);

        for (HourlyRecord r : records) {
            int x = left + r.getHour() * barW;

            int hSolar = (int) (r.getSolarMW() / max * plotH);
            int hWind  = (int) (r.getWindMW()  / max * plotH);
            int hGas   = (int) (r.getGasMW()   / max * plotH);

            int y = bottom;
            g2.setColor(SOLAR_COLOR);
            g2.fillRect(x + 1, y - hSolar, barW - 2, hSolar);
            y -= hSolar;
            g2.setColor(WIND_COLOR);
            g2.fillRect(x + 1, y - hWind, barW - 2, hWind);
            y -= hWind;
            g2.setColor(GAS_COLOR);
            g2.fillRect(x + 1, y - hGas, barW - 2, hGas);

            if (r.getHour() % 4 == 0) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(String.format("%02d", r.getHour()), x + 2, bottom + 14);
            }
        }

        // Demand line on top of the bars
        g2.setColor(DEMAND_COLOR);
        g2.setStroke(new BasicStroke(2f));
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
        int lx = left + 8;
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lx = legendItem(g2, lx, top - 12, SOLAR_COLOR, "Solar");
        lx = legendItem(g2, lx, top - 12, WIND_COLOR, "Wind");
        lx = legendItem(g2, lx, top - 12, GAS_COLOR, "Gas");
        legendItem(g2, lx, top - 12, DEMAND_COLOR, "Demand");
    }

    private int legendItem(Graphics2D g2, int x, int y, Color c, String label) {
        g2.setColor(c);
        g2.fillRect(x, y, 10, 10);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(label, x + 14, y + 9);
        return x + 14 + g2.getFontMetrics().stringWidth(label) + 12;
    }
}
