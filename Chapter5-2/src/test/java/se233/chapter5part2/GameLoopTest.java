package se233.chapter5part2;

//import com.sun.javafx.scene.traversal.Direction;
import se233.chapter52.model.Direction;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import se233.chapter52.controller.GameLoop;
import se233.chapter52.model.Food;
import se233.chapter52.model.Snake;
import se233.chapter52.view.GameStage;

import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class GameLoopTest {
     private GameStage gameStage;
     private Snake snake;
     private Food food;
     private GameLoop gameLoop;

     @BeforeEach
     public void setUp(){
         gameStage =new GameStage();
         snake= new Snake(new Point2D(0,0));
         food =new Food(new Point2D(0,1));
         gameLoop =new GameLoop(gameStage, snake, food);
         }
     private void clockTickHelper() throws Exception {
         ReflectionHelper.invokeMethod(gameLoop,"keyProcess",new Class<?>[0]);
         ReflectionHelper.invokeMethod(gameLoop,"checkCollision",new Class<?>[0]);
         ReflectionHelper.invokeMethod(gameLoop,"redraw", new Class<?>[0]);
         }
     @Test
     public void keyProcess_pressRight_snakeTurnRight() throws Exception {
         gameStage.addKey(KeyCode.RIGHT);
         ReflectionHelper.setField(snake, "direction", Direction.DOWN);
         clockTickHelper();
         Direction currentDirection = (Direction) ReflectionHelper.getField(snake, "direction");
         assertEquals(Direction.RIGHT, currentDirection);
     }
     @Test
     public void collided_snakeEatFood_shouldGrow()throws Exception {
         clockTickHelper();
         assertTrue(snake.getLength()>1);
         clockTickHelper();
         assertNotEquals(new Point2D(0,1), food.getPosition());
         }
     @Test
     public void collided_snakeHitBorder_shouldDie()throws Exception{
         gameStage.addKey(KeyCode.LEFT);
         clockTickHelper();
         Boolean running=(Boolean) ReflectionHelper.getField(gameLoop, "running");
         assertFalse(running);

    }
    @Test
    public void redraw_calledThreeTimes_snakeAndFoodShouldRenderThreeTimes() throws Exception {
        GameStage mockGameStage = Mockito.mock(GameStage.class);
        Snake mockSnake = Mockito.mock(Snake.class);
        Food mockFood = Mockito.mock(Food.class);
        GameLoop gameLoop = new GameLoop(mockGameStage, mockSnake, mockFood);
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[0]);
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[0]);
        ReflectionHelper.invokeMethod(gameLoop, "redraw", new Class<?>[0]);
        verify(mockGameStage, Mockito.times(3)).render(mockSnake, mockFood);
    }
    @Test
    public void snakeEatsFood_scoreShouldIncreaseByOne() throws Exception {
        // Arrange: ตรวจสอบว่าคะแนนเริ่มต้นเป็น 0
        // เราจะใช้ Reflection เพื่อดึงค่า score ที่ยังไม่มีอยู่จริงออกมา
        Integer initialScore = (Integer) ReflectionHelper.getField(gameLoop, "score");
        assertEquals(0, initialScore, "Initial score should be 0");

        // Act: รันเกม 1 tick (ซึ่งจะทำให้งูกินอาหารพอดี)
        clockTickHelper();

        // Assert: ตรวจสอบว่าคะแนนใหม่กลายเป็น 1
        Integer newScore = (Integer) ReflectionHelper.getField(gameLoop, "score");
        assertEquals(1, newScore, "Score should be 1 after eating food");
    }

    @Test
    public void snakeEatsSpecialFood_scoreShouldIncreaseByFive() throws Exception {
        ReflectionHelper.setField(food, "special", true);
        clockTickHelper();
        Integer score = (Integer) ReflectionHelper.getField(gameLoop, "score");
        assertEquals(5, score, "Score should be 5 after eating special food");
    }

    @Test
    public void keyProcess_whenMovingRight_shouldNotBeAbleToReverseToLeft() throws Exception {
        // Arrange: ตั้งค่างูให้ไปทางขวา และตั้งค่าการกดปุ่มเป็น "ซ้าย"
        ReflectionHelper.setField(snake, "direction", Direction.RIGHT);
        gameStage.addKey(KeyCode.LEFT);

        // Act: ประมวลผลการกดปุ่ม
        ReflectionHelper.invokeMethod(gameLoop, "keyProcess", new Class<?>[0]);

        // Assert: ทิศทางของงูจะต้องยังคงเป็น "ขวา" เหมือนเดิม
        Direction currentDirection = (Direction) ReflectionHelper.getField(snake, "direction");
        assertEquals(Direction.RIGHT, currentDirection, "Snake should not reverse direction from RIGHT to LEFT");
    }

    @Test
    public void keyProcess_whenMovingUp_shouldNotBeAbleToReverseToDown() throws Exception {

        ReflectionHelper.setField(snake, "direction", Direction.UP);
        gameStage.addKey(KeyCode.DOWN);

        // Act: ประมวลผลการกดปุ่ม
        ReflectionHelper.invokeMethod(gameLoop, "keyProcess", new Class<?>[0]);

        // Assert: ทิศทางของงูจะต้องยังคงเป็น "บน" เหมือนเดิม
        Direction currentDirection = (Direction) ReflectionHelper.getField(snake, "direction");
        assertEquals(Direction.UP, currentDirection, "Snake should not reverse direction from UP to DOWN");
    }
}

