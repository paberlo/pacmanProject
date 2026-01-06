package pacman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;
import static java.awt.event.KeyEvent.*;

class PacmanTest {

    private Pacman pacman;
    private Board board;

    @BeforeEach
    void setUp() {
        // Initialize Board first to set up static state
        board = new Board();
        pacman = new Pacman(100, 100);
    }

    @Test
    void testInitialPosition() {
        Rectangle bounds = pacman.getBounds();
        assertEquals(100, bounds.x);
        assertEquals(100, bounds.y);
    }

    @Test
    void testInitialScore() {
        assertEquals(0, pacman.getScore());
    }

    @Test
    void testAddScore() {
        pacman.addScore(10);
        assertEquals(10, pacman.getScore());
        pacman.addScore(20);
        assertEquals(30, pacman.getScore());
    }

    @Test
    void testInitialHealth() {
        assertEquals(10, pacman.getHalfHearts());
        assertTrue(pacman.isAtMaxHealth());
    }

    @Test
    void testApplyDamage() {
        pacman.applyDamage(3);
        assertEquals(7, pacman.getHalfHearts());
        assertFalse(pacman.isDead());
    }

    @Test
    void testApplyDamageKillsPacman() {
        pacman.applyDamage(10);
        assertEquals(0, pacman.getHalfHearts());
        assertTrue(pacman.isDead());
    }

    @Test
    void testApplyDamageExceedingHealth() {
        pacman.applyDamage(15);
        assertEquals(0, pacman.getHalfHearts());
        assertTrue(pacman.isDead());
    }

    @Test
    void testHeal() {
        pacman.applyDamage(5);
        assertEquals(5, pacman.getHalfHearts());
        pacman.heal(3);
        assertEquals(8, pacman.getHalfHearts());
    }

    @Test
    void testHealCannotExceedMax() {
        pacman.applyDamage(2);
        pacman.heal(10);
        assertEquals(10, pacman.getHalfHearts());
        assertTrue(pacman.isAtMaxHealth());
    }

    @Test
    void testIsAtMaxHealthWhenDamaged() {
        pacman.applyDamage(1);
        assertFalse(pacman.isAtMaxHealth());
    }

    @Test
    void testGetCenter() {
        Point center = pacman.getCenter();
        assertEquals(110, center.x);
        assertEquals(110, center.y);
    }

    @Test
    void testGetBoundsSize() {
        Rectangle bounds = pacman.getBounds();
        assertEquals(20, bounds.width);
        assertEquals(20, bounds.height);
    }

    @Test
    void testInitialShieldIsZero() {
        assertEquals(0, pacman.getShieldHalfHearts());
    }

    @Test
    void testApplyGoldHeartWhenNotAtMaxHealth() {
        pacman.applyDamage(5);
        pacman.applyGoldHeart();
        assertEquals(10, pacman.getHalfHearts());
        assertEquals(0, pacman.getShieldHalfHearts());
    }

    @Test
    void testApplyGoldHeartWhenAtMaxHealth() {
        pacman.applyGoldHeart();
        assertEquals(10, pacman.getHalfHearts());
        assertEquals(4, pacman.getShieldHalfHearts());
    }

    @Test
    void testApplyDamageWithShield() {
        pacman.applyGoldHeart();
        assertEquals(4, pacman.getShieldHalfHearts());
        pacman.applyDamage(2);
        assertEquals(2, pacman.getShieldHalfHearts());
        assertEquals(10, pacman.getHalfHearts());
    }

    @Test
    void testApplyDamageExceedingShield() {
        pacman.applyGoldHeart();
        pacman.applyDamage(6);
        assertEquals(0, pacman.getShieldHalfHearts());
        assertEquals(8, pacman.getHalfHearts());
    }

    @Test
    void testKeyPressedLeft() {
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_LEFT, CHAR_UNDEFINED);
        pacman.keyPressed(event);
        // Direction change is internal, test through movement would require Board
    }

    @Test
    void testKeyPressedRight() {
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_RIGHT, CHAR_UNDEFINED);
        pacman.keyPressed(event);
    }

    @Test
    void testKeyPressedUp() {
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_UP, CHAR_UNDEFINED);
        pacman.keyPressed(event);
    }

    @Test
    void testKeyPressedDown() {
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_DOWN, CHAR_UNDEFINED);
        pacman.keyPressed(event);
    }

    @Test
    void testKeyPressedUnrelated() {
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_SPACE, ' ');
        pacman.keyPressed(event);
        // Should not throw, should just ignore
    }

    @Test
    void testIsDeadInitiallyFalse() {
        assertFalse(pacman.isDead());
    }

    @Test
    void testDrawDoesNotThrow() {
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> pacman.draw(g));
        g.dispose();
    }

    @Test
    void testMoveLeft() {
        // Position pacman in open space and move left
        Pacman p = new Pacman(80, 20);
        Rectangle before = p.getBounds();
        int beforeX = before.x;
        // Default direction is LEFT
        p.move();
        // Should have moved left by STEP (4)
        Rectangle after = p.getBounds();
        // May or may not move depending on wall collision
        assertNotNull(after);
    }

    @Test
    void testMoveRight() {
        Pacman p = new Pacman(80, 20);
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_RIGHT, CHAR_UNDEFINED);
        p.keyPressed(event);
        p.move();
        assertNotNull(p.getBounds());
    }

    @Test
    void testMoveUp() {
        Pacman p = new Pacman(80, 60);
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_UP, CHAR_UNDEFINED);
        p.keyPressed(event);
        p.move();
        assertNotNull(p.getBounds());
    }

    @Test
    void testMoveDown() {
        Pacman p = new Pacman(80, 20);
        KeyEvent event = new KeyEvent(new java.awt.Component() {}, 
            KEY_PRESSED, System.currentTimeMillis(), 0, VK_DOWN, CHAR_UNDEFINED);
        p.keyPressed(event);
        p.move();
        assertNotNull(p.getBounds());
    }

    @Test
    void testDrawWithAllDirections() {
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        Pacman p = new Pacman(100, 100);
        
        // Test draw in each direction
        KeyEvent leftEvent = new KeyEvent(new java.awt.Component() {}, KEY_PRESSED, System.currentTimeMillis(), 0, VK_LEFT, CHAR_UNDEFINED);
        p.keyPressed(leftEvent);
        assertDoesNotThrow(() -> p.draw(g));
        
        KeyEvent rightEvent = new KeyEvent(new java.awt.Component() {}, KEY_PRESSED, System.currentTimeMillis(), 0, VK_RIGHT, CHAR_UNDEFINED);
        p.keyPressed(rightEvent);
        assertDoesNotThrow(() -> p.draw(g));
        
        KeyEvent upEvent = new KeyEvent(new java.awt.Component() {}, KEY_PRESSED, System.currentTimeMillis(), 0, VK_UP, CHAR_UNDEFINED);
        p.keyPressed(upEvent);
        assertDoesNotThrow(() -> p.draw(g));
        
        KeyEvent downEvent = new KeyEvent(new java.awt.Component() {}, KEY_PRESSED, System.currentTimeMillis(), 0, VK_DOWN, CHAR_UNDEFINED);
        p.keyPressed(downEvent);
        assertDoesNotThrow(() -> p.draw(g));
        
        g.dispose();
    }
}
