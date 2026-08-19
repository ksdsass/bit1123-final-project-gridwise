package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import service.CurrencyConverter;

/**
 * MainWindow - the application frame. It holds the dark header bar with
 * the GridWise logo, the display settings (currency and light/dark
 * mode) and the navigation buttons, plus a CardLayout body that swaps
 * between the Grid Setup and Simulation screens.
 */
public class MainWindow extends JFrame {

    private CurrencyConverter currency = new CurrencyConverter();

    private SetupPanel setupPanel = new SetupPanel();
    private SimulationPanel simulationPanel = new SimulationPanel(setupPanel, currency);

    private CardLayout cards = new CardLayout();
    private JPanel body = new JPanel(cards);

    private JPanel header = new JPanel(new BorderLayout());
    private JButton setupNav;
    private JButton simulationNav;
    private JButton themeToggle;
    private JComboBox<String> currencyBox = new JComboBox<>();
    private JLabel tagline = new JLabel("  City Energy Grid Simulator — SDG 7");

    public MainWindow() {
        Theme.applyDefaults();

        setTitle("GridWise - City Energy Grid Simulator (SDG 7)");
        setSize(1060, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(AppLogo.image(64));

        buildHeader();
        add(header, BorderLayout.NORTH);

        body.setBackground(Theme.bg());
        body.add(setupPanel, "setup");
        body.add(simulationPanel, "simulation");
        add(body, BorderLayout.CENTER);

        showSetup();
    }

    private void buildHeader() {
        header.setBackground(Theme.header());
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // --- Left: logo mark + word mark ---
        JLabel logo = new JLabel("GridWise", AppLogo.icon(30), JLabel.LEFT);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logo.setIconTextGap(10);
        tagline.setFont(Theme.SMALL);
        tagline.setForeground(new Color(0x9A, 0xB0, 0xBD));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(logo);
        left.add(tagline);
        header.add(left, BorderLayout.WEST);

        // --- Right: currency, theme toggle, navigation ---
        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setFont(Theme.SMALL);
        currencyLabel.setForeground(new Color(0x9A, 0xB0, 0xBD));
        for (String code : currency.getCodes()) {
            currencyBox.addItem(code);
        }
        currencyBox.setSelectedItem(currency.getActiveCode());
        currencyBox.setFocusable(false);
        currencyBox.addActionListener(e -> {
            currency.setActive((String) currencyBox.getSelectedItem());
            simulationPanel.refreshDisplay();
        });

        themeToggle = new JButton();
        themeToggle.setFont(Theme.BOLD);
        themeToggle.setFocusPainted(false);
        themeToggle.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        themeToggle.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        themeToggle.addActionListener(e -> toggleTheme());

        setupNav = navButton("Grid Setup");
        simulationNav = navButton("Simulation");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(currencyLabel);
        right.add(currencyBox);
        right.add(themeToggle);
        right.add(javax.swing.Box.createHorizontalStrut(10));
        right.add(setupNav);
        right.add(simulationNav);
        header.add(right, BorderLayout.EAST);

        updateThemeToggleLabel();
    }

    private JButton navButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.BOLD);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    /** Switches between light and dark mode and restyles everything. */
    private void toggleTheme() {
        setDarkMode(!Theme.isDark());
    }

    /** Applies the chosen mode and repaints the whole window. */
    public void setDarkMode(boolean darkMode) {
        Theme.setDark(darkMode);
        updateThemeToggleLabel();

        header.setBackground(Theme.header());
        body.setBackground(Theme.bg());
        Theme.restyle(setupPanel);
        Theme.restyle(simulationPanel);
        // Re-applies the advice banner colours for the new mode
        simulationPanel.refreshDisplay();
        setActive(cardIsSetup ? setupNav : simulationNav);

        revalidate();
        repaint();
    }

    private void updateThemeToggleLabel() {
        themeToggle.setText(Theme.isDark() ? "Light mode" : "Dark mode");
        themeToggle.setBackground(new Color(0x2A, 0x38, 0x44));
        themeToggle.setForeground(Color.WHITE);
        themeToggle.setOpaque(true);
        themeToggle.setBorderPainted(false);
    }

    private boolean cardIsSetup = true;

    private void setActive(JButton active) {
        for (JButton b : new JButton[] {setupNav, simulationNav}) {
            boolean isActive = (b == active);
            b.setBackground(isActive ? Theme.accent() : Theme.header());
            b.setForeground(isActive ? Color.WHITE : new Color(0x9A, 0xB0, 0xBD));
            b.setOpaque(true);
            b.setBorderPainted(false);
        }
    }

    public void showSetup() {
        cards.show(body, "setup");
        cardIsSetup = true;
        setActive(setupNav);
    }

    public void showSimulation() {
        cards.show(body, "simulation");
        cardIsSetup = false;
        setActive(simulationNav);
    }

    public SetupPanel getSetupPanel() {
        return setupPanel;
    }

    public SimulationPanel getSimulationPanel() {
        return simulationPanel;
    }
}
