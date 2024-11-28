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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import watermelonGame.Fruit;

public class SkinShopScreen extends JPanel {
    private BufferedImage backgroundImage;
    private JLabel coin; // 현재 코인 개수
    private JButton defaultButton;
    private JButton aButton;
    private JButton bButton;
    private static String appliedSkin = "default"; // 현재 적용된 스킨 (기본값은 default)

    public SkinShopScreen(MainFrame mainFrame) {
        setLayout(null);

        // 배경 이미지 설정
        try {
            backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/skinshop.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // coin 레이블
        coin = new JLabel("3000");
        coin.setBounds(65, 32, 150, 40);
        coin.setFont(new Font("Comic Sans MS", Font.BOLD, 21)); // 폰트 설정
        add(coin);

        // back 버튼
        RoundedButton backButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
        backButton.setBounds(420, 22, 64, 64);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showScreen("ShopScreen");
            }
        });
        add(backButton);

        // 각 스킨에 대한 "적용하기" 버튼 생성
        defaultButton = createButton("Default Skin - 적용하기", 350, 100);
        aButton = createButton("A Skin - 적용하기", 350, 200);
        bButton = createButton("B Skin - 적용하기", 350, 300);

        // 각 버튼에 액션 리스너 추가
        defaultButton.addActionListener(e -> applySkin("default"));
        aButton.addActionListener(e -> applySkin("A"));
        bButton.addActionListener(e -> applySkin("B"));

        // 패널에 버튼 추가
        add(defaultButton);
        add(aButton);
        add(bButton);

        updateButtonStates(); // 초기 버튼 상태 설정
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 150, 30); // 버튼 크기 및 위치 설정
        return button;
    }

    private void applySkin(String skin) {
        appliedSkin = skin;
        updateButtonStates();
        Fruit.refreshImages(); // 모든 과일의 이미지를 새로고침
        JLabel messageLabel = new JLabel("스킨 " + appliedSkin + " 이/가 적용되었습니다!");
        JOptionPane.showMessageDialog(this, messageLabel);
    }

    private void updateButtonStates() {
        defaultButton.setText("Default Skin" + (appliedSkin.equals("default") ? " - 적용중" : " - 적용하기"));
        aButton.setText("A Skin" + (appliedSkin.equals("A") ? " - 적용중" : " - 적용하기"));
        bButton.setText("B Skin" + (appliedSkin.equals("B") ? " - 적용중" : " - 적용하기"));
    }

    public static String getAppliedSkin() {
        return appliedSkin;
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
}

