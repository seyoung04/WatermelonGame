//ItemShopScreen.java
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

public class ItemShopScreen extends JPanel {
	private BufferedImage backgroundImage;
	private JLabel coin; // 현재 코인 개수
	private JLabel BombPrice; // bomb 가격
	private JLabel BombNums; // bomb 보유 개수
	private JLabel PassPrice; // pass 가격
	private JLabel PassNums; // pass 보유 개수

	public ItemShopScreen(MainFrame mainFrame) {
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/itemshop.png"));
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

		// BombPrice 레이블
		BombPrice = new JLabel("1000");
		BombPrice.setBounds(265, 320, 150, 40);
		BombPrice.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); // 폰트 설정
		add(BombPrice);

		// Bomb 보유 개수 레이블
		BombNums = new JLabel("10");
		BombNums.setBounds(390, 320, 150, 40);
		BombNums.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); // 폰트 설정
		add(BombNums);

		// PassPrice 레이블
		PassPrice = new JLabel("1000");
		PassPrice.setBounds(265, 542, 150, 40);
		PassPrice.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); // 폰트 설정
		add(PassPrice);

		// Pass 보유 개수 레이블
		PassNums = new JLabel("10");
		PassNums.setBounds(390, 542, 150, 40);
		PassNums.setFont(new Font("Comic Sans MS", Font.BOLD, 19)); // 폰트 설정
		add(PassNums);

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

}
