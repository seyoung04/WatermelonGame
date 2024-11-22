package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

	// 로그인 기능
	public static boolean login(String username, String password) {
		String query = "SELECT password FROM Users WHERE username = ?";
		try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String dbPassword = rs.getString("password");
				return password.equals(dbPassword);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 회원가입 기능
	public static boolean register(String username, String password) {
		// 아이디 중복 체크
		if (isUsernameTaken(username)) {
			return false;
		}

		String query = "INSERT INTO Users (username, password) VALUES (?, ?)";
		try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password); // TODO: 해싱 후 저장
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 아이디 중복 체크
	private static boolean isUsernameTaken(String username) {
		String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
		try (Connection conn = Database.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}
