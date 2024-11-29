package database;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "mysql 사용자id";
    private static final String PASSWORD = "mysql 사용자 비밀번호";
    private static final String DATABASE_NAME = "WatermelonGame";

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 데이터베이스 생성
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            stmt.execute("USE " + DATABASE_NAME);

            String createSkinsTableSQL = "CREATE TABLE IF NOT EXISTS skins ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "skin_name VARCHAR(50) NOT NULL, "
                    + "skin_image VARCHAR(255) NOT NULL "
                    + ");";

            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "high_score INT DEFAULT 0, "
                    + "coins INT DEFAULT 0, "
                    + "active_skinid INT DEFAULT NULL, "
                    + "FOREIGN KEY (active_skinid) REFERENCES skins(id) "
                    + ");";
            
            String createUserSkinsTableSQL = "CREATE TABLE IF NOT EXISTS user_skins ("
                    + "user_id INT, "
                    + "skin_id INT, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id), "
                    + "FOREIGN KEY (skin_id) REFERENCES skins(id), "
                    + "PRIMARY KEY (user_id, skin_id) "
                    + ");";

            stmt.executeUpdate(createSkinsTableSQL);
            stmt.executeUpdate(createUsersTableSQL);
            stmt.executeUpdate(createUserSkinsTableSQL);

            try {
                stmt.executeUpdate("ALTER TABLE users ADD CONSTRAINT fk_active_skin FOREIGN KEY (active_skinid) REFERENCES skins(id);");
            } catch (SQLException e) {
                System.out.println("Failed to add foreign key constraint: " + e.getMessage());
            }

            System.out.println("Database and tables initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed");
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed");
        }
    }
    public static String[] getUserDetails(int userId) {
        String query = "SELECT username, high_score, coins FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    rs.getString("username"),
                    String.valueOf(rs.getInt("high_score")),
                    String.valueOf(rs.getInt("coins"))
                };
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user details: " + e.getMessage());
        }
        return null;
    }
    public static void updateUserScoreAndCoins(int userId, int newScore, int newCoins) {
        if (userId == -1) {
            System.err.println("Invalid userId: " + userId);
            return;
        }

        String checkUserSQL = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSQL)) {

            checkStmt.setInt(1, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("No user found with ID: " + userId);
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to check user existence");
        }

        String updateSQL = "UPDATE users SET high_score = ?, coins = ? WHERE id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            int highScore = getCurrentHighScore(userId);
            int[] userStats = getUserScoreAndCoins(userId);
            int currentCoins = userStats[1];

            if (newScore > highScore) {
                stmt.setInt(1, newScore);
            } else {
                stmt.setInt(1, highScore);
            }

            stmt.setInt(2, currentCoins + newCoins);
            stmt.setInt(3, userId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected == 0) {
                System.out.println("No rows updated. Verify user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update score and coins");
        }
    }

    public static int getCurrentHighScore(int userId) {
        int highScore = 0;
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT high_score FROM users WHERE id = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                highScore = rs.getInt("high_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return highScore;
    }

    public static int[] getUserScoreAndCoins(int userId) {
        int[] result = new int[2];
        String querySQL = "SELECT IFNULL(high_score, 0) AS high_score, IFNULL(coins, 0) AS coins FROM users WHERE id = ?";
        
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(querySQL)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getInt("high_score");
                    result[1] = rs.getInt("coins");
                    System.out.println("UserID: " + userId + ", HighScore: " + result[0] + ", coins: " + result[1]);
                } else {
                    System.out.println("UserID: " + userId + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve score and coins");
        }
        return result;
    }

}
