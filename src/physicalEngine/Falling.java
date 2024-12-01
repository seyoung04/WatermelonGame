//Falling.java
package physicalEngine;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import screen.GameScreen;

public class Falling {
	double x, y; // 과일의 현재 위치
	double velocityX, velocityY; // 과일의 속도
	int diameter = 100; // 과일의 크기
	private Image fruitImage;
	Fruit.FruitList type;
	private Falling supportingFruit = null; // 과일이 쌓여있을 경우, 지지하는 과일
	boolean isMarkedForDeletion = false; // 삭제 예정 여부
	boolean isMerged = false; // 병합 여부

	private GameScreen gameScreen; // GameScreen 참조

	private static final double GRAVITY = 0.4; // 중력
	private static final double FRICTION = 0.98; // 마찰력
	private static final double ENERGY_LOSS = 0.5; // 에너지 손실률(반사)
	private static final int WALL_LEFT = 30;
	private static final int WALL_RIGHT = 415;
	private static final int WALL_TOP = 0;
	private static final int WALL_BOTTOM = 723;

	public Falling(int x, Image fruitImage, Fruit.FruitList type) {
		this.x = x;
		switch (type) { // y좌표 설정
		case GRAPE:
			this.y = 200;
			break;
		case TANGERINE:
			this.y = 180;
			break;
		default:
			this.y = 120;
			break;
		}
		this.fruitImage = fruitImage;
		this.type = type;
		this.diameter = type.getSize();
		// this.gameScreen = gameScreen;
	}

	// 상태 업데이트 (중력과 속도를 이용해 이동 및 충돌 처리)
	public void update(List<Falling> fruits) {
		if (isMarkedForDeletion) {
			return;
		}

		double prevX = x;
		double prevY = y;

		velocityY += GRAVITY; // 중력 추가

		for (int i = 0; i < 5; i++) { // 정밀 충돌 감지(이동을 5회 나눠서 처리)
			x += velocityX / 5;
			y += velocityY / 5;
			Collision collision = new Collision();
			collision.handleCollisions(fruits, this, prevX, prevY); // 충돌 처리
			handleWallCollision(); // 벽 충돌 처리
			ensureInBounds(); // 경계 내부 유지
		}

		velocityX *= FRICTION; // 마찰 적용
	}

	// 과일 병합 처리
	public boolean mergeWith(Falling other) {
		if (this.type == other.type) { // 과일이 같다면
			this.type = this.type.next(); // 다음 단계 과일로 변경
			this.diameter = this.type.getSize(); // 크기 변경
			this.fruitImage = this.type.getImage(); // 이미지 변경

			// GameScreen에서 점수와 코인 업데이트
			if (gameScreen != null) {
				gameScreen.updateScoreAndCoins(this.type.toString().toLowerCase());
			}
			return true; // 병합 성공
		}
		return false; // 병합 실패
	}

	// 벽 충돌 처리
	private void handleWallCollision() {
		// 바닥과 충돌
		if (y + diameter > WALL_BOTTOM) {
			y = WALL_BOTTOM - diameter;
			velocityY = -velocityY * ENERGY_LOSS; // 속도 반전 및 에너지 손실
			velocityX *= FRICTION; // 마찰 적용
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

	// 경계 내부 유지 (과일이 벽을 넘지 않도록)
	private void ensureInBounds() {
		x = Math.max(WALL_LEFT, Math.min(x, WALL_RIGHT - diameter));
		y = Math.max(WALL_TOP, Math.min(y, WALL_BOTTOM - diameter));
	}

	// 과일 이미지 그리기
	public void draw(Graphics g) {
		if (!isMarkedForDeletion) { // 삭제 되지 않았다면
			int fruitSize = type.getSize(); // 과일 크기 계산
			g.drawImage(fruitImage, (int) x, (int) y, fruitSize, fruitSize, null); // 이미지 그리기
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

	public GameScreen getGameScreen() {
		return gameScreen;
	}

	public boolean isStable() {
		return supportingFruit != null;
	}

	public void markForDeletion() { // 과일 삭제 대상 표시
		isMarkedForDeletion = true;
	}

	public boolean isMarkedForDeletion() {
		return isMarkedForDeletion;
	}

	public boolean isMerged() {
		return isMerged;
	}

	public void setFruitImage(Image fruitImage) {
		this.fruitImage = fruitImage;
	}

	public Image getFruitImage() {
		return this.fruitImage;
	}

	public Fruit.FruitList getType() {
		return this.type;
	}
}