//HomeScreen.java
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

public class HomeScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private JLabel coinLabel; // 코인
	private JLabel highScoreLabel;// 최고기록 점수

	public HomeScreen(MainFrame mainFrame) {
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/homeScreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// 코인 레이블
		coinLabel = new JLabel("" + GameData.getCoins());
		coinLabel.setBounds(340, 33, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(coinLabel);

		// 최고기록 레이블
		highScoreLabel = new JLabel("" + GameData.getHighScore());
		highScoreLabel.setBounds(110, 33, 150, 40);
		highScoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(highScoreLabel);

		// Start 버튼
		RoundedButton startButton = new RoundedButton(new Color(255, 200, 126), 10, "Start", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 47));
		startButton.setBounds(140, 373, 215, 75);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("GameScreen");
			}
		});
		add(startButton);

		// Shop 버튼
		RoundedButton shopButton = new RoundedButton(new Color(255, 193, 96), 10, "Shop", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 33));
		shopButton.setBounds(158, 465, 180, 55);
		shopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ShopScreen");
			}
		});
		add(shopButton);
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
		highScoreLabel.setText("" + GameData.getHighScore());
	}
}