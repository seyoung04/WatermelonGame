package watermelonGame;

public class Coin {
    private static int coins = 3000; // 초기 코인 값

    public static int getCoins() {
        return coins;
    }

    public static void setCoins(int newCoins) {
        coins = newCoins;
    }

    public static void addCoins(int amount) {
        coins += amount;
    }

    public static void subtractCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
        }
    }
}
