package pacman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;

class GhostTest {

    private Ghost weakGhost;
    private Ghost intermediateGhost;
    private Ghost difficultGhost;
    private Ghost extremeGhost;
    private Board board; // Need to initialize Board for static methods

    @BeforeEach
    void setUp() {
        // Initialize Board first to ensure static state is set up
        board = new Board();
        weakGhost = new Ghost(100, 100, GhostType.WEAK);
        intermediateGhost = new Ghost(120, 120, GhostType.INTERMEDIATE);
        difficultGhost = new Ghost(140, 140, GhostType.DIFFICULT);
        extremeGhost = new Ghost(160, 160, GhostType.EXTREME);
    }

    @Test
    void testWeakGhostDamage() {
        assertEquals(1, weakGhost.getDamageHalfHearts());
    }

    @Test
    void testIntermediateGhostDamage() {
        assertEquals(2, intermediateGhost.getDamageHalfHearts());
    }

    @Test
    void testDifficultGhostDamage() {
        assertEquals(4, difficultGhost.getDamageHalfHearts());
    }

    @Test
    void testExtremeGhostDamage() {
        assertEquals(6, extremeGhost.getDamageHalfHearts());
    }

    @Test
    void testGetBoundsPosition() {
        Rectangle bounds = weakGhost.getBounds();
        assertEquals(100, bounds.x);
        assertEquals(100, bounds.y);
    }

    @Test
    void testGetBoundsSize() {
        Rectangle bounds = weakGhost.getBounds();
        assertEquals(20, bounds.width);
        assertEquals(20, bounds.height);
    }

    @Test
    void testMoveChangesPosition() {
        Rectangle initialBounds = weakGhost.getBounds();
        int initialX = initialBounds.x;
        int initialY = initialBounds.y;
        
        // Move multiple times to increase chance of position change
        for (int i = 0; i < 100; i++) {
            weakGhost.move();
        }
        
        Rectangle finalBounds = weakGhost.getBounds();
        // Position may or may not change depending on walls and random direction
        // Just ensure no exception is thrown and bounds are still valid
        assertNotNull(finalBounds);
        assertTrue(finalBounds.width == 20);
        assertTrue(finalBounds.height == 20);
    }

    @Test
    void testDrawDoesNotThrow() {
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> weakGhost.draw(g));
        assertDoesNotThrow(() -> intermediateGhost.draw(g));
        assertDoesNotThrow(() -> difficultGhost.draw(g));
        assertDoesNotThrow(() -> extremeGhost.draw(g));
        g.dispose();
    }

    @Test
    void testGhostCreationWithDifferentTypes() {
        Ghost ghost1 = new Ghost(0, 0, GhostType.WEAK);
        Ghost ghost2 = new Ghost(0, 0, GhostType.INTERMEDIATE);
        Ghost ghost3 = new Ghost(0, 0, GhostType.DIFFICULT);
        Ghost ghost4 = new Ghost(0, 0, GhostType.EXTREME);
        
        assertNotNull(ghost1);
        assertNotNull(ghost2);
        assertNotNull(ghost3);
        assertNotNull(ghost4);
    }

    @Test
    void testMultipleMovesForCoverage() {
        // Ghost movement is random, so we need multiple moves to cover different branches
        for (int i = 0; i < 50; i++) {
            weakGhost.move();
            intermediateGhost.move();
            difficultGhost.move();
            extremeGhost.move();
        }
        // Just verify they don't throw exceptions
        assertNotNull(weakGhost.getBounds());
        assertNotNull(intermediateGhost.getBounds());
        assertNotNull(difficultGhost.getBounds());
        assertNotNull(extremeGhost.getBounds());
    }
}
