package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * AppLogo - the GridWise brand mark, drawn with Java 2D instead of an
 * image file so the application stays a single self-contained project
 * and the logo stays sharp at any size.
 *
 * The mark is a rounded green badge containing a lightning bolt and two
 * power-line bars, representing electricity flowing through a grid.
 */
public final class AppLogo {

    private AppLogo() {
    }

    /** Logo as a Swing Icon, for use inside labels and buttons. */
    public static Icon icon(final int size) {
        return new Icon() {
            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                paint(g2, x, y, size);
                g2.dispose();
            }
        };
    }

    /** Logo as an image, used for the window and taskbar icon. */
    public static BufferedImage image(int size) {
        BufferedImage img = new BufferedImage(size, size,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        paint(g2, 0, 0, size);
        g2.dispose();
        return img;
    }

    /** Draws the badge, the grid bars and the bolt at the given size. */
    private static void paint(Graphics2D g2, int x, int y, int size) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        double s = size / 32.0;   // the artwork is designed on a 32x32 grid

        // Rounded badge with a green gradient
        g2.setPaint(new GradientPaint(
                x, y, new Color(0x2F, 0xD1, 0x7A),
                x + size, y + size, new Color(0x0E, 0x8B, 0x4E)));
        g2.fillRoundRect(x, y, size, size, (int) (9 * s), (int) (9 * s));

        // Two faint power-line bars behind the bolt (the "grid")
        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillRect(x + (int) (5 * s), y + (int) (21 * s),
                (int) (22 * s), (int) (2.2 * s));
        g2.fillRect(x + (int) (8 * s), y + (int) (25 * s),
                (int) (16 * s), (int) (2.2 * s));

        // Lightning bolt
        Path2D bolt = new Path2D.Double();
        bolt.moveTo(x + 19.5 * s, y + 4 * s);
        bolt.lineTo(x + 9.5 * s, y + 18 * s);
        bolt.lineTo(x + 15 * s, y + 18 * s);
        bolt.lineTo(x + 13 * s, y + 28 * s);
        bolt.lineTo(x + 23 * s, y + 13.5 * s);
        bolt.lineTo(x + 17.5 * s, y + 13.5 * s);
        bolt.closePath();
        g2.setColor(Color.WHITE);
        g2.fill(bolt);
    }
}
