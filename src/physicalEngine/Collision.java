//Collision.java
package physicalEngine;

import java.util.List;

import screen.GameScreen;

public class Collision {
	private static final double COLLISION_THRESHOLD = 1.0;// 충돌기준
	private static final double ENERGY_LOSS = 0.3;

	// 충돌을 어떻게 처리할 것인지
	public void handleCollisions(List<Falling> fruits, Falling fallingInstance, double prevX, double prevY) {
		boolean hasCollision = true;

		while (hasCollision) {
			hasCollision = false;

			for (Falling otherF : fruits) {
				if (otherF != fallingInstance && !otherF.isMarkedForDeletion()
						&& !fallingInstance.isMarkedForDeletion()) {
					double overlap = getOverlap(fallingInstance, otherF);

					if (overlap > COLLISION_THRESHOLD) {
						if (fallingInstance.type == otherF.type) {
							GameScreen gameScreen = fallingInstance.getGameScreen();
							mergeFruits(fallingInstance, otherF, fruits);

							if (gameScreen != null) {
								gameScreen.updateScoreAndCoins(fallingInstance.getType().toString().toLowerCase());
							}

							if (fallingInstance.isMarkedForDeletion()) {
								return;
							}
						}

						resolveCollision(fallingInstance, otherF, prevX, prevY);
					}
				}
			}
		}
	}

	// 두 과일이 충돌중인지를 반환
	boolean isColliding(Falling fallingInstance, Falling otherF) {
		double dx = fallingInstance.x - otherF.x;
		double dy = fallingInstance.y - otherF.y;
		double minDistance = (fallingInstance.diameter + otherF.diameter) / 2.0;
		return Math.sqrt(dx * dx + dy * dy) < minDistance;
	}

	// 충돌처리
	private void resolveCollision(Falling fallingInstance, Falling otherF, double prevX, double prevY) {
		double centerX = fallingInstance.getCenterX();
		double centerY = fallingInstance.getCenterY();
		double otherCenterX = otherF.getCenterX();
		double otherCenterY = otherF.getCenterY();

		double dx = centerX - otherCenterX;
		double dy = centerY - otherCenterY;
		double distance = Math.sqrt(dx * dx + dy * dy);

		// 0이 되지 않도록
		if (distance < 0.1) {
			distance = 0.1;
			dx = 0.1;
			dy = 0.1;
		}

		double nx = dx / distance;
		double ny = dy / distance;
		double minDistance = (fallingInstance.getDiameter() + otherF.getDiameter()) / 2.0;
		double overlap = minDistance - distance;

		// 만약 겹쳐있으면 충돌한 과일이 서로 반대로 이동
		if (overlap > 0) {
			double separationFactor = 1.02;
			double moveX = nx * (overlap * separationFactor);
			double moveY = ny * (overlap * separationFactor);

			// 과일의 크기에 비례해 질량 설정
			double sizeRatio = (fallingInstance.getDiameter() * fallingInstance.getDiameter())
					/ (double) (otherF.diameter * otherF.diameter);
			double totalMass = sizeRatio * 0.5 + 1;

			// 질량이 크면 덜 움직임
			fallingInstance.x = prevX + moveX * (1 / totalMass);
			fallingInstance.y = prevY + moveY * (1 / totalMass);
			otherF.x -= moveX * (1 / totalMass);
			otherF.y -= moveY * (1 / totalMass);

			double relativeVX = fallingInstance.velocityX - otherF.velocityX;
			double relativeVY = fallingInstance.velocityY - otherF.velocityY;
			double approachSpeed = relativeVX * nx + relativeVY * ny;

			// 양수이면 서로 멀어지는 것이므로 종료
			if (approachSpeed > 0) {
				return;
			}

			// 충돌했을 때 반대로 튕기도록
			double impulse = -(1 + ENERGY_LOSS) * approachSpeed;
			impulse /= totalMass;

			fallingInstance.velocityX += nx * impulse;
			fallingInstance.velocityY += ny * impulse;
			otherF.velocityX -= nx * impulse * sizeRatio;
			otherF.velocityY -= ny * impulse * sizeRatio;

			double collisionImpact = Math.sqrt(relativeVX * relativeVX + relativeVY * relativeVY);
			double torque = collisionImpact / Math.max(fallingInstance.diameter, otherF.diameter);

			fallingInstance.angularVelocity += torque * (dy > 0 ? 1 : -1); // 충돌 방향에 따라 회전
			otherF.angularVelocity -= torque * (dy > 0 ? 1 : -1);
		}
	}

	private void mergeFruits(Falling fallingInstance, Falling otherF, List<Falling> fruits) {
		if (fallingInstance.isMarkedForDeletion || otherF.isMarkedForDeletion) {
			return;
		}

		if (fallingInstance.type == otherF.type) {
			double newX = (fallingInstance.getX() + otherF.getX()) / 2;
			double newY = (fallingInstance.getY() + otherF.getY()) / 2;

			if (fallingInstance.type.next() != null) {
				Fruit.FruitList newType = fallingInstance.type.next();
				fallingInstance.type = newType;
				fallingInstance.diameter = newType.getSize();
				fallingInstance.setFruitImage(newType.getImage());
				fallingInstance.x = newX;
				fallingInstance.y = newY;

				otherF.markForDeletion();

				// 점수와 코인 업데이트
				GameScreen gameScreen = fallingInstance.getGameScreen();
				if (gameScreen != null) {
					String fruitType = fallingInstance.type.toString().toLowerCase();
					gameScreen.updateScoreAndCoins(fruitType);
				}

				fallingInstance.angularVelocity = 0; // 병합 후 회전 속도 초기화
				fallingInstance.rotationAngle = 0; // 병합 후 회전 각도 초기화
			}
		}
	}

	// 겹친 정도 구하기
	private double getOverlap(Falling otherF, Falling fallingInstance) {
		double centerX = fallingInstance.getCenterX();
		double centerY = fallingInstance.getCenterY();
		double otherCenterX = otherF.getCenterX();
		double otherCenterY = otherF.getCenterY();

		double dx = centerX - otherCenterX;
		double dy = centerY - otherCenterY;
		double distance = Math.sqrt(dx * dx + dy * dy);

		double minDistance = (fallingInstance.diameter + otherF.diameter) / 2.0;
		return minDistance - distance;
	}
}