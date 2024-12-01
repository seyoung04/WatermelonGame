//SignUpScreen.java
package screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import database.Login;

public class SignUpScreen extends BaseScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private MainFrame mainFrame;
	private JLabel id; // 아이디 라벨
	private JLabel password; // 비밀번호 라벨
	private JLabel passwordCheck; // 비밀번호 확인 라벨
	private JTextField idField; // 아이디 입력창
	private JPasswordField passwordField; // 비밀번호 입력창
	private JPasswordField passwordCheckField; // 비밀번호 확인 입력창

	public SignUpScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);

		// 배경 이미지 설정
		try {
			backgroundImage = ImageIO.read(new File("src/image/signupScreen.png"));
		} catch (IOException e) {
			e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
		}

		// 아이디 라벨
		id = new JLabel("ID");
		id.setBounds(160, 275, 140, 27);
		id.setFont(new Font("Comic Sans Ms", Font.BOLD, 23));
		id.setForeground(new Color(181, 112, 0));
		add(id);

		// 아이디 입력창
		idField = new JTextField(10);
		idField.setBounds(280, 275, 140, 27);
		clearTextField(idField);
		add(idField);

		// 비밀번호 라벨
		password = new JLabel("Password");
		password.setBounds(120, 315, 140, 27);
		password.setFont(new Font("Comic Sans Ms", Font.BOLD, 23));
		password.setForeground(new Color(181, 112, 0));
		add(password);

		// 비밀번호 입력창
		passwordField = new JPasswordField(10);
		passwordField.setBounds(280, 315, 140, 27);
		clearTextField(passwordField);
		add(passwordField);

		// 비밀번호 확인 라벨
		passwordCheck = new JLabel("Password Check");
		passwordCheck.setBounds(80, 355, 200, 27);
		passwordCheck.setFont(new Font("Comic Sans Ms", Font.BOLD, 23));
		passwordCheck.setForeground(new Color(181, 112, 0));
		add(passwordCheck);

		// 비밀번호 확인 입력창
		passwordCheckField = new JPasswordField(10);
		passwordCheckField.setBounds(280, 355, 140, 27);
		clearTextField(passwordCheckField);
		add(passwordCheckField);

		// 회원가입 버튼
		RoundedButton signupButton = new RoundedButton(Color.WHITE, 10, "Sign up", new Color(255, 172, 62),
				new Font("Comic Sans Ms", Font.BOLD, 26));
		signupButton.setBounds(180, 400, 130, 39);
		signupButton.addActionListener(e -> {
			String id = idField.getText();
			String password = new String(passwordField.getPassword());
			String passwordCheck = new String(passwordCheckField.getPassword());

			if (id.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
				showCustomMessage("모든 필드를 입력해주세요.");
				return;
			}

			if (!password.equals(passwordCheck)) {
				showCustomMessage("비밀번호가 일치하지 않습니다.");
				return;
			}

			if (Login.register(id, password)) {
				showCustomMessage("회원가입이 완료되었습니다.");
				mainFrame.showScreen("LoginScreen");
			} else {
				showCustomMessage("이미 존재하는 아이디입니다.");
			}
			;
		});
		add(signupButton);
	}

	// 투명 텍스트 필드 설정
	private void clearTextField(JTextField textField) {
		textField.setOpaque(false); // 배경 투명
		textField.setForeground(Color.WHITE); // 텍스트 색상 하양
		textField.setCaretColor(Color.WHITE); // 커서 색상 하양
		textField.setFont(new Font("Comic Sans MS", Font.BOLD, 20)); // 글꼴과 크기 설정
		textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 밑줄만 표시

		// 포커스 효과
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.setBorder(null); // 기존 테두리 제거
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE)); // 포커스 시 밑줄 굵게
			}

			@Override
			public void focusLost(FocusEvent e) {
				textField.setBorder(null); // 기존 테두리 제거
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 포커스 잃으면 밑줄 얇게
			}
		});
	}

	// 커스텀 메시지 창
	private void showCustomMessage(String message) {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "알림", true);
		dialog.setLayout(null);
		dialog.setSize(320, 140);
		dialog.setLocationRelativeTo(this); // 화면 중앙에 표시

		// 메시지 라벨 배경 패널
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(new Color(255, 230, 204));
		messagePanel.setBounds(0, 0, 500, 300);
		messagePanel.setLayout(null);

		// 메시지 라벨
		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(17, 13, 280, 50);
		dialog.add(messageLabel);

		// 확인 버튼
		RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(115, 65, 80, 30);
		confirmButton.addActionListener(e -> dialog.dispose());
		dialog.add(confirmButton);

		dialog.add(messagePanel);
		dialog.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// 배경 이미지 그리기
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	@Override
	public void refreshData() {
	}
}