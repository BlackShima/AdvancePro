package se233.chapter51.model;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeysTest {

    private Keys keys;

    @BeforeEach
    public void setUp() {
        keys = new Keys();
    }

    @Test
    public void testSingleKeyPressAndRelease() {
        // 1. Initially, no keys should be pressed.
        assertFalse(keys.isPressed(KeyCode.A), "Key 'A' should not be pressed initially.");

        // 2. Simulate pressing a key.
        keys.add(KeyCode.A);
        assertTrue(keys.isPressed(KeyCode.A), "Key 'A' should be pressed after adding it.");

        // 3. Simulate releasing the key.
        keys.remove(KeyCode.A);
        assertFalse(keys.isPressed(KeyCode.A), "Key 'A' should not be pressed after removing it.");
    }

    @Test
    public void testMultipleKeyPresses() {
        // 1. Simulate pressing multiple keys.
        keys.add(KeyCode.W);
        keys.add(KeyCode.D);

        // 2. Verify that the pressed keys are correctly identified.
        assertTrue(keys.isPressed(KeyCode.W), "Key 'W' should be pressed.");
        assertTrue(keys.isPressed(KeyCode.D), "Key 'D' should be pressed.");

        // 3. Verify that a key that was not pressed returns false.
        assertFalse(keys.isPressed(KeyCode.S), "Key 'S' should not be pressed.");

        // 4. Simulate releasing one of the keys.
        keys.remove(KeyCode.W);

        // 5. Verify the state of all keys.
        assertFalse(keys.isPressed(KeyCode.W), "Key 'W' should be released.");
        assertTrue(keys.isPressed(KeyCode.D), "Key 'D' should still be pressed.");
        assertFalse(keys.isPressed(KeyCode.S), "Key 'S' should remain un-pressed.");
    }
}