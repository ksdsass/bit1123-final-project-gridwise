package ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * CenteredPanel - keeps its single child centred horizontally and stops
 * it from stretching across very wide screens.
 *
 * On a wide monitor a full-width form looks empty and is hard to read,
 * so the content is capped at a comfortable maximum width and centred,
 * the way modern web layouts do it.
 */
public class CenteredPanel extends JPanel {

    private Component child;
    private int maxWidth;
    private int margin;

    public CenteredPanel(Component child, int maxWidth, int margin) {
        this(child, maxWidth, margin, true);
    }

    /**
     * @param paintBackground false for the header, where the dark bar
     *                        behind this panel must stay visible.
     */
    public CenteredPanel(Component child, int maxWidth, int margin,
                         boolean paintBackground) {
        this.child = child;
        this.maxWidth = maxWidth;
        this.margin = margin;
        setLayout(null);             // positions are calculated in doLayout()
        setOpaque(paintBackground);
        if (paintBackground) {
            setBackground(Theme.bg());
        }
        add(child);
    }

    @Override
    public void doLayout() {
        int available = getWidth() - 2 * margin;
        int width = Math.min(available, maxWidth);
        int x = (getWidth() - width) / 2;
        child.setBounds(x, margin, width, getHeight() - 2 * margin);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = child.getPreferredSize();
        return new Dimension(d.width + 2 * margin, d.height + 2 * margin);
    }
}
