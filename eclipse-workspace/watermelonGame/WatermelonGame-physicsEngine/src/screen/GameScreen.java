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

import database.Database;
import database.SessionManager;
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
    private int userId;

    private int highScoreValue = 0;
    private int totalCoins = 0;

    public GameScreen(MainFrame mainFrame) {
        initializeGameScreen();

        this.mainFrame = mainFrame;
        updateUserId();
        
        setLayout(null);
        setBackground(new Color(222, 184, 135));
        currentScore = new JLabel("0");

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

    private void updateUserId() {
        this.userId = SessionManager.getInstance().getUserId();
    }

    public void refreshUserSession() {
        updateUserId();
        initializeGameScreen();
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

    public void updateScoreAndCoins(String fruitType) {
        // userId 가져오기
        int currentUserId = SessionManager.getInstance().getUserId();

        int pointIncrement = getPointIncrement(fruitType);
        score += pointIncrement;
        int newCoins = pointIncrement / 10;
        coinCount += newCoins;

        // UI 업데이트
        currentScore.setText("" + score);
        coin.setText("" + coinCount);

        Database.updateUserScoreAndCoins(currentUserId, score, coinCount);

        if (score > highScoreValue) {
            highScoreValue = score;
            highScore.setText(String.valueOf(highScoreValue));
        }
    }

    public void initializeGameScreen() {
        this.userId = SessionManager.getInstance().getUserId();
        System.out.println("initializeGameScreen에서 userId: " + userId);

        if (userId != -1) {
            int[] userData = Database.getUserScoreAndCoins(userId);

            // 데이터베이스에서 가져온 값을 로그로 출력
            System.out.println("Database에서 가져온 userData: high_score = " + userData[0] + ", game_money = " + userData[1]);

            // 데이터베이스에서 가져온 최고 점수와 코인을 화면에 표시
            highScoreValue = userData[0];
            coinCount = userData[1];

            // UI 업데이트
            highScore.setText(String.valueOf(highScoreValue));
            coin.setText("" + coinCount);

            // 새 게임 시작 시 점수는 0으로 초기화
            score = 0;
            currentScore.setText("" + score);
        } else {
            System.err.println("유효하지 않은 userId: " + userId);
        }
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
    public void updateData(int userId) {
        // Database 객체 생성 불필요
        int[] userData = Database.getUserScoreAndCoins(userId);

        // 데이터 설정
        int highScoreValue = userData[0];
        int coinCount = userData[1];

        // UI 업데이트
        highScore.setText(String.valueOf(highScoreValue));
        coin.setText(String.valueOf(coinCount));
        currentScore.setText("0"); // 게임 시작 시 현재 점수 초기화
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

