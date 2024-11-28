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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import watermelonGame.Falling;

public class HomeScreen extends JPanel {
	private JLabel highScore; // 개인 최고 기록
	private JLabel coin; // 현재 코인 개수
	private BufferedImage backgroundImage; // 배경 이미지

	List<Falling> fruits;

	// 버튼의 위치 및 크기

	public HomeScreen(MainFrame mainFrame) {
		setLayout(null);
		setBackground(new Color(222, 184, 135)); // 기본 배경 색상 설정 (이미지가 로드되지 않을 경우)

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/homescreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// high score 레이블
		highScore = new JLabel("5000");
		highScore.setBounds(110, 32, 150, 40);
		highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(highScore);

		// coin 레이블
		coin = new JLabel("3000");
		coin.setBounds(340, 32, 150, 40);
		coin.setFont(new Font("Comic Sans MS", Font.BOLD, 27)); // 폰트 설정
		add(coin);

		// Start 버튼
		RoundedButton startButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
		startButton.setBounds(140, 373, 215, 75);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("GameScreen");
			}
		});
		add(startButton);

		// Shop 버튼
		RoundedButton shopButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
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

	public void updateHighScore(int score) {
		highScore.setText("" + score);
	}

	public void updateCoins(int coins) {
		coin.setText("" + coins);
	}
}