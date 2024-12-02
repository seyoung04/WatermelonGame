package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;



public class Skin {
    public void createSkinData() {
        String createSkininfo = 
            "INSERT IGNORE INTO Skins (skin_name, price) VALUES " +
            "('default_skin', 0), " + 
            "('A', 1000), " +
            "('B', 1500), " +
            "('C', 2000), " +
            "('D', 2500);";

        try {
            // 데이터베이스 연결
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/WatermelonGame", "root", "1018");
            Statement stmt = conn.createStatement();

            // 쿼리 실행
            stmt.executeUpdate(createSkininfo);

            System.out.println("스킨 데이터가 성공적으로 추가되었습니다!");

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class SkinData {
        private final String name;
        private final int price;
        private boolean isOwned;

        public SkinData(String name, int price, boolean isOwned) {
            this.name = name;
            this.price = price;
            this.isOwned = isOwned;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public boolean isOwned() {
            return isOwned;
        }

        public void setOwned(boolean owned) {
            isOwned = owned;
        }
    }

}