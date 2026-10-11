package ui;

import dao.AgentDAO;
import model.Agent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * UI Panel for Managing Agents.
 * Provides Add, Update, Delete, Search, and Display operations.
 */
public class AgentPanel extends JPanel {

    // Design Constants
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Blue
    private static final Color SECONDARY_COLOR = new Color(44, 62, 80);    // Dark Blue
    private static final Color ACCENT_COLOR = new Color(39, 174, 96);       // Green
    private static final Color DANGER_COLOR = new Color(231, 76, 60);       // Red
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private static final Color BUTTON_GRAY = new Color(127, 140, 141);

    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    // Data Access Object
    private final AgentDAO agentDAO;

    // Form Components
    private JTextField txtAgentId;
    private JTextField txtAgentName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAgencyId;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnClear;

    // Table Components
    private JTable agentTable;
    private DefaultTableModel tableModel;

    public AgentPanel() {
        this.agentDAO = new AgentDAO();
        initComponents();
        loadAgentData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // West Panel: Form and Action Buttons
        JPanel westPanel = new JPanel(new BorderLayout(0, 15));
        westPanel.setOpaque(false);
        westPanel.setPreferredSize(new Dimension(380, 0));

        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();

        westPanel.add(formPanel, BorderLayout.CENTER);
        westPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(westPanel, BorderLayout.WEST);

        // Center Panel: Table View
        JPanel centerPanel = createTablePanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new CompoundBorder(
                        new TitledBorder(
                                new EmptyBorder(0, 0, 0, 0),
                                "Agent Details",
                                TitledBorder.LEFT,
                                TitledBorder.TOP,
                                new Font("Segoe UI", Font.BOLD, 16),
                                SECONDARY_COLOR
                        ),
                        new EmptyBorder(15, 20, 15, 20)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        txtAgentId = createStyledTextField();
        txtAgentId.setToolTipText("Enter Agent ID for Search, Update, or Delete (Auto-generated on Add)");

        txtAgentName = createStyledTextField();
        txtPhone = createStyledTextField();
        txtEmail = createStyledTextField();
        txtAgencyId = createStyledTextField();

        // Row 0: Agent ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formContainer.add(createStyledLabel("Agent ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        formContainer.add(txtAgentId, gbc);

        // Row 1: Agent Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formContainer.add(createStyledLabel("Agent Name: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        formContainer.add(txtAgentName, gbc);

        // Row 2: Phone
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formContainer.add(createStyledLabel("Phone: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        formContainer.add(txtPhone, gbc);

        // Row 3: Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formContainer.add(createStyledLabel("Email: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        formContainer.add(txtEmail, gbc);

        // Row 4: Agency ID
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formContainer.add(createStyledLabel("Agency ID: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        formContainer.add(txtAgencyId, gbc);

        // Spacer to push fields to the top
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        formContainer.add(Box.createGlue(), gbc);

        return formContainer;
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        btnPanel.setOpaque(false);

        btnAdd = createStyledButton("Add Agent", ACCENT_COLOR);
        btnUpdate = createStyledButton("Update Agent", PRIMARY_COLOR);
        btnDelete = createStyledButton("Delete Agent", DANGER_COLOR);
        btnSearch = createStyledButton("Search Agent", PRIMARY_COLOR);
        btnClear = createStyledButton("Clear", BUTTON_GRAY);
        JButton btnRefresh = createStyledButton("Refresh Table", SECONDARY_COLOR);

        btnAdd.addActionListener(e -> handleAddAgent());
        btnUpdate.addActionListener(e -> handleUpdateAgent());
        btnDelete.addActionListener(e -> handleDeleteAgent());
        btnSearch.addActionListener(e -> handleSearchAgent());
        btnClear.addActionListener(e -> handleClearForm());
        btnRefresh.addActionListener(e -> loadAgentData());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnSearch);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        return btnPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columns = {"Agent ID", "Agent Name", "Phone", "Email", "Agency ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };

        agentTable = new JTable(tableModel);
        agentTable.setFont(TABLE_FONT);
        agentTable.setRowHeight(28);
        agentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agentTable.setGridColor(new Color(224, 224, 224));
        agentTable.setShowGrid(true);

        JTableHeader header = agentTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));
        header.setReorderingAllowed(false);

        // Center align ID columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        agentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        agentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Selection listener to populate form fields
        agentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && agentTable.getSelectedRow() != -1) {
                int selectedRow = agentTable.getSelectedRow();
                txtAgentId.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                txtAgentName.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                txtPhone.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                txtEmail.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
                txtAgencyId.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
            }
        });

        JScrollPane scrollPane = new JScrollPane(agentTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(SECONDARY_COLOR);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(LABEL_FONT);
        textField.setPreferredSize(new Dimension(160, 32));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(189, 195, 199), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private boolean validateFormFields(boolean isUpdate) {
        if (isUpdate) {
            String idText = txtAgentId.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select an agent or enter Agent ID to update.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Agent ID must be a valid number.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String name = txtAgentName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Agent Name cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtAgentName.requestFocus();
            return false;
        }

        String phone = txtPhone.getText().trim();
        if (!phone.matches("\\d{10,}")) {
            JOptionPane.showMessageDialog(this,
                    "Phone must contain only digits and be at least 10 characters long.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtPhone.requestFocus();
            return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address (must contain '@' and '.').",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }

        String agencyIdText = txtAgencyId.getText().trim();
        if (agencyIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Agency ID cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtAgencyId.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(agencyIdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Agency ID must be a valid integer number.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtAgencyId.requestFocus();
            return false;
        }

        return true;
    }

    private void handleAddAgent() {
        if (!validateFormFields(false)) {
            return;
        }

        try {
            String name = txtAgentName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            int agencyId = Integer.parseInt(txtAgencyId.getText().trim());

            Agent agent = new Agent(0, name, phone, email, agencyId);
            agentDAO.addAgent(agent);

            JOptionPane.showMessageDialog(this,
                    "Agent added successfully! Assigned Agent ID: " + agent.getAgentId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            handleClearForm();
            loadAgentData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while adding agent: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateAgent() {
        if (!validateFormFields(true)) {
            return;
        }

        try {
            int agentId = Integer.parseInt(txtAgentId.getText().trim());
            String name = txtAgentName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            int agencyId = Integer.parseInt(txtAgencyId.getText().trim());

            Agent agent = new Agent(agentId, name, phone, email, agencyId);
            agentDAO.updateAgent(agent);

            JOptionPane.showMessageDialog(this,
                    "Agent (ID: " + agentId + ") updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            handleClearForm();
            loadAgentData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while updating agent: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAgent() {
        String idText = txtAgentId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select or enter an Agent ID to delete.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int agentId;
        try {
            agentId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Agent ID must be a numeric value.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Agent ID " + agentId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                agentDAO.deleteAgent(agentId);
                JOptionPane.showMessageDialog(this,
                        "Agent deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                handleClearForm();
                loadAgentData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error while deleting agent: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearchAgent() {
        String query = txtAgentName.getText().trim();
        String idQuery = txtAgentId.getText().trim();

        try {
            tableModel.setRowCount(0);

            // If ID field is provided and numeric, search by ID first
            if (!idQuery.isEmpty()) {
                try {
                    int agentId = Integer.parseInt(idQuery);
                    Agent agent = agentDAO.getAgentById(agentId);
                    if (agent != null) {
                        tableModel.addRow(new Object[]{
                                agent.getAgentId(),
                                agent.getAgentName(),
                                agent.getPhone(),
                                agent.getEmail(),
                                agent.getAgencyId()
                        });
                        JOptionPane.showMessageDialog(this,
                                "Found 1 agent matching ID " + agentId + ".",
                                "Search Result", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No agent found with ID " + agentId + ".",
                                "Search Result", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    // Not a numeric ID, fall through to name search
                }
            }

            // Search by name
            if (!query.isEmpty()) {
                List<Agent> results = agentDAO.searchAgentsByName(query);
                for (Agent a : results) {
                    tableModel.addRow(new Object[]{
                            a.getAgentId(),
                            a.getAgentName(),
                            a.getPhone(),
                            a.getEmail(),
                            a.getAgencyId()
                    });
                }
                JOptionPane.showMessageDialog(this,
                        "Found " + results.size() + " agent(s) matching '" + query + "'.",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter Agent ID or Agent Name to search.",
                        "Search Input", JOptionPane.WARNING_MESSAGE);
                loadAgentData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error during search: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClearForm() {
        txtAgentId.setText("");
        txtAgentName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAgencyId.setText("");
        agentTable.clearSelection();
    }

    public void loadAgentData() {
        try {
            tableModel.setRowCount(0);
            List<Agent> agentList = agentDAO.getAllAgents();
            for (Agent a : agentList) {
                tableModel.addRow(new Object[]{
                        a.getAgentId(),
                        a.getAgentName(),
                        a.getPhone(),
                        a.getEmail(),
                        a.getAgencyId()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading agents from database: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error loading agents: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
