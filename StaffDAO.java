package hw4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StaffDAO handles all staff-related database operations such as
 * managing reviewer requests and tracking reviewer activities.
 */
public class StaffDAO {
    private final Connection conn;

    /**
     * Constructs a StaffDAO instance with the given database connection.
     *
     * @param conn the database connection
     */
    public StaffDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieves all pending reviewer requests.
     *
     * @return list of users requesting reviewer access
     * @throws SQLException if any SQL errors occur
     */
    public List<User> getReviewerRequests() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE isReviewerRequested = 1 AND isReviewerApproved = 0";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            users.add(new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getBoolean("isReviewerRequested"),
                rs.getBoolean("isReviewerApproved")
            ));
        }
        return users;
    }

    /**
     * Approves a reviewer's request.
     *
     * @param userId ID of the user to approve
     * @throws SQLException if SQL error occurs
     */
    public void approveReviewer(int userId) throws SQLException {
        String sql = "UPDATE Users SET isReviewerApproved = 1 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.executeUpdate();
    }

    /**
     * Rejects a reviewer request by resetting request and approval flags.
     *
     * @param userId ID of the user to reject
     * @throws SQLException if SQL error occurs
     */
    public void rejectReviewer(int userId) throws SQLException {
        String sql = "UPDATE Users SET isReviewerRequested = 0, isReviewerApproved = 0 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.executeUpdate();
    }

    /**
     * Revokes a reviewer's status.
     *
     * @param userId ID of the user to revoke
     * @throws SQLException if SQL error occurs
     */
    public void revokeReviewer(int userId) throws SQLException {
        String sql = "UPDATE Users SET isReviewerApproved = 0 WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.executeUpdate();
    }

    /**
     * Retrieves activity (reviews submitted) by a reviewer.
     *
     * @param reviewerId ID of the reviewer
     * @return list of reviews submitted by the reviewer
     * @throws SQLException if SQL error occurs
     */
    public List<Review> getReviewerActivity(int reviewerId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE reviewer_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, reviewerId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            reviews.add(new Review(
                rs.getInt("id"),
                rs.getInt("reviewer_id"),
                rs.getString("content"),
                rs.getTimestamp("created_at")
            ));
        }
        return reviews;
    }
}
