package watermelonGame;

import java.awt.GridBagLayout;

import javax.swing.JFrame;

public class GameScreen extends JFrame {
	private final int FRAME_WIDTH = 1280;
	private final int FRAME_HEIGHT = 720;

	public GameScreen() {
		setTitle("Watermelon Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		setSize(FRAME_WIDTH, FRAME_HEIGHT);

		GamePanel gamePanel = new GamePanel();

		add(gamePanel);
	}

	public static void main(String[] args) {
		GameScreen gameScreen = new GameScreen();
		gameScreen.setVisible(true);
	}
}
