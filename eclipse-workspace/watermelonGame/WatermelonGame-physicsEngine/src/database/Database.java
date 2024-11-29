package database;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1018";
    private static final String DATABASE_NAME = "WatermelonGame";

    static {
        initializeDatabase();
    }

    // 데이터베이스 초기화 메서드
    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // 데이터베이스 생성
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            stmt.execute("USE " + DATABASE_NAME);

            // 테이블 생성 순서 변경
            String createSkinsTableSQL = "CREATE TABLE IF NOT EXISTS skins ("
            		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
            		+ "skin_name VARCHAR(50) NOT NULL, "
            		+ "skin_image VARCHAR(255) NOT NULL "
            		+ ");";

            String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users ("
            		+ " id INT AUTO_INCREMENT PRIMARY KEY, "
            		+ "username VARCHAR(50) UNIQUE NOT NULL, "
            		+ "password VARCHAR(255) NOT NULL, "
            		+ "high_score INT DEFAULT 0, "
            		+ "game_money INT DEFAULT 0, "
            		+ "active_skinid INT DEFAULT NULL, "
            		+ "FOREIGN KEY (active_skinid) REFERENCES skins(id) "
            		+ ");";
            
            String createUserSkinsTableSQL = "CREATE TABLE IF NOT EXISTS user_skins (\r\n"
            		+ "user_id INT, "
            		+ "skin_id INT, "
            		+ "FOREIGN KEY (user_id) REFERENCES users(id), "
            		+ "FOREIGN KEY (skin_id) REFERENCES skins(id), "
            		+ "PRIMARY KEY (user_id, skin_id) \r\n"
            		+ ");";

            // 외래 키 추가는 별도의 ALTER TABLE 문으로 처리
            String alterUsersTableSQL = "ALTER TABLE users " +
                    "ADD CONSTRAINT fk_active_skin " +
                    "FOREIGN KEY (active_skinid) REFERENCES skins(id);";

            // 테이블 순서대로 생성
            stmt.executeUpdate(createSkinsTableSQL);
            stmt.executeUpdate(createUsersTableSQL);
            stmt.executeUpdate(createUserSkinsTableSQL);

            // 외래 키 제약 조건 추가
            try {
                stmt.executeUpdate(alterUsersTableSQL);
            } catch (SQLException e) {
                // 외래 키 제약 조건 추가 실패 시 로그 출력 (이미 존재하는 경우 등)
                System.out.println("Failed to add foreign key constraint: " + e.getMessage());
            }

            System.out.println("Database and tables initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed");
        }
    }

    // 커넥션 반환 메서드
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed");
        }
    }

    // 점수 및 코인 업데이트 메서드
 // 데이터베이스 업데이트 로직
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
                    return; // 사용자 없으면 업데이트 중단
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to check user existence");
        }

        String updateSQL = "UPDATE users SET high_score = ?, game_money = ? WHERE id = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            int highScore = getCurrentHighScore(userId);
            int[] userStats = getUserScoreAndCoins(userId);
            int currentCoins = userStats[1];

            // 점수 업데이트
            if (newScore > highScore) {
                stmt.setInt(1, newScore);
            } else {
                stmt.setInt(1, highScore);
            }

            // 코인 누적 업데이트
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
        String querySQL = "SELECT IFNULL(high_score, 0) AS high_score, IFNULL(game_money, 0) AS game_money FROM users WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(querySQL)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getInt("high_score");
                    result[1] = rs.getInt("game_money");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve score and coins");
        }
        return result;
    }
 

}
