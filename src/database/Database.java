package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.SkinData;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1018";
    private static final String DATABASE_NAME = "WatermelonGame";

    static {
        initializeDatabase();
    }

    // 데이터베이스 초기화
    private static void initializeDatabase() {
    	Skin skin = new Skin();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
            stmt.execute("USE " + DATABASE_NAME);

            String createSkinsTableSQL = "CREATE TABLE IF NOT EXISTS skins ("
            	    + "skin_name VARCHAR(50) NOT NULL PRIMARY KEY, "
            	    + "price INT DEFAULT 0 "
            	    + ");";

            	String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users ("
            	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
            	    + "username VARCHAR(50) UNIQUE NOT NULL, "
            	    + "password VARCHAR(255) NOT NULL, "
            	    + "current_score INT DEFAULT 0, " 
            	    + "high_score INT DEFAULT 0, "
            	    + "coins INT DEFAULT 0, "
            	    + "bomb INT DEFAULT 0, "
            	    + "pass INT DEFAULT 0, "
            	    + "active_skin_name VARCHAR(50) DEFAULT 'default_skin' NOT NULL, " 
            	    + "FOREIGN KEY (active_skin_name) REFERENCES skins(skin_name) "
            	    + ");";
            	            
            	String createUserSkinsTableSQL = "CREATE TABLE IF NOT EXISTS user_skins ("
            	    + "user_id INT, "
            	    + "skin_name VARCHAR(50), "
            	    + "is_purchased BOOLEAN DEFAULT FALSE, "
            	    + "FOREIGN KEY (user_id) REFERENCES users(id), "
            	    + "FOREIGN KEY (skin_name) REFERENCES skins(skin_name), "
            	    + "PRIMARY KEY (user_id, skin_name)"
            	    + ");";

            	String createItemsTableSQL = "CREATE TABLE IF NOT EXISTS items ("
            	    + "item_id INT AUTO_INCREMENT PRIMARY KEY, "
            	    + "item_name VARCHAR(50), "
            	    + "price INT DEFAULT 0 "
            	    + ");";


            String alterUsersTableSQL = "ALTER TABLE users " +
                    "ADD CONSTRAINT fk_active_skin " +
                    "FOREIGN KEY (active_skin_name) REFERENCES skins(skin_name);";
            stmt.executeUpdate(createSkinsTableSQL);
            stmt.executeUpdate(createUsersTableSQL);
            stmt.executeUpdate(createUserSkinsTableSQL);
            stmt.executeUpdate(createItemsTableSQL);

            String username = "testUser"; // 예시 사용자 이름
            String password = "testPassword"; // 예시 비밀번호

            try {
                // 사용자 존재 여부 확인
                if (Login.isUsernameAvailable(username)) {
                    String insertDefaultSkinSQL = "INSERT INTO users (username, password, active_skin_name) VALUES (?, ?, 'default_skin')";
                    
                    try (Connection conn1 = DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
                         PreparedStatement pstmt = conn1.prepareStatement(insertDefaultSkinSQL)) {

                        pstmt.setString(1, username);
                        pstmt.setString(2, password);

                        int rowsAffected = pstmt.executeUpdate();
                        System.out.println("Rows affected: " + rowsAffected);
                    }
                } else {
                    System.out.println("이미 존재하는 사용자입니다.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            skin.createSkinData();
            // 외래 키 제약 조건 추가
            try {
                stmt.executeUpdate(alterUsersTableSQL);
            } catch (SQLException e) {
                // 외래 키 제약 조건 추가 실패 시 로그 출력 (이미 존재하는 경우 등)
                System.out.println("외래키 제약 조건 추가 실패" + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("데이터베이스 초기화 실패");
        }
    }

    // 커넥션 반환 메서드
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("데이터베이스 연결 실패");
        }
    }
//userId와 최고점수, 코인 반환
    public static String[] getUserDetails(int userId) {
        String query = "SELECT username, high_score, coins, bomb, pass FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    rs.getString("username"),
                    String.valueOf(rs.getInt("high_score")),
                    String.valueOf(rs.getInt("coins")),
                    String.valueOf(rs.getInt("bomb")),
                    String.valueOf(rs.getInt("pass"))

                };
            }
        } catch (SQLException e) {
            System.err.println("getUserDetails 실패" + e.getMessage());
        }
        return null;
    }
   
    //데이터베이스에 코인이랑 최고점수 업데이트
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
            throw new RuntimeException("updateUserScoreAndCoins 실패");
        }

        String updateSQL = "UPDATE users SET high_score = ?, coins = ?, current_score = ? WHERE id = ?";
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

            stmt.setInt(2, newCoins); 
            stmt.setInt(3, newScore); 
            stmt.setInt(4, userId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println("No rows updated. Verify user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("updateUserScoreAndCoins 실패");
        }
    }
    //현재의 최고기록 반환
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
    //최고기록과 코인 반환
    public static int[] getUserScoreAndCoins(int userId) {
        int[] result = new int[2];
        String querySQL = "SELECT IFNULL(high_score, 0) AS high_score, IFNULL(coins, 0) AS coins FROM users WHERE id = ?";
        
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(querySQL)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result[0] = rs.getInt("high_score");
                    result[1] = rs.getInt("coins");
                } else {
                    System.out.println("UserID: " + userId + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("getUserScoreAndCoins 실패");
        }
        return result;
    }
  
 
    public static void updateHighScore(int userId, int newHighScore) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
            String query = "UPDATE users SET high_score = ? WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, newHighScore);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //데이터베이스에 코인 업데이트
   public static void updateCoins(int userId, int coins) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
            String query = "UPDATE users SET coins = ? WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, coins);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getHighScore(int userId) {
        int highScore = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
            String query = "SELECT high_score FROM users WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                highScore = rs.getInt("high_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return highScore;
    }

    public static int getHighScore() {
        int highScore = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        SessionManager sessionManager = new SessionManager();

        try {
            conn = DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
            String query = "SELECT high_score FROM users WHERE id = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, sessionManager.getUserId());
            rs = stmt.executeQuery();

            if (rs.next()) {
                highScore = rs.getInt("high_score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return highScore;
    }

    //스킨 추가
        public static void addSkin(String name, int price) {
            String insertSkinSQL = "INSERT INTO skins (skin_name, price) VALUES (?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSkinSQL)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, price);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 스킨 데이터 가져오기
        public static SkinData getSkin(String skinName) {
            String query = "SELECT skin_name, price, " +
                           "EXISTS(SELECT * FROM user_skins WHERE skin_name = ? AND is_purchased = TRUE) AS is_owned " +
                           "FROM skins WHERE skin_name = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, skinName);
                pstmt.setString(2, skinName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new SkinData(
                            rs.getString("skin_name"),
                            rs.getInt("price"),
                            rs.getBoolean("is_owned")
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        public static List<SkinData> getAllSkins(int userId) {
            List<SkinData> skins = new ArrayList<>();
            String query = "SELECT skins.skin_name, skins.price, " +
                           "IF(user_skins.is_purchased IS NULL, FALSE, TRUE) AS is_owned " +
                           "FROM skins LEFT JOIN user_skins " +
                           "ON skins.skin_name = user_skins.skin_name " +
                           "AND user_skins.user_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        skins.add(new SkinData(
                            rs.getString("skin_name"),
                            rs.getInt("price"),
                            rs.getBoolean("is_owned")
                        ));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return skins;
        }


        


        // 스킨 소유 상태 업데이트
        public static void updateSkinOwnership(int userId, String skinName, boolean isOwned) {
            String updateSQL = "INSERT INTO user_skins (user_id, skin_name, is_purchased) VALUES (?, ?, ?) " +
                               "ON DUPLICATE KEY UPDATE is_purchased = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, skinName);
                pstmt.setBoolean(3, isOwned);
                pstmt.setBoolean(4, isOwned);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



        // 사용자 코인 가져오기
        public static int getUsercoin(int userId) {
            String query = "SELECT coins FROM users WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("coins");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }


        // 유저 정보 가져오기
        public static int getUserCoins(int userId) {
            String query = "SELECT coins FROM users WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("coins");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0; 
        }

        public static int getUserBombs(int userId) {
            String query = "SELECT bomb FROM users WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("bomb");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        // 유저 정보 업데이트
        public static void updateUserCoins(int userId, int coins) {
            String updateSQL = "UPDATE users SET coins = ? WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, coins);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void updateUserBombs(int userId, int bombs) {
            String updateSQL = "UPDATE users SET bomb = ? WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, bombs);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void updateUserPasses(int userId, int passes) {
            String updateSQL = "UPDATE users SET pass = ? WHERE id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                pstmt.setInt(1, passes);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public static int getUserPasses(int userId) {
            String query = "SELECT pass FROM users WHERE id = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("pass");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }
        public static void updateItemCount(String itemName, int count, int userId) {
            String query = "UPDATE users SET " + itemName + " = ? WHERE id = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, count);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public static int getItemCount(String itemName, int userId) {
            String query = "SELECT " + itemName + " FROM users WHERE id = ?";
            try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(itemName);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

    }




