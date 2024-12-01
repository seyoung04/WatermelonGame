//MainFrame.java
package screen;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	private CardLayout cardLayout;
	private Map<String, JPanel> screens; // 화면 이름과 인스턴스를 저장하는 맵

	public MainFrame() {
		setTitle("수박게임: 나만의 스타일");
		setSize(500, 750); // 창 크기 설정
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // 창을 중앙에 배치
		setResizable(false); // 창 크기 고정
		setLayout(cardLayout = new CardLayout()); // CardLayout 사용

		screens = new HashMap<>(); // 화면을 관리할 맵 초기화

		// 화면 추가
		addScreen(new LoginScreen(this), "LoginScreen");
		addScreen(new SignUpScreen(this), "SignUpScreen");
		addScreen(new HomeScreen(this), "HomeScreen");
		addScreen(new GameScreen(this), "GameScreen");
		addScreen(new ShopScreen(this), "ShopScreen");
		addScreen(new ItemShopScreen(this), "ItemShopScreen");
		addScreen(new SkinShopScreen(this), "SkinShopScreen");

		// 첫 화면을 로그인 화면으로 설정
		cardLayout.show(getContentPane(), "GameScreen");
	}

	private void addScreen(JPanel panel, String name) {
		add(panel, name);
		screens.put(name, panel); // 화면 이름과 인스턴스를 맵에 저장
	}

	public void showScreen(String screen) {
		cardLayout.show(getContentPane(), screen);
		JPanel currentScreen = screens.get(screen);
		if (currentScreen instanceof BaseScreen) { // 화면이 전환될 때 데이터 동기화
			((BaseScreen) currentScreen).refreshData();
		}
	}

	public static void main(String[] args) {
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}
}