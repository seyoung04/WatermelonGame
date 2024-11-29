package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import database.Database;
import watermelonGame.Falling;

public class HomeScreen extends JPanel {
    private JLabel highScore;
    private JLabel coin;
    private BufferedImage backgroundImage;
    private GameScreen gameScreen;
    private int userId;

    public HomeScreen(MainFrame mainFrame) {

        setLayout(null);
        setBackground(new Color(222, 184, 135));

        // 배경 이미지 설정
        try {
            backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/homescreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 최고 점수 라벨 설정
        highScore = new JLabel("Loading...");
        highScore.setBounds(110, 32, 150, 40);
        highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 27));
        add(highScore);

        // 코인 라벨 설정
        coin = new JLabel("Loading...");
        coin.setBounds(340, 32, 150, 40);
        coin.setFont(new Font("Comic Sans MS", Font.BOLD, 27));
        add(coin);

        // 게임 시작 버튼 설정
        RoundedButton startButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
        startButton.setBounds(140, 373, 215, 75);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 게임 시작 시 데이터 업데이트

                mainFrame.showScreen("GameScreen");
            }
        });
        add(startButton);

        // 상점 버튼 설정
        RoundedButton shopButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
        shopButton.setBounds(158, 465, 180, 55);
        shopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showScreen("ShopScreen");
            }
        });
        add(shopButton);

        // 데이터베이스에서 점수와 코인 정보 가져오기
        loadUserData();
    }

    private void loadUserData() {
        // 데이터베이스에서 점수와 코인 가져오기
        int[] userStats = Database.getUserScoreAndCoins(userId);
        int highScoreValue = userStats[0];
        int coinsValue = userStats[1];

        // 라벨 업데이트
        updateHighScore(highScoreValue);
        updateCoins(coinsValue);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // 최고 점수 업데이트 메서드
    public void updateHighScore(int score) {
        highScore.setText("" + score);
    }

    // 코인 업데이트 메서드
    public void updateCoins(int coins) {
        coin.setText("" + coins);
    }

    // userId를 업데이트하는 메서드
    public void updateUserId(int userId) {
        this.userId = userId;
        // userId 변경 시 데이터 갱신
        loadUserData();
    }
}
