package screen;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.util.List;
import java.awt.Component; // 필요한 경우 다른 필요한 임포트도 추가

import database.Database;
import database.DatabaseManager;
import watermelonGame.Coin;
import watermelonGame.Fruit;

public class SkinShopScreen extends JPanel implements RefreshableScreen {
    public  String currentSkin = "default-skin";
	private JLabel coin;
    private JScrollPane scrollPane;
    private JPanel skinListPanel;
    private  int userId = -1; 
    private  int coins = 0; 
    private static String appliedSkin = "default_skin"; 


    private final Map<String, SkinData> skins = new LinkedHashMap<>();

    public SkinShopScreen(MainFrame mainFrame) {
        setLayout(null);
        this.skinListPanel = new JPanel(); 
        this.add(skinListPanel, BorderLayout.CENTER);  

        // 코인 레이블
        coin = new JLabel(String.valueOf(Coin.getCoins()));
        coin.setBounds(65, 32, 150, 40);
        coin.setFont(new Font("Comic Sans MS", Font.BOLD, 21));
        add(coin);

        // Back 버튼
        RoundedButton backButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
        backButton.setBounds(420, 22, 64, 64);
        backButton.addActionListener(e -> mainFrame.showScreen("ShopScreen"));
        add(backButton);

        // 스킨 데이터 초기화
        initializeSkinDataFromDB();

        // 스킨 목록 패널
        skinListPanel = new JPanel();
        skinListPanel.setLayout(new BoxLayout(skinListPanel, BoxLayout.Y_AXIS));
        skinListPanel.setOpaque(false);
        for (String skinKey : skins.keySet()) {
            addSkinItem(skinKey, skins.get(skinKey));
        }
        // 스크롤 패널 설정
        scrollPane = new JScrollPane(skinListPanel);
        scrollPane.setBounds(50, 100, 400, 550);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane);

