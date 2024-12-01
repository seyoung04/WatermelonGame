//RoundedButton.java
package screen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class RoundedButton extends JButton {
	private Color backgroundColor;
	private Color textColor;
	private int arcSize;

	public RoundedButton(Color backgroundColor, int arcSize, String text, Color textColor, Font font) {
		super(text);
		this.backgroundColor = backgroundColor;
		this.arcSize = arcSize;
		this.textColor = textColor;

		// 기본 스타일 설정
		setContentAreaFilled(false); // 내부 배경 제거
		setBorderPainted(false); // 테두리 제거
		setFocusPainted(false); // 포커스 테두리 제거
		setForeground(textColor); // 텍스트 색상 설정
		setFont(font); // 글꼴 설정

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) { // 마우스가 버튼 위에 올라왔을 때
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) { // 마우스가 버튼 위를 벗어났을 때
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcSize, arcSize); // 둥근 사각형 그리기
		super.paintComponent(g); // 기본 컴포넌트 그리기
	}
}