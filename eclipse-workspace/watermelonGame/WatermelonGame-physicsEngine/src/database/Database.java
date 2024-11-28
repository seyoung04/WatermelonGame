package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // 데이터베이스 연결 정보
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1018";
    private static final String DATABASE_NAME = "WatermelonGame";

    // 데이터베이스 연결 및 초기화
    public static Connection getConnection() {
        try {
            // 데이터베이스 연결
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 데이터베이스 생성
            try (Statement stmt = conn.createStatement()) {
                String createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
                stmt.executeUpdate(createDatabaseSQL);

                // 데이터베이스 선택
                stmt.execute("USE " + DATABASE_NAME);

                // 테이블 생성
                String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) UNIQUE NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "high_score INT DEFAULT 0, " +
                        "game_money INT DEFAULT 0, " +
                        "active_skinid INT DEFAULT NULL, " +  // 활성화된 스킨은 NULL로 초기화
                        "FOREIGN KEY (active_skinid) REFERENCES skins(id) " +  // active_skinid는 skins 테이블의 id를 참조
                        ");";

                String createSkinsTableSQL = "CREATE TABLE IF NOT EXISTS skins (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "skin_name VARCHAR(50) NOT NULL, " +
                        "skin_image VARCHAR(255) NOT NULL " +  // 이미지 경로 저장
                        ");";

                String createUserSkinsTableSQL = "CREATE TABLE IF NOT EXISTS user_skins (" +
                        "user_id INT, " +
                        "skin_id INT, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id), " +
                        "FOREIGN KEY (skin_id) REFERENCES skins(id), " +
                        "PRIMARY KEY (user_id, skin_id) " +  // 하나의 사용자가 동일한 스킨을 중복 보유할 수 없음
                        ");";

                // 테이블 생성 쿼리 실행
                stmt.executeUpdate(createUsersTableSQL);
                stmt.executeUpdate(createSkinsTableSQL);
                stmt.executeUpdate(createUserSkinsTableSQL);

                System.out.println("Tables created or already exist.");
            }

            // 데이터베이스 연결 반환
            return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("데이터베이스 연결 실패");
        }
    }

    // 점수와 코인을 업데이트하는 메소드
    public static void updateUserScoreAndCoins(int userId, int newScore, int newCoins) {
        try (Connection conn = getConnection()) {
            String updateSQL = "UPDATE users SET high_score = ?, game_money = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                stmt.setInt(1, newScore);  // 점수 업데이트
                stmt.setInt(2, newCoins);  // 코인 업데이트
                stmt.setInt(3, userId);    // 사용자 ID
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("점수 및 코인 업데이트 실패");
        }
    }

    // 사용자 ID로 점수와 코인 정보 조회 메소드
    public static int[] getUserScoreAndCoins(int userId) {
        int[] result = new int[2]; // 0: score, 1: coins
        try (Connection conn = getConnection()) {
            String querySQL = "SELECT high_score, game_money FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        result[0] = rs.getInt("high_score");
                        result[1] = rs.getInt("game_money");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("사용자 점수 및 코인 조회 실패");
        }
        return result;
    }
}
