package ui;

import dao.AreaDAO;
import model.GeographicalArea;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel for managing Geographical Areas in the Real Estate Mandate Management System.
 * Allows adding, updating population, deleting, searching, and viewing areas.
 */
public class AreaPanel extends JPanel {

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

    // Data Access Object
    private final AreaDAO areaDAO;

    // Form Components
    private JTextField txtAreaId;
    private JTextField txtAreaName;
    private JTextField txtPopulation;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnClear;

    // Table Components
    private JTable areaTable;
    private DefaultTableModel tableModel;

    public AreaPanel() {
        this.areaDAO = new AreaDAO();
        initializeUI();
        loadAreaData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // West: Form Panel
        JPanel formContainer = createFormPanel();
        add(formContainer, BorderLayout.WEST);

        // Center: Table Panel
        JPanel tableContainer = createTablePanel();
        add(tableContainer, BorderLayout.CENTER);

        // Wire event handlers
        setupEventHandlers();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Geographical Area Management");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(340, 500));

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                " Area Details ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                BUTTON_FONT,
                SECONDARY_COLOR
        );
        formPanel.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(15, 15, 15, 15)));

        // Fields Panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Area ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblId = new JLabel("Area ID:");
        lblId.setFont(LABEL_FONT);
        fieldsPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtAreaId = new JTextField();
        txtAreaId.setFont(LABEL_FONT);
        txtAreaId.setEditable(false);
        txtAreaId.setBackground(new Color(245, 245, 245));
        fieldsPanel.add(txtAreaId, gbc);

        // Area Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblName = new JLabel("Area Name:");
        lblName.setFont(LABEL_FONT);
        fieldsPanel.add(lblName, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtAreaName = new JTextField();
        txtAreaName.setFont(LABEL_FONT);
        fieldsPanel.add(txtAreaName, gbc);

        // Population
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblPopulation = new JLabel("Population:");
        lblPopulation.setFont(LABEL_FONT);
        fieldsPanel.add(lblPopulation, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPopulation = new JTextField();
        txtPopulation.setFont(LABEL_FONT);
        fieldsPanel.add(txtPopulation, gbc);

        formPanel.add(fieldsPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setMaximumSize(new Dimension(300, 220));

        btnAdd = createStyledButton("Add Area", ACCENT_COLOR);
        btnUpdate = createStyledButton("Update Area", PRIMARY_COLOR);
        btnDelete = createStyledButton("Delete Area", DANGER_COLOR);
        btnSearch = createStyledButton("Search Area", SECONDARY_COLOR);
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
                " Geographical Areas List ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                BUTTON_FONT,
                SECONDARY_COLOR
        );
        tableContainer.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));

        String[] columnNames = {"Area ID", "Area Name", "Population"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        areaTable = new JTable(tableModel);
        areaTable.setFont(TABLE_FONT);
        areaTable.setRowHeight(28);
        areaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        areaTable.getTableHeader().setFont(TABLE_HEADER_FONT);
        areaTable.getTableHeader().setBackground(SECONDARY_COLOR);
        areaTable.getTableHeader().setForeground(Color.WHITE);
        areaTable.getTableHeader().setReorderingAllowed(false);

        // Center align Area ID and Population columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        areaTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        areaTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(areaTable);
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
        btnAdd.addActionListener(e -> addArea());
        btnUpdate.addActionListener(e -> updateArea());
        btnDelete.addActionListener(e -> deleteArea());
        btnSearch.addActionListener(e -> searchArea());
        btnClear.addActionListener(e -> clearForm());

        areaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = areaTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateFormFromSelectedRow(selectedRow);
                }
            }
        });
    }

    private void populateFormFromSelectedRow(int row) {
        txtAreaId.setText(tableModel.getValueAt(row, 0).toString());
        txtAreaName.setText(tableModel.getValueAt(row, 1).toString());
        txtPopulation.setText(tableModel.getValueAt(row, 2).toString());
    }

    public void loadAreaData() {
        try {
            tableModel.setRowCount(0);
            List<GeographicalArea> areas = areaDAO.getAllAreas();
            for (GeographicalArea area : areas) {
                tableModel.addRow(new Object[]{
                        area.getAreaId(),
                        area.getAreaName(),
                        area.getPopulation()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while loading areas: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addArea() {
        String name = txtAreaName.getText().trim();
        String popStr = txtPopulation.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Area name cannot be empty.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtAreaName.requestFocus();
            return;
        }

        int population;
        try {
            population = Integer.parseInt(popStr);
            if (population <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Population must be a positive integer greater than zero.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                txtPopulation.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid population. Please enter a valid whole number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtPopulation.requestFocus();
            return;
        }

        try {
            GeographicalArea area = new GeographicalArea(0, name, population);
            areaDAO.addArea(area);
            JOptionPane.showMessageDialog(this,
                    "Area added successfully! (Assigned ID: " + area.getAreaId() + ")",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadAreaData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to add area: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateArea() {
        String idStr = txtAreaId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an area from the table to update.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int areaId;
        try {
            areaId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Area ID format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String popStr = txtPopulation.getText().trim();
        int population;
        try {
            population = Integer.parseInt(popStr);
            if (population <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Population must be a positive integer greater than zero.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                txtPopulation.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid population. Please enter a valid positive number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            txtPopulation.requestFocus();
            return;
        }

        try {
            GeographicalArea existingArea = areaDAO.getAreaById(areaId);
            if (existingArea == null) {
                JOptionPane.showMessageDialog(this,
                        "Area with ID " + areaId + " does not exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update only population (name kept from existing record)
            existingArea.setPopulation(population);
            areaDAO.updateArea(existingArea);

            JOptionPane.showMessageDialog(this,
                    "Population for area '" + existingArea.getAreaName() + "' updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadAreaData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update area: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteArea() {
        String idStr = txtAreaId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an area from the table to delete.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int areaId;
        try {
            areaId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Area ID format.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String areaName = txtAreaName.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Area ID: " + areaId + " (" + areaName + ")?\n" +
                        "Note: Mandates referencing this area may also be affected.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                areaDAO.deleteArea(areaId);
                JOptionPane.showMessageDialog(this,
                        "Area deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAreaData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete area: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchArea() {
        String nameSearch = txtAreaName.getText().trim();
        String idSearch = txtAreaId.getText().trim();

        if (nameSearch.isEmpty() && idSearch.isEmpty()) {
            // Prompt user with search dialog
            String keyword = JOptionPane.showInputDialog(this,
                    "Enter Area ID or Area Name to search:",
                    "Search Area",
                    JOptionPane.QUESTION_MESSAGE);
            if (keyword == null || keyword.trim().isEmpty()) {
                return;
            }
            nameSearch = keyword.trim();
        }

        try {
            tableModel.setRowCount(0);
            boolean found = false;

            // Check if search term is an ID
            if (!idSearch.isEmpty()) {
                try {
                    int areaId = Integer.parseInt(idSearch);
                    GeographicalArea area = areaDAO.getAreaById(areaId);
                    if (area != null) {
                        tableModel.addRow(new Object[]{
                                area.getAreaId(),
                                area.getAreaName(),
                                area.getPopulation()
                        });
                        found = true;
                    }
                } catch (NumberFormatException ignored) {
                    // Not a numeric ID, fall back to name search
                }
            }

            if (!found && !nameSearch.isEmpty()) {
                List<GeographicalArea> results = areaDAO.searchAreasByName(nameSearch);
                for (GeographicalArea area : results) {
                    tableModel.addRow(new Object[]{
                            area.getAreaId(),
                            area.getAreaName(),
                            area.getPopulation()
                    });
                    found = true;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "No areas found matching the search criteria.",
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                loadAreaData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error during search: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtAreaId.setText("");
        txtAreaName.setText("");
        txtPopulation.setText("");
        areaTable.clearSelection();
    }
}
