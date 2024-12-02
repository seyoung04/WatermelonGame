// ItemShopScreen.java
package screen;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import database.Database;

public class ItemShopScreen extends BaseScreen implements RefreshableScreen {
    private BufferedImage backgroundImage;
    private JLabel coinLabel;
    private JLabel bombPrice;
    private JLabel bombNums;
    private JLabel passPrice;
    private JLabel passNums;
    private int userId;

    public ItemShopScreen(MainFrame mainFrame, int userId) {
        setLayout(null);
        this.userId = userId;

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("src/image/itemShop.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // UI 구성 요소 초기화
        initComponents(mainFrame);
        refreshUI(); // 화면 초기화
    }

    private void initComponents(MainFrame mainFrame) {
        // Title Label
        JLabel iLabel = new JLabel("Item");
        iLabel.setBounds(200, 30, 150, 40);
        iLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        iLabel.setForeground(Color.WHITE);
        add(iLabel);

        // Coin Label
        coinLabel = new JLabel("" + GameData.getCoins());
        coinLabel.setBounds(65, 33, 150, 40);
        coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 21));
        add(coinLabel);

        // Back Button
        RoundedButton backButton = new RoundedButton(new Color(255, 222, 178), 10, "", Color.WHITE,
                new Font("Comic Sans MS", Font.BOLD, 18));
        backButton.setBounds(390, 22, 64, 64);
        backButton.setIcon(loadIcon("src/image/item/back.png", 54, 54));
        backButton.addActionListener(e -> mainFrame.showScreen("ShopScreen"));
        add(backButton);

        // Bomb Section
        createItemSection("Bomb", "원하는 과일 하나를<br>&nbsp;삭제하는 아이템", 205, 322, bombNums,
                e -> purchaseItem(1000, true));

        // Pass Section
        createItemSection("Pass", "원하지 않는 과일을<br>&nbsp;패스하는 아이템", 430, 543, passNums,
                e -> purchaseItem(1000, false));
    }

    private JLabel createItemSection(String title, String description, int titleY, int priceY, JLabel j, 
            ActionListener purchaseListener) {
// Title
JLabel titleLabel = new JLabel(title);
titleLabel.setBounds(270, titleY, 150, 40);
titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
titleLabel.setForeground(title.equals("Bomb") ? new Color(255, 109, 109) : new Color(98, 147, 255));
add(titleLabel);

// Description
JLabel descLabel = new JLabel("<html>" + description + "<html>", SwingConstants.CENTER);
descLabel.setBounds(220, titleY - 18, 200, 200);
descLabel.setFont(new Font("Bazzi", Font.PLAIN, 26));
descLabel.setHorizontalAlignment(SwingConstants.CENTER);
add(descLabel);

// Price
JLabel priceLabel = new JLabel("1000");
priceLabel.setBounds(265, priceY, 150, 40);
priceLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 21));
add(priceLabel);

// Count
JLabel countLabel = new JLabel("보유개수: " + (title.equals("Bomb") ? GameData.getBombs() : GameData.getPasses()));
countLabel.setBounds(325, priceY, 150, 40);
countLabel.setFont(new Font("Bazzi", Font.PLAIN, 21));
add(countLabel);

// Purchase Button
RoundedButton purchaseButton = new RoundedButton(new Color(255, 172, 62), 10, "구매하기", Color.WHITE,
new Font("Malgun Gothic", Font.BOLD, 20));
purchaseButton.setBounds(80, priceY + 3, 120, 38);
purchaseButton.addActionListener(purchaseListener);
add(purchaseButton);

return countLabel;
}

    private ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void purchaseItem(int price, boolean isBomb) {
        if (GameData.getCoins() >= price) {
            GameData.subtractCoins(price);
            if (isBomb) {
                GameData.addBombs(1);
            } else {
                GameData.addPasses(1);
            }
            refreshUI();
        } else {
            showCustomMessage("코인이 부족합니다!");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    @Override
    public void refreshUI() {
        if (userId <= 0) {
            System.err.println("Invalid userId: " + userId);
            return;
        }

        coinLabel.setText("" + GameData.getCoins());
        bombNums.setText("보유개수: " + GameData.getBombs());
        passNums.setText("보유개수: " + GameData.getPasses());
    }

    private void showCustomMessage(String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "알림", true);
        dialog.setLayout(null);
        dialog.setSize(250, 140);
        dialog.setLocationRelativeTo(this);

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        messageLabel.setBounds(0, 20, 250, 30);

        RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
                new Font("Malgun Gothic", Font.BOLD, 15));
        confirmButton.setBounds(75, 70, 100, 30);
        confirmButton.addActionListener(e -> dialog.dispose());

        dialog.add(messageLabel);
        dialog.add(confirmButton);
        dialog.setVisible(true);
    }

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}
	public void setUserId(int userId) {
        this.userId = userId;
        refreshUI(); 
    }
}
