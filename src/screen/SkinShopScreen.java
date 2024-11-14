//SkinShopScreen.java
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

public class SkinShopScreen extends JPanel {
	private BufferedImage backgroundImage;
	private JLabel coin; // 현재 코인 개수

	public SkinShopScreen(MainFrame mainFrame) {
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/skinshop.png"));
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
