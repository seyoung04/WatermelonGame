package screen;

import java.awt.*;
import watermelonGame.HighScore;
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
import watermelonGame.Coin;
import watermelonGame.Falling;
import watermelonGame.Fruit;

public class GameScreen extends JPanel implements RefreshableScreen{
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
    private int score = 0; 
    private int coinCount = 0; 
    private Map<String, Integer> fruitCoinValues; // 과일에 대한 코인 값 저장
    private int userId; 
    private boolean isGameOver = false;
    private static final int GAME_OVER_HEIGHT = 250; 
    private static final int GAME_OVER_DURATION = 3000;
    private long gameOverStartTime = 0; 
    private MainFrame mainFrame;


    public GameScreen(MainFrame mainFrame, int userId) {
        this.userId = userId;
        setLayout(null);
        setBackground(new Color(222, 184, 135));
        this.mainFrame = mainFrame;
        this.coinCount = Database.getUsercoin(userId); 

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

        highScore = new JLabel("" + HighScore.loadHighScore(userId));
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
	    refreshUI();

    }
    //다음으로 생성될 과일
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
        int pointIncrement = getPointIncrement(fruitType);
        score += pointIncrement;

        // 하이스코어 업데이트
        if (score > HighScore.loadHighScore(userId)) {
            HighScore.setHighScore(userId, score);
            highScore.setText("" + score);
        }

        // 코인 업데이트
        int newCoins = pointIncrement / 10;
        coinCount += newCoins;

        // 데이터베이스에 점수 및 코인 저장
        Database.updateUserScoreAndCoins(userId, score, coinCount);

        // UI 업데이트
        coin.setText("" + coinCount);
        currentScore.setText("" + score);
        repaint();
    }


    @Override
    public void refreshUI() {
        if (userId <= 0) {
            System.err.println("Invalid userId home: " + userId);
            return;
        }
        String[] userDetails = Database.getUserDetails(userId);

        if (userDetails != null) {
            // 하이스코어만 데이터베이스 값으로 설정
            int highScoreValue = Integer.parseInt(userDetails[1]);
            highScore.setText(String.valueOf(highScoreValue));
            
            // 코인 업데이트
            coin.setText(userDetails[2]);
            coinCount = Integer.parseInt(userDetails[2]);

            currentScore.setText(String.valueOf(score));
        } else {
            System.err.println("User details not found for userId: " + userId);
        }
    }

    //코인 추가 정도
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
        checkGameOverCondition();

    }
    
	public int getScore() {
		return score;
	}

	public void setUserId(int userId) {
        this.userId = userId;
        refreshUI();
    }

	public void updateHighScore(int score) {
	    // UI 업데이트를 위해 하이스코어 갱신
	    if (score > HighScore.getHighScore()) {
	        HighScore.setHighScore(userId, score); 
	        highScore.setText(String.valueOf(score)); 
	    }
	}

	// 게임 오버 상태를 체크하는 메서드
	 private void checkGameOverCondition() {
	     boolean isAboveLine = checkGameOver(); 
	     long currentTime = System.currentTimeMillis();

	     if (isAboveLine) {
	         if (gameOverStartTime == 0) {
	             gameOverStartTime = currentTime;
	         } else if (currentTime - gameOverStartTime >= GAME_OVER_DURATION) {
	             // 게임 오버 상태가 일정 시간 이상 지속되면 대화상자 띄우기
	             showGameOverDialog();
	             gameOverStartTime = 0;
	         }
	     } else {
	         gameOverStartTime = 0; // 게임 오버 조건이 아니면 타이머 리셋
	     }
	 }

	 // 게임 오버 대화상자를 표시하고 '홈으로 돌아가기'를 선택한 경우
	 private void showGameOverDialog() {
	     isGameOver = true;  
	     int option = JOptionPane.showConfirmDialog(this, "게임 오버!\n홈으로 돌아가시겠습니까?", "게임 오버", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

	     if (option == JOptionPane.YES_OPTION) {
	         returnToHome(); 
	     } else {
	         resetGame(); 
	     }
	 }

	 // 게임 오버 상태에서 홈으로 돌아가는 메서드
	 private void returnToHome() {
	     resetGame();
	     mainFrame.showScreen("HomeScreen"); 
	 }

	 // 게임 리셋 메서드
	 private void resetGame() {
	     fruits.clear();
	     isGameOver = false; // 게임 오버 상태 초기화
	     gameOverStartTime = 0; // 게임 오버 타이머 초기화
	     currentPreviewFruit = new Fruit(); // 프리뷰 과일 초기화
	     nextFruit = new Fruit(); // 다음 과일 초기화
	     nextFruitLabel(); // 다음 과일 라벨 업데이트
	     currentScore.setText("0"); // 점수 초기화
	     repaint(); // UI 업데이트
	 }

	 // 게임 오버 조건을 체크하는 메서드
	 private boolean checkGameOver() {
	     for (Falling fruit : fruits) {
	         // 과일이 정적인 상태이고 게임오버 높이보다 위에 있을 때 게임 오버
	         if (isStationary(fruit) && fruit.getY() <= GAME_OVER_HEIGHT) {
	             return true;
	         }
	     }
	     return false;
	 }

	 // 과일이 정지 상태인지 확인하는 메서드
	 private boolean isStationary(Falling fruit) {
	     return !fruit.isMoving() || fruit.getVelocityY() == 0;
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