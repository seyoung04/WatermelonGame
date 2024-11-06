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
import javax.swing.JPanel;

public class HomeScreen extends JPanel {
	private JLabel highScore; // 개인 최고 기록
	private JLabel coin; // 현재 코인 개수
	private BufferedImage backgroundImage; // 배경 이미지
	private MainFrame mainFrame; // MainFrame 인스턴스 추가

	// 버튼의 위치 및 크기
	private int startButtonX = 110; // 스타트 버튼의 X좌표
	private int startButtonY = 295; // 스타트 버튼의 Y좌표
	private int startButtonWidth = 177; // 스타트 버튼의 너비
	private int startButtonHeight = 67; // 스타트 버튼의 높이
	private int shopButtonX = 125; // 상점 버튼의 X좌표
	private int shopButtonY = 365; // 상점 버튼의 Y좌표
	private int shopButtonWidth = 149; // 상점 버튼의 너비
	private int shopButtonHeight = 50; // 상점 버튼의 높이
	private int arcSize = 40; // 모서리 둥글기

	public HomeScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame; // MainFrame 인스턴스를 필드에 저장
		setLayout(null);
		setBackground(new Color(222, 184, 135)); // 기본 배경 색상 설정 (이미지가 로드되지 않을 경우)

		// 배경 이미지 로드
		try {
			backgroundImage = ImageIO.read(new File("src/image/homescreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// high score 레이블
		highScore = new JLabel("5000");
		highScore.setBounds(120, 32, 150, 20);
		highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 14)); // 폰트 설정
		add(highScore);

		// coin 레이블
		coin = new JLabel("3000");
		coin.setBounds(270, 31, 150, 20);
		coin.setFont(new Font("Comic Sans MS", Font.BOLD, 16)); // 폰트 설정
		add(coin);

		// Start 버튼
		RoundedButton startButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
		startButton.setBounds(110, 295, 177, 67);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("GameScreen");
			}
		});
		add(startButton);

		// Shop 버튼
		RoundedButton shopButton = new RoundedButton("", new Color(0, 0, 0, 0), Color.WHITE, 10);
		shopButton.setBounds(125, 365, 149, 50);
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
