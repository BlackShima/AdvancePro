package se233.chapter6.model;

import javafx.geometry.Point2D;
import se233.chapter6.view.GameStage;

import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Food {
    private static final Logger logger = LogManager.getLogger(Food.class);
    private Point2D position;
    private Random rn;
    private boolean special;

    public Food(Point2D position) {
        this.rn = new Random();
        this.position = position;
        this.special = false;
        respawn();
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
        } while (prev_position.equals(this.position));
        this.special = rn.nextInt(5) == 0; // สุ่มเลข 0-4 ถ้าได้ 0 (โอกาส 1/5) จะเป็น true
        System.out.println("DEBUG: respawn() method was called!");
        logger.info("food respawned at: x:{}, y:{}", this.position.getX(), this.position.getY());
    }
    public Point2D getPosition() {
        return position;
    }
    public boolean isSpecial() {
        return special;
    }
}
