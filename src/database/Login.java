//Login.java
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login {

	// 로그인 기능
	public static boolean login(String username, String password) {
		String query = "SELECT id, password FROM Users WHERE username = ?";
		try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String dbPassword = rs.getString("password");
				int userId = rs.getInt("id");
				if (password.equals(dbPassword)) {
					// 로그인 성공 시, 세션에 userId 저장
					SessionManager.getInstance().setUserId(userId);
					System.out.println("로그인 성공, User ID: " + userId);
					return true;
				} else {
					System.out.println("비밀번호가 일치하지 않습니다.");
				}
			} else {
				System.out.println("사용자 이름을 찾을 수 없습니다.");
			}
		} catch (SQLException e) {
			System.err.println("로그인 중 오류 발생: " + e.getMessage());
		}
		return false;
	}

	// 회원가입 기능
	public static boolean register(String username, String password) {
		// 아이디 중복 체크
		if (!isUsernameAvailable(username)) {
			System.out.println("이미 존재하는 사용자 이름입니다.");
			return false;
		}

		String query = "INSERT INTO Users (username, password) VALUES (?, ?)";
		try (Connection conn = Database.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, username);
			pstmt.setString(2, password);

			int affectedRows = pstmt.executeUpdate();

			if (affectedRows == 0) {
				System.err.println("회원가입 실패: 데이터베이스 업데이트 실패.");
				return false;
			}

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int userId = generatedKeys.getInt(1);
					System.out.println("회원가입 성공! 사용자 ID: " + userId);
					return true;
				}
			}
		} catch (SQLException e) {
			System.err.println("회원가입 중 오류 발생: " + e.getMessage());
		}
		return false;
	}

	// 사용자 이름 사용 가능 여부 체크
	private static boolean isUsernameAvailable(String username) {
		String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
		try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) == 0; // 0이면 사용 가능
			}
		} catch (SQLException e) {
			System.err.println("사용자 이름 중복 확인 중 오류 발생: " + e.getMessage());
		}
		return false;
	}
}