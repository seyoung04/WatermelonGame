package database;

public class SessionManager {
    private static volatile SessionManager instance;
    private int userId = -1; // -1은 로그인되지 않은 상태

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

    private SessionManager() {}

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("User ID set to: " + userId);
    }

    public int getUserId() {
        return userId;
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
