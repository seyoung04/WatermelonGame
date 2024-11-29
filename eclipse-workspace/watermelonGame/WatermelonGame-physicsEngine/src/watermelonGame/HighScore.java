package watermelonGame;

public class HighScore {
    private static int highScore = 3000; // 초기 하이스코어 값

    public static int getHighScore() {
        return highScore;
    }

    public static void setHighScore(int newHighScore) {
        if (newHighScore > highScore) {
            highScore = newHighScore;
        }
    }
}