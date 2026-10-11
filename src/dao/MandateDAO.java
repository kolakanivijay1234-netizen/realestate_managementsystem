package dao;

import database.DatabaseConnection;
import model.Mandate;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Mandate entities.
 * Handles CRUD operations, date-based filtering, and analytical queries.
 */
public class MandateDAO {

    /**
     * Inserts a new mandate into the database.
     * Sets the auto-generated mandate ID on the mandate object.
     *
     * @param mandate the Mandate object to add
     * @throws SQLException on database error
     */
    public void addMandate(Mandate mandate) throws SQLException {
        String sql = "INSERT INTO mandates (agent_id, agency_id, area_id, start_date, end_date, mandate_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, mandate.getAgentId());
            stmt.setInt(2, mandate.getAgencyId());
            stmt.setInt(3, mandate.getAreaId());
            stmt.setDate(4, mandate.getStartDate() != null ? Date.valueOf(mandate.getStartDate()) : null);
            stmt.setDate(5, mandate.getEndDate() != null ? Date.valueOf(mandate.getEndDate()) : null);
            stmt.setString(6, mandate.getMandateType());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mandate.setMandateId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Updates an existing mandate's type and end date.
     *
     * @param mandate the Mandate object containing updated mandateType and endDate
     * @throws SQLException on database error
     */
    public void updateMandate(Mandate mandate) throws SQLException {
        String sql = "UPDATE mandates SET mandate_type = ?, end_date = ? WHERE mandate_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mandate.getMandateType());
            stmt.setDate(2, mandate.getEndDate() != null ? Date.valueOf(mandate.getEndDate()) : null);
            stmt.setInt(3, mandate.getMandateId());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a mandate by its ID.
     *
     * @param mandateId ID of the mandate to delete
     * @throws SQLException on database error
     */
    public void deleteMandate(int mandateId) throws SQLException {
        String sql = "DELETE FROM mandates WHERE mandate_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mandateId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a mandate by its ID.
     *
     * @param mandateId ID of the mandate
     * @return Mandate object or null if not found
     * @throws SQLException on database error
     */
    public Mandate getMandateById(int mandateId) throws SQLException {
        String sql = "SELECT mandate_id, agent_id, agency_id, area_id, start_date, end_date, mandate_type " +
                     "FROM mandates WHERE mandate_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mandateId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMandate(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all mandates ordered by mandate_id.
     *
     * @return list of all Mandate objects
     * @throws SQLException on database error
     */
    public List<Mandate> getAllMandates() throws SQLException {
        List<Mandate> mandates = new ArrayList<>();
        String sql = "SELECT mandate_id, agent_id, agency_id, area_id, start_date, end_date, mandate_type " +
                     "FROM mandates ORDER BY mandate_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mandates.add(mapResultSetToMandate(rs));
            }
        }
        return mandates;
    }

    /**
     * Searches for mandates matching a specific type (e.g., SALE, RENT, PURCHASE, LEASE).
     *
     * @param type mandate type string
     * @return list of matching Mandate objects
     * @throws SQLException on database error
     */
    public List<Mandate> searchMandatesByType(String type) throws SQLException {
        List<Mandate> mandates = new ArrayList<>();
        String sql = "SELECT mandate_id, agent_id, agency_id, area_id, start_date, end_date, mandate_type " +
                     "FROM mandates WHERE UPPER(mandate_type) = UPPER(?) ORDER BY mandate_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mandates.add(mapResultSetToMandate(rs));
                }
            }
        }
        return mandates;
    }

    /**
     * Counts the total number of mandates.
     *
     * @return total count
     * @throws SQLException on database error
     */
    public int getTotalMandates() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mandates";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Counts active mandates where current date is between start_date and end_date.
     *
     * @return count of currently active mandates
     * @throws SQLException on database error
     */
    public int getActiveMandateCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mandates WHERE CURDATE() BETWEEN start_date AND end_date";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Retrieves mandates that are active on a specified date (start_date <= date <= end_date).
     *
     * @param date reference date
     * @return list of active Mandate objects
     * @throws SQLException on database error
     */
    public List<Mandate> getActiveMandates(LocalDate date) throws SQLException {
        List<Mandate> mandates = new ArrayList<>();
        String sql = "SELECT mandate_id, agent_id, agency_id, area_id, start_date, end_date, mandate_type " +
                     "FROM mandates WHERE ? BETWEEN start_date AND end_date ORDER BY mandate_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mandates.add(mapResultSetToMandate(rs));
                }
            }
        }
        return mandates;
    }

    /**
     * Retrieves mandates that are expired relative to a specified date (end_date < date).
     *
     * @param date reference date
     * @return list of expired Mandate objects
     * @throws SQLException on database error
     */
    public List<Mandate> getExpiredMandates(LocalDate date) throws SQLException {
        List<Mandate> mandates = new ArrayList<>();
        String sql = "SELECT mandate_id, agent_id, agency_id, area_id, start_date, end_date, mandate_type " +
                     "FROM mandates WHERE end_date < ? ORDER BY mandate_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mandates.add(mapResultSetToMandate(rs));
                }
            }
        }
        return mandates;
    }

    /**
     * Returns mandates grouped by mandate type.
     * Returns {mandateType, mandateCount}.
     *
     * @return list of Object arrays
     * @throws SQLException on database error
     */
    public List<Object[]> getMandatesGroupedByType() throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT mandate_type, COUNT(mandate_id) AS mandate_count " +
                     "FROM mandates " +
                     "GROUP BY mandate_type " +
                     "ORDER BY mandate_count DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getString("mandate_type"),
                    rs.getInt("mandate_count")
                });
            }
        }
        return results;
    }

    /**
     * Returns all mandates along with an evaluated status column ('Active', 'Expired', or 'Future').
     * Returns {mandateId, agentId, agencyId, areaId, startDate, endDate, mandateType, status}.
     *
     * @param date the reference date to evaluate active vs expired status
     * @return list of Object arrays representing mandates with status
     * @throws SQLException on database error
     */
    public List<Object[]> getActiveAndExpiredMandates(LocalDate date) throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT mandate_id, agent_id, agency_id, area_id, start_date, end_date, mandate_type, " +
                     "CASE " +
                     "  WHEN ? BETWEEN start_date AND end_date THEN 'Active' " +
                     "  WHEN end_date < ? THEN 'Expired' " +
                     "  ELSE 'Future' " +
                     "END AS status " +
                     "FROM mandates " +
                     "ORDER BY mandate_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Date sqlDate = Date.valueOf(date);
            stmt.setDate(1, sqlDate);
            stmt.setDate(2, sqlDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date start = rs.getDate("start_date");
                    Date end = rs.getDate("end_date");
                    LocalDate startLocal = start != null ? start.toLocalDate() : null;
                    LocalDate endLocal = end != null ? end.toLocalDate() : null;

                    results.add(new Object[]{
                        rs.getInt("mandate_id"),
                        rs.getInt("agent_id"),
                        rs.getInt("agency_id"),
                        rs.getInt("area_id"),
                        startLocal,
                        endLocal,
                        rs.getString("mandate_type"),
                        rs.getString("status")
                    });
                }
            }
        }
        return results;
    }

    /**
     * Maps ResultSet row to a Mandate object.
     *
     * @param rs ResultSet row
     * @return Mandate instance
     * @throws SQLException on column reading error
     */
    private Mandate mapResultSetToMandate(ResultSet rs) throws SQLException {
        Date start = rs.getDate("start_date");
        Date end = rs.getDate("end_date");
        LocalDate startLocal = start != null ? start.toLocalDate() : null;
        LocalDate endLocal = end != null ? end.toLocalDate() : null;

        return new Mandate(
            rs.getInt("mandate_id"),
            rs.getInt("agent_id"),
            rs.getInt("agency_id"),
            rs.getInt("area_id"),
            startLocal,
            endLocal,
            rs.getString("mandate_type")
        );
    }
}
