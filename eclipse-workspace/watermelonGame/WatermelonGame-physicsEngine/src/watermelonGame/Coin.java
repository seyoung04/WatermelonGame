package watermelonGame;

import database.Database;

public class Coin {
    private static int coins = 0; 

    public static int getCoins() {
        return coins;
    }

    public static void setCoins(int userId, int newCoins) {
        coins += newCoins;
            Database.updateHighScore(userId, coins);
    }

    public static void addCoins(int amount) {
        coins += amount;
    }

    public static void subtractCoins(int userId, int amount) {
        int currentCoins = Database.getUsercoin(userId);
        if (currentCoins >= amount) {
            currentCoins -= amount;
            Database.updateCoins(userId, currentCoins); 
        } 
    }

}