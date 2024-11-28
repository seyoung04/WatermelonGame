//Fruit.java
package watermelonGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import screen.SkinShopScreen;

public class Fruit {
    private FruitList type;

    // 포도, 귤, 사과, 오렌지, 복숭아, 메론, 수박
    public enum FruitList {
        GRAPE(Color.decode("#C93BAA"), 50, "grape.png"),
        TANGERINE(Color.decode("#FF9808"), 70, "tangerine.png"),
        APPLE(Color.decode("#F83232"), 100, "apple.png"),
        ORANGE(Color.decode("#FFEE00"), 130, "orange.png"),
        PEACH(Color.decode("#FF98A9"), 160, "peach.png"),
        MELON(Color.decode("#9BFF98"), 190, "melon.png"),
        WATERMELON(Color.decode("#00A305"), 210, "watermelon.png");

        private Color color;
        private int size;
        private String imageName;
        private Image image;

        FruitList(Color color, int size, String imageName) {
            this.color = color;
            this.size = size;
            this.imageName = imageName;
            loadImage(); // 이미지 초기 로드
        }

        public void loadImage() {
            String skinPath = "WatermelonGame-physicsEngine/src/image/" + SkinShopScreen.getAppliedSkin() + "/"; // 스킨 경로 반영
            try {
                this.image = ImageIO.read(new File(skinPath + imageName));
            } catch (IOException e) {
                e.printStackTrace();
                this.image = null; // 로드 실패 시 null로 설정
            }
        }

        public Color getColor() {
            return color;
        }

        public int getSize() {
            return size;
        }

        public Image getImage() {
            return image;
        }

        public FruitList next() {
            FruitList[] fruits = FruitList.values();
            int nextIndex = (this.ordinal() + 1) % fruits.length;
            return fruits[nextIndex];
        }
    }

    public Fruit() {
        this.type = getRandomFruitType();
        if (this.type == null) {
            this.type = FruitList.GRAPE;
        }
    }

    // 랜덤 과일 타입 선택
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

    // 미리보기 그리기
    public void drawPreview(Graphics g, int x, int y) {
        Image image = type.getImage();
        int size = type.getSize();
        if (image != null) {
            g.drawImage(image, x, y, size, size, null);
        } else {
            g.setColor(type.getColor());
            g.fillOval(x, y, size, size);
        }
    }

    public FruitList getType() {
        return this.type;
    }
    public static FruitList getType(Falling falling) {
        return falling.getType(); // Falling 객체에서 타입 반환
    }

    public static void refreshImages() {
        for (FruitList fruit : FruitList.values()) {
            fruit.loadImage(); // 현재 스킨에 맞게 이미지 새로고침
        }
    }
}


