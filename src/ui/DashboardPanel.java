package ui;

import dao.AgencyDAO;
import dao.AgentDAO;
import dao.AreaDAO;
import dao.MandateDAO;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * UI Panel representing the executive dashboard with key performance indicators (KPIs)
 * and summary metrics across Agents, Agencies, Geographical Areas, and Mandates.
 */
public class DashboardPanel extends JPanel {

    // Design Constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Blue
    private static final Color SECONDARY_COLOR = new Color(44, 62, 80);    // Dark Blue
    private static final Color ACCENT_COLOR = new Color(39, 174, 96);       // Green
    private static final Color DANGER_COLOR = new Color(231, 76, 60);       // Red
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private static final Color CARD_BG = Color.WHITE;

    private static final Font STAT_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font STAT_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font STAT_SUB_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);

    // DAOs
    private final AgentDAO agentDAO;
    private final AgencyDAO agencyDAO;
    private final AreaDAO areaDAO;
    private final MandateDAO mandateDAO;

    // Metric Labels to Update
    private JLabel lblTotalAgentsVal;
    private JLabel lblTotalAgenciesVal;
    private JLabel lblTotalAreasVal;
    private JLabel lblTotalMandatesVal;
    private JLabel lblActiveMandatesVal;
    private JLabel lblAgentsWithMandatesVal;
    private JLabel lblAgentMaxMandatesVal;
    private JLabel lblAgentMaxMandatesSub;

    public DashboardPanel() {
        this.agentDAO = new AgentDAO();
        this.agencyDAO = new AgencyDAO();
        this.areaDAO = new AreaDAO();
        this.mandateDAO = new MandateDAO();

        initComponents();
        loadDashboardStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top Header Bar with Refresh Button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("System Overview & Statistics");
        title.setFont(HEADER_FONT);
        title.setForeground(SECONDARY_COLOR);

        JButton btnRefresh = new JButton("Refresh Statistics");
        btnRefresh.setFont(BUTTON_FONT);
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadDashboardStatistics());

        topBar.add(title, BorderLayout.WEST);
        topBar.add(btnRefresh, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Grid Container for Summary Cards
        // 7 cards: 3 columns x 3 rows with spacing
        JPanel cardsGrid = new JPanel(new GridLayout(3, 3, 20, 20));
        cardsGrid.setOpaque(false);

        // 1. Total Agents
        lblTotalAgentsVal = new JLabel("0", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Total Agents", lblTotalAgentsVal, PRIMARY_COLOR, null));

        // 2. Total Agencies
        lblTotalAgenciesVal = new JLabel("0", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Total Agencies", lblTotalAgenciesVal, SECONDARY_COLOR, null));

        // 3. Total Areas
        lblTotalAreasVal = new JLabel("0", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Geographical Areas", lblTotalAreasVal, new Color(142, 68, 173), null));

        // 4. Total Mandates
        lblTotalMandatesVal = new JLabel("0", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Total Mandates", lblTotalMandatesVal, new Color(230, 126, 34), null));

        // 5. Active Mandates
        lblActiveMandatesVal = new JLabel("0", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Active Mandates", lblActiveMandatesVal, ACCENT_COLOR, null));

        // 6. Agents With Mandates
        lblAgentsWithMandatesVal = new JLabel("0", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Agents With Mandates", lblAgentsWithMandatesVal, new Color(22, 160, 133), null));

        // 7. Agent With Max Mandates
        lblAgentMaxMandatesVal = new JLabel("N/A", SwingConstants.CENTER);
        lblAgentMaxMandatesSub = new JLabel("0 mandates", SwingConstants.CENTER);
        cardsGrid.add(createStatCard("Top Performing Agent", lblAgentMaxMandatesVal, DANGER_COLOR, lblAgentMaxMandatesSub));

        // 8. Info Card
        JLabel infoVal = new JLabel("Real Estate Management", SwingConstants.CENTER);
        infoVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoVal.setForeground(SECONDARY_COLOR);
        JLabel infoSub = new JLabel("Centralized Mandate Hub", SwingConstants.CENTER);
        infoSub.setFont(STAT_SUB_FONT);
        infoSub.setForeground(new Color(127, 140, 141));
        cardsGrid.add(createStatCard("System Status", infoVal, PRIMARY_COLOR, infoSub));

        // 9. Quick Note Card
        JLabel statusVal = new JLabel("Active & Connected", SwingConstants.CENTER);
        statusVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusVal.setForeground(ACCENT_COLOR);
        JLabel statusSub = new JLabel("All modules initialized", SwingConstants.CENTER);
        statusSub.setFont(STAT_SUB_FONT);
        statusSub.setForeground(new Color(127, 140, 141));
        cardsGrid.add(createStatCard("Database Health", statusVal, ACCENT_COLOR, statusSub));

        add(cardsGrid, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String titleText, JLabel valueLabel, Color topBorderColor, JLabel subLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);

        // Colored top border (4px) with subtle outline
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(218, 225, 231), 1, true),
                new MatteBorder(4, 0, 0, 0, topBorderColor)
        ));

        // Header Panel for Card Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(12, 10, 0, 10));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(STAT_LABEL_FONT);
        titleLabel.setForeground(SECONDARY_COLOR);
        titlePanel.add(titleLabel);

        card.add(titlePanel, BorderLayout.NORTH);

        // Center Panel for Main Stat Value
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(5, 10, 15, 10));

        valueLabel.setFont(STAT_VALUE_FONT);
        valueLabel.setForeground(topBorderColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(valueLabel);

        if (subLabel != null) {
            subLabel.setFont(STAT_SUB_FONT);
            subLabel.setForeground(new Color(127, 140, 141));
            subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(subLabel);
        }

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Loads statistics from DAOs and updates the cards.
     * Catches any SQLExceptions or connection issues gracefully.
     */
    public void loadDashboardStatistics() {
        // Total Agents
        try {
            int totalAgents = agentDAO.getTotalAgents();
            lblTotalAgentsVal.setText(String.valueOf(totalAgents));
        } catch (SQLException | Exception e) {
            lblTotalAgentsVal.setText("0");
        }

        // Total Agencies
        try {
            int totalAgencies = agencyDAO.getTotalAgencies();
            lblTotalAgenciesVal.setText(String.valueOf(totalAgencies));
        } catch (SQLException | Exception e) {
            lblTotalAgenciesVal.setText("0");
        }

        // Total Areas
        try {
            int totalAreas = areaDAO.getTotalAreas();
            lblTotalAreasVal.setText(String.valueOf(totalAreas));
        } catch (SQLException | Exception e) {
            lblTotalAreasVal.setText("0");
        }

        // Total Mandates
        try {
            int totalMandates = mandateDAO.getTotalMandates();
            lblTotalMandatesVal.setText(String.valueOf(totalMandates));
        } catch (SQLException | Exception e) {
            lblTotalMandatesVal.setText("0");
        }

        // Active Mandates
        try {
            int activeMandates = mandateDAO.getActiveMandateCount();
            lblActiveMandatesVal.setText(String.valueOf(activeMandates));
        } catch (SQLException | Exception e) {
            lblActiveMandatesVal.setText("0");
        }

        // Agents With Mandates
        try {
            java.util.List<Object[]> agentsWithMandates = agentDAO.getAgentsWithMandates();
            int count = (agentsWithMandates != null) ? agentsWithMandates.size() : 0;
            lblAgentsWithMandatesVal.setText(String.valueOf(count));
        } catch (SQLException | Exception e) {
            lblAgentsWithMandatesVal.setText("0");
        }

        // Agent with Max Mandates
        try {
            Object[] maxAgent = agentDAO.getAgentWithMaxMandates();
            if (maxAgent != null && maxAgent.length >= 2) {
                String agentName = String.valueOf(maxAgent[0]);
                int mandateCount = (Integer) maxAgent[1];
                lblAgentMaxMandatesVal.setFont(new Font("Segoe UI", Font.BOLD, 20));
                lblAgentMaxMandatesVal.setText(agentName);
                if (lblAgentMaxMandatesSub != null) {
                    lblAgentMaxMandatesSub.setText(mandateCount + " Mandate(s)");
                }
            } else {
                lblAgentMaxMandatesVal.setFont(STAT_VALUE_FONT);
                lblAgentMaxMandatesVal.setText("None");
                if (lblAgentMaxMandatesSub != null) {
                    lblAgentMaxMandatesSub.setText("0 Mandates");
                }
            }
        } catch (SQLException | Exception e) {
            lblAgentMaxMandatesVal.setFont(STAT_VALUE_FONT);
            lblAgentMaxMandatesVal.setText("N/A");
            if (lblAgentMaxMandatesSub != null) {
                lblAgentMaxMandatesSub.setText("Database Unavailable");
            }
        }
    }
}
