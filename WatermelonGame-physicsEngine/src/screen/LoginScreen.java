package screen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginScreen extends JPanel {
	private MainFrame mainFrame;
	private JTextField idField;
	private JPasswordField passwordField;

	public LoginScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);

		// 배경 이미지 설정
		ImageIcon backgroundIcon = new ImageIcon("src/image/loginscreen.png");
		JLabel backgroundLabel = new JLabel(backgroundIcon);
		backgroundLabel.setBounds(0, 0, 490, 745);

		// 투명한 텍스트 필드 생성
		idField = new JTextField(20);
		passwordField = new JPasswordField(20);

		// 예시 위치 (실제 위치로 수정 필요)
		idField.setBounds(250, 325, 140, 30); // x, y, width, height
		passwordField.setBounds(250, 360, 140, 30); // x, y, width, height

		// 텍스트 필드 스타일링
		styleTransparentTextField(idField);
		styleTransparentTextField(passwordField);

		// 투명 버튼 생성
		JButton loginButton = new JButton();
		loginButton.setBounds(145, 397, 83, 29);
		styleTransparentButton(loginButton);

		JButton signupButton = new JButton();
		signupButton.setBounds(260, 397, 95, 29);
		styleTransparentButton(signupButton);

		// 로그인 버튼 이벤트
		loginButton.addActionListener(e -> {
			String id = idField.getText();
			String password = new String(passwordField.getPassword());
			if (id.equals("user") && password.equals("pass")) {
				mainFrame.showScreen("HomeScreen");
			} else {
				JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 일치하지 않습니다.");
			}
		});

		// 회원가입 버튼 이벤트
		signupButton.addActionListener(e -> {
			mainFrame.showScreen("SignUpScreen");
		});

		// 컴포넌트 추가
		add(idField);
		add(passwordField);
		add(loginButton);
		add(signupButton);
		add(backgroundLabel);

		setBounds(0, 0, 500, 750);
	}

	// 투명 버튼 스타일 설정
	private void styleTransparentButton(JButton button) {
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}

	// 투명 텍스트 필드 스타일 설정
	private void styleTransparentTextField(JTextField textField) {
		textField.setOpaque(false); // 배경 투명
		textField.setForeground(Color.WHITE); // 텍스트 색상
		textField.setCaretColor(Color.WHITE); // 커서 색상
		textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 밑줄만 표시

		// 포커스 효과
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE)); // 포커스 시 밑줄 굵게
			}

			@Override
			public void focusLost(FocusEvent e) {
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 포커스 잃으면 밑줄 얇게
			}
		});
	}
}