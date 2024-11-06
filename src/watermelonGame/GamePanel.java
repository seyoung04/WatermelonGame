package watermelonGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel {
	private List<Falling> fruits = new ArrayList<>();

	public GamePanel() {
		setPreferredSize(new Dimension(400, 600));
		setBackground(Color.WHITE);
		setFocusable(true);
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				fruits.add(new Falling(e.getX()));
			}
		});

		Timer timer = new Timer(20, e -> {
			update();
			repaint();
		});
		timer.start();
	}

	private void update() {
		for (Falling fruit : fruits) {
			fruit.update(fruits);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (Falling fruit : fruits) {
			fruit.draw(g);
		}
	}
}
