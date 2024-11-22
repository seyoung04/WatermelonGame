package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	// 데이터베이스 연결 정보
	private static final String URL = "jdbc:mysql://localhost:3306/";
	private static final String USER = "Mysql 사용자 이름 입력";
	private static final String PASSWORD = "Mysql 사용자 비밀번호 입력";
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
				String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
						+ "username VARCHAR(50) UNIQUE NOT NULL, " + // id
						"password VARCHAR(255) NOT NULL, " + // password
						"high_score INT DEFAULT 0, " + // 최고 점수
						"game_money INT DEFAULT 0, " + // 게임 머니
						"skin_inventory TEXT DEFAULT NULL, " + // 구매한 스킨 목록
						"active_skin VARCHAR(50) DEFAULT NULL, " + // 활성화된 스킨
						"created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + // 계정 생성 시간
						")";
				stmt.executeUpdate(createTableSQL);
				System.out.println("Table created or already exists.");
			}

			// 데이터베이스 연결 반환
			return DriverManager.getConnection(URL + DATABASE_NAME, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("데이터베이스 연결 실패");
		}
	}

}
