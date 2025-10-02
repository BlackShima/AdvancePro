package se233.chapter6.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se233.chapter6.model.Food;
import se233.chapter6.model.Snake;

import java.util.LinkedList;
import java.util.Queue;

public class GameStage extends Pane {
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    public static final int TILE_SIZE = 10;
    private Canvas canvas;
    //private KeyCode key;
    private Queue<KeyCode> keyQueue;

    public GameStage() {
        this.setHeight(TILE_SIZE * HEIGHT);
        this.setWidth(TILE_SIZE * WIDTH);
        canvas = new Canvas(TILE_SIZE * WIDTH, TILE_SIZE * HEIGHT);
        this.getChildren().add(canvas);
        this.keyQueue = new LinkedList<>();
    }
    public void render(Snake snake, Food food) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,WIDTH*TILE_SIZE,HEIGHT*TILE_SIZE);
        gc.setFill(Color.BLUE);
        snake.getBody().forEach(p -> {
            gc.fillRect(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        });
        gc.setFill(food.isSpecial() ? Color.GREEN : Color.RED);
        gc.fillRect(food.getPosition().getX() * TILE_SIZE, food.getPosition().getY()
        * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }
    //public KeyCode getKey() { return key; }
    //public void setKey(KeyCode key) { this.key = key; }
    public void addKey(KeyCode key) {
        this.keyQueue.add(key);
    }
    public KeyCode pollKey() {
        return keyQueue.poll();
    }
}
