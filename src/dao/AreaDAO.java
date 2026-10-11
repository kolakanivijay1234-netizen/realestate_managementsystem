package dao;

import database.DatabaseConnection;
import model.GeographicalArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for GeographicalArea entities.
 * Handles CRUD and aggregate reports on geographical zones.
 */
public class AreaDAO {

    /**
     * Inserts a new geographical area into the database.
     * Sets the auto-generated area ID on the area object.
     *
     * @param area the GeographicalArea object to add
     * @throws SQLException on database error
     */
    public void addArea(GeographicalArea area) throws SQLException {
        String sql = "INSERT INTO geographical_areas (area_name, population) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, area.getAreaName());
            stmt.setInt(2, area.getPopulation());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    area.setAreaId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Updates an existing geographical area's population (and name).
     *
     * @param area the GeographicalArea object containing updated values
     * @throws SQLException on database error
     */
    public void updateArea(GeographicalArea area) throws SQLException {
        String sql = "UPDATE geographical_areas SET area_name = ?, population = ? WHERE area_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, area.getAreaName());
            stmt.setInt(2, area.getPopulation());
            stmt.setInt(3, area.getAreaId());

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a geographical area by ID.
     *
     * @param areaId the ID of the area to delete
     * @throws SQLException on database error
     */
    public void deleteArea(int areaId) throws SQLException {
        String sql = "DELETE FROM geographical_areas WHERE area_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a geographical area by ID.
     *
     * @param areaId ID of the area
     * @return GeographicalArea object or null if not found
     * @throws SQLException on database error
     */
    public GeographicalArea getAreaById(int areaId) throws SQLException {
        String sql = "SELECT area_id, area_name, population FROM geographical_areas WHERE area_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArea(rs);
                }
            }
        }
        return null;
    }

    /**
     * Searches geographical areas by name substring.
     *
     * @param name substring to match
     * @return list of matching GeographicalArea objects
     * @throws SQLException on database error
     */
    public List<GeographicalArea> searchAreasByName(String name) throws SQLException {
        List<GeographicalArea> areas = new ArrayList<>();
        String sql = "SELECT area_id, area_name, population FROM geographical_areas WHERE area_name LIKE ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    areas.add(mapResultSetToArea(rs));
                }
            }
        }
        return areas;
    }

    /**
     * Retrieves all geographical areas ordered by area_id.
     *
     * @return list of all GeographicalArea objects
     * @throws SQLException on database error
     */
    public List<GeographicalArea> getAllAreas() throws SQLException {
        List<GeographicalArea> areas = new ArrayList<>();
        String sql = "SELECT area_id, area_name, population FROM geographical_areas ORDER BY area_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                areas.add(mapResultSetToArea(rs));
            }
        }
        return areas;
    }

    /**
     * Counts total geographical areas.
     *
     * @return total count
     * @throws SQLException on database error
     */
    public int getTotalAreas() throws SQLException {
        String sql = "SELECT COUNT(*) FROM geographical_areas";
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
     * Checks if an area exists with the specified ID.
     *
     * @param areaId ID to verify
     * @return true if exists, false otherwise
     * @throws SQLException on database error
     */
    public boolean areaExists(int areaId) throws SQLException {
        String sql = "SELECT 1 FROM geographical_areas WHERE area_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Returns mandates grouped by geographical area.
     * Returns {areaId, areaName, population, mandateCount}.
     *
     * @return list of Object arrays
     * @throws SQLException on database error
     */
    public List<Object[]> getMandatesGroupedByArea() throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT ga.area_id, ga.area_name, ga.population, COUNT(m.mandate_id) AS mandate_count " +
                     "FROM geographical_areas ga " +
                     "LEFT JOIN mandates m ON ga.area_id = m.area_id " +
                     "GROUP BY ga.area_id, ga.area_name, ga.population " +
                     "ORDER BY mandate_count DESC, ga.area_id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Unable to obtain database connection.");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(new Object[]{
                    rs.getInt("area_id"),
                    rs.getString("area_name"),
                    rs.getInt("population"),
                    rs.getInt("mandate_count")
                });
            }
        }
        return results;
    }

    /**
     * Maps ResultSet row to a GeographicalArea object.
     *
     * @param rs ResultSet row
     * @return GeographicalArea instance
     * @throws SQLException on column access error
     */
    private GeographicalArea mapResultSetToArea(ResultSet rs) throws SQLException {
        return new GeographicalArea(
            rs.getInt("area_id"),
            rs.getString("area_name"),
            rs.getInt("population")
        );
    }
}
