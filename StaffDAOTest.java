package hw4;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class StaffDAOTest {
    private Connection conn;
    private StaffDAO staffDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        conn.setAutoCommit(false);
        staffDAO = new StaffDAO(conn);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        conn.rollback();
        conn.close();
    }

    @Test
    public void testGetReviewerRequests() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO Users (id, username, isReviewerRequested, isReviewerApproved) VALUES (1, 'reviewUser', 1, 0)");

        List<User> requests = staffDAO.getReviewerRequests();
        assertFalse(requests.isEmpty());
        assertEquals("reviewUser", requests.get(0).getUsername());
    }

    @Test
    public void testApproveReviewer() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO Users (id, username, isReviewerRequested, isReviewerApproved) VALUES (2, 'approveUser', 1, 0)");

        staffDAO.approveReviewer(2);

        PreparedStatement ps = conn.prepareStatement("SELECT isReviewerApproved FROM Users WHERE id = ?");
        ps.setInt(1, 2);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(1, rs.getInt("isReviewerApproved"));
    }

    @Test
    public void testRejectReviewer() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO Users (id, username, isReviewerRequested, isReviewerApproved) VALUES (3, 'rejectUser', 1, 0)");

        staffDAO.rejectReviewer(3);

        PreparedStatement ps = conn.prepareStatement("SELECT isReviewerRequested, isReviewerApproved FROM Users WHERE id = ?");
        ps.setInt(1, 3);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(0, rs.getInt("isReviewerRequested"));
        assertEquals(0, rs.getInt("isReviewerApproved"));
    }

    @Test
    public void testRevokeReviewer() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO Users (id, username, isReviewerRequested, isReviewerApproved) VALUES (4, 'revokeUser', 1, 1)");

        staffDAO.revokeReviewer(4);

        PreparedStatement ps = conn.prepareStatement("SELECT isReviewerApproved FROM Users WHERE id = ?");
        ps.setInt(1, 4);
        ResultSet rs = ps.executeQuery();
        assertTrue(rs.next());
        assertEquals(0, rs.getInt("isReviewerApproved"));
    }

    @Test
    public void testGetReviewerActivity() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO Users (id, username, isReviewerRequested, isReviewerApproved) VALUES (5, 'activityUser', 1, 1)");
        stmt.executeUpdate("INSERT INTO Reviews (id, reviewer_id, content, created_at) VALUES (1, 5, 'Well written article.', CURRENT_TIMESTAMP)");

        List<Review> activity = staffDAO.getReviewerActivity(5);

        assertFalse(activity.isEmpty());
        assertEquals("Well written article.", activity.get(0).getContent());
    }
}
