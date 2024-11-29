package screen;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import watermelonGame.Coin;
import watermelonGame.Fruit;

public class SkinShopScreen extends JPanel implements RefreshableScreen {
    private JLabel coinLabel; // 현재 코인 개수 표시
    private JScrollPane scrollPane; // 스크롤 추가
    private JPanel skinListPanel; // 스크롤 가능한 패널
    private static String appliedSkin = "default"; // 기본 스킨

    // 스킨 데이터 관리
    private final Map<String, SkinData> skins = new LinkedHashMap<>(); // 순서를 유지하기 위해 LinkedHashMap 사용

    public SkinShopScreen(MainFrame mainFrame) {
        setLayout(null);

        // 코인 레이블
        coinLabel = new JLabel(String.valueOf(Coin.getCoins()));
        coinLabel.setBounds(65, 32, 150, 40);
        coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 21));
        add(coinLabel);

        // Back 버튼
        RoundedButton backButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
        backButton.setBounds(420, 22, 64, 64);
        backButton.addActionListener(e -> mainFrame.showScreen("ShopScreen"));
        add(backButton);

        // 스킨 데이터 초기화
        initializeSkinData();

        // 스킨 목록 패널
        skinListPanel = new JPanel();
        skinListPanel.setLayout(new BoxLayout(skinListPanel, BoxLayout.Y_AXIS));
        skinListPanel.setOpaque(false); // 배경 투명

        // 스킨 항목 추가
        for (String skinKey : skins.keySet()) {
            addSkinItem(skinKey, skins.get(skinKey));
        }

     // 스크롤 패널 설정
        scrollPane = new JScrollPane(skinListPanel);
        scrollPane.setBounds(50, 100, 400, 550); // 폭을 380으로 조정
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤바 숨김
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        add(scrollPane);


        updateButtonStates(); // 초기 버튼 상태 설정
    }

    private void initializeSkinData() {
        // Default Skin은 항상 맨 위
        skins.put("default", new SkinData("Default Skin", 0, true));
        skins.put("A", new SkinData("Skin A", 1000, false));
        skins.put("B", new SkinData("Skin B", 1500, false));
        skins.put("C", new SkinData("Skin C", 2000, false));
        skins.put("D", new SkinData("Skin D", 2500, false));
    }

    private void addSkinItem(String skinKey, SkinData skinData) {
        JPanel skinItemPanel = new JPanel();
        skinItemPanel.setLayout(null);
        skinItemPanel.setPreferredSize(new Dimension(400, 130)); // 폭을 400, 높이를 130으로 조정
        skinItemPanel.setBackground(new Color(255, 243, 220, 200)); // 투명도 추가
        skinItemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // 스킨 사진 (왼쪽에 배치)
        JLabel skinImageLabel = new JLabel();
        skinImageLabel.setBounds(20, 15, 120, 100); // 이미지 크기를 120x100으로 설정
        skinImageLabel.setIcon(new ImageIcon("src/skins/" + skinKey + "/default")); // 스킨마다 경로 다르게 설정
        skinItemPanel.add(skinImageLabel);

        // 스킨 이름 라벨 (조금 더 오른쪽에 배치)
        JLabel nameLabel = new JLabel(skinData.getName());
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        nameLabel.setBounds(260, 15, 200, 30); // x 좌표를 조정하여 오른쪽으로 이동
        skinItemPanel.add(nameLabel);

        // 상태 버튼 (오른쪽 아래에 배치)
        RoundedButton actionButton = new RoundedButton("", new Color(220, 80, 80), Color.WHITE, 10);
        actionButton.setBounds(260, 60, 90, 40); // 버튼 크기와 위치를 조정
        actionButton.addActionListener(e -> handleSkinAction(skinKey, skinData, actionButton));
        skinItemPanel.add(actionButton);

        skinListPanel.add(skinItemPanel);
    }


    private void handleSkinAction(String skinKey, SkinData skinData, RoundedButton button) {
        if (skinData.isOwned()) {
            applySkin(skinKey);
        } else {
            if (Coin.getCoins() >= skinData.getPrice()) {
                int response = JOptionPane.showConfirmDialog(this, "스킨을 구매하시겠습니까? (" + skinData.getPrice() + " 코인)", 
                        "스킨 구매", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    Coin.subtractCoins(skinData.getPrice());
                    coinLabel.setText(String.valueOf(Coin.getCoins()));
                    skinData.setOwned(true);
                    updateButtonStates();
                    JOptionPane.showMessageDialog(this, "스킨 " + skinData.getName() + "을/를 구매하셨습니다!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "코인이 부족합니다!", "구매 실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applySkin(String skinKey) {
        appliedSkin = skinKey;
        updateButtonStates();
        Fruit.refreshImages();
        JOptionPane.showMessageDialog(this, "스킨 " + skins.get(skinKey).getName() + " 이/가 적용되었습니다!");
    }

    private void updateButtonStates() {
        Component[] components = skinListPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                for (Component subComponent : panel.getComponents()) {
                    if (subComponent instanceof RoundedButton) {
                        RoundedButton button = (RoundedButton) subComponent;
                        String skinKey = skins.keySet().toArray(new String[0])[skinListPanel.getComponentZOrder(panel)];
                        SkinData skinData = skins.get(skinKey);

                        if (skinData.isOwned()) {
                            if (appliedSkin.equals(skinKey)) {
                                button.setText("적용중");
                                button.setBackground(new Color(218, 165, 32)); // 약간 어두운 갈색
                           
                                button.setContentAreaFilled(false); // Look-and-Feel 무시
                                button.setEnabled(false); // 비활성화
                            } else {
                                button.setText("적용하기");
                                button.setBackground(new Color(198, 155, 93)); // 갈색 계열
                               
                                button.setContentAreaFilled(false);
                                button.setEnabled(true); // 활성화
                            }
                        } else {
                            button.setText(skinData.getPrice() + " 코인");
                            button.setBackground(new Color(245, 190, 60)); // 어두운 노란색
                          
                            button.setContentAreaFilled(false);
                            button.setEnabled(true); // 활성화
                        }
                    }
                }
            }
        }
    }



    public static String getAppliedSkin() { // 추가된 메서드
        return appliedSkin;
    }

    @Override
    public void refreshUI() {
        coinLabel.setText(String.valueOf(Coin.getCoins()));
        updateButtonStates();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 배경 이미지를 그립니다
        ImageIcon backgroundImage = new ImageIcon("WatermelonGame-physicsEngine/src/image/skinshop.png");
        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
    

    private static class SkinData {
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