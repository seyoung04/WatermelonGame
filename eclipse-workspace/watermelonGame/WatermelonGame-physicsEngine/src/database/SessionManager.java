package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionManager {
    private static volatile SessionManager instance;
    private int userId = -1; // -1은 로그인되지 않은 상태
    private int coins;
    private int high_score;
    private String username;
    
    
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public SessionManager() {}
    
    public void refreshUserDetails() {
        if (userId != -1) {
            int[] userStats = Database.getUserScoreAndCoins(userId);
            String[] userInfo = Database.getUserDetails(userId);

            if (userStats != null && userInfo != null) {
                this.high_score = userStats[0];
                this.coins = userStats[1];
                this.username = userInfo[0];
            }
        }
    }
    

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID set to: " + userId);
    }
    public void setUserName(int userId) {
        this.userId = userId;
        System.out.println("User ID set to: " + userId);
    
    }
    public int getUserId() {
        return userId;
    }

    // 사용자 이름 설정
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    // 하이 스코어 설정
    public void setHighScore(int high_Score) {
        this.high_score = high_Score;
    }

    public int getHighScore() {
        return high_score;
    }

    // 코인 수 설정
    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getCoins() {
        return coins;
    }
    public boolean isLoggedIn() {
        return userId != -1;
    }

    public void logout() {
        this.userId = -1;
        System.out.println("User logged out.");
    }

    public void clearSession() {
        this.userId = -1;
        System.out.println("Session cleared.");
    }
}
    
    

