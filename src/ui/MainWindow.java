package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import service.CurrencyConverter;

/**
 * MainWindow - the application frame. It holds the dark header bar with
 * the GridWise logo, the display settings (currency and light/dark
 * mode) and the navigation pills, plus a CardLayout body that swaps
 * between the Grid Setup and Simulation screens.
 *
 * The body is wrapped in a CenteredPanel so the content stays centred
 * and readable instead of stretching across a wide monitor.
 */
public class MainWindow extends JFrame {

    /** Content is never wider than this, however large the window is. */
    private static final int CONTENT_MAX_WIDTH = 1080;

    private CurrencyConverter currency = new CurrencyConverter();

    private SetupPanel setupPanel = new SetupPanel();
    private SimulationPanel simulationPanel = new SimulationPanel(setupPanel, currency);

    private CardLayout cards = new CardLayout();
    private JPanel body = new JPanel(cards);
    private CenteredPanel bodyWrapper;

    private JPanel header = new JPanel(new BorderLayout());
    private JPanel headerContent = new JPanel(new BorderLayout());
    private JButton setupNav;
    private JButton simulationNav;
    private JButton themeToggle;
    private JComboBox<String> currencyBox = new JComboBox<>();
    private JLabel tagline = new JLabel("  City Energy Grid Simulator — SDG 7");
    private JLabel currencyLabel = new JLabel("Currency:");

    private boolean showingSetup = true;

    public MainWindow() {
        Theme.applyDefaults();

        setTitle("GridWise - City Energy Grid Simulator (SDG 7)");
        setSize(1160, 720);
        setMinimumSize(new java.awt.Dimension(940, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(AppLogo.image(64));

        buildHeader();
        add(header, BorderLayout.NORTH);

        body.setOpaque(false);
        body.add(setupPanel, "setup");
        body.add(simulationPanel, "simulation");

        bodyWrapper = new CenteredPanel(body, CONTENT_MAX_WIDTH, 0);
        add(bodyWrapper, BorderLayout.CENTER);

        showSetup();
    }

    private void buildHeader() {
        header.setBackground(Theme.header());
        header.setBorder(BorderFactory.createEmptyBorder(11, 0, 11, 0));
        headerContent.setOpaque(false);

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
        headerContent.add(left, BorderLayout.WEST);

        // --- Right: currency, theme toggle, navigation ---
        currencyLabel.setFont(Theme.SMALL);
        currencyLabel.setForeground(new Color(0x9A, 0xB0, 0xBD));
        for (String code : currency.getCodes()) {
            currencyBox.addItem(code);
        }
        currencyBox.setSelectedItem(currency.getActiveCode());
        currencyBox.setFocusable(false);
        currencyBox.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        currencyBox.addActionListener(e -> {
            currency.setActive((String) currencyBox.getSelectedItem());
            simulationPanel.refreshDisplay();
        });

        themeToggle = Theme.navButton(themeLabel());
        themeToggle.addActionListener(e -> toggleTheme());

        setupNav = Theme.navButton("Grid Setup");
        simulationNav = Theme.navButton("Simulation");
        // These listeners are what make the two screens switch
        setupNav.addActionListener(e -> showSetup());
        simulationNav.addActionListener(e -> showSimulation());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(currencyLabel);
        right.add(currencyBox);
        right.add(themeToggle);
        right.add(Box.createHorizontalStrut(14));
        right.add(setupNav);
        right.add(simulationNav);
        headerContent.add(right, BorderLayout.EAST);

        // The header content lines up with the centred body content.
        // paintBackground = false so the dark header bar stays visible.
        header.add(new CenteredPanel(headerContent, CONTENT_MAX_WIDTH, 0, false) {
            @Override
            public java.awt.Dimension getPreferredSize() {
                return new java.awt.Dimension(10,
                        headerContent.getPreferredSize().height);
            }
        }, BorderLayout.CENTER);
    }

    private String themeLabel() {
        return Theme.isDark() ? "Light mode" : "Dark mode";
    }

    /** Switches between light and dark mode and restyles everything. */
    private void toggleTheme() {
        setDarkMode(!Theme.isDark());
    }

    /** Applies the chosen mode and repaints the whole window. */
    public void setDarkMode(boolean darkMode) {
        Theme.setDark(darkMode);
        themeToggle.setText(themeLabel());

        header.setBackground(Theme.header());
        bodyWrapper.setBackground(Theme.bg());
        Theme.restyle(setupPanel);
        Theme.restyle(simulationPanel);
        // Re-applies the advice banner colours for the new mode
        simulationPanel.refreshDisplay();
        highlightActiveNav();

        revalidate();
        repaint();
    }

    private void highlightActiveNav() {
        Theme.setActive(setupNav, showingSetup);
        Theme.setActive(simulationNav, !showingSetup);
        Theme.setActive(themeToggle, false);
    }

    public void showSetup() {
        cards.show(body, "setup");
        showingSetup = true;
        highlightActiveNav();
    }

    public void showSimulation() {
        cards.show(body, "simulation");
        showingSetup = false;
        highlightActiveNav();
    }

    public SetupPanel getSetupPanel() {
        return setupPanel;
    }

    public SimulationPanel getSimulationPanel() {
        return simulationPanel;
    }
}
