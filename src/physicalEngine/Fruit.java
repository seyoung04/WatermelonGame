//Fruit.java
package physicalEngine;

import java.awt.Color;
import java.util.Random;

//import watermelonGame.Fruit.FruitList;

public class Fruit {
	// 포도 귤 사과 오렌지 복숭아 메론 수박
	enum FruitList {
		GRAPE(Color.MAGENTA, 50), TANGERINE(Color.yellow, 70), APPLE(Color.red, 100), ORANGE(Color.orange, 130),
		PEACH(Color.pink, 160), MELON(Color.green, 190), WATERMELON(Color.black, 210);

		private Color color;
		private int size;

		FruitList(Color color, int size) {
			this.color = color;
			this.size = size;
		}

		public Color getColor() {
			return color;
		}

		public int getSize() {
			return size;
		}

		public FruitList next() {
			FruitList[] fruits = FruitList.values();
			int nextIndex = (this.ordinal() + 1) % fruits.length;
			return fruits[nextIndex];
		}
	}

	public FruitList getRandomFruitType() {
		Random random = new Random();
		int rand = random.nextInt(100);
		if (rand < 55) {
			return FruitList.GRAPE;
		} else if (rand < 85) {
			return FruitList.TANGERINE;
		} else if (rand < 95) {
			return FruitList.APPLE;
		} else {
			return FruitList.ORANGE;
		}
	}
}