package screen;

import java.awt.CardLayout;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
	private CardLayout cardLayout;

	public MainFrame() {
		setTitle("수박게임: 나만의 스타일");
		setSize(500, 780); // 창 크기 설정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // 창을 중앙에 배치
		setResizable(false); // 창 크기 고정
		setLayout(cardLayout = new CardLayout()); // CardLayout 사용

		// 화면을 "로그인 화면"부터 시작
		add(new LoginScreen(this), "LoginScreen");
		add(new HomeScreen(this), "HomeScreen");
		add(new GameScreen(this), "GameScreen");
		add(new ShopScreen(this), "ShopScreen");
		add(new ItemShopScreen(this), "ItemShopScreen");
		add(new SkinShopScreen(this), "SkinShopScreen");
		add(new SignUpScreen(this), "SignUpScreen");

		// 로그인 화면이 처음으로 보이도록 설정
		cardLayout.show(getContentPane(), "LoginScreen");
	}

	public void showScreen(String screen) {
		cardLayout.show(getContentPane(), screen);
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}
}
