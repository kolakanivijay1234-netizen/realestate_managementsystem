package ui;

import dao.AgencyDAO;
import model.Agency;
import model.FranchiseAgency;
import model.IndependentAgency;

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
 * UI Panel for Managing Real Estate Agencies.
 * Demonstrates OOP polymorphism with IndependentAgency and FranchiseAgency subclasses.
 */
public class AgencyPanel extends JPanel {

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
    private final AgencyDAO agencyDAO;

    // Form Components
    private JTextField txtAgencyId;
    private JTextField txtAgencyName;
    private JComboBox<String> cmbAgencyType;
    private JTextField txtAddress;
    private JTextField txtPhone;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnClear;

    // Table Components
    private JTable agencyTable;
    private DefaultTableModel tableModel;

    public AgencyPanel() {
        this.agencyDAO = new AgencyDAO();
        initComponents();
        loadAgencyData();
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
                                "Agency Details",
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
        txtAgencyId = createStyledTextField();
        txtAgencyId.setToolTipText("Enter Agency ID for Search, Update, or Delete (Auto-generated on Add)");

        txtAgencyName = createStyledTextField();

        cmbAgencyType = new JComboBox<>(new String[]{"Independent", "Franchise"});
        cmbAgencyType.setFont(LABEL_FONT);
        cmbAgencyType.setBackground(Color.WHITE);
        cmbAgencyType.setPreferredSize(new Dimension(160, 32));

        txtAddress = createStyledTextField();
        txtPhone = createStyledTextField();

        // Row 0: Agency ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formContainer.add(createStyledLabel("Agency ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formContainer.add(txtAgencyId, gbc);

        // Row 1: Agency Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        formContainer.add(createStyledLabel("Agency Name: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.65;
        formContainer.add(txtAgencyName, gbc);

        // Row 2: Agency Type
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        formContainer.add(createStyledLabel("Agency Type: *"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.65;
        formContainer.add(cmbAgencyType, gbc);

        // Row 3: Address
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        formContainer.add(createStyledLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.65;
        formContainer.add(txtAddress, gbc);

        // Row 4: Phone
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35;
        formContainer.add(createStyledLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.65;
        formContainer.add(txtPhone, gbc);

        // Spacer to push fields to the top
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        formContainer.add(Box.createGlue(), gbc);

        return formContainer;
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        btnPanel.setOpaque(false);

        btnAdd = createStyledButton("Add Agency", ACCENT_COLOR);
        btnUpdate = createStyledButton("Update Agency", PRIMARY_COLOR);
        btnDelete = createStyledButton("Delete Agency", DANGER_COLOR);
        btnSearch = createStyledButton("Search Agency", PRIMARY_COLOR);
        btnClear = createStyledButton("Clear", BUTTON_GRAY);
        JButton btnRefresh = createStyledButton("Refresh Table", SECONDARY_COLOR);

        btnAdd.addActionListener(e -> handleAddAgency());
        btnUpdate.addActionListener(e -> handleUpdateAgency());
        btnDelete.addActionListener(e -> handleDeleteAgency());
        btnSearch.addActionListener(e -> handleSearchAgency());
        btnClear.addActionListener(e -> handleClearForm());
        btnRefresh.addActionListener(e -> loadAgencyData());

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

        String[] columns = {"Agency ID", "Agency Name", "Agency Type", "Address", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only
            }
        };

        agencyTable = new JTable(tableModel);
        agencyTable.setFont(TABLE_FONT);
        agencyTable.setRowHeight(28);
        agencyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        agencyTable.setGridColor(new Color(224, 224, 224));
        agencyTable.setShowGrid(true);

        JTableHeader header = agencyTable.getTableHeader();
        header.setFont(TABLE_HEADER_FONT);
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 35));
        header.setReorderingAllowed(false);

        // Center align ID and Type columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        agencyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        agencyTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Selection listener to populate form fields
        agencyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && agencyTable.getSelectedRow() != -1) {
                int selectedRow = agencyTable.getSelectedRow();
                txtAgencyId.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                txtAgencyName.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                String typeVal = String.valueOf(tableModel.getValueAt(selectedRow, 2));
                if ("Franchise".equalsIgnoreCase(typeVal)) {
                    cmbAgencyType.setSelectedItem("Franchise");
                } else {
                    cmbAgencyType.setSelectedItem("Independent");
                }
                txtAddress.setText(tableModel.getValueAt(selectedRow, 3) != null ? String.valueOf(tableModel.getValueAt(selectedRow, 3)) : "");
                txtPhone.setText(tableModel.getValueAt(selectedRow, 4) != null ? String.valueOf(tableModel.getValueAt(selectedRow, 4)) : "");
            }
        });

        JScrollPane scrollPane = new JScrollPane(agencyTable);
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
            String idText = txtAgencyId.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select an agency or enter Agency ID to update.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Agency ID must be a valid number.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String name = txtAgencyName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Agency Name cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            txtAgencyName.requestFocus();
            return false;
        }

        if (cmbAgencyType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Agency Type must be selected.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            cmbAgencyType.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Demonstrates OOP polymorphism by instantiating the appropriate Agency subclass.
     */
    private Agency createPolymorphicAgency(int id, String name, String type, String address, String phone) {
        if ("Independent".equalsIgnoreCase(type)) {
            String owner = name + " Owner";
            return new IndependentAgency(id, name, type, address, phone, owner);
        } else if ("Franchise".equalsIgnoreCase(type)) {
            String brand = name + " Franchise";
            return new FranchiseAgency(id, name, type, address, phone, brand);
        } else {
            return new Agency(id, name, type, address, phone);
        }
    }

    private void handleAddAgency() {
        if (!validateFormFields(false)) {
            return;
        }

        try {
            String name = txtAgencyName.getText().trim();
            String type = (String) cmbAgencyType.getSelectedItem();
            String address = txtAddress.getText().trim();
            String phone = txtPhone.getText().trim();

            Agency agency = createPolymorphicAgency(0, name, type, address, phone);
            agencyDAO.addAgency(agency);

            // Polymorphic call demonstration
            String description = agency.getAgencyDescription();

            JOptionPane.showMessageDialog(this,
                    "Agency added successfully!\n" +
                            "Assigned ID: " + agency.getAgencyId() + "\n" +
                            "Description: " + description,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            handleClearForm();
            loadAgencyData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while adding agency: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateAgency() {
        if (!validateFormFields(true)) {
            return;
        }

        try {
            int agencyId = Integer.parseInt(txtAgencyId.getText().trim());
            String name = txtAgencyName.getText().trim();
            String type = (String) cmbAgencyType.getSelectedItem();
            String address = txtAddress.getText().trim();
            String phone = txtPhone.getText().trim();

            Agency agency = createPolymorphicAgency(agencyId, name, type, address, phone);
            agencyDAO.updateAgency(agency);

            // Polymorphic call demonstration
            String description = agency.getAgencyDescription();

            JOptionPane.showMessageDialog(this,
                    "Agency (ID: " + agencyId + ") updated successfully!\n" +
                            "Description: " + description,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            handleClearForm();
            loadAgencyData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while updating agency: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteAgency() {
        String idText = txtAgencyId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select or enter an Agency ID to delete.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int agencyId;
        try {
            agencyId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Agency ID must be a numeric value.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Agency ID " + agencyId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                agencyDAO.deleteAgency(agencyId);
                JOptionPane.showMessageDialog(this,
                        "Agency deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                handleClearForm();
                loadAgencyData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database error while deleting agency: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearchAgency() {
        String query = txtAgencyName.getText().trim();
        String idQuery = txtAgencyId.getText().trim();

        try {
            tableModel.setRowCount(0);

            // Search by numeric ID if provided
            if (!idQuery.isEmpty()) {
                try {
                    int agencyId = Integer.parseInt(idQuery);
                    Agency agency = agencyDAO.getAgencyById(agencyId);
                    if (agency != null) {
                        tableModel.addRow(new Object[]{
                                agency.getAgencyId(),
                                agency.getAgencyName(),
                                agency.getAgencyType(),
                                agency.getAddress(),
                                agency.getPhone()
                        });
                        JOptionPane.showMessageDialog(this,
                                "Found 1 agency matching ID " + agencyId + " (" + agency.getAgencyDescription() + ").",
                                "Search Result", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No agency found with ID " + agencyId + ".",
                                "Search Result", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    // Not a numeric ID, fall through to name search
                }
            }

            // Search by name
            if (!query.isEmpty()) {
                List<Agency> results = agencyDAO.searchAgenciesByName(query);
                for (Agency a : results) {
                    tableModel.addRow(new Object[]{
                            a.getAgencyId(),
                            a.getAgencyName(),
                            a.getAgencyType(),
                            a.getAddress(),
                            a.getPhone()
                    });
                }
                JOptionPane.showMessageDialog(this,
                        "Found " + results.size() + " agency/agencies matching '" + query + "'.",
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter Agency ID or Agency Name to search.",
                        "Search Input", JOptionPane.WARNING_MESSAGE);
                loadAgencyData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error during search: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClearForm() {
        txtAgencyId.setText("");
        txtAgencyName.setText("");
        cmbAgencyType.setSelectedIndex(0);
        txtAddress.setText("");
        txtPhone.setText("");
        agencyTable.clearSelection();
    }

    public void loadAgencyData() {
        try {
            tableModel.setRowCount(0);
            List<Agency> agencyList = agencyDAO.getAllAgencies();
            for (Agency a : agencyList) {
                tableModel.addRow(new Object[]{
                        a.getAgencyId(),
                        a.getAgencyName(),
                        a.getAgencyType(),
                        a.getAddress(),
                        a.getPhone()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading agencies from database: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error loading agencies: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
