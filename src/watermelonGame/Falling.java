package watermelonGame;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

public class Falling {
	double x;
	double y;
	private Image fruitImage;
	double velocityX = 0;
	double velocityY = 0;
	int diameter = 100;
	private Falling supportingFruit = null;
	Fruit.FruitList type;
	boolean isMarkedForDeletion = false;

	private static final double GRAVITY = 0.4;
	private static final double FRICTION = 0.98;
	private static final int WALL_LEFT = 30;
	private static final int WALL_RIGHT = 415;
	private static final int WALL_TOP = 0;
	private static final int WALL_BOTTOM = 695;
	private static final double ENERGY_LOSS = 0.5;

	public Falling(int x, Image fruitImage, Fruit.FruitList type) {
		this.x = x;
		this.y = 200;
		this.fruitImage = fruitImage;
		this.type = type;
		this.diameter = type.getSize();
	}

	// 상태 업데이트
	public void update(List<Falling> fruits) {
		if (isMarkedForDeletion) {
			return;
		}

		double prevX = x;
		double prevY = y;

		velocityY += GRAVITY;

		for (int i = 0; i < 5; i++) {
			x += velocityX / 5;
			y += velocityY / 5;
			Collision collision = new Collision();
			collision.handleCollisions(fruits, this, prevX, prevY);
			handleWallCollision();
			ensureInBounds();
		}

		velocityX *= FRICTION;
	}

	// 과일이 벽을 넘지 않도록
	private void ensureInBounds() {
		x = Math.max(WALL_LEFT, Math.min(x, WALL_RIGHT - diameter));
		y = Math.max(WALL_TOP, Math.min(y, WALL_BOTTOM - diameter));
	}

	// 벽과 충돌 관리
	private void handleWallCollision() {
		// 바닥과 충돌
		if (y + diameter > WALL_BOTTOM) {
			y = WALL_BOTTOM - diameter;
			velocityY = -velocityY * ENERGY_LOSS;
			velocityX *= FRICTION;
		}
		// 왼쪽 벽에 충돌
		if (x < WALL_LEFT) {
			x = WALL_LEFT;
			velocityX = -velocityX * ENERGY_LOSS;
		}
		// 오른쪽 벽에 충돌
		else if (x + diameter > WALL_RIGHT) {
			x = WALL_RIGHT - diameter;
			velocityX = -velocityX * ENERGY_LOSS;
		}

	}

	// 과일 이미지 그리기
	public void draw(Graphics g) {
		if (!isMarkedForDeletion) {
			if (fruitImage != null) {
				int fruitSize = type.getSize();
				g.drawImage(fruitImage, (int) x, (int) y, fruitSize, fruitSize, null);
			}
		}
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public double getCenterX() {
		return x + diameter / 2;
	}

	public double getCenterY() {
		return y + diameter / 2;
	}

	public int getDiameter() {
		return diameter;
	}

	public boolean isStable() {
		return supportingFruit != null;
	}

	public void markForDeletion() {
		isMarkedForDeletion = true;
	}

	public boolean isMarkedForDeletion() {
		return isMarkedForDeletion;
	}

	public void setFruitImage(Image fruitImage) {
		this.fruitImage = fruitImage;
	}

	public Image getFruitImage() {
		return this.fruitImage;
	}
}
