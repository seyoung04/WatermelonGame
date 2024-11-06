package watermelonGame;

import java.util.List;

public class Collision {
	private static final double COLLISION_THRESHOLD = 1.0;
	private static final double ENERGY_LOSS = 0.5;

	public void handleCollisions(List<Falling> fruits, Falling fallingInstance, double prevX, double prevY) {
		boolean hasCollision = true;

		while (hasCollision) {
			hasCollision = false;

			for (Falling otherF : fruits) {
				if (otherF != fallingInstance && !otherF.isMarkedForDeletion && !fallingInstance.isMarkedForDeletion) {
					double overlap = getOverlap(fallingInstance, otherF);

					if (overlap > COLLISION_THRESHOLD) {
						if (fallingInstance.type == otherF.type) {
							mergeFruits(fallingInstance, otherF, fruits);
							if (fallingInstance.isMarkedForDeletion) {
								return;
							}
						}
						resolveCollision(fallingInstance, otherF, prevX, prevY);
					}
				}
			}
		}
	}

	boolean isColliding(Falling fallingInstance, Falling otherF) {
		double dx = fallingInstance.x - otherF.x;
		double dy = fallingInstance.y - otherF.y;
		double minDistance = (fallingInstance.diameter + otherF.diameter) / 2.0;
		return Math.sqrt(dx * dx + dy * dy) < minDistance * 0.99;
	}

	private void resolveCollision(Falling fallingInstance, Falling otherF, double prevX, double prevY) {
		double centerX = fallingInstance.getCenterX();
		double centerY = fallingInstance.getCenterY();
		double otherCenterX = otherF.getCenterX();
		double otherCenterY = otherF.getCenterY();

		double dx = centerX - otherCenterX;
		double dy = centerY - otherCenterY;
		double distance = Math.sqrt(dx * dx + dy * dy);

		if (distance < 0.1) {
			distance = 0.1;
			dx = 0.1;
			dy = 0.1;
		}

		double nx = dx / distance;
		double ny = dy / distance;
		double minDistance = (fallingInstance.getDiameter() + otherF.getDiameter()) / 2.0;
		double overlap = minDistance - distance;

		if (overlap > 0) {
			double separationFactor = 1.05;
			double moveX = nx * (overlap * separationFactor);
			double moveY = ny * (overlap * separationFactor);

			double massRatio = (fallingInstance.getDiameter() * fallingInstance.getDiameter())
					/ (double) (otherF.diameter * otherF.diameter);
			double totalMass = massRatio + 1;

			fallingInstance.x = prevX + moveX * (1 / totalMass);
			fallingInstance.y = prevY + moveY * (1 / totalMass);
			otherF.x -= moveX * (massRatio / totalMass);
			otherF.y -= moveY * (massRatio / totalMass);

			double relativeVX = fallingInstance.velocityX - otherF.velocityX;
			double relativeVY = fallingInstance.velocityY - otherF.velocityY;
			double normalVelocity = relativeVX * nx + relativeVY * ny;

			if (normalVelocity > 0) {
				return;
			}

			double restitution = ENERGY_LOSS;
			double impulse = -(1 + restitution) * normalVelocity;
			impulse /= totalMass;

			fallingInstance.velocityX += nx * impulse;
			fallingInstance.velocityY += ny * impulse;
			otherF.velocityX -= nx * impulse * massRatio;
			otherF.velocityY -= ny * impulse * massRatio;
		}
	}

	private void mergeFruits(Falling fallingInstance, Falling otherF, List<Falling> fruits) {
		if (fallingInstance.isMarkedForDeletion || otherF.isMarkedForDeletion) {
			return;
		}

		double newX = (fallingInstance.x + otherF.x) / 2;
		double newY = (fallingInstance.y + otherF.y) / 2;

		if (fallingInstance.type.next() != null) {
			fallingInstance.type = fallingInstance.type.next();
			fallingInstance.diameter = fallingInstance.type.getSize();
			fallingInstance.x = newX;
			fallingInstance.y = newY;

			otherF.markForDeletion();
		} else {
			otherF.markForDeletion();
		}
	}

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
