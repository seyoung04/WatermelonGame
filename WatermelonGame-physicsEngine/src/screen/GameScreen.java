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
import javax.swing.JPanel;
import javax.swing.Timer;

import watermelonGame.Falling;
import watermelonGame.Fruit;

public class GameScreen extends JPanel {
    private List<Falling> fruits = new ArrayList<>();
    private long lastCreationTime = 0;
    private JLabel currentScore; // 현재 점수
    private JLabel highScore;
    private JLabel nextFruitLabel; // 다음에 나올 과일
    private BufferedImage backgroundImage; // 배경 이미지
    private JLabel coin; // 현재 코인 개수
    private Point mousePosition = new Point(0, 200); // 마우스 위치 저장
    private Fruit currentPreviewFruit = new Fruit(); // 이번 클릭에서 생성될 과일
    private Fruit nextFruit = new Fruit(); // 다음 과일

    public GameScreen(MainFrame mainFrame) {
        setLayout(null);
        setBackground(new Color(222, 184, 135)); // 기본 배경색 설정

        // 마우스 클릭 시 새로운 과일 생성
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCreationTime > 500) {
                    lastCreationTime = currentTime;
                    fruits.add(new Falling(e.getX(), currentPreviewFruit.getType().getImage(),
                            currentPreviewFruit.getType()));

                    // 다음 과일을 현재 미리보기로 설정
                    currentPreviewFruit = nextFruit;
                    // 새로운 다음 과일 생성
                    nextFruit = new Fruit();
                    nextFruitLabel();
                    repaint();
                }
            }
        });

        // 마우스 이동 시 미리보기 위치 업데이트
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();
                repaint();
            }
        });

        // 주기적으로 업데이트 및 리페인트
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
        nextFruitLabel = new JLabel();
        nextFruitLabel.setBounds(440, 190, 30, 30);
        add(nextFruitLabel);
        nextFruitLabel();
    }

    // 다음 과일 레이블에 이미지 업데이트
    private void nextFruitLabel() {
        Image nextFruitImage = nextFruit.getType().getImage();
        if (nextFruitImage != null) {
            // nextFruitLabel에 이미지를 30x30 크기로 설정
            Image scaledImage = nextFruitImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            nextFruitLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            nextFruitLabel.setIcon(null);
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경 이미지 그리기
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.red);

            g.drawLine(50, 250, 400, 250);
        }

        // 미리보기 과일 그리기
        if (mousePosition != null && currentPreviewFruit != null) {
            currentPreviewFruit.drawPreview(g, mousePosition.x, 200);
        }

        // 과일들 그리기
        paintFruits(g);
    }

    protected void paintFruits(Graphics g) {
        for (Falling fruit : fruits) {
            fruit.draw(g);
        }
    }
}
