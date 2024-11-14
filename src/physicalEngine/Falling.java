//Falling.java
package physicalEngine;

import java.awt.Graphics;
import java.util.List;

public class Falling {
	double x;
	double y;
	double velocityX = 0;
	double velocityY = 0;
	int diameter = 100;
	private Falling supportingFruit = null;
	Fruit.FruitList type;
	boolean isMarkedForDeletion = false;

	private static final double GRAVITY = 0.4;
	private static final double FRICTION = 0.98;
	private static final double STABILITY_THRESHOLD = 0.2;
	private static final double STACKING_THRESHOLD = 2.0;
	private static final double NATURAL_FORCE = 5;
	private static final int PANEL_WIDTH = 400;
	private static final int PANEL_HEIGHT = 600;
	private static final double ENERGY_LOSS = 0.5;

	public Falling(int startX) {
		this.x = startX;
		this.y = 0;
		this.type = new Fruit().getRandomFruitType();
		this.diameter = type.getSize();
	}

	public void update(List<Falling> fruits) {
		if (isMarkedForDeletion) {
			return;
		}

		double prevX = x;
		double prevY = y;

		velocityY += GRAVITY;

		for (int i = 0; i < 3; i++) {
			x += velocityX / 3;
			y += velocityY / 3;
			Collision collision = new Collision();
			collision.handleCollisions(fruits, this, prevX, prevY);
		}

		handleWallCollision();
		checkStability(fruits);

		velocityX *= FRICTION;

		if (isStable() && supportingFruit != null) {
			naturalRoll();
		}

		preventPerfectStack(fruits);
		ensureInBounds();
	}

	private void ensureInBounds() {
		x = Math.max(0, Math.min(x, PANEL_WIDTH - diameter));
		y = Math.max(0, Math.min(y, PANEL_HEIGHT - diameter));
	}

	private boolean isStacked(Falling otherF) {
		double verticalDist = (y + diameter / 2) - (otherF.y + otherF.diameter / 2);
		return verticalDist > 0 && verticalDist < STACKING_THRESHOLD;
	}

	private void naturalRoll() {
		if (supportingFruit != null) {
			double dx = x - supportingFruit.x;
			velocityX += Math.signum(dx) * NATURAL_FORCE * (Math.abs(dx) / (diameter * 0.5));
		}
	}

	private void preventPerfectStack(List<Falling> fruits) {
		for (Falling otherF : fruits) {
			if (otherF != this && !otherF.isMarkedForDeletion && Math.abs(x - otherF.x) < diameter * 0.1) {
				if (isStacked(otherF)) {
					velocityX += (x > otherF.x ? 1 : -1) * NATURAL_FORCE;
				}
			}
		}
	}

	private void handleWallCollision() {
		if (y + diameter > PANEL_HEIGHT) {
			y = PANEL_HEIGHT - diameter;
			velocityY = -velocityY * ENERGY_LOSS;
			velocityX *= FRICTION;
		}
		if (x < 0) {
			x = 0;
			velocityX = -velocityX * ENERGY_LOSS;
		} else if (x + diameter > PANEL_WIDTH) {
			x = PANEL_WIDTH - diameter;
			velocityX = -velocityX * ENERGY_LOSS;
		}
	}

	private void checkStability(List<Falling> fruits) {
		boolean isStable = false;
		supportingFruit = null;

		if (y + diameter / 2 >= PANEL_HEIGHT) {
			isStable = true;
		} else {
			for (Falling otherF : fruits) {
				Collision collision = new Collision();
				if (otherF != this && !otherF.isMarkedForDeletion && collision.isColliding(this, otherF)) {
					if (isStacked(otherF)) {
						isStable = Math.abs(velocityX) < STABILITY_THRESHOLD
								&& Math.abs(velocityY) < STABILITY_THRESHOLD;
						supportingFruit = otherF;
						if (isStable) {
							velocityY *= 0.5;
						}
						break;
					}
				}
			}
		}
	}

	public void draw(Graphics g) {
		if (!isMarkedForDeletion) {
			g.setColor(type.getColor());
			g.fillOval((int) x, (int) y, diameter, diameter);
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
}