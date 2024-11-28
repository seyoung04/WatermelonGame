package watermelonGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import screen.GameScreen;

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
    boolean isMerged = false; 
    double angularVelocity = 5; // 회전 속도
    double rotationAngle = 0;  // 현재 회전 각도
    


    private GameScreen gameScreen; // GameScreen 참조 추가

    private static final double GRAVITY = 0.4;
    private static final double FRICTION = 0.98;
    private static final int WALL_LEFT = 30;
    private static final int WALL_RIGHT = 415;
    private static final int WALL_TOP = 0;
    private static final int WALL_BOTTOM = 723;
    private static final double ENERGY_LOSS = 0.5;

    public Falling(int x, Image fruitImage, Fruit.FruitList type, GameScreen gameScreen) {
        this.x = x;
        this.y = 150;
        this.fruitImage = fruitImage;
        this.type = type;
        this.diameter = type.getSize();
        this.gameScreen = gameScreen; // GameScreen 참조 저장
    }
    
    public GameScreen getGameScreen() {
        return gameScreen;
    }


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

            // 회전 각도 업데이트
            rotationAngle += angularVelocity / 5;
            rotationAngle %= 360; // 각도는 360도를 초과하지 않도록 제한
        }

        velocityX *= FRICTION;
        angularVelocity *= FRICTION/5; // 마찰로 회전 속도 감소
    }

    public boolean mergeWith(Falling other) {
        if (this.type == other.type) {
            this.type = this.type.next(); // 다음 단계 과일로 변경
            this.diameter = this.type.getSize(); // 크기 변경
            this.fruitImage = this.type.getImage(); // 이미지 변경

            // GameScreen에서 점수와 코인 업데이트
            if (gameScreen != null) {
                gameScreen.updateScoreAndCoins(this.type.toString().toLowerCase());
            }
            return true;
        }
        return false;
    }

    private void ensureInBounds() {
        x = Math.max(WALL_LEFT, Math.min(x, WALL_RIGHT - diameter));
        y = Math.max(WALL_TOP, Math.min(y, WALL_BOTTOM - diameter));
    }

    private void handleWallCollision() {
        if (y + diameter > WALL_BOTTOM) {
            y = WALL_BOTTOM - diameter;
            velocityY = -velocityY * ENERGY_LOSS;
            velocityX *= FRICTION;
        }
        if (x < WALL_LEFT) {
            x = WALL_LEFT;
            velocityX = -velocityX * ENERGY_LOSS;
        } else if (x + diameter > WALL_RIGHT) {
            x = WALL_RIGHT - diameter;
            velocityX = -velocityX * ENERGY_LOSS;
        }
    }

    public void draw(Graphics g) {
        if (!isMarkedForDeletion) {
            if (fruitImage != null) {
                int fruitSize = type.getSize();

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.rotate(Math.toRadians(rotationAngle), x + fruitSize / 2, y + fruitSize / 2);
                g2d.drawImage(fruitImage, (int) x, (int) y, fruitSize, fruitSize, null);
                g2d.dispose();
            }
        }
    }

    public boolean isMoving() {
		return Math.abs(velocityY) > 1;
	}
    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }
    public double getVelocityY() {
		return velocityY;
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
