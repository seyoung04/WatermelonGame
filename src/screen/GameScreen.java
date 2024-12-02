//itemshop에서 구매한 item이 개수가 반영이 안됌
//skinshop에서 구매한 스킨이 합성순서 이미지에 반영이 안됌
//중간에 합쳐질 때 게임이 멈추고 과일이 안 떨어짐
//코인하고 점수가 업데이트 안됌 (업데이트 되면 최고기록 갱신 되는지도 확인)
//합성순서 위치 수정하기
//게임오버, 빨간색 선 통에 맞춰서 수정하기
//GameScreen.java
package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import database.Database;
import database.SessionManager;
import physicalEngine.Falling;
import physicalEngine.Fruit;
import physicalEngine.HighScore;

public class GameScreen extends BaseScreen implements RefreshableScreen{
	private BufferedImage backgroundImage; // 배경 이미지
	private List<Falling> fruits = new ArrayList<>();
	private int userId;
	private int coin = 0; // 코인 개수
	private JLabel coinLabel; // 코인 레이블
	private int score = 0; // 현재 점수
	private JLabel scoreLabel; // 현재 점수 레이블
	private int highScore; // 최고기록 점수
	private JLabel highScoreLabel; // 최고기록 점수 레이블
	private RoundedButton bombButton; // 폭탄 버튼
	private RoundedButton passButton; // 패스 버튼
	private boolean isBombActive = false; // 폭탄 사용 상태
	private JLabel nextFruitLabel; // next 과일
	private Fruit currentPreviewFruit = new Fruit();
	private Fruit nextFruit = new Fruit();
    private int coinCount = 0; 


	private Point mousePosition = new Point(0, 200);
	private MainFrame mainFrame;
	private boolean isGameOver = false;
	private long lastCreationTime = 0;
	private static final int GAME_OVER_HEIGHT = 200; // 게임 오버가 되는 높이
	private static final int GAME_OVER_DURATION = 2000; // 2초
	private long gameOverStartTime = 0; // 게임 오버 조건이 시작된 시간

	private List<Image> evolutionImages = new ArrayList<>(); // 과일 합성 단계 이미지 리스트
	private List<Point> evolutionPositions = new ArrayList<>(); // 과일 위치 리스트
	private Map<String, Integer> fruitCoinValues; // 과일에 대한 코인 값 저장

