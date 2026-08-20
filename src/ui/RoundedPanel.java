package ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * RoundedPanel - a panel that paints itself as a rounded "card".
 *
 * Swing borders cannot round the background, so this class paints the
 * fill and the outline itself. The colours are read from Theme at paint
 * time, which means a card automatically follows the current light or
 * dark mode without any extra work.
 */
public class RoundedPanel extends JPanel {

    private int radius;
    private java.awt.Color fill;   // null means "use the normal card colour"

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);            // we paint our own background
    }

    /** Overrides the card colour, e.g. for the red/green advice banner. */
    public void setFillColor(java.awt.Color fill) {
        this.fill = fill;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(fill != null ? fill : Theme.surface());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.setColor(Theme.border());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
