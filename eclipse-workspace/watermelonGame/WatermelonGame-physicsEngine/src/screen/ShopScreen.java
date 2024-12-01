//ShopScreen.java
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

public class ShopScreen extends JPanel implements RefreshableScreen{
	private JLabel coin; // 현재 코인 개수
	private BufferedImage backgroundImage;
	private MainFrame mainFrame;
	private GameScreen gameScreen;
	private int userId;
	private int coins;

	public ShopScreen(MainFrame mainFrame, int userId) {
		this.mainFrame = mainFrame;
		setLayout(null);
        this.userId = userId; // userId 저장

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/shopscreen.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// coin 레이블
		coin = new JLabel("0");
		coin.setBounds(100, 32, 150, 40);
		coin.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(coin);

		// back 버튼
		RoundedButton backButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
		backButton.setBounds(420, 22, 64, 64);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("HomeScreen");
			}
			
		});
		add(backButton);

		// item 버튼
		RoundedButton itemButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 80);
		itemButton.setBounds(60, 190, 365, 200);
		itemButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ItemShopScreen");
			}
		});
		add(itemButton);

		// skin 버튼
		RoundedButton skinButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 80);
		skinButton.setBounds(60, 442, 365, 200);
		skinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("SkinShopScreen");
			}
		});
		add(skinButton);
		refreshUI();
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
	        System.err.println("Invalid userId home: " + userId);
	        return;
	    }
	    
	    String[] userDetails = Database.getUserDetails(userId);

	    if (userDetails != null && userDetails.length > 2) {	        
	        coin.setText(userDetails[2]); // Coins
	        coins = Integer.parseInt(userDetails[2]);
	    } else {
	        System.err.println("User details not found or incomplete for userId: " + userId);
	        coin.setText("0"); 
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

