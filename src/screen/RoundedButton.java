//RoundedButton.java
package screen;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;

public class RoundedButton extends JButton {
	private Color backgroundColor;
	private Color textColor;
	private int arcSize;

	public RoundedButton(String text, Color backgroundColor, Color textColor, int arcSize) {
		super(text);
		this.backgroundColor = backgroundColor;
		this.textColor = textColor;
		this.arcSize = arcSize;

		setContentAreaFilled(false); // 기본 버튼 모양 제거
		setFocusPainted(false); // 포커스 표시 제거
		setBorderPainted(false); // 테두리 제거
		setForeground(textColor); // 텍스트 색상 설정
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), arcSize, arcSize); // 둥근 사각형 그리기
		super.paintComponent(g); // 기본 컴포넌트 그리기
	}
}