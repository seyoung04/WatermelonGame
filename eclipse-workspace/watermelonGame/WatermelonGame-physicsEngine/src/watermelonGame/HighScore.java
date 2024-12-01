package watermelonGame;

import database.Database;

public class HighScore {
    private static int highScore = 0; 

    //데이터베이스에서 하이스코어 가져오기
    public static int loadHighScore(int userId) {
        highScore = Database.getHighScore(userId);
        return highScore;
    }

    public static int getHighScore() {
        return highScore;
    }

    // 하이스코어 설정
    public static void setHighScore(int userId, int newHighScore) {
        if (newHighScore > highScore) {
            highScore = newHighScore;
            Database.updateHighScore(userId, newHighScore);
        }
    }
    
}
