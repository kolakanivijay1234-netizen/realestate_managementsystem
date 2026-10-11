package ui;

import dao.AgencyDAO;
import dao.AgentDAO;
import dao.AreaDAO;
import dao.MandateDAO;
import model.Agency;
import model.Agent;
import model.GeographicalArea;
import model.Mandate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for generating and displaying analytical reports and summaries.
 * Features 15 comprehensive reports backed by DAO methods.
 */
public class ReportPanel extends JPanel {

    // Design Constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(44, 62, 80);
    private static final Color ACCENT_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BG_COLOR = new Color(236, 240, 241);

    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // DAOs
    private final AgentDAO agentDAO;
    private final AgencyDAO agencyDAO;
    private final AreaDAO areaDAO;
    private final MandateDAO mandateDAO;

    // UI Components
    private JComboBox<String> cmbReports;
    private JButton btnGenerate;
    private JButton btnRefresh;
    private JButton btnClear;
    private JLabel lblReportStatus;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    private static final String[] REPORT_OPTIONS = {
            "1. All Agents",
            "2. All Agencies with Type",
            "3. All Geographical Areas",
            "4. Active Mandates on Date",
            "5. Total Agents Count",
            "6. Total Agencies Count",
            "7. Total Mandates Count",
            "8. Mandates Per Agent",
            "9. Agents with At Least One Mandate",
            "10. Agencies That Issued Mandates",
            "11. Agent with Maximum Mandates",
            "12. Active and Expired Mandates",
            "13. Mandates Grouped by Type",
            "14. Mandates Grouped by Agency",
            "15. Mandates Grouped by Area"
    };

    public ReportPanel() {
        this.agentDAO = new AgentDAO();
        this.agencyDAO = new AgencyDAO();
        this.areaDAO = new AreaDAO();
        this.mandateDAO = new MandateDAO();

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // North: Header & Controls
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(BG_COLOR);

        JPanel headerPanel = createHeaderPanel();
        JPanel controlPanel = createControlPanel();

        topContainer.add(headerPanel);
        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(controlPanel);

        add(topContainer, BorderLayout.NORTH);

        // Center: Results Table
        JPanel tableContainer = createTablePanel();
        add(tableContainer, BorderLayout.CENTER);

        // Wire handlers
        setupEventHandlers();

        // Default: generate first report
        generateSelectedReport();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("System Analytics & Reports");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        lblReportStatus = new JLabel("Ready");
        lblReportStatus.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblReportStatus.setForeground(new Color(189, 195, 199));
        headerPanel.add(lblReportStatus, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(Color.WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                " Select Analytical Report ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                BUTTON_FONT,
                SECONDARY_COLOR
        );
        controlPanel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(5, 10, 5, 10)));

        JLabel lblSelect = new JLabel("Report Type:");
        lblSelect.setFont(LABEL_FONT);
        controlPanel.add(lblSelect);

        cmbReports = new JComboBox<>(REPORT_OPTIONS);
        cmbReports.setFont(LABEL_FONT);
        cmbReports.setPreferredSize(new Dimension(300, 34));
        cmbReports.setBackground(Color.WHITE);
        controlPanel.add(cmbReports);

        btnGenerate = createStyledButton("Generate Report", ACCENT_COLOR);
        btnRefresh = createStyledButton("Refresh", PRIMARY_COLOR);
        btnClear = createStyledButton("Clear", new Color(127, 140, 141));

        controlPanel.add(btnGenerate);
        controlPanel.add(btnRefresh);
        controlPanel.add(btnClear);

