package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import watermelonGame.Falling;
import watermelonGame.Fruit;

public class GameScreen extends JPanel {
	private List<Falling> fruits = new ArrayList<>();
	private long lastCreationTime = 0;
	private JLabel currentScore;
	private JLabel highScore;
	private JLabel nextFruitLabel;
	private BufferedImage backgroundImage;
	private JLabel coin;
	private Point mousePosition = new Point(0, 200);
	private Fruit currentPreviewFruit = new Fruit();
	private Fruit nextFruit = new Fruit();
	private MainFrame mainFrame;
	private boolean isGameOver = false;
	private static final int GAME_OVER_HEIGHT = 250; // 게임 오버가 되는 높이
	private static final int GAME_OVER_DURATION = 3000; // 2초(2000밀리초)
	private long gameOverStartTime = 0; // 게임 오버 조건이 시작된 시간

	public GameScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);
		setBackground(new Color(222, 184, 135));

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!isGameOver) {
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastCreationTime > 1000) {
						lastCreationTime = currentTime;
						fruits.add(new Falling(e.getX(), currentPreviewFruit.getType().getImage(),
								currentPreviewFruit.getType()));

						currentPreviewFruit = nextFruit;
						nextFruit = new Fruit();
						nextFruitLabel();
						repaint();
					}
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mousePosition = e.getPoint();
				repaint();
			}
		});

		// 주기적으로 업데이트 및 리페인트
		Timer gameTimer = new Timer(20, e -> {
			if (!isGameOver) {
				update();
				checkGameOverCondition();
				repaint();
			}
		});
		gameTimer.start();

		// 나머지 초기화 코드...
		initializeComponents();
	}

	private void initializeComponents() {
		try {
			backgroundImage = ImageIO.read(new File("src/image/gamescreen.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		currentScore = new JLabel("5000");
		currentScore.setBounds(210, 38, 100, 50);
		currentScore.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
		add(currentScore);

		highScore = new JLabel("3000");
		highScore.setBounds(220, 115, 100, 40);
		highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
		add(highScore);

		coin = new JLabel("3000");
		coin.setBounds(380, 30, 150, 40);
		coin.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
		add(coin);

		nextFruitLabel = new JLabel();
		nextFruitLabel.setBounds(440, 190, 30, 30);
		add(nextFruitLabel);
		nextFruitLabel();
	}

	private void checkGameOverCondition() {
		boolean isAboveLine = checkGameOver();
		long currentTime = System.currentTimeMillis();

		if (isAboveLine) {
			// 게임 오버 조건이 처음 감지되었을 때
			if (gameOverStartTime == 0) {
				gameOverStartTime = currentTime;
			}
			// 2초 이상 지속되었는지 확인
			else if (currentTime - gameOverStartTime >= GAME_OVER_DURATION) {
				showGameOverDialog();
				gameOverStartTime = 0;
			}
		} else {
			// 게임 오버 조건이 해제되면 타이머 초기화
			gameOverStartTime = 0;
		}
	}

	private boolean checkGameOver() {
		for (Falling fruit : fruits) {
			if (fruit.getY() <= GAME_OVER_HEIGHT) {
				return true;
			}
		}
		return false;
	}

	private void showGameOverDialog() {
		isGameOver = true;
		int option = JOptionPane.showConfirmDialog(this, "게임 오버!\n홈으로 돌아가시겠습니까?", "게임 오버", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE);

		if (option == JOptionPane.YES_OPTION) {
			returnToHome();
		}
		resetGame();
	}

	private void returnToHome() {
		resetGame();
		mainFrame.showScreen("HomeScreen");
	}

	private void resetGame() {
		fruits.clear();
		isGameOver = false;
		gameOverStartTime = 0; // 게임 오버 타이머 초기화
		currentPreviewFruit = new Fruit();
		nextFruit = new Fruit();
		nextFruitLabel();
		currentScore.setText("5000");
		repaint();
	}

	// 나머지 메서드들은 동일하게 유지...
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

	private void nextFruitLabel() {
		Image nextFruitImage = nextFruit.getType().getImage();
		if (nextFruitImage != null) {
			Image scaledImage = nextFruitImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
			nextFruitLabel.setIcon(new ImageIcon(scaledImage));
		} else {
			nextFruitLabel.setIcon(null);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			g.setColor(Color.red);
			g.drawLine(50, 250, 400, 250);
		}

		if (!isGameOver && mousePosition != null && currentPreviewFruit != null) {
			currentPreviewFruit.drawPreview(g, mousePosition.x, 150);
		}

		paintFruits(g);
	}

	protected void paintFruits(Graphics g) {
		for (Falling fruit : fruits) {
			fruit.draw(g);
		}
	}
}