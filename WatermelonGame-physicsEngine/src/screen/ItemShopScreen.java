package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import watermelonGame.Coin;
import watermelonGame.ItemManager;

public class ItemShopScreen extends JPanel implements RefreshableScreen {
    private BufferedImage backgroundImage;
    private JLabel coinLabel; // 현재 코인 개수 표시
    private JLabel bombPriceLabel, bombCountLabel;
    private JLabel passPriceLabel, passCountLabel;

    public ItemShopScreen(MainFrame mainFrame) {
        setLayout(null);

        // 배경 이미지 설정
        try {
            backgroundImage = ImageIO.read(new File("src/image/itemshop.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        // Bomb 가격 및 개수
        bombPriceLabel = new JLabel(ItemManager.getBombPrice() + "");
        bombPriceLabel.setBounds(258, 298, 150, 40);
        bombPriceLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 19));
        add(bombPriceLabel);

        bombCountLabel = new JLabel("" + ItemManager.getBombCount());
        bombCountLabel.setBounds(380, 298, 150, 40);
        bombCountLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 19));
        add(bombCountLabel);

        // Bomb 구매 버튼 (디자인 변경)
        RoundedButton buyBombButton = new RoundedButton("구매하기", new Color(255, 153, 153), Color.WHITE, 15);
        buyBombButton.setBounds(270, 337, 120, 30);
        buyBombButton.addActionListener(e -> handlePurchase("bomb"));
        add(buyBombButton);

        // Pass 가격 및 개수
        passPriceLabel = new JLabel(ItemManager.getPassPrice() + "");
        passPriceLabel.setBounds(258, 515, 150, 40);
        passPriceLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 19));
        add(passPriceLabel);

        passCountLabel = new JLabel("" + ItemManager.getPassCount());
        passCountLabel.setBounds(380, 515, 150, 40);
        passCountLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 19));
        add(passCountLabel);

        // Pass 구매 버튼 (디자인 변경)
        RoundedButton buyPassButton = new RoundedButton("구매하기", new Color(153, 204, 255), Color.WHITE, 15);
        buyPassButton.setBounds(270, 558, 120, 30);
        buyPassButton.addActionListener(e -> handlePurchase("pass"));
        add(buyPassButton);
    }

    private void handlePurchase(String item) {
        int price = item.equals("bomb") ? ItemManager.getBombPrice() : ItemManager.getPassPrice();
        if (Coin.getCoins() >= price) {
            int response = JOptionPane.showConfirmDialog(this, "구매하시겠습니까? (" + price + " 코인)", 
                    "구매 확인", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                Coin.subtractCoins(price);
                if (item.equals("bomb")) {
                    ItemManager.addBomb(1);
                    bombCountLabel.setText("" + ItemManager.getBombCount());
                } else if (item.equals("pass")) {
                    ItemManager.addPass(1);
                    passCountLabel.setText("" + ItemManager.getPassCount());
                }
                coinLabel.setText(String.valueOf(Coin.getCoins()));
                JOptionPane.showMessageDialog(this, "구매 완료!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "코인이 부족합니다!", "구매 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refreshUI() {
        coinLabel.setText(String.valueOf(Coin.getCoins()));
        bombCountLabel.setText("" + ItemManager.getBombCount());
        passCountLabel.setText("" + ItemManager.getPassCount());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
