package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import database.Database;

public class ItemShopScreen extends JPanel  implements RefreshableScreen {
    private BufferedImage backgroundImage;
    private JLabel coin; // 현재 코인 개수
    private JLabel BombPrice; // bomb 가격
    private JLabel BombNums; // bomb 보유 개수
    private JLabel PassPrice; // pass 가격
    private JLabel PassNums; // pass 보유 개수
    private int userId;

    public ItemShopScreen(MainFrame mainFrame, int userId) {
        this.userId = userId;
        setLayout(null);

        // 배경 이미지 설정
        try {
            backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/itemshop.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // coin 레이블
        coin = new JLabel();
        coin.setBounds(65, 32, 150, 40);
        coin.setFont(new Font("Comic Sans MS", Font.BOLD, 21));
        add(coin);

        // back 버튼
        RoundedButton backButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
        backButton.setBounds(420, 22, 64, 64);
        backButton.addActionListener(e -> mainFrame.showScreen("ShopScreen"));
        add(backButton);

        // BombPrice 레이블
        BombPrice = new JLabel("1000");
        BombPrice.setBounds(265, 320, 150, 40);
        BombPrice.setFont(new Font("Comic Sans MS", Font.BOLD, 19));
        add(BombPrice);

        // Bomb 보유 개수 레이블
        BombNums = new JLabel();
        BombNums.setBounds(390, 320, 150, 40);
        BombNums.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); 
        add(BombNums);

        // PassPrice 레이블
        PassPrice = new JLabel("1000");
        PassPrice.setBounds(265, 542, 150, 40);
        PassPrice.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); 
        add(PassPrice);

        // Pass 보유 개수 레이블
        PassNums = new JLabel();
        PassNums.setBounds(390, 542, 150, 40);
        PassNums.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); 
        add(PassNums);

        // UI 초기화
        refreshUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void updateCoins(int coins) {
        coin.setText("" + coins);
    }

    public void updateBombNums(int num) {
        BombNums.setText("" + num);
    }

    public void updatePassNums(int num) {
        PassNums.setText("" + num);
    }

    @Override
    public void refreshUI() {
        // 데이터베이스에서 사용자 정보 가져오기
        int coins = Database.getUserCoins(userId);
        int bombs = Database.getUserBombs(userId);
        int passes = Database.getUserPasses(userId);

        // UI 업데이트
        updateCoins(coins);
        updateBombNums(bombs);
        updatePassNums(passes);
    }

	public void setUserId(int userId) {
        this.userId = userId;
        refreshUI();
    }
}
