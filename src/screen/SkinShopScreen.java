//SkinShopScreen.java
package screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import physicalEngine.Fruit;

public class SkinShopScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private JLabel coinLabel; // 코인
	private JScrollPane scrollPane; // 스크롤
	private JPanel skinListPanel; // 스크롤 가능한 패널
	private static String appliedSkin = "default"; // 현재 적용돼있는 스킨. 기본값은 default
	private final Map<String, SkinData> skins = new LinkedHashMap<>(); // 스킨 데이터 관리

	public SkinShopScreen(MainFrame mainFrame) {
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/skinShop.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// Skin 레이블
		JLabel sLabel = new JLabel("Skin");
		sLabel.setBounds(200, 30, 150, 40);
		sLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
		sLabel.setForeground(new Color(83, 67, 47));
		add(sLabel);

		// 코인 레이블
		coinLabel = new JLabel("" + GameData.getCoins());
		coinLabel.setBounds(65, 33, 150, 40);
		coinLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 21));
		add(coinLabel);

		// back 버튼
		RoundedButton backButton = new RoundedButton(new Color(0, 0, 0, 0), 10, "", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 18));
		backButton.setBounds(420, 22, 64, 64);
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ShopScreen");
			}
		});
		add(backButton);

		// 스킨 데이터 초기화
		initializeSkinData();

		// 스킨 목록 패널
		skinListPanel = new JPanel();
		skinListPanel.setLayout(new BoxLayout(skinListPanel, BoxLayout.Y_AXIS)); // 세로로 정렬
		skinListPanel.setOpaque(false); // 배경 투명

		// 스킨 항목 추가
		for (String skinKey : skins.keySet()) {
			addSkinItem(skinKey, skins.get(skinKey));
		}

		// 스크롤 패널 설정
		scrollPane = new JScrollPane(skinListPanel); // 스킨 목록 패널을 스크롤 가능하게
		scrollPane.setBounds(30, 120, 430, 550);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // 세로 스크롤바
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤바 숨김
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		add(scrollPane);

		// 구매 버튼 문구 설정
		updateButtonStates();
	}

	// 스킨 데이터 초기화
	private void initializeSkinData() {
		skins.put("default", new SkinData(0, true)); // 보유
		skins.put("A", new SkinData(1000, false)); // 미보유
		skins.put("B", new SkinData(1500, false));
		skins.put("C", new SkinData(2000, false));
		skins.put("D", new SkinData(2500, false));
	}

	// 스킨 데이터 클래스
	private static class SkinData {
		private final int price; // 가격
		private boolean isOwned; // 스킨 보유여부

		public SkinData(int price, boolean isOwned) {
			this.price = price;
			this.isOwned = isOwned;
		}

		public int getPrice() {
			return price;
		}

		public boolean isOwned() {
			return isOwned;
		}

		public void setOwned(boolean owned) {
			isOwned = owned;
		}
	}

	// 스킨 항목 추가
	private void addSkinItem(String skinKey, SkinData skinData) {
		JPanel skinItemPanel = new JPanel();
		skinItemPanel.setLayout(null);
		skinItemPanel.setPreferredSize(new Dimension(400, 160));
		skinItemPanel.setBackground(new Color(255, 243, 220, 200));
		skinItemPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // 테두리 색

		// 스킨 사진
		JLabel skinImageLabel = new JLabel();
		skinImageLabel.setBounds(10, 10, 280, 140);
		skinImageLabel.setIcon(new ImageIcon("src/image/" + skinKey + "/set.png"));
		skinItemPanel.add(skinImageLabel);

		// 구매 버튼
		RoundedButton actionButton = new RoundedButton(new Color(220, 80, 80), 10, "", Color.WHITE,
				new Font("Bazzi", Font.PLAIN, 17));
		actionButton.setBounds(310, 60, 95, 40);
		actionButton.addActionListener(e -> handleSkinAction(skinKey, skinData, actionButton));
		skinItemPanel.add(actionButton);

		skinListPanel.add(skinItemPanel);
	}

	// 구매 버튼 문구 설정
	private void updateButtonStates() {
		Component[] components = skinListPanel.getComponents();
		for (Component component : components) {
			if (component instanceof JPanel) {
				JPanel panel = (JPanel) component;
				for (Component subComponent : panel.getComponents()) {
					if (subComponent instanceof RoundedButton) {
						RoundedButton button = (RoundedButton) subComponent;
						String skinKey = skins.keySet().toArray(new String[0])[skinListPanel.getComponentZOrder(panel)];
						SkinData skinData = skins.get(skinKey);

						if (skinData.isOwned()) {
							if (appliedSkin.equals(skinKey)) {
								button.setText("적용중");
								button.setBackground(new Color(218, 165, 22)); // 약간 어두운 갈색

								button.setContentAreaFilled(false); // Look-and-Feel 무시
								button.setEnabled(false); // 비활성화
							} else {
								button.setText("적용하기");
								button.setBackground(new Color(198, 155, 93)); // 갈색 계열

								button.setContentAreaFilled(false);
								button.setEnabled(true); // 활성화
							}
						} else {
							button.setText(skinData.getPrice() + " 코인");
							button.setBackground(new Color(245, 190, 60)); // 어두운 노란색

							button.setContentAreaFilled(false);
							button.setEnabled(true); // 활성화
						}
					}
				}
			}
		}
	}

	// 구매버튼 이벤트
	private void handleSkinAction(String skinKey, SkinData skinData, RoundedButton button) {
		if (skinData.isOwned()) { // 스킨 보유 시 적용하기
			applySkin(skinKey);
			return;
		}

		if (GameData.getCoins() >= skinData.getPrice()) {
			yesnoMessage(skinData, "스킨을 구매하겠습니까?");
		} else {
			showCustomMessage("코인이 부족합니다!");
		}
	}

	// 스킨 적용
	private void applySkin(String skin) {
		appliedSkin = skin;
		updateButtonStates();
		Fruit.refreshImages();
		showCustomMessage("스킨이 적용되었습니다!");
	}

	public static String getAppliedSkin() {
		return appliedSkin;
	}

	// 커스텀 메시지 창
	private void showCustomMessage(String message) {
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
		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(10, 0, 270, 50);
		dialog.add(messageLabel);

		// 확인 버튼
		RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(100, 50, 80, 30);
		confirmButton.addActionListener(e -> dialog.dispose());
		dialog.add(confirmButton);

		dialog.add(messagePanel);
		dialog.setVisible(true);
	}

	// 커스텀 메시지 창 (예, 아니요)
	private void yesnoMessage(SkinData skinData, String message) {
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
		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(10, 0, 270, 50);
		dialog.add(messageLabel);

		// Yes 버튼
		RoundedButton yesButton = new RoundedButton(new Color(255, 172, 62), 10, "예", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		yesButton.setBounds(50, 50, 80, 26);
		yesButton.addActionListener(e -> {
			GameData.subtractCoins(skinData.getPrice());
			coinLabel.setText("" + GameData.getCoins());
			skinData.setOwned(true);
			updateButtonStates();
			dialog.dispose();
			showCustomMessage("스킨을 구매했습니다!");
		});
		dialog.add(yesButton);

		// No 버튼
		RoundedButton noButton = new RoundedButton(new Color(255, 172, 62), 10, "아니요", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		noButton.setBounds(155, 50, 80, 26);
		noButton.addActionListener(e -> dialog.dispose());
		dialog.add(noButton);

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
	}

	@Override
	public void refreshData() {
		coinLabel.setText("" + GameData.getCoins());
		updateButtonStates();
	}
}