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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ShopScreen extends JPanel {
	private JLabel coin; // 현재 코인 개수
	private BufferedImage backgroundImage;
	private MainFrame mainFrame;

	public ShopScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/shopscreen.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// coin 레이블
		coin = new JLabel("3000");
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
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	private void showInsufficientCoinsMessage() {
		JOptionPane.showMessageDialog(this, "Not enough coins!", "Warning", JOptionPane.WARNING_MESSAGE);

	}

	public void updateCoins(int coins) {
		coin.setText("" + coins);
	}
}