	public GameScreen(MainFrame mainFrame, int userId) {
        this.userId = userId;
		this.mainFrame = mainFrame;
		updateUserId();
        this.coinCount = Database.getUsercoin(userId); 
		GameData.initialize(userId);


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

		// 주기적으로 업데이트 및 리페인트
		Timer gameTimer = new Timer(20, e -> {
			if (!isGameOver) {
				update();
				checkGameOverCondition();
				repaint();
			}
		});
		gameTimer.start();

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/Group 1.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// 합성순서 이미지
		initializeEvolutionImages();

		// 현재 점수 레이블
		scoreLabel = new JLabel("0");
		scoreLabel.setBounds(210, 38, 100, 50);
		scoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
		add(scoreLabel);

		// 최고기록 레이블
		highScoreLabel = new JLabel("" + HighScore.loadHighScore(userId));
		highScoreLabel.setBounds(220, 100, 100, 40);
		highScoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
		add(highScoreLabel);

		// back 버튼
		RoundedButton backButton = new RoundedButton(new Color(255, 222, 178), 10, "", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 18));
		backButton.setBounds(412, 16, 60, 60);
		ImageIcon icon = new ImageIcon("src/image/item/back.png");
		Image scaledImage = icon.getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH); // 버튼 크기에 맞게
		backButton.setIcon(new ImageIcon(scaledImage));
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("HomeScreen");
			}
		});
		add(backButton);

		// 코인 레이블
		coinLabel = new JLabel("" + GameData.getCoins());
		coinLabel.setBounds(380, 90, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
		add(coinLabel);

		// Next 레이블
		JLabel nextLabel = new JLabel("Next");
		nextLabel.setBounds(414, 260, 100, 50);
		nextLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
		add(nextLabel);

		// next 과일 레이블
		nextFruitLabel = new JLabel();
		nextFruitLabel.setBounds(413, 285, 70, 70);
		add(nextFruitLabel);
		nextFruitLabel();

		// 폭탄 버튼
		bombButton = new RoundedButton(new Color(255, 222, 178), 10, "" + GameData.getBombs(), new Color(255, 109, 109),
				new Font("Comic Sans MS", Font.BOLD, 17));
		bombButton.setBounds(395, 380, 75, 85);
		ImageIcon iconBomb = new ImageIcon("src/image/item/bomb.png");
		Image bombScaledImage = iconBomb.getImage().getScaledInstance(62, 62, Image.SCALE_SMOOTH); // 버튼 크기에 맞게
		bombButton.setIcon(new ImageIcon(bombScaledImage));
		bombButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 가운데 정렬
		bombButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 아래로
		bombButton.setIconTextGap(-4); // 이미지와 텍스트 간격
		bombButton.setMargin(new Insets(5, 0, 0, 0)); // 버튼 패딩 조정
		bombButton.addActionListener(e -> useBomb());
		add(bombButton);

		// 패스 버튼
		passButton = new RoundedButton(new Color(255, 222, 178), 10, "" + GameData.getPasses(), new Color(98, 147, 255),
				new Font("Comic Sans MS", Font.BOLD, 17));
		passButton.setBounds(395, 495, 75, 77);
		ImageIcon iconPass = new ImageIcon("src/image/item/pass.png");
		Image passScaledImage = iconPass.getImage().getScaledInstance(60, 52, Image.SCALE_SMOOTH); // 버튼 크기에 맞게
		passButton.setIcon(new ImageIcon(passScaledImage));
		passButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 가운데 정렬
		passButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 아래로
		passButton.setIconTextGap(-4); // 이미지와 텍스트 간격
		passButton.setMargin(new Insets(5, 0, 0, 0)); // 버튼 패딩 조정
		passButton.addActionListener(e -> usePass());
		add(passButton);

		// 각 과일의 점수 값 정의
		fruitCoinValues = new HashMap<>();
		fruitCoinValues.put("grape", 50);
		fruitCoinValues.put("tangerine", 70);
		fruitCoinValues.put("apple", 110);
		fruitCoinValues.put("orange", 170);
		fruitCoinValues.put("peach", 250);
		fruitCoinValues.put("melon", 350);
		fruitCoinValues.put("watermelon", 470);
		refreshUI();
	}
	public void initializeGameScreen() {
	    this.userId = SessionManager.getInstance().getUserId();
	    if (userId != -1) {
	        int[] userData = Database.getUserScoreAndCoins(userId);
	        
	        highScore = userData[0];
	        int currentCoins = userData[2];  // 현재 코인 값
	        
	        highScoreLabel.setText("" + highScore);
	        coinLabel.setText("" + currentCoins);  // GameData 대신 직접 데이터베이스 값 사용
	        
	        score = 0;
	        scoreLabel.setText("0");
	    }
	}
	// 게임 종료 선을 넘는지에 대한 여부
	private boolean checkGameOver() {
		for (Falling fruit : fruits) {
			if (fruit.getY() + fruit.getDiameter() / 2 <= GAME_OVER_HEIGHT) {
				return true;
			}
		}
		return false;
	}

	// 선 넘기고 2초 이상 지속 시 종료
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

	// 폭탄 사용(원하는 과일 하나 삭제)
	private void useBomb() {
		if (GameData.getBombs() < 1) {
			showCustomMessage("아이템이 없습니다.");
			return;
		}

		if (fruits.isEmpty()) {
			showCustomMessage("삭제할 과일이 없습니다.");
			return;
		}

		isBombActive = true; // 폭탄 사용 활성화
		bombButton.setEnabled(false); // 버튼 비활성화

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 클릭 위치를 기준으로 과일 찾기
				for (int i = fruits.size() - 1; i >= 0; i--) {
					Falling fruit = fruits.get(i);
					if (isClickedOnFruit(fruit, e.getX(), e.getY())) {
						fruits.remove(fruit); // 과일 삭제
						GameData.subtractBombs(1); // 폭탄 개수 감소
						isBombActive = false; // 폭탄 사용 종료
						refreshData();
						bombButton.setText("" + GameData.getBombs()); // 버튼 텍스트 초기화
						bombButton.setEnabled(true); // 버튼 활성화

						removeMouseListener(this); // 이벤트 리스너 제거
						repaint();
						return;
					}
				}
			}
		});
	}

	// 특정 과일을 클릭했는지 확인
	private boolean isClickedOnFruit(Falling fruit, int clickX, int clickY) {
		int fruitX = fruit.getX();
		int fruitY = fruit.getY();
		int fruitDiameter = fruit.getDiameter();

		// 클릭 위치가 과일의 경계 내에 있는지 확인
		return clickX >= fruitX && clickX <= fruitX + fruitDiameter && clickY >= fruitY
				&& clickY <= fruitY + fruitDiameter;
	}

	// 패스 사용(미리보기 과일 넘기기)
	private void usePass() {
		if (GameData.getPasses() < 1) {
			showCustomMessage("아이템이 없습니다.");
		} else {
			currentPreviewFruit = nextFruit; // 현재 과일을 다음 과일로 교체
			nextFruit = new Fruit(); // 새로운 과일 생성
			nextFruitLabel(); // 미리보기 갱신
			GameData.subtractPasses(1); // 패스 개수 감소
			refreshData();
			passButton.setText("" + GameData.getPasses()); // 버튼 텍스트 초기화
			repaint();
		}
	}

	/*
	 * public void refreshUserSession() { updateUserId(); initializeGameScreen(); }
	 */

	private void updateUserId() {
		this.userId = SessionManager.getInstance().getUserId();
	}


	//과일 업데이트
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
	// 현재 점수와 코인 업데이트
	public void updateScoreAndCoins(String fruitType) {
        int pointIncrement = getPointIncrement(fruitType);
        score += pointIncrement;

        // 하이스코어 업데이트
        if (score > HighScore.loadHighScore(userId)) {
            HighScore.setHighScore(userId, score);
            highScoreLabel.setText("" + score);
        }

        // 코인 업데이트
        int newCoins = pointIncrement / 10;
        coinCount += newCoins;

        // 데이터베이스에 점수 및 코인 저장
        Database.updateUserScoreAndCoins(userId, score, coinCount);

        // UI 업데이트
        coinLabel.setText("" + coinCount);
        scoreLabel.setText("" + score);
        repaint();
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
	        highScoreLabel.setText(String.valueOf(score)); 
	    }
	}
    
	public int getPointIncrement(String fruitType) {
		return fruitCoinValues.getOrDefault(fruitType, 0);
	}

	/*
	 * public void updateData(int userId) { // Database 객체 생성 불필요 int[] userData =
	 * Database.getUserScoreAndCoins(userId);
	 * 
	 * // 데이터 설정 int highScoreValue = userData[0]; int coinCount = userData[1];
	 * highScoreLabel.setText("" + highScoreValue); coinLabel.setText("" +
	 * coinCount); scoreLabel.setText("0"); // 게임 시작 시 현재 점수 초기화 }
	 */

	// 다음에 나올 과일
	private void nextFruitLabel() {
		Image nextFruitImage = nextFruit.getType().getImage();
		if (nextFruitImage != null) {
			Image nextScaledImage = nextFruitImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			nextFruitLabel.setIcon(new ImageIcon(nextScaledImage));
		} else {
			nextFruitLabel.setIcon(null);
		}
	}

	// 커스텀 메세지 창(게임 종료)
	private void showGameOverDialog() {
		isGameOver = true;

		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "알림", true);
		dialog.setLayout(null);
		dialog.setSize(300, 125);
		dialog.setLocationRelativeTo(this); // 화면 중앙에 표시

		// 메시지 라벨 배경 패널
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(new Color(255, 230, 204));
		messagePanel.setBounds(0, 0, 500, 300);
		messagePanel.setLayout(null);

		// 메시지 라벨
		JLabel messageLabel = new JLabel("게임이 종료되었습니다.", SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(10, 0, 270, 50);
		dialog.add(messageLabel);

		// 확인 버튼
		RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(110, 50, 80, 30);
		confirmButton.addActionListener(e -> {
			dialog.dispose();
			resetGame();
			mainFrame.showScreen("HomeScreen");
		});
		dialog.add(confirmButton);

		dialog.add(messagePanel);
		dialog.setVisible(true);
	}

	private void resetGame() {
		fruits.clear();
		isGameOver = false;
		gameOverStartTime = 0; // 게임 오버 타이머 초기화
		currentPreviewFruit = new Fruit();
		nextFruit = new Fruit();
		nextFruitLabel();
		scoreLabel.setText("0");
		repaint();
	}

	// 합성순서 이미지 그리기
	private void initializeEvolutionImages() {
		String[] fruitNames = { "grape", "tangerine", "apple", "orange", "peach", "melon", "watermelon" };

		int[] xPositions = { 110, 60, 13, 13, 40, 80, 120 };
		int[] yPositions = { 120, 120, 120, 80, 43, 43, 43 };

		for (int i = 0; i < fruitNames.length; i++) {
			try {
				// 스킨 경로와 함께 이미지를 로드
				BufferedImage image = ImageIO
						.read(new File("src/image/" + SkinShopScreen.getAppliedSkin() + "/" + fruitNames[i] + ".png"));
				evolutionImages.add(image);
				evolutionPositions.add(new Point(xPositions[i], yPositions[i]));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 합성순서 이미지 그리기
	private void drawEvolutionImages(Graphics g) {
		for (int i = 0; i < evolutionImages.size(); i++) {
			Image image = evolutionImages.get(i);
			Point position = evolutionPositions.get(i);
			if (image != null) {
				g.drawImage(image, position.x, position.y, 30, 30, this);
			}
		}
	}

	// 커스텀 메시지 창
	private void showCustomMessage(String message) {

		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "알림", true);
		dialog.setLayout(null);
		dialog.setSize(410, 125);
		dialog.setLocationRelativeTo(this); // 화면 중앙에 표시

		// 메시지 라벨 배경 패널
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(new Color(255, 230, 204));
		messagePanel.setBounds(0, 0, 500, 300);
		messagePanel.setLayout(null);

		// 메시지 라벨
		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(5, 0, 390, 50);
		dialog.add(messageLabel);

		// 확인 버튼
		RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(160, 50, 80, 30);
		confirmButton.addActionListener(e -> dialog.dispose());
		dialog.add(confirmButton);

		dialog.add(messagePanel);
		dialog.setVisible(true);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// 배경 이미지 그리기
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}

		// 합성순서 이미지 그리기
		drawEvolutionImages(g);

		// 미리보기 과일 그리기
		if (!isGameOver && mousePosition != null && currentPreviewFruit != null) {
			int previewY = currentPreviewFruit.getPreviewY();
			currentPreviewFruit.drawPreview(g, mousePosition.x, previewY);
		}
		g.setColor(Color.red);
		g.drawLine(30, 300, 370, 300);

		// 떨어지는 과일 그리기
		paintFruits(g);
	}

	protected void paintFruits(Graphics g) {
		for (Falling fruit : fruits) {
			fruit.draw(g);
		}
	}

	@Override
	public void refreshData() {
		GameData.initialize(userId);
		coinLabel.setText("" + GameData.getCoins());
		scoreLabel.setText("" + score);
		highScoreLabel.setText("" + GameData.getHighScore());
		bombButton.setText("" + GameData.getBombs());
		passButton.setText("" + GameData.getPasses());
	}

	public void refreshUI() {
	    if (userId <= 0) {
	        System.err.println("Invalid userId home: " + userId);
	        return;
	    }
	    String[] userDetails = Database.getUserDetails(userId);

	    if (userDetails != null) {
	        // 하이스코어만 데이터베이스 값으로 설정
	        int highScoreValue = Integer.parseInt(userDetails[1]);
	        highScoreLabel.setText(String.valueOf(highScoreValue));
	        
	        // 코인 업데이트
	        coinLabel.setText(userDetails[2]);
            coinCount = Integer.parseInt(userDetails[2]);

	        // scoreLabel 업데이트
	        scoreLabel.setText(String.valueOf(score));
	    } else {
	        System.err.println("User details not found for userId: " + userId);
	    }
	}}
