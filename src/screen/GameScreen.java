//GameScreen.java
package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import physicalEngine.Falling;

public class GameScreen extends JPanel {
	private List<Falling> fruits = new ArrayList<>();

	private JLabel currentScore; // 현재 점수
	private JLabel highScore;
	private JLabel nextFruitLabel; // 다음에 나올 과일
	private BufferedImage backgroundImage; // 배경 이미지
	private JLabel coin; // 현재 코인 개수

	public GameScreen(MainFrame mainFrame) {
		setLayout(null);
		setBackground(new Color(222, 184, 135)); // 기본 배경색 설정
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fruits.add(new Falling(e.getX()));
			}
		});

		Timer timer = new Timer(20, e -> {
			update();
			repaint();
		});
		timer.start();

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/gamescreen.png")); // 게임 화면 배경 이미지
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// current score 레이블
		currentScore = new JLabel("5000");
		currentScore.setBounds(210, 38, 100, 50);
		currentScore.setFont(new Font("Comic Sans MS", Font.BOLD, 25)); // 폰트 설정
		add(currentScore);

		// high score 레이블
		highScore = new JLabel("3000");
		highScore.setBounds(220, 115, 100, 40);
		highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 17)); // 폰트 설정
		add(highScore);

		// coin 레이블
		coin = new JLabel("3000");
		coin.setBounds(380, 30, 150, 40);
		coin.setFont(new Font("Comic Sans MS", Font.BOLD, 22)); // 폰트 설정
		add(coin);

		// 다음 과일 표시 위치 조정
		nextFruitLabel = new JLabel("O");
		nextFruitLabel.setBounds(445, 190, 200, 20);
		nextFruitLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16)); // 폰트 설정
		add(nextFruitLabel);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// 배경 이미지 그리기
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
		paintFruits(g);
	}

	public void updateScore(int score) {
		currentScore.setText("" + score);
	}

	public void updateHighScore(int score) {
		highScore.setText("" + score);
	}

	private void update() {
		for (Falling fruit : fruits) {
			fruit.update(fruits);
		}
	}

	protected void paintFruits(Graphics f) {
		for (Falling fruit : fruits) {
			fruit.draw(f);
		}
	}
}