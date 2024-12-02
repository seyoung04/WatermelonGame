package screen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import database.Database;
import database.Login;
import database.SessionManager;

public class LoginScreen extends JPanel   implements RefreshableScreen {
	private BufferedImage backgroundImage; // 배경 이미지
	private MainFrame mainFrame;
	private JLabel id; // 아이디 라벨
	private JLabel password; // 비밀번호 라벨
	private JTextField idField; // 아이디 입력창
	private JPasswordField passwordField; // 비밀번호 입력창


	public LoginScreen(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);
		setBounds(0, 0, 500, 750);


		/// 배경 이미지 설정
				try {
					backgroundImage = ImageIO.read(new File("src/image/loginScreen.png"));
				} catch (IOException e) {
					e.printStackTrace(); // 이미지 로드 실패 시 에러 메시지 출력
				}

				// 아이디 라벨
				id = new JLabel("ID");
				id.setBounds(150, 310, 140, 30);
				id.setFont(new Font("Comic Sans Ms", Font.BOLD, 25));
				id.setForeground(new Color(181, 112, 0));
				add(id);

				// 아이디 입력창
				idField = new JTextField(20);
				idField.setBounds(240, 310, 140, 30);
				clearTextField(idField);
				add(idField);

				// 비밀번호 라벨
				password = new JLabel("Password");
				password.setBounds(110, 345, 140, 30);
				password.setFont(new Font("Comic Sans Ms", Font.BOLD, 25));
				password.setForeground(new Color(181, 112, 0));
				add(password);

				// 비밀번호 입력창
				passwordField = new JPasswordField(20);
				passwordField.setBounds(240, 345, 140, 30);
				clearTextField(passwordField);
				add(passwordField);

				RoundedButton loginButton = new RoundedButton(new Color(255, 172, 62), 10, "Login", Color.WHITE,
						new Font("Comic Sans Ms", Font.BOLD, 20));
				loginButton.setBounds(145, 387, 83, 29);

				// 로그인 버튼 이벤트
				passwordField.addActionListener(e -> login());

				loginButton.addActionListener(e -> {
				    String id = idField.getText();
				    String password = new String(passwordField.getPassword());

				    if (password.isEmpty() || id.isEmpty()) {
				        JOptionPane.showMessageDialog(null, "모든 필드를 입력해주세요.");
				        return;
				    }

				    if (Login.login(id, password)) {
				        JOptionPane.showMessageDialog(null, "로그인 성공!");
				        int userId = SessionManager.getInstance().getUserId(); 
				        mainFrame.loginSuccess(userId); 
				        mainFrame.showScreen("HomeScreen");
				    } else {
				        JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 일치하지 않습니다.");
				    }
				});
				add(loginButton);

				RoundedButton signupButton = new RoundedButton(Color.WHITE, 10, "Sign up", new Color(255, 172, 62),
						new Font("Comic Sans Ms", Font.BOLD, 20));
				signupButton.setBounds(250, 387, 104, 29);
				signupButton.addActionListener(e -> {
					mainFrame.showScreen("SignUpScreen");
				});
				// 회원가입 버튼 이벤트
			
				add(signupButton);
			}
    private void login() {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());

        if (id.isEmpty() || password.isEmpty()) {
            showCustomMessage("모든 필드를 입력해주세요.");
            return;
        }

        if (Login.login(id, password)) {
            showCustomMessage("로그인 성공!");
            int userId = SessionManager.getInstance().getUserId(); 
            mainFrame.loginSuccess(userId); 
            mainFrame.showScreen("HomeScreen");
        } else {
            showCustomMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }


	// 투명 텍스트 필드 설정
	private void clearTextField(JTextField textField) {
		textField.setOpaque(false); // 배경 투명
		textField.setForeground(Color.WHITE); // 텍스트 색상 하양
		textField.setCaretColor(Color.WHITE); // 커서 색상 하양
		textField.setFont(new Font("Comic Sans Ms", Font.BOLD, 21)); // 글꼴과 크기 설정
		textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 밑줄만 표시

		// 포커스 효과
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE)); // 포커스 시 밑줄 굵게
			}

			@Override
			public void focusLost(FocusEvent e) {
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.WHITE)); // 포커스 잃으면 밑줄 얇게
			}
		});
	}
	private void showCustomMessage(String message) {
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "알림", true);
		dialog.setLayout(null);
		dialog.setSize(410, 125);
		dialog.setLocationRelativeTo(this); // 화면 중앙에 표시

		// 메시지 라벨 배경 패널
		JPanel messagePanel = new JPanel();
		messagePanel.setBackground(new Color(255, 230, 204));
		messagePanel.setBounds(0, 0, 500, 300);
		messagePanel.setLayout(null);

		// 메시지 라벨
		JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
		messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		messageLabel.setForeground(Color.BLACK);
		messageLabel.setBounds(5, 0, 390, 50);
		dialog.add(messageLabel);

		// 확인 버튼
		RoundedButton confirmButton = new RoundedButton(new Color(255, 172, 62), 10, "확인", Color.WHITE,
				new Font("Malgun Gothic", Font.BOLD, 15));
		confirmButton.setBounds(160, 50, 80, 30);
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

	
	public void refreshData() {
	}
	@Override
	public void refreshUI() {		
	}
	
}