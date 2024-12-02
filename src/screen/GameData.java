package screen;

import database.Database;

public class GameData {
    private static int coins;
    private static int highScore;
    private static int bombNums;
    private static int passNums;
    private static int userId;

    public static void initialize(int id) {
        userId = id;
        coins = Database.getUserCoins(userId);
        highScore = Database.getCurrentHighScore(userId);
        bombNums = Database.getUserBombs(userId);
        passNums = Database.getUserPasses(userId);
        
        System.out.println("Initialize GameData: userId=" + userId + ", coins=" + coins + ", highScore=" + highScore + ", bombNums=" + bombNums + ", passNums=" + passNums);
    }

    // 코인 설정
    public static int getCoins() {
        return coins;
    }

    public static void setCoins(int i) {
        coins = i;
        Database.updateUserCoins(userId, coins);
        System.out.println("Updated coins: " + coins);
    }

    public static void addCoins(int i) {
        coins += i;
        Database.updateUserCoins(userId, coins);
        System.out.println("Added coins: " + coins);
    }

    public static void subtractCoins(int i) {
        coins -= i;
        Database.updateUserCoins(userId, coins);
        System.out.println("Subtracted coins: " + coins);
    }

    // 최고기록 설정
    public static int getHighScore() {
        return highScore;
    }

    public static void setHighScore(int i) {
        highScore = i;
        Database.updateHighScore(userId, highScore);
        System.out.println("Updated highScore: " + highScore);
    }

    // 폭탄 아이템 보유개수 설정
    public static int getBombs() {
        return bombNums;
    }

    public static void setBombs(int i) {
        bombNums = i;
        Database.updateUserBombs(userId, bombNums);
        System.out.println("Updated bombNums: " + bombNums);
    }

    public static void addBombs(int i) {
        bombNums += i;
        Database.updateUserBombs(userId, bombNums);
        System.out.println("Added bombNums: " + bombNums);
    }

    public static void subtractBombs(int i) {
        bombNums -= i;
        Database.updateUserBombs(userId, bombNums);
        System.out.println("Subtracted bombNums: " + bombNums);
    }

    // 패스 아이템 보유개수 설정
    public static int getPasses() {
        return passNums;
    }

    public static void setPasses(int i) {
        passNums = i;
        Database.updateUserPasses(userId, passNums);
        System.out.println("Updated passNums: " + passNums);
    }

    public static void addPasses(int i) {
        passNums += i;
        Database.updateUserPasses(userId, passNums);
        System.out.println("Added passNums: " + passNums);
    }

    public static void subtractPasses(int i) {
        passNums -= i;
        Database.updateUserPasses(userId, passNums);
        System.out.println("Subtracted passNums: " + passNums);
    }
}
