import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class Main extends JFrame {

    // ================= DATABASE =================

    static final String URL =
            "jdbc:mysql://localhost:3306/real_estate_mandate_db"
            + "?useSSL=false&serverTimezone=UTC";

    static final String USER = "root";

    // CHANGE THIS
    static final String PASSWORD = "YOUR_MYSQL_PASSWORD";


    // ================= CONNECTION =================

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    // ================= MAIN WINDOW =================

    JTabbedPane tabs;

    public Main() {

        setTitle("Real Estate Agent Mandate Management System");

        setSize(1100, 700);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabs = new JTabbedPane();

        tabs.addTab("Dashboard", new DashboardPanel());
        tabs.addTab("Agents", new AgentPanel());
        tabs.addTab("Agencies", new AgencyPanel());
        tabs.addTab("Areas", new AreaPanel());
        tabs.addTab("Mandates", new MandatePanel());
        tabs.addTab("Reports", new ReportPanel());

        add(tabs);
    }


    // ================= DASHBOARD =================

    class DashboardPanel extends JPanel {

        JLabel agents;
        JLabel agencies;
        JLabel areas;
        JLabel mandates;

        DashboardPanel() {

            setLayout(new GridLayout(2, 2, 20, 20));

            agents = createCard("Agents");
            agencies = createCard("Agencies");
            areas = createCard("Geographical Areas");
            mandates = createCard("Mandates");

            add(agents);
            add(agencies);
            add(areas);
            add(mandates);

            refresh();
        }

        JLabel createCard(String title) {

            JLabel label = new JLabel(
                    "<html><center>"
                            + "<h1>" + title + "</h1>"
                            + "<h2>0</h2>"
                            + "</center></html>",
                    SwingConstants.CENTER
            );

            label.setBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 2)
            );

            return label;
        }

        void refresh() {

            agents.setText(
                    "<html><center><h1>Agents</h1><h2>"
                            + count("Agent")
                            + "</h2></center></html>"
            );

            agencies.setText(
                    "<html><center><h1>Agencies</h1><h2>"
                            + count("Agency")
                            + "</h2></center></html>"
            );

            areas.setText(
                    "<html><center><h1>Areas</h1><h2>"
                            + count("GeographicalArea")
                            + "</h2></center></html>"
            );

            mandates.setText(
                    "<html><center><h1>Mandates</h1><h2>"
                            + count("Mandate")
                            + "</h2></center></html>"
            );
        }

        int count(String table) {

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs =
                         st.executeQuery("SELECT COUNT(*) FROM " + table)) {

                if (rs.next())
                    return rs.getInt(1);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return 0;
        }
    }


    // ================= AGENT PANEL =================

    class AgentPanel extends JPanel {

        JTextField name;
        JTextField phone;
        JTextField email;
        JTextField agencyId;

        JTable table;
        DefaultTableModel model;

        AgentPanel() {

            setLayout(new BorderLayout());

            JPanel form = new JPanel(
                    new GridLayout(5, 2, 10, 10)
            );

            name = new JTextField();
            phone = new JTextField();
            email = new JTextField();
            agencyId = new JTextField();

            form.add(new JLabel("Agent Name"));
            form.add(name);

            form.add(new JLabel("Phone"));
            form.add(phone);

            form.add(new JLabel("Email"));
            form.add(email);

            form.add(new JLabel("Agency ID"));
            form.add(agencyId);

            JButton add = new JButton("Add Agent");
            JButton delete = new JButton("Delete Agent");

            form.add(add);
            form.add(delete);

            add(form, BorderLayout.NORTH);

            model = new DefaultTableModel(
                    new String[]{
                            "ID",
                            "Name",
                            "Phone",
                            "Email",
                            "Agency ID"
                    }, 0
            );

            table = new JTable(model);

            add(new JScrollPane(table), BorderLayout.CENTER);

            add.addActionListener(e -> addAgent());

            delete.addActionListener(e -> deleteAgent());

            loadAgents();
        }


        void addAgent() {

            String sql =
                    "INSERT INTO Agent "
                            + "(agent_name, phone, email, agency_id) "
                            + "VALUES (?, ?, ?, ?)";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name.getText());
                ps.setString(2, phone.getText());
                ps.setString(3, email.getText());

                if (agencyId.getText().isEmpty())
                    ps.setNull(4, Types.INTEGER);
                else
                    ps.setInt(4, Integer.parseInt(agencyId.getText()));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Agent added successfully"
                );

                clearFields();

                loadAgents();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }


        void deleteAgent() {

            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Select an agent first"
                );
                return;
            }

            int id = (int) model.getValueAt(row, 0);

            try (Connection con = getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(
                                 "DELETE FROM Agent WHERE agent_id=?"
                         )) {

                ps.setInt(1, id);

                ps.executeUpdate();

                loadAgents();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }


        void loadAgents() {

            model.setRowCount(0);

            String sql =
                    "SELECT agent_id, agent_name, phone, email, agency_id "
                            + "FROM Agent";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("agent_id"),
                            rs.getString("agent_name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getObject("agency_id")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        void clearFields() {

            name.setText("");
            phone.setText("");
            email.setText("");
            agencyId.setText("");
        }
    }


    // ================= AGENCY PANEL =================

    class AgencyPanel extends JPanel {

        JTextField name;
        JTextField type;
        JTextField address;
        JTextField phone;

        JTable table;
        DefaultTableModel model;

        AgencyPanel() {

            setLayout(new BorderLayout());

            JPanel form = new JPanel(
                    new GridLayout(5, 2, 10, 10)
            );

            name = new JTextField();
            type = new JTextField();
            address = new JTextField();
            phone = new JTextField();

            form.add(new JLabel("Agency Name"));
            form.add(name);

            form.add(new JLabel("Agency Type"));
            form.add(type);

            form.add(new JLabel("Address"));
            form.add(address);

            form.add(new JLabel("Phone"));
            form.add(phone);

            JButton add = new JButton("Add Agency");
            JButton delete = new JButton("Delete Agency");

            form.add(add);
            form.add(delete);

            add(form, BorderLayout.NORTH);

            model = new DefaultTableModel(
                    new String[]{
                            "ID",
                            "Name",
                            "Type",
                            "Address",
                            "Phone"
                    }, 0
            );

            table = new JTable(model);

            add(new JScrollPane(table), BorderLayout.CENTER);

            add.addActionListener(e -> addAgency());

            delete.addActionListener(e -> deleteAgency());

            loadAgencies();
        }


        void addAgency() {

            String sql =
                    "INSERT INTO Agency "
                            + "(agency_name, agency_type, address, phone) "
                            + "VALUES (?, ?, ?, ?)";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name.getText());
                ps.setString(2, type.getText());
                ps.setString(3, address.getText());
                ps.setString(4, phone.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Agency added successfully"
                );

                clearFields();

                loadAgencies();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }


        void deleteAgency() {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Select an agency first"
                );

                return;
            }

            int id = (int) model.getValueAt(row, 0);

            try (Connection con = getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(
                                 "DELETE FROM Agency WHERE agency_id=?"
                         )) {

                ps.setInt(1, id);

                ps.executeUpdate();

                loadAgencies();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cannot delete agency.\n"
                                + "It may be referenced by agents or mandates."
                );
            }
        }


        void loadAgencies() {

            model.setRowCount(0);

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs =
                         st.executeQuery(
                                 "SELECT * FROM Agency"
                         )) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("agency_id"),
                            rs.getString("agency_name"),
                            rs.getString("agency_type"),
                            rs.getString("address"),
                            rs.getString("phone")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        void clearFields() {

            name.setText("");
            type.setText("");
            address.setText("");
            phone.setText("");
        }
    }


    // ================= AREA PANEL =================

    class AreaPanel extends JPanel {

        JTextField name;
        JTextField population;

        JTable table;
        DefaultTableModel model;

        AreaPanel() {

            setLayout(new BorderLayout());

            JPanel form = new JPanel(
                    new GridLayout(3, 2, 10, 10)
            );

            name = new JTextField();
            population = new JTextField();

            form.add(new JLabel("Area Name"));
            form.add(name);

            form.add(new JLabel("Population"));
            form.add(population);

            JButton add = new JButton("Add Area");
            JButton delete = new JButton("Delete Area");

            form.add(add);
            form.add(delete);

            add(form, BorderLayout.NORTH);

            model = new DefaultTableModel(
                    new String[]{
                            "ID",
                            "Area Name",
                            "Population"
                    }, 0
            );

            table = new JTable(model);

            add(new JScrollPane(table), BorderLayout.CENTER);

            add.addActionListener(e -> addArea());

            delete.addActionListener(e -> deleteArea());

            loadAreas();
        }


        void addArea() {

            try (Connection con = getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(
                                 "INSERT INTO GeographicalArea "
                                         + "(area_name, population) "
                                         + "VALUES (?, ?)"
                         )) {

                ps.setString(1, name.getText());

                ps.setInt(
                        2,
                        Integer.parseInt(population.getText())
                );

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Area added successfully"
                );

                name.setText("");
                population.setText("");

                loadAreas();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }


        void deleteArea() {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Select an area first"
                );

                return;
            }

            int id = (int) model.getValueAt(row, 0);

            try (Connection con = getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(
                                 "DELETE FROM GeographicalArea "
                                         + "WHERE area_id=?"
                         )) {

                ps.setInt(1, id);

                ps.executeUpdate();

                loadAreas();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cannot delete area. "
                                + "It may be used by a mandate."
                );
            }
        }


        void loadAreas() {

            model.setRowCount(0);

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs =
                         st.executeQuery(
                                 "SELECT * FROM GeographicalArea"
                         )) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("area_id"),
                            rs.getString("area_name"),
                            rs.getInt("population")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ================= MANDATE PANEL =================

    class MandatePanel extends JPanel {

        JTextField agentId;
        JTextField agencyId;
        JTextField areaId;
        JTextField startDate;
        JTextField endDate;
        JTextField mandateType;

        JTable table;
        DefaultTableModel model;

        MandatePanel() {

            setLayout(new BorderLayout());

            JPanel form = new JPanel(
                    new GridLayout(7, 2, 10, 10)
            );

            agentId = new JTextField();
            agencyId = new JTextField();
            areaId = new JTextField();

            startDate = new JTextField("2026-10-01");
            endDate = new JTextField("2026-12-31");

            mandateType = new JTextField();

            form.add(new JLabel("Agent ID"));
            form.add(agentId);

            form.add(new JLabel("Agency ID"));
            form.add(agencyId);

            form.add(new JLabel("Area ID"));
            form.add(areaId);

            form.add(new JLabel("Start Date"));
            form.add(startDate);

            form.add(new JLabel("End Date"));
            form.add(endDate);

            form.add(new JLabel("Mandate Type"));
            form.add(mandateType);

            JButton add = new JButton("Add Mandate");
            JButton delete = new JButton("Delete Mandate");

            form.add(add);
            form.add(delete);

            add(form, BorderLayout.NORTH);

            model = new DefaultTableModel(
                    new String[]{
                            "ID",
                            "Agent",
                            "Agency",
                            "Area",
                            "Start",
                            "End",
                            "Type"
                    }, 0
            );

            table = new JTable(model);

            add(new JScrollPane(table), BorderLayout.CENTER);

            add.addActionListener(e -> addMandate());

            delete.addActionListener(e -> deleteMandate());

            loadMandates();
        }


        void addMandate() {

            try {

                LocalDate start =
                        LocalDate.parse(startDate.getText());

                LocalDate end =
                        LocalDate.parse(endDate.getText());

                if (!end.isAfter(start)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "End date must be after start date."
                    );

                    return;
                }

                String sql =
                        "INSERT INTO Mandate "
                                + "(agent_id, agency_id, area_id, "
                                + "start_date, end_date, mandate_type) "
                                + "VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection con = getConnection();
                     PreparedStatement ps =
                             con.prepareStatement(sql)) {

                    ps.setInt(
                            1,
                            Integer.parseInt(agentId.getText())
                    );

                    ps.setInt(
                            2,
                            Integer.parseInt(agencyId.getText())
                    );

                    ps.setInt(
                            3,
                            Integer.parseInt(areaId.getText())
                    );

                    ps.setDate(
                            4,
                            Date.valueOf(start)
                    );

                    ps.setDate(
                            5,
                            Date.valueOf(end)
                    );

                    ps.setString(
                            6,
                            mandateType.getText()
                    );

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(
                            this,
                            "Mandate added successfully"
                    );

                    loadMandates();
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }


        void deleteMandate() {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        this,
                        "Select a mandate first"
                );

                return;
            }

            int id = (int) model.getValueAt(row, 0);

            try (Connection con = getConnection();
                 PreparedStatement ps =
                         con.prepareStatement(
                                 "DELETE FROM Mandate "
                                         + "WHERE mandate_id=?"
                         )) {

                ps.setInt(1, id);

                ps.executeUpdate();

                loadMandates();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error: " + e.getMessage()
                );
            }
        }


        void loadMandates() {

            model.setRowCount(0);

            String sql =
                    "SELECT m.mandate_id, "
                            + "a.agent_name, "
                            + "ag.agency_name, "
                            + "ga.area_name, "
                            + "m.start_date, "
                            + "m.end_date, "
                            + "m.mandate_type "
                            + "FROM Mandate m "
                            + "JOIN Agent a "
                            + "ON m.agent_id=a.agent_id "
                            + "JOIN Agency ag "
                            + "ON m.agency_id=ag.agency_id "
                            + "JOIN GeographicalArea ga "
                            + "ON m.area_id=ga.area_id";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {

                    model.addRow(new Object[]{
                            rs.getInt("mandate_id"),
                            rs.getString("agent_name"),
                            rs.getString("agency_name"),
                            rs.getString("area_name"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getString("mandate_type")
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ================= REPORT PANEL =================

    class ReportPanel extends JPanel {

        JTextArea output;

        ReportPanel() {

            setLayout(new BorderLayout());

            JPanel buttons = new JPanel();

            JButton allMandates =
                    new JButton("All Mandates");

            JButton active =
                    new JButton("Active Mandates");

            JButton agentCount =
                    new JButton("Agent Mandate Count");

            JButton agencyReport =
                    new JButton("Agency Report");

            buttons.add(allMandates);
            buttons.add(active);
            buttons.add(agentCount);
            buttons.add(agencyReport);

            add(buttons, BorderLayout.NORTH);

            output = new JTextArea();

            output.setFont(
                    new Font("Monospaced", Font.PLAIN, 14)
            );

            add(new JScrollPane(output), BorderLayout.CENTER);

            allMandates.addActionListener(
                    e -> allMandates()
            );

            active.addActionListener(
                    e -> activeMandates()
            );

            agentCount.addActionListener(
                    e -> agentMandateCount()
            );

            agencyReport.addActionListener(
                    e -> agencyReport()
            );
        }


        void allMandates() {

            output.setText("");

            String sql =
                    "SELECT m.mandate_id, "
                            + "a.agent_name, "
                            + "ag.agency_name, "
                            + "ga.area_name, "
                            + "m.start_date, "
                            + "m.end_date, "
                            + "m.mandate_type "
                            + "FROM Mandate m "
                            + "JOIN Agent a "
                            + "ON m.agent_id=a.agent_id "
                            + "JOIN Agency ag "
                            + "ON m.agency_id=ag.agency_id "
                            + "JOIN GeographicalArea ga "
                            + "ON m.area_id=ga.area_id";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {

                    output.append(
                            "Mandate ID: "
                                    + rs.getInt("mandate_id")
                                    + "\n"
                    );

                    output.append(
                            "Agent: "
                                    + rs.getString("agent_name")
                                    + "\n"
                    );

                    output.append(
                            "Agency: "
                                    + rs.getString("agency_name")
                                    + "\n"
                    );

                    output.append(
                            "Area: "
                                    + rs.getString("area_name")
                                    + "\n"
                    );

                    output.append(
                            "Start: "
                                    + rs.getDate("start_date")
                                    + "\n"
                    );

                    output.append(
                            "End: "
                                    + rs.getDate("end_date")
                                    + "\n"
                    );

                    output.append(
                            "Type: "
                                    + rs.getString("mandate_type")
                                    + "\n"
                    );

                    output.append(
                            "-----------------------------\n"
                    );
                }

            } catch (Exception e) {

                output.setText(
                        "Error: " + e.getMessage()
                );
            }
        }


        void activeMandates() {

            output.setText("");

            String sql =
                    "SELECT COUNT(*) AS total "
                            + "FROM Mandate "
                            + "WHERE CURDATE() "
                            + "BETWEEN start_date AND end_date";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                if (rs.next()) {

                    output.append(
                            "ACTIVE MANDATES TODAY: "
                                    + rs.getInt("total")
                    );
                }

            } catch (Exception e) {

                output.setText(
                        "Error: " + e.getMessage()
                );
            }
        }


        void agentMandateCount() {

            output.setText("");

            String sql =
                    "SELECT a.agent_name, "
                            + "COUNT(m.mandate_id) AS total "
                            + "FROM Agent a "
                            + "LEFT JOIN Mandate m "
                            + "ON a.agent_id=m.agent_id "
                            + "GROUP BY a.agent_id, a.agent_name "
                            + "ORDER BY total DESC";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                output.append(
                        "AGENT MANDATE COUNT\n"
                                + "====================\n\n"
                );

                while (rs.next()) {

                    output.append(
                            String.format(
                                    "%-25s %d mandates%n",
                                    rs.getString("agent_name"),
                                    rs.getInt("total")
                            )
                    );
                }

            } catch (Exception e) {

                output.setText(
                        "Error: " + e.getMessage()
                );
            }
        }


        void agencyReport() {

            output.setText("");

            String sql =
                    "SELECT ag.agency_name, "
                            + "COUNT(m.mandate_id) AS total "
                            + "FROM Agency ag "
                            + "LEFT JOIN Mandate m "
                            + "ON ag.agency_id=m.agency_id "
                            + "GROUP BY ag.agency_id, ag.agency_name "
                            + "ORDER BY total DESC";

            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                output.append(
                        "AGENCY MANDATE REPORT\n"
                                + "====================\n\n"
                );

                while (rs.next()) {

                    output.append(
                            String.format(
                                    "%-25s %d mandates%n",
                                    rs.getString("agency_name"),
                                    rs.getInt("total")
                            )
                    );
                }

            } catch (Exception e) {

                output.setText(
                        "Error: " + e.getMessage()
                );
            }
        }
    }


    // ================= MAIN =================

    public static void main(String[] args) {

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "MySQL JDBC Driver not found!"
            );

            return;
        }

        SwingUtilities.invokeLater(() -> {

            Main app = new Main();

            app.setVisible(true);
        });
    }
}