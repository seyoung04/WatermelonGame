//GameData.java
package screen;

public class GameData {
	private static int coins = 3000;
	private static int highScore = 0;
	private static int bombNums = 3;
	private static int passNums = 3;

	// 코인 설정
	public static int getCoins() {
		return coins;
	}

	public static void setCoins(int i) {
		coins = i;
	}

	public static void addCoins(int i) {
		coins += i;
	}

	public static void subtractCoins(int i) {
		coins -= i;
	}

	// 최고기록 설정
	public static int getHighScore() {
		return highScore;
	}

	public static void setHighScore(int i) {
		highScore = i;
	}

	// 폭탄 아이템 보유개수 설정
	public static int getBombs() {
		return bombNums;
	}

	public static void setBombs(int i) {
		bombNums = i;
	}

	public static void addBombs(int i) {
		bombNums += i;
	}

	public static void subtractBombs(int i) {
		bombNums -= i;
	}

	// 패스 아이템 보유개수 설정
	public static int getPasses() {
		return passNums;
	}

	public static void setPasses(int i) {
		passNums = i;
	}

	public static void addPasses(int i) {
		passNums += i;
	}

	public static void subtractPasses(int i) {
		passNums -= i;
	}
}