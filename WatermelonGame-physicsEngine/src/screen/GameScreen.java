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

    public GameScreen(MainFrame mainFrame) {
        setLayout(null);
        setBackground(new Color(222, 184, 135));

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCreationTime > 500) {
                    lastCreationTime = currentTime;

                    // GameScreen 참조를 Falling 객체에 전달
                    fruits.add(new Falling(e.getX(), currentPreviewFruit.getType().getImage(),
                            currentPreviewFruit.getType(), GameScreen.this));

                    currentPreviewFruit = nextFruit;
                    nextFruit = new Fruit();
                    nextFruitLabel();
                    repaint();
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
            backgroundImage = ImageIO.read(new File("src/image/gamescreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentScore = new JLabel("0");
        currentScore.setBounds(210, 38, 100, 50);
        currentScore.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        add(currentScore);

     // 초기화
        highScore = new JLabel("" + HighScore.getHighScore()); // 초기 하이스코어 설정
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
        score += additionalScore; // 현재 점수 업데이트
        currentScore.setText("" + score);

        // 하이스코어 업데이트
        if (score > HighScore.getHighScore()) {
            HighScore.setHighScore(score); // 새로운 하이스코어 설정
            highScore.setText("" + score); // UI 업데이트
        }

        // 코인 업데이트
        int newCoins = additionalScore / 10;
        coinCount += newCoins;
        coin.setText("" + coinCount);
    }

    public void updateScoreAndCoins(String fruitType) {
        int pointIncrement = getPointIncrement(fruitType);
        score += pointIncrement;

        // 하이스코어 업데이트
        if (score > HighScore.getHighScore()) {
            HighScore.setHighScore(score);
            highScore.setText("" + score);
        }

        // 코인 업데이트
        int newCoins = pointIncrement / 10;
        coinCount += newCoins;

        // UI 업데이트
        coin.setText("" + coinCount);
        currentScore.setText("" + score);
        repaint();
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
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

