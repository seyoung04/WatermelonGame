//BaseScreen.java
package screen;

import javax.swing.JPanel;

public abstract class BaseScreen extends JPanel {
	public abstract void refreshData(); // 데이터 동기화 메서드
}