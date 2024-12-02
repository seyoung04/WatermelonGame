//HomeScreen.java
package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import database.Database;
import database.SessionManager;
import physicalEngine.Coin;
import physicalEngine.Falling;
import physicalEngine.HighScore;

public class HomeScreen extends JPanel implements RefreshableScreen{
	private JLabel highScore; 
	private JLabel coin; 
	private BufferedImage backgroundImage;
    private int currentUserId;


	List<Falling> fruits;
	private int userId;


	// 버튼의 위치 및 크기

	public HomeScreen(MainFrame mainFrame) {
		setLayout(null);
		setBackground(new Color(222, 184, 135)); 
		this.currentUserId=0;

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/homescreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); 
		}

		// high score 레이블
		highScore = new JLabel("0");
		highScore.setBounds(100, 32, 105, 40);
		highScore.setFont(new Font("Comic Sans MS", Font.BOLD, 23)); 
		highScore.setHorizontalAlignment(SwingConstants.RIGHT);
		add(highScore);


		// coin 레이블
		coin = new JLabel("0");
		coin.setBounds(340, 32, 150, 40);
		coin.setFont(new Font("Comic Sans MS", Font.BOLD, 23)); // 폰트 설정
		add(coin);

		// Start 버튼
		RoundedButton startButton = new RoundedButton(new Color(255, 200, 126), 10, "Start", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 47));
		startButton.setBounds(140, 373, 215, 75);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("GameScreen");
			}
		});
		add(startButton);

		// Shop 버튼
		RoundedButton shopButton = new RoundedButton(new Color(255, 193, 96), 10, "Shop", Color.WHITE,
				new Font("Comic Sans MS", Font.BOLD, 33));
		shopButton.setBounds(158, 465, 180, 55);
		shopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("ShopScreen");
			}
		});
		add(shopButton);
	    refreshUI();

	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}
	


	 public void loginSuccess(int userId) {
	        this.currentUserId = SessionManager.getInstance().getUserId();
	        refreshUI();
	    }

	    @Override
	    public void refreshUI() {
	        if (currentUserId <= 0) {
	            return;
	        }
	        String[] userDetails = Database.getUserDetails(currentUserId);
	        if (userDetails != null) {
	            highScore.setText(userDetails[1]);
	            coin.setText(userDetails[2]);
	        } else {
	            System.err.println("User details not found for userId: " + currentUserId);
	        }
	    }
	    
	    public void setUserId(int userId) {
	        this.currentUserId = userId;
	        refreshUI(); 
	    }


}