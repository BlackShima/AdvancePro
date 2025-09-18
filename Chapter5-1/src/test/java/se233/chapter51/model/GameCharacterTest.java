package se233.chapter51.model;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.chapter51.model.GameCharacter;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class GameCharacterTest {
    Field xVelocityField, yVelocityField, yAccelerationField;
    Field canJumpField, isJumpingField;
    Field isFallingField, isMoveRightField, xField, yField;

    private GameCharacter gameCharacter;
    private GameCharacter otherCharacter;

    @BeforeAll
    public static void initJfxRuntime() {
        javafx.application.Platform.startup(() -> {});
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException {
        gameCharacter = new GameCharacter(0, 30, 30, "assets/Character1.png", 4, 3, 2, 111, 97, KeyCode.A, KeyCode.D, KeyCode.W);
        xVelocityField = gameCharacter.getClass().getDeclaredField("xVelocity");
        yVelocityField = gameCharacter.getClass().getDeclaredField("yVelocity");
        yAccelerationField = gameCharacter.getClass().getDeclaredField("yAcceleration");
        xVelocityField.setAccessible(true);
        yVelocityField.setAccessible(true);
        yAccelerationField.setAccessible(true);

        canJumpField = gameCharacter.getClass().getDeclaredField("canJump");
        isJumpingField = gameCharacter.getClass().getDeclaredField("isJumping");
        canJumpField.setAccessible(true);
        isJumpingField.setAccessible(true);

        otherCharacter = new GameCharacter(1, 200, 250, "assets/Character2.png", 4, 4, 1, 129, 66, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP);
        isFallingField = gameCharacter.getClass().getDeclaredField("isFalling");
        isMoveRightField = gameCharacter.getClass().getDeclaredField("isMoveRight");
        xField = gameCharacter.getClass().getDeclaredField("x");
        yField = gameCharacter.getClass().getDeclaredField("y");

        isFallingField.setAccessible(true);
        isMoveRightField.setAccessible(true);
        xField.setAccessible(true);
        yField.setAccessible(true);
    }
    @Test
    public void moveY_givenTwoConsecutiveCalls_thenYVelocityIncreases() throws
            IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveY();
        int yVelocity1 = yVelocityField.getInt(gameCharacter);
        gameCharacter.moveY();
        int yVelocity2 = yVelocityField.getInt(gameCharacter);
        assertTrue(yVelocity2 > yVelocity1, "Velocity is increasing");
    }
    @Test
    public void moveY_givenTwoConsecutiveCalls_thenYAccelerationUnchanged() throws
            IllegalAccessException {
        gameCharacter.respawn();
        gameCharacter.moveY();
        int yAcceleration1 = yAccelerationField.getInt(gameCharacter);
        gameCharacter.moveY();
        int yAcceleration2 = yAccelerationField.getInt(gameCharacter);
        assertTrue(yAcceleration1 == yAcceleration2, "Acceleration is not change");
    }


    @Test
    public void respawn_givenGameCharacter_thenCoordinatesAre30_30() {
        gameCharacter.respawn();
        assertEquals(30, gameCharacter.getX(), "Initial x");
        assertEquals(30, gameCharacter.getY(), "Initial y");
    }
    @Test
    public void jump_whenCanJump_isSuccessful() throws IllegalAccessException {
        canJumpField.setBoolean(gameCharacter, true);
        gameCharacter.jump();

        //ตรวจสอบผลลัพธ์ - สถานะ isJumping ควรเป็น true และ canJump ควรเป็น false
        assertTrue(isJumpingField.getBoolean(gameCharacter), "isJumping should be true after jump");
        assertFalse(canJumpField.getBoolean(gameCharacter), "canJump should be false after jump");
    }

    @Test
    public void jump_whenCannotJump_isNotPermitted() throws IllegalAccessException {
        canJumpField.setBoolean(gameCharacter, false);
        isJumpingField.setBoolean(gameCharacter, false); // กำหนดสถานะเริ่มต้น
        gameCharacter.jump();
        assertFalse(isJumpingField.getBoolean(gameCharacter), "isJumping should remain false");
    }

    @Test
    public void collided_whenFallingOnOther_returnsTrue() throws IllegalAccessException {
        // Arrange: จัดเตรียมสถานการณ์ให้ gameCharacter ตกลงมาเหยียบ otherCharacter
        isFallingField.setBoolean(gameCharacter, true);

        // ตั้งค่าตำแหน่งให้เกิดการชนในแนวตั้ง
        xField.setInt(gameCharacter, 200);
        yField.setInt(gameCharacter, 200);
        xField.setInt(otherCharacter, 200);
        yField.setInt(otherCharacter, 250);

        // Act: เรียกใช้เมธอด collided
        boolean result = gameCharacter.collided(otherCharacter);

        // Assert: ตรวจสอบผลลัพธ์ (ควรเป็น true เพราะเป็นการเหยียบ)
        assertTrue(result, "Should return true for a vertical stomp collision");
    }

    @Test
    public void collided_whenBumpingHorizontally_returnsFalse() throws IllegalAccessException {
        // Arrange: จัดเตรียมสถานการณ์ให้ gameCharacter วิ่งไปชน otherCharacter ด้านข้าง
        isMoveRightField.setBoolean(gameCharacter, true);
        isFallingField.setBoolean(gameCharacter, false);

        // ตั้งค่าตำแหน่งให้เกิดการชนในแนวนอน
        xField.setInt(gameCharacter, 100);
        yField.setInt(gameCharacter, 200);
        xField.setInt(otherCharacter, 150);
        yField.setInt(otherCharacter, 200);

        // Act: เรียกใช้เมธอด collided
        boolean result = gameCharacter.collided(otherCharacter);

        // Assert: ตรวจสอบผลลัพธ์
        // 1. ควรคืนค่า false เพราะไม่ใช่การเหยียบ
        assertFalse(result, "Should return false for a horizontal bump");
        // 2. ตรวจสอบว่าตัวละครหยุดวิ่ง (isMoveRight กลายเป็น false)
        assertFalse(isMoveRightField.getBoolean(gameCharacter), "Character should stop after bumping");
    }
    @Test
    public void checkReachGameWall_whenAtLeftBoundary_stopsAtZero() throws IllegalAccessException {
        // Arrange: ตั้งค่าให้ตัวละครอยู่ที่ตำแหน่งเลยขอบซ้ายไปแล้ว
        xField.setInt(gameCharacter, -10);

        // Act: เรียกใช้เมธอด
        gameCharacter.checkReachGameWall();
        gameCharacter.repaint(); // เรียกเพื่อให้ตำแหน่งแสดงผล (TranslateX) อัปเดตตามค่า x

        // Assert: ตรวจสอบว่าตำแหน่ง x กลับมาอยู่ที่ 0
        assertEquals(0, gameCharacter.getTranslateX(), "Should stop at the left wall (x=0)");
    }

    @Test
    public void checkReachGameWall_whenAtRightBoundary_stopsAtEdge() throws IllegalAccessException {
        // Arrange: ตั้งค่าให้ตัวละครอยู่ที่ตำแหน่งเลยขอบขวาไปแล้ว
        // ความกว้างจอ = 800, ความกว้างตัวละคร = 111, ขอบจอจะอยู่ที่ x = 689
        xField.setInt(gameCharacter, 795);

        // Act: เรียกใช้เมธอด
        gameCharacter.checkReachGameWall();
        gameCharacter.repaint();

        // Assert: ตรวจสอบว่าตำแหน่ง x กลับมาอยู่ที่ขอบจอพอดี
        double expectedX = 800 - 111; // 689
        assertEquals(expectedX, gameCharacter.getTranslateX(), "Should stop at the right wall");
    }
}