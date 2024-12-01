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

public class ShopScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private MainFrame mainFrame;
	private JLabel coinLabel; // 코인

	public ShopScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/shopScreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// 코인 레이블
		coinLabel = new JLabel("" + GameData.getCoins());
		coinLabel.setBounds(90, 33, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(coinLabel);

		// back 버튼
		RoundedButton backButton = new RoundedButton(new Color(0, 0, 0, 0), 10, "", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 18));
		backButton.setBounds(420, 22, 64, 64);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("HomeScreen");
			}
		});
		add(backButton);

		// itemShop 버튼
		RoundedButton itemButton = new RoundedButton(new Color(152, 118, 73), 50, "Item", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 65));
		itemButton.setBounds(60, 190, 365, 200);
		itemButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ItemShopScreen");
			}
		});
		add(itemButton);

		// skinShop 버튼
		RoundedButton skinButton = new RoundedButton(new Color(197, 174, 143), 50, "Skin", new Color(83, 67, 47),
				new Font("Comic Sans MS", Font.BOLD, 65));
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
		// 배경 이미지 그리기
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	@Override
	public void refreshData() {
		coinLabel.setText("" + GameData.getCoins());
	}
}