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

public class SignUpScreen extends JPanel {
	private MainFrame mainFrame;
	private JTextField idField;
	private JPasswordField passwordField;
	private JPasswordField passwordCheckField;

	public SignUpScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);

		// 배경 이미지 설정
		ImageIcon backgroundIcon = new ImageIcon("src/image/signupscreen.png");
		JLabel backgroundLabel = new JLabel(backgroundIcon);
		backgroundLabel.setBounds(0, 0, 500, 750);

		// 투명한 텍스트 필드 생성
		idField = new JTextField(20);
		passwordField = new JPasswordField(20);
		passwordCheckField = new JPasswordField(20);

		// 예시 위치 (실제 위치로 수정 필요)
		// 텍스트 필드 위치 조정
		idField.setBounds(250, 290, 140, 30); // y: 295
		passwordField.setBounds(250, 330, 140, 30); // y: 325 -> 335 (간격 10 증가)
		passwordCheckField.setBounds(250, 370, 140, 30); // y: 360 -> 375 (간격 10 증가)
		// 텍스트 필드 스타일링
		styleTransparentTextField(idField);
		styleTransparentTextField(passwordField);
		styleTransparentTextField(passwordCheckField);

		// 투명 버튼 생성

		JButton signupButton = new JButton();
		signupButton.setBounds(195, 420, 112, 45);
		styleTransparentButton(signupButton);

		// 회원가입 버튼 이벤트

		signupButton.addActionListener(e -> {
			String id = idField.getText();
			String password = new String(passwordField.getPassword());
			String passwordCheck = new String(passwordCheckField.getPassword());

			if (id.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
				JOptionPane.showMessageDialog(null, "모두 입력해주세요.");
				return;
			}

			if (!password.equals(passwordCheck)) {
				JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.");
			} else {
				JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
				mainFrame.showScreen("LoginScreen");
			}
		});

		// 컴포넌트 추가
		add(idField);
		add(passwordField);
		add(passwordCheckField);
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

		// 초기 테두리 설정을 확실히 하기 위해
		textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 새로운 테두리 설정

		// 포커스 효과
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.setBorder(null); // 기존 테두리 제거
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));
			}

			@Override
			public void focusLost(FocusEvent e) {
				textField.setBorder(null); // 기존 테두리 제거
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE));
			}
		});
	}
}