//ShopScreen.java
package screen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ShopScreen extends JPanel {
	private JLabel coinLabel;
	private JPanel itemShopPanel;
	private JPanel skinShopPanel;

	public ShopScreen(MainFrame mainFrame) {
		setLayout(new BorderLayout());

		// 상단: 코인 개수와 이전 버튼 위치 조정
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		coinLabel = new JLabel("Coins: 0");
		topPanel.add(coinLabel);

		JButton backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.showScreen("HomeScreen");
			}
		});
		topPanel.add(backButton);
		add(topPanel, BorderLayout.NORTH);

		// 아이템 상점 및 스킨 상점 레이아웃 조정
		itemShopPanel = new JPanel(new GridLayout(2, 1, 10, 10));
		itemShopPanel.setPreferredSize(new Dimension(480, 100)); // 아이템 상점 패널 크기 조정
		JLabel bombItem = new JLabel("Bomb - Price: 10 coins");
		itemShopPanel.add(bombItem);
		JButton bombBuyButton = new JButton("Buy Bomb");
		bombBuyButton.addActionListener(e -> showInsufficientCoinsMessage());
		itemShopPanel.add(bombBuyButton);

		JLabel passItem = new JLabel("Pass - Price: 15 coins");
		itemShopPanel.add(passItem);
		JButton passBuyButton = new JButton("Buy Pass");
		passBuyButton.addActionListener(e -> showInsufficientCoinsMessage());
		itemShopPanel.add(passBuyButton);

		skinShopPanel = new JPanel();
		skinShopPanel.setLayout(new BoxLayout(skinShopPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(skinShopPanel);

		for (int i = 0; i < 5; i++) {
			JPanel skinPanel = new JPanel();
			skinPanel.setLayout(new FlowLayout());
			JLabel skinLabel = new JLabel("Skin " + (i + 1));
			JButton buyButton = new JButton("Buy");
			buyButton.addActionListener(e -> showInsufficientCoinsMessage());

			skinPanel.add(skinLabel);
			skinPanel.add(buyButton);
			skinShopPanel.add(skinPanel);
		}

		add(itemShopPanel, BorderLayout.CENTER);
		add(scrollPane, BorderLayout.SOUTH);
	}

	private void showInsufficientCoinsMessage() {
		JOptionPane.showMessageDialog(this, "Not enough coins!", "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public void updateCoins(int coins) {
		coinLabel.setText("Coins: " + coins);
	}
}