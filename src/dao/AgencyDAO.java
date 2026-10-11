package dao;

import database.DatabaseConnection;
import model.Agency;
import model.FranchiseAgency;
import model.IndependentAgency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Agency entities.
 * Demonstrates polymorphism when instantiating IndependentAgency or FranchiseAgency subclasses.
 */
public class AgencyDAO {

    /**
     * Inserts a new agency into the database.
     * Sets the auto-generated primary key onto the agency object.
     *
     * @param agency the Agency object to add
     * @throws SQLException on database error
     */
    public void addAgency(Agency agency) throws SQLException {
        String sql = "INSERT INTO agencies (agency_name, agency_type, address, phone) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, agency.getAgencyName());
            stmt.setString(2, agency.getAgencyType());
            stmt.setString(3, agency.getAddress());
            stmt.setString(4, agency.getPhone());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    agency.setAgencyId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Updates an existing agency's details in the database.
     *
     * @param agency the Agency object containing updated values
     * @throws SQLException on database error
     */
    public void updateAgency(Agency agency) throws SQLException {
        String sql = "UPDATE agencies SET agency_name = ?, agency_type = ?, address = ?, phone = ? WHERE agency_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, agency.getAgencyName());
            stmt.setString(2, agency.getAgencyType());
            stmt.setString(3, agency.getAddress());
            stmt.setString(4, agency.getPhone());
            stmt.setInt(5, agency.getAgencyId());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an agency by ID.
     *
     * @param agencyId the ID of the agency to delete
     * @throws SQLException on database error
     */
    public void deleteAgency(int agencyId) throws SQLException {
        String sql = "DELETE FROM agencies WHERE agency_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agencyId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves an agency by ID, instantiating the appropriate polymorphic subclass.
     *
     * @param agencyId the ID of the agency
     * @return Agency (or IndependentAgency/FranchiseAgency) or null if not found
     * @throws SQLException on database error
     */
    public Agency getAgencyById(int agencyId) throws SQLException {
        String sql = "SELECT agency_id, agency_name, agency_type, address, phone FROM agencies WHERE agency_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agencyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAgency(rs);
                }
            }
        }
        return null;
    }

    /**
     * Searches for agencies by name substring.
     *
     * @param name substring to match
     * @return list of matching Agency objects
     * @throws SQLException on database error
     */
    public List<Agency> searchAgenciesByName(String name) throws SQLException {
        List<Agency> agencies = new ArrayList<>();
        String sql = "SELECT agency_id, agency_name, agency_type, address, phone FROM agencies WHERE agency_name LIKE ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    agencies.add(mapResultSetToAgency(rs));
                }
            }
        }
        return agencies;
    }

    /**
     * Retrieves all agencies ordered by agency_id.
     *
     * @return list of all Agency objects
     * @throws SQLException on database error
     */
    public List<Agency> getAllAgencies() throws SQLException {
        List<Agency> agencies = new ArrayList<>();
        String sql = "SELECT agency_id, agency_name, agency_type, address, phone FROM agencies ORDER BY agency_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                agencies.add(mapResultSetToAgency(rs));
            }
        }
        return agencies;
    }

    /**
     * Counts total registered agencies.
     *
     * @return total agency count
     * @throws SQLException on database error
     */
    public int getTotalAgencies() throws SQLException {
        String sql = "SELECT COUNT(*) FROM agencies";
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
     * Checks if an agency with the given ID exists.
     *
     * @param agencyId ID to check
     * @return true if exists, false otherwise
     * @throws SQLException on database error
     */
    public boolean agencyExists(int agencyId) throws SQLException {
        String sql = "SELECT 1 FROM agencies WHERE agency_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agencyId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Returns agencies that have issued mandates.
     * Returns {agencyId, agencyName, agencyType, mandateId, mandateType}.
     *
     * @return list of Object arrays
     * @throws SQLException on database error
     */
    public List<Object[]> getAgenciesWithMandates() throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT DISTINCT ag.agency_id, ag.agency_name, ag.agency_type, m.mandate_id, m.mandate_type " +
                     "FROM agencies ag " +
                     "JOIN mandates m ON ag.agency_id = m.agency_id " +
                     "ORDER BY ag.agency_id, m.mandate_id";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("agency_id"),
                    rs.getString("agency_name"),
                    rs.getString("agency_type"),
                    rs.getInt("mandate_id"),
                    rs.getString("mandate_type")
                });
            }
        }
        return results;
    }

    /**
     * Returns mandates grouped by agency.
     * Returns {agencyId, agencyName, mandateCount}.
     *
     * @return list of Object arrays
     * @throws SQLException on database error
     */
    public List<Object[]> getMandatesGroupedByAgency() throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT ag.agency_id, ag.agency_name, COUNT(m.mandate_id) AS mandate_count " +
                     "FROM agencies ag " +
                     "LEFT JOIN mandates m ON ag.agency_id = m.agency_id " +
                     "GROUP BY ag.agency_id, ag.agency_name " +
                     "ORDER BY mandate_count DESC, ag.agency_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("agency_id"),
                    rs.getString("agency_name"),
                    rs.getInt("mandate_count")
                });
            }
        }
        return results;
    }

    /**
     * Helper method to map ResultSet to polymorphic Agency subclass.
     * If agency_type is "Independent", instantiates IndependentAgency.
     * If agency_type is "Franchise", instantiates FranchiseAgency.
     * Otherwise instantiates default Agency.
     *
     * @param rs ResultSet row
     * @return Agency instance
     * @throws SQLException on column reading error
     */
    private Agency mapResultSetToAgency(ResultSet rs) throws SQLException {
        int id = rs.getInt("agency_id");
        String name = rs.getString("agency_name");
        String type = rs.getString("agency_type");
        String address = rs.getString("address");
        String phone = rs.getString("phone");

        if (type != null && type.equalsIgnoreCase("Independent")) {
            String ownerName = name != null ? name + " Owner" : "Default Owner";
            return new IndependentAgency(id, name, type, address, phone, ownerName);
        } else if (type != null && type.equalsIgnoreCase("Franchise")) {
            String franchiseBrand = name != null ? name + " Brand" : "Default Brand";
            return new FranchiseAgency(id, name, type, address, phone, franchiseBrand);
        } else {
            return new Agency(id, name, type, address, phone);
        }
    }
}
