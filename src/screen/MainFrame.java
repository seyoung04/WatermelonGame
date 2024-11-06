//MainFrame.java
package screen;

import java.awt.CardLayout;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	private CardLayout cardLayout;

	public MainFrame() {
		setTitle("수박게임: 나만의 스타일");
		setSize(400, 600); // 창 크기 설정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // 창을 중앙에 배치
		setResizable(false); // 창 크기 고정
		setLayout(null); // 레이아웃 매니저 미사용

		cardLayout = new CardLayout();
		setLayout(cardLayout);
		add(new HomeScreen(this), "HomeScreen");
		add(new GameScreen(this), "GameScreen");
		add(new ShopScreen(this), "ShopScreen");

		cardLayout.show(getContentPane(), "HomeScreen"); // 게임시작 시, 첫 화면을 홈 화면으로 설정
	}

	public void showScreen(String screen) {
		cardLayout.show(getContentPane(), screen);
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}
}
