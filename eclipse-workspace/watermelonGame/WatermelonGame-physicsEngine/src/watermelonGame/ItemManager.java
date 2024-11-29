package watermelonGame;

public class ItemManager {
    private static int bombPrice = 1000;
    private static int passPrice = 1000;
    private static int bombCount = 10; // 초기값
    private static int passCount = 10; // 초기값

    public static int getBombPrice() {
        return bombPrice;
    }

    public static int getPassPrice() {
        return passPrice;
    }

    public static int getBombCount() {
        return bombCount;
    }

    public static int getPassCount() {
        return passCount;
    }

    public static void addBomb(int amount) {
        bombCount += amount;
    }

    public static void addPass(int amount) {
        passCount += amount;
    }
}