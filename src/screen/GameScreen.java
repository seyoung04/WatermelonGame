//GameScreen.java 
//CheckGameover 조건 수정 
//private List<Image> evolutionImages = new ArrayList<>(); 추가
//private List<Point> evolutionPositions = new ArrayList<>();  추가
//initializeEvolutionImages(); 추가 
//paintcomponents메서드 변경 
//drawEvolutionImages추가 
//코인라벨 위치수정 
//refreshData코인포함으로 수정 
//updateScoreAndCoins
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import physicalEngine.Falling;
import physicalEngine.Fruit;

public class GameScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private List<Falling> fruits = new ArrayList<>();
	private long lastCreationTime = 0;
	private JLabel coinLabel; // 코인
	private JLabel currentScoreLabel; // 현재 점수
	private JLabel highScoreLabel; // 최고기록 점수
	private JButton bombButton; // 폭탄 버튼
	private JButton passButton; // 패스 버튼
	private JLabel bombNums; // 폭탄 보유 개수
	private JLabel passNums; // 패스 보유 개수
	private int score = 0; // 현재 점수
	private int coinCount = 0; // 현재 코인 개수
	private boolean isBombActive = false; // 폭탄 사용 상태
	private JLabel nextFruitLabel;
	private Point mousePosition = new Point(0, 200);
	private Fruit currentPreviewFruit = new Fruit();
	private Fruit nextFruit = new Fruit();
	private MainFrame mainFrame;
	private boolean isGameOver = false;
	private static final int GAME_OVER_HEIGHT = 200; // 게임 오버가 되는 높이
	private static final int GAME_OVER_DURATION = 3000; // 2초(2000밀리초)
	private long gameOverStartTime = 0; // 게임 오버 조건이 시작된 시간
	//수정
	private List<Image> evolutionImages = new ArrayList<>(); // 과일 진화 단계 이미지 리스트
	private List<Point> evolutionPositions = new ArrayList<>(); // 과일 위치 리스트
	//수정

	private Map<String, Integer> fruitCoinValues; // 과일에 대한 코인 값 저장

	public GameScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);
		setBackground(new Color(222, 184, 135));

		//수정
		addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        if (!isGameOver) {
		            long currentTime = System.currentTimeMillis();
		            if (currentTime - lastCreationTime > 1000) {
		                lastCreationTime = currentTime;
		                fruits.add(new Falling(e.getX(), currentPreviewFruit.getType().getImage(),
		                        currentPreviewFruit.getType(), GameScreen.this)); // GameScreen 참조 전달

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
				int previewY = currentPreviewFruit.getPreviewY();
				currentPreviewFruit.drawPreview(getGraphics(), mousePosition.x, previewY);
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
			backgroundImage = ImageIO.read(new File("src/image/GameScreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}
		//수정
		initializeEvolutionImages(); 

		// 코인 레이블   수정 
		coinLabel = new JLabel("0");
		coinLabel.setBounds(380, 100, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
		add(coinLabel);

		// 현재 점수 레이블
		currentScoreLabel = new JLabel("0");
		currentScoreLabel.setBounds(210, 38, 100, 50);
		currentScoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
		add(currentScoreLabel);

		// 최고기록 레이블
		highScoreLabel = new JLabel("0");
		highScoreLabel.setBounds(220, 115, 100, 40);
		highScoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 17));
		add(highScoreLabel);

		// next 과일 레이블
		nextFruitLabel = new JLabel();
		nextFruitLabel.setBounds(418, 290, 30, 30);
		add(nextFruitLabel);
		nextFruitLabel();

		bombButton = new RoundedButton(
			    GameData.getBombs() > 0 ? new Color(255, 165, 0) : Color.GRAY, // 주황색 또는 회색
			    20,
			    "폭탄",
			    Color.WHITE,
			    new Font("Bazzi", Font.BOLD, 20)
			);
			bombButton.setBounds(50, 650, 130, 40);
			bombButton.addActionListener(e -> useBomb());
			add(bombButton);


		// 폭탄 보유개수 레이블
		bombNums = new JLabel("보유 폭탄: " + GameData.getBombs());
		bombNums.setFont(new Font("Bazzi", Font.BOLD, 18));
		bombNums.setBounds(50, 600, 200, 200);
		add(bombNums);

		passButton = new RoundedButton(
			    GameData.getPasses() > 0 ? new Color(135, 206, 235) : Color.GRAY, // 하늘색 또는 회색
			    20,
			    "패스",
			    Color.WHITE,
			    new Font("Bazzi", Font.BOLD, 20)
			);
			passButton.setBounds(350, 650, 100, 40);
			passButton.addActionListener(e -> usePass());
			add(passButton);


		// 패스 보유개수 레이블
		passNums = new JLabel("보유 패스: " + GameData.getPasses());
		passNums.setFont(new Font("Bazzi", Font.BOLD, 18));
		passNums.setBounds(350, 600, 200, 200);
		add(passNums);


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

	// 폭탄 사용: 원하는 과일 하나 삭제
	private void useBomb() {
		if (GameData.getBombs() < 1) {
			JOptionPane.showMessageDialog(this, "Bomb이 없습니다!");
			return;
		}

		if (fruits.isEmpty()) {
			JOptionPane.showMessageDialog(this, "삭제할 과일이 없습니다!");
			return;
		}

		isBombActive = true; // 폭탄 사용 활성화
		bombButton.setText("선택중"); // 버튼 텍스트 변경
		bombButton.setEnabled(false); // 버튼 비활성화로 중복 클릭 방지

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!isBombActive)
					return; // 폭탄 사용 중이 아니면 무시

				// 클릭 위치를 기준으로 과일 찾기
				for (int i = fruits.size() - 1; i >= 0; i--) {
					Falling fruit = fruits.get(i);
					if (isClickedOnFruit(fruit, e.getX(), e.getY())) {
						fruits.remove(fruit); // 과일 삭제
						GameData.subtractBombs(1); // 폭탄 개수 감소
						bombNums.setText("보유 폭탄: " + GameData.getBombs()); // UI 업데이트

						isBombActive = false; // 폭탄 사용 종료
						bombButton.setText("폭탄"); // 버튼 텍스트 초기화
						bombButton.setEnabled(true); // 버튼 활성화

						removeMouseListener(this); // 이벤트 리스너 제거
						repaint();
						refreshData();
						return; // 과일 삭제 후 종료
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

	// 패스 사용: 미리보기 과일 넘기기
	private void usePass() {
		if (GameData.getPasses() < 1) {
			JOptionPane.showMessageDialog(this, "Pass가 없습니다!");
		} else {
			currentPreviewFruit = nextFruit; // 현재 과일을 다음 과일로 교체
			nextFruit = new Fruit(); // 새로운 과일 생성
			nextFruitLabel(); // 미리보기 갱신
			GameData.subtractPasses(1); // 패스 개수 감소
			passNums.setText("보유 패스: " + GameData.getPasses()); // 패스 개수 갱신
			refreshData();
			repaint();
		}
	}

	private boolean checkGameOver() {
	    for (Falling fruit : fruits) {
	        // 과일의 중심 y값 + 반지름이 GAME_OVER_HEIGHT를 넘었는지 확인
	        if (fruit.getY() + fruit.getDiameter() / 2 <= GAME_OVER_HEIGHT) {
	            
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
		currentScoreLabel.setText("0");
		repaint();
	}

	// 합쳐질 때 과일 업데이트
	private void update() {
		for (int i = 0; i < fruits.size(); i++) {
			Falling fruit = fruits.get(i);
			fruit.update(fruits); // 과일 상태 업데이트

			if (fruit.isMarkedForDeletion()) {
				// 삭제된 과일은 더 이상 리스트에 포함되지 않도록 처리
				fruits.remove(i);
				i--; // 리스트 인덱스 조정
			}

			if (fruit.isMerged() && !fruit.isMarkedForDeletion()) {
				String fruitType = fruit.getType().toString().toLowerCase();
				updateScoreAndCoins(fruitType); // 점수와 코인 업데이트
			}
		}
	}

	public void updateScoreAndCoins(String fruitType) {
	    int pointIncrement = fruitCoinValues.getOrDefault(fruitType, 0); // 기본값 0
	    if (pointIncrement == 0) {
	        System.out.println("Error: fruitType not found in fruitCoinValues: " + fruitType);
	        return;
	    }

	    // 점수 증가
	    score += pointIncrement;
	    System.out.println("Score incremented by " + pointIncrement + ". New score: " + score);

	    // 하이스코어 업데이트
	    if (score > GameData.getHighScore()) {
	        GameData.setHighScore(score);
	        highScoreLabel.setText("" + GameData.getHighScore());
	    }

	    // 코인 증가
	    int newCoins = pointIncrement / 10;
	    coinCount += newCoins;
	    GameData.setCoins(coinCount);

	    // UI 업데이트
	    coinLabel.setText("" + coinCount);
	    currentScoreLabel.setText("" + score);
	    repaint();
	}



	// 다음에 나올 과일
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

	    // 배경 이미지 그리기
	    if (backgroundImage != null) {
	        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	    }

	    // 진화 이미지 그리기 (과일의 진화 단계)
	    drawEvolutionImages(g);

	    // 프리뷰 과일 그리기
	    if (!isGameOver && mousePosition != null && currentPreviewFruit != null) {
	        int previewY = currentPreviewFruit.getPreviewY();
	        currentPreviewFruit.drawPreview(g, mousePosition.x, previewY);
	    }
	    g.setColor(Color.red);
	    g.drawLine(30, 300, 370, 300); // 빨간 선을 그리는 코드


	    // 떨어지는 과일 그리기
	    paintFruits(g);
	}



	protected void paintFruits(Graphics g) {
		for (Falling fruit : fruits) {
			fruit.draw(g);
		}
	}

	//수정 
	@Override
	public void refreshData() {
	    coinLabel.setText("" + GameData.getCoins()); // GameData에서 초기 코인 값 가져오기
	    currentScoreLabel.setText("" + score); // 현재 점수는 GameScreen에서 관리
	    highScoreLabel.setText("" + GameData.getHighScore()); // 최고 점수는 GameData에서 가져오기
	 // 폭탄 버튼 재생성
	    remove(bombButton); // 기존 버튼 제거
	    bombButton = new RoundedButton(
	        GameData.getBombs() > 0 ? new Color(255, 165, 0) : Color.GRAY, // 주황색 또는 회색
	        20,
	        "폭탄",
	        Color.WHITE,
	        new Font("Bazzi", Font.BOLD, 20)
	    );
	    bombButton.setBounds(50, 650, 130, 40);
	    bombButton.addActionListener(e -> useBomb());
	    add(bombButton);

	    // 패스 버튼 재생성
	    remove(passButton); // 기존 버튼 제거
	    passButton = new RoundedButton(
	        GameData.getPasses() > 0 ? new Color(135, 206, 235) : Color.GRAY, // 하늘색 또는 회색
	        20,
	        "패스",
	        Color.WHITE,
	        new Font("Bazzi", Font.BOLD, 20)
	    );
	    passButton.setBounds(350, 650, 100, 40);
	    passButton.addActionListener(e -> usePass());
	    add(passButton);
	    repaint(); // 변경 사항 화면 반영
	}

	//수정
	private void initializeEvolutionImages() {
	    String[] fruitNames = {"grape", "tangerine", "apple", "orange", "peach", "melon", "watermelon"};

	    // 화살표 경로를 따라 수동으로 설정된 X, Y 좌표
	    int[] xPositions = {110, 60, 13, 13, 40, 80, 120}; // 화살표를 따라가는 X 좌표
	    int[] yPositions = {120, 120, 120, 80, 43, 43, 43}; // 화살표를 따라가는 Y 좌표

	    for (int i = 0; i < fruitNames.length; i++) {
	        try {
	            // 스킨 경로와 함께 이미지를 로드
	            BufferedImage image = ImageIO.read(new File("src/image/" + SkinShopScreen.getAppliedSkin() + "/" + fruitNames[i] + ".png"));
	            evolutionImages.add(image);
	            evolutionPositions.add(new Point(xPositions[i], yPositions[i]));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	private void drawEvolutionImages(Graphics g) {
	    for (int i = 0; i < evolutionImages.size(); i++) {
	        Image image = evolutionImages.get(i);
	        Point position = evolutionPositions.get(i);
	        if (image != null) {
	            g.drawImage(image, position.x, position.y, 30, 30, this); // 30x30 크기로 그리기
	        }
	    }
	}



}