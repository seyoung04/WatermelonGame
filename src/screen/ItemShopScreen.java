//ItemShopScreen.java
package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ItemShopScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private JLabel coinLabel; // 코인
	private JLabel bombPrice; // 폭탄 가격
	private JLabel bombNums; // 폭탄 보유 개수
	private JLabel passPrice; // 패스 가격
	private JLabel passNums; // 패스 보유 개수

	public ItemShopScreen(MainFrame mainFrame) {
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/itemShop.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// Item
		JLabel iLabel = new JLabel("Item");
		iLabel.setBounds(200, 30, 150, 40);
		iLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40)); // 폰트 설정
		iLabel.setForeground(Color.WHITE);
		add(iLabel);

		// 코인 레이블
		coinLabel = new JLabel("" + GameData.getCoins());
		coinLabel.setBounds(65, 33, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 21)); // 폰트 설정
		add(coinLabel);

		// back 버튼
		RoundedButton backButton = new RoundedButton(new Color(255, 222, 178), 10, "", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 18));
		backButton.setBounds(390, 22, 64, 64);
		ImageIcon icon = new ImageIcon("src/image/item/back.png");
		Image scaledImage = icon.getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH); // 버튼 크기에 맞게
		backButton.setIcon(new ImageIcon(scaledImage));
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ShopScreen");
			}
		});
		add(backButton);

		// Bomb 레이블
		JLabel b1Label = new JLabel("Bomb");
		b1Label.setBounds(270, 205, 150, 40);
		b1Label.setFont(new Font("Comic Sans MS", Font.BOLD, 40)); // 폰트 설정
		b1Label.setForeground(new Color(255, 109, 109));
		add(b1Label);

		// Bomb 설명 레이블
		JLabel b2Label = new JLabel("<html>원하는 과일 하나를<br>&nbsp;삭제하는 아이템<html>", SwingConstants.CENTER);
		b2Label.setBounds(220, 187, 200, 200);
		b2Label.setFont(new Font("Bazzi", Font.PLAIN, 26)); // 폰트 설정
		b2Label.setHorizontalAlignment(SwingConstants.CENTER);
		add(b2Label);

		// Bomb 가격 레이블
		bombPrice = new JLabel("1000");
		bombPrice.setBounds(265, 322, 150, 40);
		bombPrice.setFont(new Font("Comic Sans MS", Font.PLAIN, 21)); // 폰트 설정
		add(bombPrice);

		// Bomb 보유 개수 레이블
		bombNums = new JLabel("보유개수: " + GameData.getBombs());
		bombNums.setBounds(325, 322, 150, 40);
		bombNums.setFont(new Font("Bazzi", Font.PLAIN, 21)); // 폰트 설정
		add(bombNums);

		// Bomb 구매 버튼
		RoundedButton buyBombButton = new RoundedButton(new Color(255, 172, 62), 10, "구매하기", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 20));
		buyBombButton.setBounds(80, 325, 120, 38);
		buyBombButton.addActionListener(e -> {
			if (GameData.getCoins() >= 50) {
				GameData.subtractCoins(50);
				GameData.addBombs(1);
				refreshData();
			} else {
				showCustomMessage("코인이 부족합니다!");
			}
		});
		add(buyBombButton);

		// Pass 레이블
		JLabel p1Label = new JLabel("Pass");
		p1Label.setBounds(275, 430, 150, 40);
		p1Label.setFont(new Font("Comic Sans MS", Font.BOLD, 40)); // 폰트 설정
		p1Label.setForeground(new Color(98, 147, 255));
		add(p1Label);

		// Pass 설명 레이블
		JLabel p2Label = new JLabel("<html>원하지 않는 과일을<br>&nbsp;패스하는 아이템<html>", SwingConstants.CENTER);
		p2Label.setBounds(220, 410, 200, 200);
		p2Label.setFont(new Font("Bazzi", Font.PLAIN, 26)); // 폰트 설정
		p2Label.setHorizontalAlignment(SwingConstants.CENTER);
		add(p2Label);

		// Pass 가격 레이블
		passPrice = new JLabel("1000");
		passPrice.setBounds(265, 543, 150, 40);
		passPrice.setFont(new Font("Comic Sans MS", Font.PLAIN, 21)); // 폰트 설정
		add(passPrice);

		// Pass 보유 개수 레이블
		passNums = new JLabel("보유개수: " + GameData.getPasses());
		passNums.setBounds(325, 543, 150, 40);
		passNums.setFont(new Font("Bazzi", Font.PLAIN, 21)); // 폰트 설정
		add(passNums);

		// Pass 구매 버튼
		RoundedButton buyPassButton = new RoundedButton(new Color(255, 172, 62), 10, "구매하기", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 20));
		buyPassButton.setBounds(80, 548, 120, 38);
		buyPassButton.addActionListener(e -> {
			if (GameData.getCoins() >= 50) {
				GameData.subtractCoins(50);
				GameData.addPasses(1);
				refreshData();
			} else {
				showCustomMessage("코인이 부족합니다!");
			}
		});
		add(buyPassButton);
	}

	// 커스텀 메시지 창
	private void showCustomMessage(String message) {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "알림", true);
		dialog.setLayout(null);
		dialog.setSize(250, 140);
		dialog.setLocationRelativeTo(this); // 화면 중앙에 표시

		// 메시지 라벨 배경 패널
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(new Color(255, 230, 204));
		messagePanel.setBounds(0, 0, 250, 150);
		messagePanel.setLayout(null);

		// 메시지 라벨
		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(0, 0, 240, 70);
		dialog.add(messageLabel);

		// 확인 버튼
		RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(70, 65, 100, 30);
		confirmButton.addActionListener(e -> dialog.dispose());
		dialog.add(confirmButton);

		dialog.add(messagePanel);
		dialog.setVisible(true);
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
		bombNums.setText("보유개수: " + GameData.getBombs());
		passNums.setText("보유개수: " + GameData.getPasses());
	}
}