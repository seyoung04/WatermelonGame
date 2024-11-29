package screen;

import java.awt.CardLayout;
import javax.swing.JFrame;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private int userId; // 로그인한 사용자 ID
    private GameScreen gameScreen;

    public MainFrame() {
        setTitle("수박게임: 나만의 스타일");
        setSize(500, 780); // 창 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 창을 중앙에 배치
        setResizable(false); // 창 크기 고정
        setLayout(cardLayout = new CardLayout()); // CardLayout 사용

        // 초기 userId 설정 (예: 로그인 전에는 -1)
        userId = -1;


        // 각 화면 추가
        add(new LoginScreen(this), "LoginScreen");
        add(new HomeScreen(this), "HomeScreen"); // userId 전달
        add(new GameScreen(this), "GameScreen");
        add(new ShopScreen(this), "ShopScreen");
        add(new ItemShopScreen(this), "ItemShopScreen");
        add(new SkinShopScreen(this), "SkinShopScreen");
        add(new SignUpScreen(this), "SignUpScreen");

        // 로그인 화면이 처음으로 보이도록 설정
        cardLayout.show(getContentPane(), "LoginScreen");
    }

    // 화면 전환 메서드
    public void showScreen(String screen) {
        cardLayout.show(getContentPane(), screen);
    }

    // 로그인 성공 시 userId 업데이트
    public void setUserId(int userId) {
        this.userId = userId;

        // HomeScreen과 GameScreen 업데이트
        HomeScreen homeScreen = (HomeScreen) getContentPane().getComponent(1); // HomeScreen 찾기
        homeScreen.updateUserId(userId); // userId 업데이트
        gameScreen.updateData(userId); // GameScreen 데이터 업데이트
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
