package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Application Window for Real Estate Agent Mandate Management System.
 * Hosts navigation tabs for Dashboard, Agents, Agencies, Areas, Mandates, and Reports.
 */
public class MainFrame extends JFrame {

    // Design Constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Blue
    private static final Color SECONDARY_COLOR = new Color(44, 62, 80);    // Dark Blue
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Tab Panels
    private DashboardPanel dashboardPanel;
    private AgentPanel agentPanel;
    private AgencyPanel agencyPanel;
    private JPanel areaPanel;
    private JPanel mandatePanel;
    private JPanel reportPanel;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        super("Real Estate Agent Mandate Management System");
        setupLookAndFeel();
        initComponents();
    }

    private void setupLookAndFeel() {
        try {
            // Attempt System Look and Feel first, fallback to Nimbus
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {
                // Keep default Swing L&F
            }
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null); // Centered on screen

        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Header Panel at the top
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Navigation
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(SECONDARY_COLOR);

        // 1. Dashboard Tab
        dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);

        // 2. Agents Tab
        agentPanel = new AgentPanel();
        tabbedPane.addTab("Agents", agentPanel);

        // 3. Agencies Tab
        agencyPanel = new AgencyPanel();
        tabbedPane.addTab("Agencies", agencyPanel);

        // 4. Geographical Areas Tab
        areaPanel = createAreaPanelInstance();
        tabbedPane.addTab("Geographical Areas", areaPanel);

        // 5. Mandates Tab
        mandatePanel = createMandatePanelInstance();
        tabbedPane.addTab("Mandates", mandatePanel);

        // 6. Reports Tab
        reportPanel = createReportPanelInstance();
        tabbedPane.addTab("Reports", reportPanel);

        // Tab change listener to auto-refresh data when tabs are switched
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent == dashboardPanel) {
                dashboardPanel.loadDashboardStatistics();
            } else if (selectedComponent == agentPanel) {
                agentPanel.loadAgentData();
            } else if (selectedComponent == agencyPanel) {
                agencyPanel.loadAgencyData();
            } else if (selectedComponent instanceof AreaPanel) {
                ((AreaPanel) selectedComponent).loadAreaData();
            } else if (selectedComponent instanceof MandatePanel) {
                ((MandatePanel) selectedComponent).loadMandateData();
            } else if (selectedComponent instanceof ReportPanel) {
                ((ReportPanel) selectedComponent).generateSelectedReport();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(16, 25, 16, 25));

        JLabel titleLabel = new JLabel("Real Estate Agent Mandate Management System");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("Enterprise Portal");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(new Color(189, 195, 199));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(subLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createAreaPanelInstance() {
        return new AreaPanel();
    }

    private JPanel createMandatePanelInstance() {
        return new MandatePanel();
    }

    private JPanel createReportPanelInstance() {
        return new ReportPanel();
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        JLabel label = new JLabel(title + " Module Loading / Under Construction");
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(PRIMARY_COLOR);
        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
