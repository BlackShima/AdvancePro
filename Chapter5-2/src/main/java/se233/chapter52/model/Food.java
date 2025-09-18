package se233.chapter52.model;

import javafx.geometry.Point2D;
import se233.chapter52.view.GameStage;

import java.util.Random;

public class Food {
    private Point2D position;
    private Random rn;
    private boolean special;

    public Food(Point2D position) {
        this.rn = new Random();
        this.position = position;
        this.special = false;
    }
    public Food() {
        this.rn = new Random();
        this.position = new Point2D(rn.nextInt(GameStage.WIDTH), rn.nextInt(GameStage.HEIGHT));
        respawn();
    }
    public void respawn() {
        Point2D prev_position = this.position;
        do {
            this.position = new Point2D(rn.nextInt(GameStage.WIDTH), rn.nextInt(GameStage.HEIGHT));
        } while (prev_position == this.position);
        this.special = rn.nextInt(5) == 0; // สุ่มเลข 0-4 ถ้าได้ 0 (โอกาส 1/5) จะเป็น true
    }
    public Point2D getPosition() {
        return position;
    }
    public boolean isSpecial() {
        return special;
    }
}
