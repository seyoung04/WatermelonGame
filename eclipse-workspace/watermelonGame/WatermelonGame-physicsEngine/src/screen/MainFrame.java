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
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        screens = new HashMap<>(); // 화면을 관리할 맵 초기화

        // 화면 추가
        addScreen(new LoginScreen(this), "LoginScreen");
        addScreen(new SignUpScreen(this), "SignUpScreen");
        addScreen(new HomeScreen(this), "HomeScreen");
        addScreen(new GameScreen(this), "GameScreen");
        addScreen(new ShopScreen(this), "ShopScreen");
        addScreen(new ItemShopScreen(this), "ItemShopScreen");
        addScreen(new SkinShopScreen(this), "SkinShopScreen");

        cardLayout.show(getContentPane(), "LoginScreen");
    }

    private void addScreen(JPanel panel, String name) {
        add(panel, name);
        screens.put(name, panel); // 화면 이름과 인스턴스를 맵에 저장
    }

    public void showScreen(String screen) {
        cardLayout.show(getContentPane(), screen);

        // 현재 화면 인스턴스를 가져와 refreshUI 호출
        JPanel currentScreen = screens.get(screen);
        if (currentScreen instanceof RefreshableScreen) {
            ((RefreshableScreen) currentScreen).refreshUI();
        }
    }


    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}