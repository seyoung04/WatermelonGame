package screen;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
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
    private int score = 0; // 현재 점수
    private int coinCount = 0; // 현재 코인 개수
    private Map<String, Integer> fruitCoinValues; // 과일에 대한 코인 값 저장
    private boolean isGameOver = false;
	private static final int GAME_OVER_HEIGHT = 250; // 게임 오버가 되는 높이
	private static final int GAME_OVER_DURATION = 3000; // 2초(2000밀리초)
	private long gameOverStartTime = 0; // 게임 오버 조건이 시작된 시간
	private MainFrame mainFrame;

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
	                            currentPreviewFruit.getType(), GameScreen.this));

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

        Timer timer = new Timer(20, e -> {
            update();
            repaint();
        });
        timer.start();

        try {
            backgroundImage = ImageIO.read(new File("WatermelonGame-physicsEngine/src/image/gamescreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentScore = new JLabel("0");
        currentScore.setBounds(210, 38, 100, 50);
        currentScore.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        add(currentScore);

        highScore = new JLabel("0");
        highScore.setBounds(220, 115, 100, 40);
        highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
        add(highScore);

        coin = new JLabel("0");
        coin.setBounds(380, 30, 150, 40);
        coin.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        add(coin);

        nextFruitLabel = new JLabel();
        nextFruitLabel.setBounds(440, 190, 30, 30);
        add(nextFruitLabel);
        nextFruitLabel();

        // 각 과일의 코인 값 정의
        fruitCoinValues = new HashMap<>();
        fruitCoinValues.put("grape", 10);
        fruitCoinValues.put("tangerine", 30);
        fruitCoinValues.put("apple", 50);
        fruitCoinValues.put("orange", 70);
        fruitCoinValues.put("peach", 100);
        fruitCoinValues.put("melon", 130);
        fruitCoinValues.put("watermelon", 170);
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

    public void updateScore(int additionalScore) {
        score += additionalScore;
        currentScore.setText("" + score);

        // 점수의 1/10을 코인으로 반영
        int newCoins = additionalScore / 10;
        coinCount += newCoins;
        coin.setText("" + coinCount);
    }

    public void updateScoreAndCoins(String fruitType) {
        int pointIncrement = getPointIncrement(fruitType); // 과일 점수 가져오기
        score += pointIncrement; // 점수 증가

        // 코인은 점수를 10으로 나눈 값만큼 증가
        int newCoins = pointIncrement / 10; // 점수의 1/10 계산
        coinCount += newCoins; // 코인 증가

        // UI 업데이트
        coin.setText("" + coinCount); // 코인 라벨 업데이트
        currentScore.setText("" + score); // 점수 라벨 업데이트
        repaint();  // 화면 갱신
    }



    public int getPointIncrement(String fruitType) {
        return fruitCoinValues.getOrDefault(fruitType, 0);
    }

    private void update() {
        for (Falling fruit : fruits) {
            fruit.update(fruits);

            if (fruit.isMerged() && !fruit.isMarkedForDeletion()) {
                String fruitType = fruit.getType().toString().toLowerCase(); 
                updateScoreAndCoins(fruitType);
            }
        }
    }
    private boolean isStationary(Falling fruit) {
		return !fruit.isMoving() || fruit.getVelocityY() == 0;
	}

	private boolean checkGameOver() {
		for (Falling fruit : fruits) {
			// 과일이 정적인 상태이고 게임오버 높이보다 위에 있을 때만 true 반환
			if (isStationary(fruit) && fruit.getY() <= GAME_OVER_HEIGHT) {
				return true;
			}
		}
		return false;
	}

	private void checkGameOverCondition() {
		boolean isAboveLine = checkGameOver();
		long currentTime = System.currentTimeMillis();

		if (isAboveLine) {
			if (gameOverStartTime == 0) {
				gameOverStartTime = currentTime;
			} else if (currentTime - gameOverStartTime >= GAME_OVER_DURATION) {
				showGameOverDialog();
				gameOverStartTime = 0;
			}
		} else {
			gameOverStartTime = 0;
		}
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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.red);
			g.drawLine(50, 250, 400, 250);
        }

        if (mousePosition != null && currentPreviewFruit != null) {
            currentPreviewFruit.drawPreview(g, mousePosition.x, 200);
        }

        paintFruits(g);
    }

    protected void paintFruits(Graphics g) {
        for (Falling fruit : fruits) {
            fruit.draw(g);
        }
    }
}
