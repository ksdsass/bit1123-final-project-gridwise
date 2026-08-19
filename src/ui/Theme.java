package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Theme - one place for the application's look: the colour palettes for
 * LIGHT and DARK mode, the fonts, and factory methods for cards and
 * rounded buttons.
 *
 * Colours are exposed as methods (not constants) so that every component
 * asks for the colour of the CURRENT mode when it paints. Switching mode
 * therefore only requires calling setDark(...) and then restyle(...) on
 * the window, which walks the component tree and re-applies colours
 * using the role stored in each component's client properties.
 */
public final class Theme {

    /** Client-property key used to remember what a component is. */
    public static final String ROLE = "gridwise.role";

    // Role values
    public static final String ROLE_CARD  = "card";
    public static final String ROLE_TITLE = "cardTitle";
    public static final String ROLE_MUTED = "muted";
    public static final String ROLE_STAT  = "stat";
    public static final String ROLE_INPUT = "input";

    private static boolean dark = false;

    private Theme() {
    }

    public static boolean isDark() {
        return dark;
    }

    public static void setDark(boolean value) {
        dark = value;
    }

    // ---------------- Palette ----------------

    public static Color bg() {
        return dark ? new Color(0x14, 0x1A, 0x21) : new Color(0xEF, 0xF2, 0xF5);
    }

    public static Color surface() {
        return dark ? new Color(0x1D, 0x26, 0x30) : Color.WHITE;
    }

    public static Color border() {
        return dark ? new Color(0x2E, 0x3A, 0x46) : new Color(0xDC, 0xE2, 0xE8);
    }

    public static Color accent() {
        return dark ? new Color(0x25, 0xC4, 0x74) : new Color(0x15, 0x9A, 0x5B);
    }

    public static Color accentDark() {
        return dark ? new Color(0x1B, 0xA5, 0x60) : new Color(0x0E, 0x7A, 0x47);
    }

    public static Color header() {
        return dark ? new Color(0x0E, 0x14, 0x1A) : new Color(0x16, 0x24, 0x2E);
    }

    public static Color text() {
        return dark ? new Color(0xE6, 0xEC, 0xF2) : new Color(0x1F, 0x29, 0x33);
    }

    public static Color muted() {
        return dark ? new Color(0x8F, 0xA0, 0xAE) : new Color(0x74, 0x80, 0x8C);
    }

    public static Color danger() {
        return dark ? new Color(0xFF, 0x7B, 0x7B) : new Color(0xD6, 0x45, 0x45);
    }

    public static Color dangerBg() {
        return dark ? new Color(0x3A, 0x20, 0x24) : new Color(0xFC, 0xED, 0xEC);
    }

    public static Color okBg() {
        return dark ? new Color(0x16, 0x30, 0x22) : new Color(0xE9, 0xF6, 0xEF);
    }

    public static Color selection() {
        return dark ? new Color(0x24, 0x3B, 0x31) : new Color(0xDE, 0xF2, 0xE7);
    }

    /** Faint gridlines used by the chart. */
    public static Color chartGrid() {
        return dark ? new Color(0x2A, 0x35, 0x40) : new Color(0xEC, 0xEF, 0xF3);
    }

    // ---------------- Fonts ----------------

    public static final Font BASE  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font STAT  = new Font("Segoe UI", Font.BOLD, 19);

    /** Global defaults applied once at startup. */
    public static void applyDefaults() {
        UIManager.put("Label.font", BASE);
        UIManager.put("Button.font", BOLD);
        UIManager.put("TextField.font", BASE);
        UIManager.put("ComboBox.font", BASE);
        UIManager.put("Table.font", BASE);
        UIManager.put("TableHeader.font", BOLD);
        UIManager.put("OptionPane.messageFont", BASE);
        UIManager.put("OptionPane.buttonFont", BOLD);
    }

    // ---------------- Component factories ----------------

    /** A filled, rounded, hover-aware action button. */
    public static JButton primaryButton(String text) {
        return roundButton(text, true);
    }

    /** A neutral rounded button for secondary actions. */
    public static JButton ghostButton(String text) {
        return roundButton(text, false);
    }

    private static JButton roundButton(String text, final boolean primary) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hot = getModel().isRollover() || getModel().isPressed();
                Color fill;
                if (primary) {
                    fill = hot ? accentDark() : accent();
                } else {
                    fill = hot ? border() : surface();
                }
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (!primary) {
                    g2.setColor(border());
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }
                g2.dispose();
                // Foreground follows the current mode
                setForeground(primary ? Color.WHITE : text());
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setFont(BOLD);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    /** A surface-coloured card panel with a soft border and padding. */
    public static JPanel card(String titleText) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.putClientProperty(ROLE, ROLE_CARD);
        card.setBackground(surface());
        card.setBorder(cardBorder());
        if (titleText != null) {
            JLabel title = new JLabel(titleText.toUpperCase());
            title.putClientProperty(ROLE, ROLE_TITLE);
            title.setFont(SMALL);
            title.setForeground(muted());
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(title);
            card.add(Box.createVerticalStrut(8));
        }
        return card;
    }

    private static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border(), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    /** Consistent styling for data tables in the current mode. */
    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setGridColor(border());
        table.setBackground(surface());
        table.setForeground(text());
        table.setSelectionBackground(selection());
        table.setSelectionForeground(text());
        table.getTableHeader().setBackground(surface());
        table.getTableHeader().setForeground(muted());
        table.setFillsViewportHeight(true);
    }

    // ---------------- Theme switching ----------------

    /**
     * Walks the whole component tree and re-applies the colours of the
     * current mode, using each component's stored role.
     */
    public static void restyle(Component comp) {
        Object role = (comp instanceof javax.swing.JComponent)
                ? ((javax.swing.JComponent) comp).getClientProperty(ROLE) : null;

        if (ROLE_CARD.equals(role)) {
            comp.setBackground(surface());
            ((JPanel) comp).setBorder(cardBorder());
        } else if (ROLE_TITLE.equals(role) || ROLE_MUTED.equals(role)) {
            comp.setForeground(muted());
        } else if (ROLE_STAT.equals(role)) {
            comp.setForeground(text());
        } else if (ROLE_INPUT.equals(role)) {
            comp.setBackground(surface());
            comp.setForeground(text());
            if (comp instanceof javax.swing.JComponent) {
                ((javax.swing.JComponent) comp).setBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(border()),
                                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
            }
        } else if (comp instanceof JTable) {
            styleTable((JTable) comp);
        } else if (comp instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) comp;
            sp.setBorder(BorderFactory.createLineBorder(border()));
            sp.getViewport().setBackground(surface());
        } else if (comp instanceof JLabel) {
            comp.setForeground(text());
        } else if (comp instanceof JTextField || comp instanceof JComboBox) {
            comp.setBackground(surface());
            comp.setForeground(text());
        } else if (comp instanceof JPanel && ((JPanel) comp).isOpaque()) {
            comp.setBackground(bg());
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                restyle(child);
            }
        }
        comp.repaint();
    }

    /** Marks an input so the theme walker restyles it correctly. */
    public static <T extends javax.swing.JComponent> T input(T field) {
        field.putClientProperty(ROLE, ROLE_INPUT);
        field.setBackground(surface());
        field.setForeground(text());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border()),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        return field;
    }
}
