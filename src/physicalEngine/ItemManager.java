package physicalEngine;

import database.Database;

public class ItemManager {
    private static int bombPrice = 1000;
    private static int passPrice = 1000;
    private static int bombCount;
    private static int passCount;
    private static int userId;

    // 초기화 메서드 추가
    public static void initialize(int userId) {
        ItemManager.userId = userId;
        bombCount = Database.getItemCount("bomb", userId);
        passCount = Database.getItemCount("pass", userId);
        System.out.println("Initialized ItemManager: bombCount=" + bombCount + ", passCount=" + passCount);
    }

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
        Database.updateItemCount("bomb", bombCount, userId);
        System.out.println("Updated bombCount: " + bombCount);
    }

    public static void addPass(int amount) {
        passCount += amount;
        Database.updateItemCount("pass", passCount, userId);
        System.out.println("Updated passCount: " + passCount);
    }
}
