import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SkinData {
    private String skinName;
    private int price;
    private boolean isOwned;

    // Constructor
    public SkinData(String skinName, int price, boolean isOwned) {
        this.skinName = skinName;
        this.price = price;
        this.isOwned = isOwned;
    }

    // Getters
    public String getSkinName() {
        return skinName;
    }

    public int getPrice() {
        return price;
    }

    public boolean isOwned() {
        return isOwned;
    }

    // Method to retrieve skin data
    public static SkinData getSkin(String skinName) {
        String query = "SELECT skin_name, price, " + 
                       "EXISTS(SELECT * FROM user_skins WHERE skin_name = ? AND is_purchased = TRUE) AS is_owned " + 
                       "FROM skins WHERE skin_name = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, skinName);
            pstmt.setString(2, skinName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new SkinData(
                        rs.getString("skin_name"), 
                        rs.getInt("price"), 
                        rs.getBoolean("is_owned")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private static Connection getConnection() throws SQLException {
        return null;
    }
}