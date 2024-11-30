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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import physicalEngine.Fruit;

public class SkinShopScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private JLabel coinLabel; // 코인
	private JButton fruitButton;
	private JButton aButton;
	private JButton bButton;
	private static String appliedSkin = "fruit"; // 현재 적용돼있는 스킨 (기본값은 fruit)

	public SkinShopScreen(MainFrame mainFrame) {
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/skinShop.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// Skin
		JLabel sLabel = new JLabel("Skin");
		sLabel.setBounds(200, 30, 150, 40);
		sLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40)); // 폰트 설정
		sLabel.setForeground(new Color(83, 67, 47));
		add(sLabel);

		// 코인 레이블
		coinLabel = new JLabel("" + GameData.getCoins());
		coinLabel.setBounds(65, 33, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(coinLabel);

		// back 버튼
		RoundedButton backButton = new RoundedButton(new Color(0, 0, 0, 0), 10, "", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 18));
		backButton.setBounds(420, 22, 64, 64);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ShopScreen");
			}
		});
		add(backButton);

		// fruit 버튼
		fruitButton = createButton("Fruit Skin - 적용하기", 300, 100);
		fruitButton.addActionListener(e -> applySkin("fruit"));
		add(fruitButton);

		// a 버튼
		aButton = createButton("A Skin - 적용하기", 300, 200);
		aButton.addActionListener(e -> applySkin("A"));
		add(aButton);

		// b 버튼
		bButton = createButton("B Skin - 적용하기", 300, 300);
		bButton.addActionListener(e -> applySkin("B"));
		add(bButton);

		updateButtonStates(); // 초기 버튼 상태 설정
	}

	// 버튼 설정
	private JButton createButton(String text, int x, int y) {
		JButton button = new JButton(text);
		button.setBounds(x, y, 150, 30); // 버튼 크기 및 위치 설정
		return button;
	}

	// 스킨 적용하기
	private void applySkin(String skin) {
		appliedSkin = skin;
		updateButtonStates();
		Fruit.refreshImages(); // 모든 과일의 이미지를 새로고침
		JLabel messageLabel = new JLabel("스킨 " + appliedSkin + " 이/가 적용되었습니다!");
		JOptionPane.showMessageDialog(this, messageLabel);
	}

	// 버튼 문구 바꾸기
	private void updateButtonStates() {
		fruitButton.setText("Fruit Skin" + (appliedSkin.equals("Fruit") ? " - 적용중" : " - 적용하기"));
		aButton.setText("A Skin" + (appliedSkin.equals("A") ? " - 적용중" : " - 적용하기"));
		bButton.setText("B Skin" + (appliedSkin.equals("B") ? " - 적용중" : " - 적용하기"));
	}

	public static String getAppliedSkin() {
		return appliedSkin;
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