        return controlPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                " Report Results ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                BUTTON_FONT,
                SECONDARY_COLOR
        );
        tableContainer.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setFont(TABLE_FONT);
        reportTable.setRowHeight(28);
        reportTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        reportTable.getTableHeader().setBackground(SECONDARY_COLOR);
        reportTable.getTableHeader().setForeground(Color.WHITE);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(145, 36));
        return button;
    }

    private void setupEventHandlers() {
        btnGenerate.addActionListener(e -> generateSelectedReport());
        btnRefresh.addActionListener(e -> generateSelectedReport());
        btnClear.addActionListener(e -> clearReport());
    }

    private void clearReport() {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        lblReportStatus.setText("Table cleared.");
    }

    public void generateSelectedReport() {
        int index = cmbReports.getSelectedIndex();
        lblReportStatus.setText("Running report " + (index + 1) + "...");

        try {
            switch (index) {
                case 0:
                    generateAllAgentsReport();
                    break;
                case 1:
                    generateAllAgenciesReport();
                    break;
                case 2:
                    generateAllAreasReport();
                    break;
                case 3:
                    generateActiveMandatesOnDateReport();
                    break;
                case 4:
                    generateTotalAgentsCountReport();
                    break;
                case 5:
                    generateTotalAgenciesCountReport();
                    break;
                case 6:
                    generateTotalMandatesCountReport();
                    break;
                case 7:
                    generateMandatesPerAgentReport();
                    break;
                case 8:
                    generateAgentsWithMandatesReport();
                    break;
                case 9:
                    generateAgenciesWithMandatesReport();
                    break;
                case 10:
                    generateAgentWithMaxMandatesReport();
                    break;
                case 11:
                    generateActiveAndExpiredMandatesReport();
                    break;
                case 12:
                    generateMandatesGroupedByTypeReport();
                    break;
                case 13:
                    generateMandatesGroupedByAgencyReport();
                    break;
                case 14:
                    generateMandatesGroupedByAreaReport();
                    break;
                default:
                    JOptionPane.showMessageDialog(this,
                            "Unrecognized report selection.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while generating report: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            lblReportStatus.setText("Error generating report.");
        }
    }

    // 1. All Agents
    private void generateAllAgentsReport() throws SQLException {
        String[] cols = {"Agent ID", "Agent Name", "Phone", "Email", "Agency ID"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Agent> list = agentDAO.getAllAgents();
        for (Agent a : list) {
            tableModel.addRow(new Object[]{
                    a.getAgentId(),
                    a.getAgentName(),
                    a.getPhone(),
                    a.getEmail(),
                    a.getAgencyId()
            });
        }
        lblReportStatus.setText("Showing all " + list.size() + " agent(s).");
    }

    // 2. All Agencies with Type & Polymorphic Description
    private void generateAllAgenciesReport() throws SQLException {
        String[] cols = {"Agency ID", "Agency Name", "Agency Type", "Address", "Phone", "Description"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Agency> list = agencyDAO.getAllAgencies();
        for (Agency ag : list) {
            tableModel.addRow(new Object[]{
                    ag.getAgencyId(),
                    ag.getAgencyName(),
                    ag.getAgencyType(),
                    ag.getAddress(),
                    ag.getPhone(),
                    ag.getAgencyDescription()
            });
        }
        lblReportStatus.setText("Showing all " + list.size() + " agency/agencies.");
    }

    // 3. All Geographical Areas
    private void generateAllAreasReport() throws SQLException {
        String[] cols = {"Area ID", "Area Name", "Population"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<GeographicalArea> list = areaDAO.getAllAreas();
        for (GeographicalArea a : list) {
            tableModel.addRow(new Object[]{
                    a.getAreaId(),
                    a.getAreaName(),
                    a.getPopulation()
            });
        }
        lblReportStatus.setText("Showing all " + list.size() + " geographical area(s).");
    }

    // 4. Active Mandates on Date
    private void generateActiveMandatesOnDateReport() throws SQLException {
        String defaultDate = LocalDate.now().toString();
        String input = JOptionPane.showInputDialog(this,
                "Enter target date to check active mandates (YYYY-MM-DD):",
                defaultDate);

        if (input == null || input.trim().isEmpty()) {
            lblReportStatus.setText("Report cancelled.");
            return;
        }

        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(input.trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Expected YYYY-MM-DD (e.g. 2026-05-15).",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
            lblReportStatus.setText("Invalid date entered.");
            return;
        }

        String[] cols = {"Mandate ID", "Agent ID", "Agency ID", "Area ID", "Start Date", "End Date", "Mandate Type"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Mandate> list = mandateDAO.getActiveMandates(targetDate);
        for (Mandate m : list) {
            tableModel.addRow(new Object[]{
                    m.getMandateId(),
                    m.getAgentId(),
                    m.getAgencyId(),
                    m.getAreaId(),
                    m.getStartDate(),
                    m.getEndDate(),
                    m.getMandateType()
            });
        }
        lblReportStatus.setText("Found " + list.size() + " active mandate(s) on " + targetDate + ".");
        JOptionPane.showMessageDialog(this,
                "Found " + list.size() + " active mandate(s) on " + targetDate + ".",
                "Active Mandates Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 5. Total Agents Count
    private void generateTotalAgentsCountReport() throws SQLException {
        int count = agentDAO.getTotalAgents();
        String[] cols = {"Metric", "Total Count"};
        tableModel.setDataVector(new Object[][]{
                {"Total Agents Registered", count}
        }, cols);
        centerColumns();

        lblReportStatus.setText("Total Agents: " + count);
        JOptionPane.showMessageDialog(this,
                "Total Agents Count: " + count,
                "Total Agents",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 6. Total Agencies Count
    private void generateTotalAgenciesCountReport() throws SQLException {
        int count = agencyDAO.getTotalAgencies();
        String[] cols = {"Metric", "Total Count"};
        tableModel.setDataVector(new Object[][]{
                {"Total Agencies Registered", count}
        }, cols);
        centerColumns();

        lblReportStatus.setText("Total Agencies: " + count);
        JOptionPane.showMessageDialog(this,
                "Total Agencies Count: " + count,
                "Total Agencies",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 7. Total Mandates Count
    private void generateTotalMandatesCountReport() throws SQLException {
        int count = mandateDAO.getTotalMandates();
        int activeCount = mandateDAO.getActiveMandateCount();
        String[] cols = {"Metric", "Count"};
        tableModel.setDataVector(new Object[][]{
                {"Total Mandates (All)", count},
                {"Currently Active Mandates", activeCount},
                {"Currently Inactive/Expired Mandates", (count - activeCount)}
        }, cols);
        centerColumns();

        lblReportStatus.setText("Total Mandates: " + count + " (Active: " + activeCount + ")");
        JOptionPane.showMessageDialog(this,
                "Total Mandates Count: " + count + "\nCurrently Active Mandates: " + activeCount,
                "Total Mandates",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 8. Mandates Per Agent
    private void generateMandatesPerAgentReport() throws SQLException {
        String[] cols = {"Agent ID", "Agent Name", "Mandate Count"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = agentDAO.getMandateCountPerAgent();
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Showing mandate counts across " + list.size() + " agent(s).");
    }

    // 9. Agents with At Least One Mandate
    private void generateAgentsWithMandatesReport() throws SQLException {
        String[] cols = {"Agent ID", "Agent Name", "Phone", "Email", "Agency ID", "Mandate ID", "Mandate Type"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = agentDAO.getAgentsWithMandates();
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Found " + list.size() + " mandate record(s) for active agents.");
    }

    // 10. Agencies That Issued Mandates
    private void generateAgenciesWithMandatesReport() throws SQLException {
        String[] cols = {"Agency ID", "Agency Name", "Agency Type", "Mandate ID", "Mandate Type"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = agencyDAO.getAgenciesWithMandates();
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Found " + list.size() + " mandate record(s) linked to agencies.");
    }

    // 11. Agent with Maximum Mandates
    private void generateAgentWithMaxMandatesReport() throws SQLException {
        String[] cols = {"Agent Name", "Mandate Count"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        Object[] result = agentDAO.getAgentWithMaxMandates();
        if (result != null && result.length >= 2) {
            tableModel.addRow(result);
            lblReportStatus.setText("Top Agent: " + result[0] + " (" + result[1] + " mandates)");
            JOptionPane.showMessageDialog(this,
                    "Top Agent: " + result[0] + "\nTotal Mandates: " + result[1],
                    "Agent with Maximum Mandates",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            lblReportStatus.setText("No mandates found in system.");
            JOptionPane.showMessageDialog(this,
                    "No mandate records found in the database.",
                    "Agent with Maximum Mandates",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 12. Active and Expired Mandates
    private void generateActiveAndExpiredMandatesReport() throws SQLException {
        String[] cols = {"Mandate ID", "Agent ID", "Agency ID", "Area ID", "Start Date", "End Date", "Mandate Type", "Status"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = mandateDAO.getActiveAndExpiredMandates(LocalDate.now());
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Evaluated " + list.size() + " mandate(s) as of " + LocalDate.now() + ".");
    }

    // 13. Mandates Grouped by Type
    private void generateMandatesGroupedByTypeReport() throws SQLException {
        String[] cols = {"Mandate Type", "Mandate Count"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = mandateDAO.getMandatesGroupedByType();
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Mandates grouped by " + list.size() + " type(s).");
    }

    // 14. Mandates Grouped by Agency
    private void generateMandatesGroupedByAgencyReport() throws SQLException {
        String[] cols = {"Agency ID", "Agency Name", "Mandate Count"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = agencyDAO.getMandatesGroupedByAgency();
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Mandates grouped across " + list.size() + " agency/agencies.");
    }

    // 15. Mandates Grouped by Area
    private void generateMandatesGroupedByAreaReport() throws SQLException {
        String[] cols = {"Area ID", "Area Name", "Population", "Mandate Count"};
        tableModel.setDataVector(new Object[][]{}, cols);
        centerColumns();

        List<Object[]> list = areaDAO.getMandatesGroupedByArea();
        for (Object[] row : list) {
            tableModel.addRow(row);
        }
        lblReportStatus.setText("Mandates grouped across " + list.size() + " geographical zone(s).");
    }

    private void centerColumns() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            reportTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