        updateButtonStates(); 
        refreshUI();
    }
    //스킨 적용
    private void setAppliedSkin(String skinName) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET active_skin_name = ? WHERE id = ?")) {
            
            stmt.setString(1, skinName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
            this.appliedSkin = skinName;
            currentSkin = skinName;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "스킨 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    //스킨데이터 초기화
    private void initializeSkinDataFromDB() {
    	try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement userQuery = conn.prepareStatement("SELECT coins, active_skin_name FROM users WHERE id = ?"); 
                PreparedStatement skinsQuery = conn.prepareStatement(
                        "SELECT s.skin_name, s.price, us.user_id IS NOT NULL AS owned " +
                        "FROM skins s " +
                        "LEFT JOIN user_skins us ON s.skin_name = us.skin_name AND us.user_id = ?")) {

               // 사용자 코인 및 현재 적용된 스킨 정보 가져오기
               userQuery.setInt(1, userId);
               ResultSet userResult = userQuery.executeQuery();
               if (userResult.next()) {
                   coins = userResult.getInt("coins");
                   coin.setText(String.valueOf(coins)); 
                   
                   // 현재 적용된 스킨 설정
                   appliedSkin = userResult.getString("active_skin_name");
                   if (appliedSkin == null || appliedSkin.isEmpty()) {
                       appliedSkin = "default_skin";
                   }
               }

            // 스킨 데이터 가져오기
            skinsQuery.setInt(1, userId);
            ResultSet skinsResult = skinsQuery.executeQuery();
            while (skinsResult.next()) {
                String skin_name = skinsResult.getString("skin_name");
                int price = skinsResult.getInt("price");
                boolean owned = skinsResult.getBoolean("owned");
                skins.put(skin_name, new SkinData("Skin " + skin_name, price, owned));
            }

            // UI 업데이트
            updateSkinListUI();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터를 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void updateSkinListUI() {
        skinListPanel.removeAll();
        for (String skinkey : skins.keySet()) {
            addSkinItem(skinkey, skins.get(skinkey));
        }
        skinListPanel.revalidate();
        skinListPanel.repaint();
    }

    private void addSkinItem( String skinKey, SkinData skinData) {
        JPanel skinItemPanel = new JPanel();
        skinItemPanel.setLayout(null);
        skinItemPanel.setPreferredSize(new Dimension(400, 130));
        skinItemPanel.setBackground(new Color(255, 243, 220, 200));
        skinItemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
       
        JLabel skinImageLabel = new JLabel();
        skinImageLabel.setBounds(20, 15, 120, 100); 
        skinImageLabel.setIcon(new ImageIcon("src/skins/" + skinKey + "/default"));
        skinItemPanel.add(skinImageLabel);
        // 스킨 이름
        JLabel nameLabel = new JLabel(skinData.getName());
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        nameLabel.setBounds(260, 15, 200, 30); 
        skinItemPanel.add(nameLabel);

        // 액션 버튼
        RoundedButton actionButton = new RoundedButton("", new Color(220, 80, 80), Color.WHITE, 10);
        actionButton.setBounds(260, 60, 90, 40); 
        actionButton.addActionListener(e -> handleSkinAction(skinKey, skinData, actionButton));
        skinItemPanel.add(actionButton);


        skinListPanel.add(skinItemPanel);
    }
    //스킨 구매나 적용
    private void handleSkinAction(String skinKey, SkinData skinData, RoundedButton button) {
        skinData = Database.getSkin(skinKey); 
        if (skinData.isOwned()) {
            // 이미 소유한 스킨은 적용만
            applySkin(skinKey);
            refreshUI();
        } else {
            // 사용자의 코인 정보 가져오기
            int userCoin = Database.getUsercoin(userId);            
            if (userCoin >= skinData.getPrice()) {
                int response = JOptionPane.showConfirmDialog(this, "스킨을 구매하시겠습니까? (" + skinData.getPrice() + " 코인)", 
                        "스킨 구매", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    // 코인 차감 및 업데이트
                    Coin.subtractCoins(userId, skinData.getPrice());
                    
                    // 스킨 소유 상태 및 데이터베이스 업데이트
                    skinData.setOwned(true);
                    Database.updateSkinOwnership(userId, skinKey, true);
                    skins.put(skinKey, skinData);
                    // 버튼 상태 갱신
                    updateButtonStates();
                    // 구매 성공 메시지
                    JOptionPane.showMessageDialog(this, "스킨 " + skinData.getName() + "을/를 구매하셨습니다!");
                }
            } else {
                // 코인 부족 메시지
                System.out.print(Coin.getCoins());
                JOptionPane.showMessageDialog(this, "코인이 부족합니다!", "구매 실패", JOptionPane.ERROR_MESSAGE);
            }
            refreshUI();
        }
    }

    //버튼 업데이트
    private void updateButtonStates() {
        List<SkinData> skinDataList = Database.getAllSkins(userId);
        Component[] components = skinListPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component subComponent : panel.getComponents()) {
                    if (subComponent instanceof RoundedButton) {
                        RoundedButton button = (RoundedButton) subComponent;
                        String skinKey = skins.keySet().toArray(new String[0])[skinListPanel.getComponentZOrder(panel)];
                        SkinData skinData = skinDataList.stream()
                            .filter(s -> s.getName().equals(skinKey))
                            .findFirst()
                            .orElse(null);

                        if (skinData != null) {
                            System.out.println("Updating button for skin: " + skinKey + " | Owned: " + skinData.isOwned()); // 디버깅용 출력

                            if (skinData.isOwned()) {
                                if (appliedSkin.equals(skinKey)) {
                                    button.setText("적용중");
                                    button.setBackground(new Color(218, 165, 32)); 
                                    button.setContentAreaFilled(false); 
                                    button.setEnabled(false); 
                                } else {
                                    button.setText("적용하기");
                                    button.setBackground(new Color(198, 155, 93)); 
                                    button.setContentAreaFilled(false);
                                    button.setEnabled(true); 
                                }
                            } else {
                                button.setText(skinData.getPrice() + " 코인");
                                button.setBackground(new Color(245, 190, 60)); 
                                button.setContentAreaFilled(false);
                                button.setEnabled(true);
                            }
                        }
                    }
                }
            }
        }
    }


    //스킨 적용하기
    private void applySkin(String skinkey) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT skin_name FROM skins WHERE skin_name = ?")) {
            
            stmt.setString(1, skinkey);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String skin_name = rs.getString("skin_name");

                // setAppliedSkin 메서드 사용
                setAppliedSkin(skin_name);
                
                updateButtonStates();
                Fruit.refreshImages();
                JOptionPane.showMessageDialog(this, "스킨이 적용되었습니다!");
            } else {
                JOptionPane.showMessageDialog(this, "스킨을 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "적용 처리 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String getAppliedSkin() { 
        return appliedSkin;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon backgroundImage = new ImageIcon("WatermelonGame-physicsEngine/src/image/skinshop.png");
        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
    public static class SkinData {
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
    
    @Override
    public void refreshUI() {
        if (userId <= 0) {
            System.err.println("Invalid userId: " + userId);
            return;
        }
        String[] userDetails = Database.getUserDetails(userId);

        if (userDetails != null) {
            coin.setText(userDetails[2]); 
            // 스킨 데이터 갱신
            for (Map.Entry<String, SkinData> entry : skins.entrySet()) {
                String skinKey = entry.getKey();
                SkinData skinData = Database.getSkin(skinKey);
                if (skinData != null) {
                    skins.put(skinKey, skinData);
                }
            }

            // 버튼 상태 갱신
            updateButtonStates();
        } else {
            System.err.println("User details not found for userId: " + userId);
        }
    }


	public void updateCoins(int coins) {
		coin.setText("" + coins);
	}

	public void setUserId(int userId) {
        this.userId = userId;
        refreshUI();  
    }
}
