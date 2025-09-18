package se233.chapter52.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import se233.chapter52.model.Direction;
import se233.chapter52.model.Food;
import se233.chapter52.model.Snake;
import se233.chapter52.view.GameStage;

public class GameLoop implements Runnable {
    private GameStage gameStage;
    private Snake snake;
    private Food food;
    private float interval = 1000.0f / 10;
    private boolean running;
    private int score;

    public GameLoop(GameStage gameStage, Snake snake, Food food) {
        this.snake = snake;
        this.gameStage = gameStage;
        this.food = food;
        running = true;
        this.score = 0;
    }

    private void keyProcess() {
        // ดึง Key ออกจากคิวมาประมวลผลทีละตัวจนกว่าคิวจะว่าง
        KeyCode curKey;
        while ((curKey = gameStage.pollKey()) != null) {
            Direction curDirection = snake.getDirection();
            if (curKey == KeyCode.UP && curDirection != Direction.DOWN)
                snake.setDirection(Direction.UP);
            else if (curKey == KeyCode.DOWN && curDirection != Direction.UP)
                snake.setDirection(Direction.DOWN);
            else if (curKey == KeyCode.LEFT && curDirection != Direction.RIGHT)
                snake.setDirection(Direction.LEFT);
            else if (curKey == KeyCode.RIGHT && curDirection != Direction.LEFT)
                snake.setDirection(Direction.RIGHT);
        }
        // หลังจากประมวลผลคีย์ทั้งหมดแล้ว ค่อยสั่งให้งูเดิน 1 ครั้ง
        snake.move();
    }

    private void checkCollision() {
        if (snake.collided(food)) {
            snake.grow();
            if (food.isSpecial()) {
                score += 5; // อาหารพิเศษได้ 5 แต้ม
            } else {
                score++;    // อาหารธรรมดาได้ 1 แต้ม
            }
            food.respawn();

        }
        if (snake.checkDead()) {
            running = false;
        }
    }

    private void redraw() {
        gameStage.render(snake, food);
    }
    private void showGameOverDialog() {
        // Platform.runLater ใช้เพื่อรันโค้ดที่เกี่ยวกับ UI บน JavaFX Application Thread
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("You lost!");
            // คะแนนคือความยาวของงู - 1
            //int score = snake.getLength() - 1;
            alert.setContentText("Your score: " + score);
            alert.showAndWait();
        });
    }

    @Override
    public void run() {
        while (running) {
            keyProcess();
            checkCollision();
            if (running) {
                redraw();
            }
            try {
                Thread.sleep((long) interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        showGameOverDialog();
    }
}
