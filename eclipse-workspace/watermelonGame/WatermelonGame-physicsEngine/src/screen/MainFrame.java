package screen;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private Map<String, JPanel> screens;
    private int currentUserId;

    public MainFrame() {
        setTitle("수박게임: 나만의 스타일");
        setSize(500, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); 
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        this.currentUserId = 0;

        screens = new HashMap<>();

        // 화면 추가
        addScreen(new LoginScreen(this), "LoginScreen");
        addScreen(new SignUpScreen(this), "SignUpScreen");
        addScreen(new HomeScreen(this), "HomeScreen");
        addScreen(new GameScreen(this, currentUserId), "GameScreen");
        addScreen(new ShopScreen(this, currentUserId), "ShopScreen");
        addScreen(new ItemShopScreen(this, currentUserId), "ItemShopScreen");
        addScreen(new SkinShopScreen(this), "SkinShopScreen");

        cardLayout.show(getContentPane(), "LoginScreen");
    }

    private void addScreen(JPanel panel, String name) {
        add(panel, name);
        screens.put(name, panel);
    }
    public void loginSuccess(int userId) {
        this.currentUserId = userId;

        showScreen("HomeScreen");
    }


    public void showScreen(String screenName) {
        if (screenName.equals("HomeScreen") && currentUserId != 0) {
            ((HomeScreen) screens.get(screenName)).setUserId(currentUserId); 
        }
        if (screenName.equals("GameScreen") && currentUserId != 0) {
            ((GameScreen) screens.get(screenName)).setUserId(currentUserId); 
        }
        if (screenName.equals("ShopScreen") && currentUserId != 0) {
            ((ShopScreen) screens.get(screenName)).setUserId(currentUserId); 
        }
        if (screenName.equals("SkinShopScreen") && currentUserId != 0) {
            ((SkinShopScreen) screens.get(screenName)).setUserId(currentUserId);
        }
        if (screenName.equals("ItemShopScreen") && currentUserId != 0) {
            ((ItemShopScreen) screens.get(screenName)).setUserId(currentUserId);
        }
        ((RefreshableScreen) screens.get(screenName)).refreshUI();
        cardLayout.show(getContentPane(), screenName); 
        
    }
    
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
