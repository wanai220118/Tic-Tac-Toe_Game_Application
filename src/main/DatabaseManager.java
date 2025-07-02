package main;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/tictactoe_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // default in Laragon

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void saveMatch(
            String mode,
            String winner,
            String playerX, String playerO,
            int scoreX, int scoreO,
            int spotsUsed, int duration
    ) {
        String sql = """
            INSERT INTO matches (mode, winner, player_x, player_o, player_x_score, player_o_score, spots_used, duration_seconds)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mode);
            stmt.setString(2, winner);
            stmt.setString(3, playerX);
            stmt.setString(4, playerO);
            stmt.setInt(5, scoreX);
            stmt.setInt(6, scoreO);
            stmt.setInt(7, spotsUsed);
            stmt.setInt(8, duration);

            stmt.executeUpdate();
            System.out.println("Match saved to database.");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
