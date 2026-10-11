package ui;

import dao.AgencyDAO;
import dao.AgentDAO;
import dao.AreaDAO;
import dao.MandateDAO;
import model.Mandate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel for managing Mandates in the Real Estate Mandate Management System.
 * Supports adding, updating, deleting, searching, and viewing mandates.
 */
public class MandatePanel extends JPanel {

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

    // Data Access Objects
    private final MandateDAO mandateDAO;
    private final AgentDAO agentDAO;
    private final AgencyDAO agencyDAO;
    private final AreaDAO areaDAO;

    // Form Components
    private JTextField txtMandateId;
    private JTextField txtAgentId;
    private JTextField txtAgencyId;
    private JTextField txtAreaId;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JComboBox<String> cmbMandateType;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnClear;

    // Table Components
    private JTable mandateTable;
    private DefaultTableModel tableModel;

    public MandatePanel() {
        this.mandateDAO = new MandateDAO();
        this.agentDAO = new AgentDAO();
        this.agencyDAO = new AgencyDAO();
        this.areaDAO = new AreaDAO();

        initializeUI();
        loadMandateData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // North: Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // West: Form Container
        JPanel formContainer = createFormPanel();
        add(formContainer, BorderLayout.WEST);

        // Center: Table Container
        JPanel tableContainer = createTablePanel();
        add(tableContainer, BorderLayout.CENTER);

        // Events
        setupEventHandlers();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Mandate Management");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(360, 600));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                " Mandate Information ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                BUTTON_FONT,
                SECONDARY_COLOR
        );
        formPanel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 15, 10, 15)));

        // Fields Grid
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Mandate ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        JLabel lblMandateId = new JLabel("Mandate ID:");
        lblMandateId.setFont(LABEL_FONT);
        fieldsPanel.add(lblMandateId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtMandateId = new JTextField();
        txtMandateId.setFont(LABEL_FONT);
        txtMandateId.setEditable(false);
        txtMandateId.setBackground(new Color(245, 245, 245));
        fieldsPanel.add(txtMandateId, gbc);

        // 2. Agent ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.35;
        JLabel lblAgentId = new JLabel("Agent ID:");
        lblAgentId.setFont(LABEL_FONT);
        fieldsPanel.add(lblAgentId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtAgentId = new JTextField();
        txtAgentId.setFont(LABEL_FONT);
        fieldsPanel.add(txtAgentId, gbc);

        // 3. Agency ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.35;
        JLabel lblAgencyId = new JLabel("Agency ID:");
        lblAgencyId.setFont(LABEL_FONT);
        fieldsPanel.add(lblAgencyId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtAgencyId = new JTextField();
        txtAgencyId.setFont(LABEL_FONT);
        fieldsPanel.add(txtAgencyId, gbc);

        // 4. Area ID
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.35;
        JLabel lblAreaId = new JLabel("Area ID:");
        lblAreaId.setFont(LABEL_FONT);
        fieldsPanel.add(lblAreaId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtAreaId = new JTextField();
        txtAreaId.setFont(LABEL_FONT);
        fieldsPanel.add(txtAreaId, gbc);

        // 5. Start Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.35;
        JLabel lblStartDate = new JLabel("Start Date:");
        lblStartDate.setFont(LABEL_FONT);
        fieldsPanel.add(lblStartDate, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtStartDate = new JTextField();
        txtStartDate.setFont(LABEL_FONT);
        txtStartDate.setToolTipText("Format: YYYY-MM-DD");
        fieldsPanel.add(txtStartDate, gbc);

        // Hint under start date
        gbc.gridx = 1;
        gbc.gridy = 5;
        JLabel lblStartHint = new JLabel("YYYY-MM-DD (e.g. 2026-01-01)");
        lblStartHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStartHint.setForeground(Color.GRAY);
        fieldsPanel.add(lblStartHint, gbc);

        // 6. End Date
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.35;
        JLabel lblEndDate = new JLabel("End Date:");
        lblEndDate.setFont(LABEL_FONT);
        fieldsPanel.add(lblEndDate, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        txtEndDate = new JTextField();
        txtEndDate.setFont(LABEL_FONT);
        txtEndDate.setToolTipText("Format: YYYY-MM-DD");
        fieldsPanel.add(txtEndDate, gbc);

        // Hint under end date
        gbc.gridx = 1;
        gbc.gridy = 7;
        JLabel lblEndHint = new JLabel("YYYY-MM-DD (e.g. 2026-12-31)");
        lblEndHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblEndHint.setForeground(Color.GRAY);
        fieldsPanel.add(lblEndHint, gbc);

        // 7. Mandate Type
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.35;
        JLabel lblType = new JLabel("Mandate Type:");
        lblType.setFont(LABEL_FONT);
        fieldsPanel.add(lblType, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        cmbMandateType = new JComboBox<>(new String[]{"SALE", "RENT", "PURCHASE", "LEASE"});
        cmbMandateType.setFont(LABEL_FONT);
        cmbMandateType.setBackground(Color.WHITE);
        fieldsPanel.add(cmbMandateType, gbc);

        formPanel.add(fieldsPanel);
        formPanel.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 0, 8));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setMaximumSize(new Dimension(320, 210));

        btnAdd = createStyledButton("Add Mandate", ACCENT_COLOR);
        btnUpdate = createStyledButton("Update Mandate", PRIMARY_COLOR);
        btnDelete = createStyledButton("Delete Mandate", DANGER_COLOR);
        btnSearch = createStyledButton("Search Mandate", SECONDARY_COLOR);
        btnClear = createStyledButton("Clear", new Color(127, 140, 141));

        buttonsPanel.add(btnAdd);
        buttonsPanel.add(btnUpdate);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnSearch);
        buttonsPanel.add(btnClear);

        formPanel.add(buttonsPanel);
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                " Mandates List ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                BUTTON_FONT,
                SECONDARY_COLOR
        );
        tableContainer.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        String[] columnNames = {
                "Mandate ID", "Agent ID", "Agency ID", "Area ID",
                "Start Date", "End Date", "Mandate Type", "Status"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        mandateTable = new JTable(tableModel);
        mandateTable.setFont(TABLE_FONT);
        mandateTable.setRowHeight(28);
        mandateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mandateTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        mandateTable.getTableHeader().setBackground(SECONDARY_COLOR);
        mandateTable.getTableHeader().setForeground(Color.WHITE);
        mandateTable.getTableHeader().setReorderingAllowed(false);

        // Center align integer IDs and date columns, color status
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columnNames.length; i++) {
            if (i != 6) { // Except mandate type which looks fine left/center aligned
                mandateTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Custom status cell renderer
        mandateTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected && value != null) {
                    if ("Active".equalsIgnoreCase(value.toString())) {
                        c.setForeground(new Color(39, 174, 96));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if ("Expired".equalsIgnoreCase(value.toString())) {
                        c.setForeground(new Color(231, 76, 60));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(mandateTable);
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
        button.setPreferredSize(new Dimension(140, 36));
        return button;
    }

    private void setupEventHandlers() {
        btnAdd.addActionListener(e -> addMandate());
        btnUpdate.addActionListener(e -> updateMandate());
        btnDelete.addActionListener(e -> deleteMandate());
        btnSearch.addActionListener(e -> searchMandate());
        btnClear.addActionListener(e -> clearForm());

        mandateTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = mandateTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromSelectedRow(selectedRow);
                }
            }
        });
    }

    private void populateFormFromSelectedRow(int row) {
        txtMandateId.setText(tableModel.getValueAt(row, 0).toString());
        txtAgentId.setText(tableModel.getValueAt(row, 1).toString());
        txtAgencyId.setText(tableModel.getValueAt(row, 2).toString());
        txtAreaId.setText(tableModel.getValueAt(row, 3).toString());
        txtStartDate.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        txtEndDate.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");

        Object typeObj = tableModel.getValueAt(row, 6);
        if (typeObj != null) {
            cmbMandateType.setSelectedItem(typeObj.toString());
        }
    }

    public void loadMandateData() {
        try {
            tableModel.setRowCount(0);
            List<Mandate> mandates = mandateDAO.getAllMandates();
            LocalDate today = LocalDate.now();

            for (Mandate m : mandates) {
                String status = m.isActive(today) ? "Active" : "Expired";
                tableModel.addRow(new Object[]{
                        m.getMandateId(),
                        m.getAgentId(),
                        m.getAgencyId(),
                        m.getAreaId(),
                        m.getStartDate(),
                        m.getEndDate(),
                        m.getMandateType(),
                        status
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while loading mandates: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMandate() {
        String agentStr = txtAgentId.getText().trim();
        String agencyStr = txtAgencyId.getText().trim();
        String areaStr = txtAreaId.getText().trim();
        String startStr = txtStartDate.getText().trim();
        String endStr = txtEndDate.getText().trim();
        String mandateType = (String) cmbMandateType.getSelectedItem();

        // 1. Basic empty check
        if (agentStr.isEmpty() || agencyStr.isEmpty() || areaStr.isEmpty() ||
                startStr.isEmpty() || endStr.isEmpty() || mandateType == null) {
            JOptionPane.showMessageDialog(this,
                    "All fields (Agent ID, Agency ID, Area ID, Start Date, End Date, Type) are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Number parsing
        int agentId;
        int agencyId;
        int areaId;
        try {
            agentId = Integer.parseInt(agentStr);
            agencyId = Integer.parseInt(agencyStr);
            areaId = Integer.parseInt(areaStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Agent ID, Agency ID, and Area ID must be valid integers.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Foreign key checks via DAOs
        try {
            if (!agentDAO.agentExists(agentId)) {
                JOptionPane.showMessageDialog(this,
                        "Agent with ID " + agentId + " does not exist.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                txtAgentId.requestFocus();
                return;
            }

            if (!agencyDAO.agencyExists(agencyId)) {
                JOptionPane.showMessageDialog(this,
                        "Agency with ID " + agencyId + " does not exist.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                txtAgencyId.requestFocus();
                return;
            }

            if (!areaDAO.areaExists(areaId)) {
                JOptionPane.showMessageDialog(this,
                        "Geographical Area with ID " + areaId + " does not exist.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                txtAreaId.requestFocus();
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while validating foreign keys: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. Date parsing
        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Start Date format. Please use YYYY-MM-DD (e.g. 2026-01-15).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtStartDate.requestFocus();
            return;
        }

        try {
            endDate = LocalDate.parse(endStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid End Date format. Please use YYYY-MM-DD (e.g. 2026-12-31).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtEndDate.requestFocus();
            return;
        }

        // 5. Date logic validation (endDate > startDate)
        Mandate mandate = new Mandate(0, agentId, agencyId, areaId, startDate, endDate, mandateType);
        try {
            mandate.validateDates();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtEndDate.requestFocus();
            return;
        }

        // 6. Persistence
        try {
            mandateDAO.addMandate(mandate);
            JOptionPane.showMessageDialog(this,
                    "Mandate added successfully! (Assigned ID: " + mandate.getMandateId() + ")",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadMandateData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to add mandate: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMandate() {
        String idStr = txtMandateId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a mandate from the table to update.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mandateId;
        try {
            mandateId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Mandate ID format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String endStr = txtEndDate.getText().trim();
        String startStr = txtStartDate.getText().trim();
        String mandateType = (String) cmbMandateType.getSelectedItem();

        if (endStr.isEmpty() || mandateType == null) {
            JOptionPane.showMessageDialog(this,
                    "End Date and Mandate Type are required for updating.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid End Date format. Please use YYYY-MM-DD.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtEndDate.requestFocus();
            return;
        }

        try {
            Mandate existingMandate = mandateDAO.getMandateById(mandateId);
            if (existingMandate == null) {
                JOptionPane.showMessageDialog(this,
                        "Mandate with ID " + mandateId + " not found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate startDate = existingMandate.getStartDate();
            if (!startStr.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startStr);
                } catch (DateTimeParseException ignored) {
                    // fallback to existing
                }
            }

            // Validate that endDate > startDate
            if (startDate != null && !endDate.isAfter(startDate)) {
                JOptionPane.showMessageDialog(this,
                        "Invalid Mandate: End date must be greater than start date.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                txtEndDate.requestFocus();
                return;
            }

            existingMandate.setEndDate(endDate);
            existingMandate.setMandateType(mandateType);

            mandateDAO.updateMandate(existingMandate);
            JOptionPane.showMessageDialog(this,
                    "Mandate ID " + mandateId + " updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadMandateData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update mandate: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMandate() {
        String idStr = txtMandateId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a mandate from the table to delete.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int mandateId;
        try {
            mandateId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Mandate ID format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Mandate ID: " + mandateId + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                mandateDAO.deleteMandate(mandateId);
                JOptionPane.showMessageDialog(this,
                        "Mandate deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMandateData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete mandate: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchMandate() {
        String idSearch = txtMandateId.getText().trim();
        String typeSearch = (String) cmbMandateType.getSelectedItem();

        // Prompt user options if both are blank or mandate id is blank
        Object[] searchOptions = {"Search by Mandate ID", "Search by Mandate Type", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choose search criteria:",
                "Search Mandate",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                searchOptions,
                searchOptions[0]);

        if (choice == 0) { // By ID
            String input = JOptionPane.showInputDialog(this,
                    "Enter Mandate ID:",
                    idSearch.isEmpty() ? "" : idSearch);
            if (input == null || input.trim().isEmpty()) {
                return;
            }
            try {
                int mandateId = Integer.parseInt(input.trim());
                Mandate mandate = mandateDAO.getMandateById(mandateId);
                tableModel.setRowCount(0);
                if (mandate != null) {
                    LocalDate today = LocalDate.now();
                    String status = mandate.isActive(today) ? "Active" : "Expired";
                    tableModel.addRow(new Object[]{
                            mandate.getMandateId(),
                            mandate.getAgentId(),
                            mandate.getAgencyId(),
                            mandate.getAreaId(),
                            mandate.getStartDate(),
                            mandate.getEndDate(),
                            mandate.getMandateType(),
                            status
                    });
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No mandate found with ID " + mandateId + ".",
                            "Not Found",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadMandateData();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Mandate ID must be a valid integer.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (choice == 1) { // By Type
            String[] types = {"SALE", "RENT", "PURCHASE", "LEASE"};
            String selected = (String) JOptionPane.showInputDialog(this,
                    "Select Mandate Type:",
                    "Search by Type",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    types,
                    typeSearch != null ? typeSearch : types[0]);

            if (selected == null) {
                return;
            }

            try {
                tableModel.setRowCount(0);
                List<Mandate> results = mandateDAO.searchMandatesByType(selected);
                LocalDate today = LocalDate.now();
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No mandates found for type: " + selected,
                            "Search Results",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadMandateData();
                } else {
                    for (Mandate m : results) {
                        String status = m.isActive(today) ? "Active" : "Expired";
                        tableModel.addRow(new Object[]{
                                m.getMandateId(),
                                m.getAgentId(),
                                m.getAgencyId(),
                                m.getAreaId(),
                                m.getStartDate(),
                                m.getEndDate(),
                                m.getMandateType(),
                                status
                        });
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtMandateId.setText("");
        txtAgentId.setText("");
        txtAgencyId.setText("");
        txtAreaId.setText("");
        txtStartDate.setText("");
        txtEndDate.setText("");
        cmbMandateType.setSelectedIndex(0);
        mandateTable.clearSelection();
    }
}
