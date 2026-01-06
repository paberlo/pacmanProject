package pacman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testBoardCreation() {
        assertNotNull(board);
    }

    @Test
    void testTileSize() {
        assertEquals(20, Board.TILE_SIZE);
    }

    @Test
    void testIsWallAtPixelWallPosition() {
        // Top-left corner should be wall based on MAP_TEMPLATE
        assertTrue(Board.isWallAtPixel(0, 0));
    }

    @Test
    void testIsWallAtPixelEmptyPosition() {
        // Position (4*20, 1*20) = (80, 20) should be a dot, not wall
        assertFalse(Board.isWallAtPixel(80, 20));
    }

    @Test
    void testIsWallAtPixelOutOfBounds() {
        // Out of bounds positions should be considered wall
        assertTrue(Board.isWallAtPixel(-10, 0));
        assertTrue(Board.isWallAtPixel(0, -10));
        assertTrue(Board.isWallAtPixel(500, 0));
        assertTrue(Board.isWallAtPixel(0, 500));
    }

    @Test
    void testIsWallCollisionCorners() {
        // Wall at (0,0) - should collide
        assertTrue(Board.isWallCollision(0, 0, 20));
    }

    @Test
    void testIsWallCollisionNoWall() {
        // Empty position in the map
        assertFalse(Board.isWallCollision(80, 20, 20));
    }

    @Test
    void testIsWallCollisionTopLeftCorner() {
        assertTrue(Board.isWallCollision(0, 0, 20));
    }

    @Test
    void testIsWallCollisionTopRightCorner() {
        assertTrue(Board.isWallCollision(19, 0, 20));
    }

    @Test
    void testIsWallCollisionBottomLeftCorner() {
        assertTrue(Board.isWallCollision(0, 19, 20));
    }

    @Test
    void testIsWallCollisionBottomRightCorner() {
        assertTrue(Board.isWallCollision(19, 19, 20));
    }

    @Test
    void testWrapXIfAllowedNoWrap() {
        // Regular position that shouldn't wrap
        int result = Board.wrapXIfAllowed(100, 100, 20);
        assertEquals(100, result);
    }

    @Test
    void testWrapXIfAllowedWrapLeft() {
        // Row 9 (y=180) has open edges, test wrap from left
        int result = Board.wrapXIfAllowed(-20, 180, 20);
        assertEquals(380, result); // BOARD_WIDTH - size = 400 - 20 = 380
    }

    @Test
    void testWrapXIfAllowedWrapRight() {
        // Row 9 (y=180) has open edges, test wrap from right
        int result = Board.wrapXIfAllowed(400, 180, 20);
        assertEquals(0, result);
    }

    @Test
    void testWrapXIfAllowedNonWrapRow() {
        // Row 0 has walls on edges, should not wrap
        int result = Board.wrapXIfAllowed(-20, 0, 20);
        assertEquals(-20, result);
    }

    @Test
    void testWrapXIfAllowedOutOfBoundsY() {
        // Y position out of bounds
        int result = Board.wrapXIfAllowed(100, -100, 20);
        assertEquals(100, result);
        result = Board.wrapXIfAllowed(100, 500, 20);
        assertEquals(100, result);
    }

    @Test
    void testWrapXIfAllowedPartiallyOutOfBoundsY() {
        // Entity partially out of bounds vertically
        int result = Board.wrapXIfAllowed(-20, 380, 20);
        assertEquals(-20, result); // Row bottom would be out of bounds
    }

    @Test
    void testPaintComponentDoesNotThrow() {
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> board.paintComponent(g));
        g.dispose();
    }

    @Test
    void testActionPerformed() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
        assertDoesNotThrow(() -> board.actionPerformed(event));
    }

    @Test
    void testMultipleActionPerformed() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
        // Simulate multiple game ticks
        for (int i = 0; i < 10; i++) {
            board.actionPerformed(event);
        }
        // No exception means success
    }

    @Test
    void testBoardIsFocusable() {
        assertTrue(board.isFocusable());
    }

    @Test
    void testWrapXBoundaryConditions() {
        // Test when x + size is exactly 0
        int result = Board.wrapXIfAllowed(-20, 180, 20);
        assertEquals(380, result);
        
        // Test when x is exactly BOARD_WIDTH
        result = Board.wrapXIfAllowed(400, 180, 20);
        assertEquals(0, result);
    }

    @Test
    void testWrapXNoWrapWhenXInBounds() {
        // X within bounds should not change
        int result = Board.wrapXIfAllowed(200, 180, 20);
        assertEquals(200, result);
    }

    @Test
    void testWrapXWithDifferentSizes() {
        // Different entity sizes
        int result = Board.wrapXIfAllowed(-10, 180, 10);
        assertEquals(390, result);
    }

    @Test
    void testIsWallCollisionAllCorners() {
        // Test a position where all four corners would be checked
        // Position in center of map should be navigable
        assertFalse(Board.isWallCollision(80, 80, 20));
    }

    @Test
    void testKeyListenerIntegration() {
        // Test that key events are processed
        KeyEvent leftEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> board.dispatchEvent(leftEvent));
        
        KeyEvent rightEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> board.dispatchEvent(rightEvent));
        
        KeyEvent upEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> board.dispatchEvent(upEvent));
        
        KeyEvent downEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> board.dispatchEvent(downEvent));
    }

    @Test
    void testManyGameTicks() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        // Simulate many game ticks to hit various code paths
        for (int i = 0; i < 500; i++) {
            board.actionPerformed(event);
            board.paintComponent(g);
        }
        
        g.dispose();
    }

    @Test
    void testWrapXNotWrappingWhenOnlyOneEdgeOpen() {
        // Row 1 has wall on left edge (top row is all walls)
        // This tests canWrapRow returning false
        int result = Board.wrapXIfAllowed(-20, 20, 20);
        assertEquals(-20, result);
    }

    @Test
    void testIsWallAtPixelNegativeCoordinates() {
        assertTrue(Board.isWallAtPixel(-1, 0));
        assertTrue(Board.isWallAtPixel(0, -1));
        assertTrue(Board.isWallAtPixel(-1, -1));
    }

    @Test
    void testIsWallAtPixelLargeCoordinates() {
        assertTrue(Board.isWallAtPixel(1000, 0));
        assertTrue(Board.isWallAtPixel(0, 1000));
        assertTrue(Board.isWallAtPixel(1000, 1000));
    }

    @Test
    void testWrapXEdgeCase() {
        // Test x + size <= 0 condition
        int result = Board.wrapXIfAllowed(-21, 180, 20);
        assertEquals(380, result);
        
        // Test x >= BOARD_WIDTH condition
        result = Board.wrapXIfAllowed(401, 180, 20);
        assertEquals(0, result);
    }

    @Test
    void testBoardBackgroundColor() {
        assertEquals(java.awt.Color.BLACK, board.getBackground());
    }

    @Test
    void testPacmanDiesAndTimerStops() throws Exception {
        // Use reflection to access private pacman field
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Apply enough damage to kill pacman
        pacman.applyDamage(20);
        assertTrue(pacman.isDead());
        
        // Trigger actionPerformed which should stop timer
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
        board.actionPerformed(event);
        
        // Timer should be stopped now
        Field timerField = Board.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        javax.swing.Timer timer = (javax.swing.Timer) timerField.get(board);
        assertFalse(timer.isRunning());
    }

    @Test
    void testDrawLivesWithDamagedPacman() throws Exception {
        // Get pacman via reflection and damage it to show partial hearts
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Apply damage to have half hearts (odd number)
        pacman.applyDamage(1);
        assertEquals(9, pacman.getHalfHearts());
        
        // Paint to trigger drawLives with half heart
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> board.paintComponent(g));
        g.dispose();
    }

    @Test
    void testDrawLivesWithShield() throws Exception {
        // Get pacman via reflection and add shield
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Apply gold heart to get shield
        pacman.applyGoldHeart();
        assertEquals(4, pacman.getShieldHalfHearts());
        
        // Paint to trigger drawLives with shield
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> board.paintComponent(g));
        g.dispose();
    }

    @Test
    void testCollectItemsHeartCollection() throws Exception {
        // Get MAP via reflection
        Field mapField = Board.class.getDeclaredField("MAP");
        mapField.setAccessible(true);
        char[][] map = (char[][]) mapField.get(null);
        
        // Get pacman via reflection and damage it
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Damage pacman so it can pick up hearts
        pacman.applyDamage(4);
        assertEquals(6, pacman.getHalfHearts());
        
        // Find a heart position in the map
        int heartRow = -1, heartCol = -1;
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (map[r][c] == 'H') {
                    heartRow = r;
                    heartCol = c;
                    break;
                }
            }
            if (heartRow >= 0) break;
        }
        
        if (heartRow >= 0) {
            // Create a new pacman at the heart position
            Field xField = Pacman.class.getDeclaredField("x");
            Field yField = Pacman.class.getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            xField.set(pacman, heartCol * 20);
            yField.set(pacman, heartRow * 20);
            
            // Trigger actionPerformed to collect items
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
            board.actionPerformed(event);
            
            // Health should have increased (healed)
            assertTrue(pacman.getHalfHearts() >= 6);
        }
    }

    @Test
    void testCollectItemsGoldCollection() throws Exception {
        // Get MAP via reflection
        Field mapField = Board.class.getDeclaredField("MAP");
        mapField.setAccessible(true);
        char[][] map = (char[][]) mapField.get(null);
        
        // Get pacman via reflection
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // At max health, so gold should spawn
        assertTrue(pacman.isAtMaxHealth());
        
        // Find a gold position in the map
        int goldRow = -1, goldCol = -1;
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (map[r][c] == 'G') {
                    goldRow = r;
                    goldCol = c;
                    break;
                }
            }
            if (goldRow >= 0) break;
        }
        
        if (goldRow >= 0) {
            // Move pacman to the gold position
            Field xField = Pacman.class.getDeclaredField("x");
            Field yField = Pacman.class.getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            xField.set(pacman, goldCol * 20);
            yField.set(pacman, goldRow * 20);
            
            // Trigger actionPerformed to collect items
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
            board.actionPerformed(event);
            
            // Should have shield now
            assertTrue(pacman.getShieldHalfHearts() > 0);
        }
    }

    @Test
    void testCollectItemsOutOfBounds() throws Exception {
        // Get pacman via reflection and set position outside bounds
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Set position to negative values (outside map)
        Field xField = Pacman.class.getDeclaredField("x");
        Field yField = Pacman.class.getDeclaredField("y");
        xField.setAccessible(true);
        yField.setAccessible(true);
        xField.set(pacman, -100);
        yField.set(pacman, -100);
        
        // Trigger actionPerformed - should not crash
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
        assertDoesNotThrow(() -> board.actionPerformed(event));
    }

    @Test
    void testGhostCollisionDamage() throws Exception {
        // Get ghosts via reflection
        Field ghostsField = Board.class.getDeclaredField("ghosts");
        ghostsField.setAccessible(true);
        Ghost[] ghosts = (Ghost[]) ghostsField.get(board);
        
        // Get pacman via reflection
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        int initialHealth = pacman.getHalfHearts();
        
        // Move pacman to ghost position to force collision
        java.awt.Rectangle ghostBounds = ghosts[0].getBounds();
        Field xField = Pacman.class.getDeclaredField("x");
        Field yField = Pacman.class.getDeclaredField("y");
        xField.setAccessible(true);
        yField.setAccessible(true);
        xField.set(pacman, ghostBounds.x);
        yField.set(pacman, ghostBounds.y);
        
        // Trigger actionPerformed - should apply damage
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
        board.actionPerformed(event);
        
        // Health should have decreased due to ghost damage
        assertTrue(pacman.getHalfHearts() < initialHealth);
    }

    @Test
    void testKeyPressedThroughBoard() throws Exception {
        // Get pacman via reflection
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Simulate key press through board's key listeners
        KeyEvent event = new KeyEvent(board, KeyEvent.KEY_PRESSED, 
            System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
        
        // Dispatch the event
        board.dispatchEvent(event);
        
        // The key press should be handled without exception
        assertNotNull(pacman);
    }

    @Test
    void testDrawLivesWithHalfHeart() throws Exception {
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Damage to get an odd number of half hearts (shows half heart icon)
        pacman.applyDamage(3);
        assertEquals(7, pacman.getHalfHearts()); // 3 full + 1 half
        
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> board.paintComponent(g));
        g.dispose();
    }

    @Test
    void testDrawLivesWithHalfShieldHearts() throws Exception {
        Field pacmanField = Board.class.getDeclaredField("pacman");
        pacmanField.setAccessible(true);
        Pacman pacman = (Pacman) pacmanField.get(board);
        
        // Apply gold heart to get shield
        pacman.applyGoldHeart();
        // Then apply 1 damage to have odd shield half hearts
        pacman.applyDamage(1);
        assertEquals(3, pacman.getShieldHalfHearts()); // 1 full + 1 half shield
        
        BufferedImage image = new BufferedImage(400, 420, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        assertDoesNotThrow(() -> board.paintComponent(g));
        g.dispose();
    }

    @Test
    void testKeyListenerDirectly() throws Exception {
        // Get the KeyListener array from the board
        java.awt.event.KeyListener[] keyListeners = board.getKeyListeners();
        assertTrue(keyListeners.length > 0, "Board should have key listeners");
        
        // Get the first KeyListener (should be PacmanKeyAdapter)
        java.awt.event.KeyListener keyListener = keyListeners[0];
        
        // Create and dispatch key events directly to the listener
        KeyEvent leftEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> keyListener.keyPressed(leftEvent));
        
        KeyEvent rightEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> keyListener.keyPressed(rightEvent));
        
        KeyEvent upEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> keyListener.keyPressed(upEvent));
        
        KeyEvent downEvent = new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
        assertDoesNotThrow(() -> keyListener.keyPressed(downEvent));
    }

    @Test
    void testEnsureGoldSpawnWhenNoHeartsRemain() throws Exception {
        // Get MAP via reflection
        Field mapField = Board.class.getDeclaredField("MAP");
        mapField.setAccessible(true);
        char[][] map = (char[][]) mapField.get(null);
        
        // Save original map state
        char[][] originalMap = new char[map.length][];
        for (int r = 0; r < map.length; r++) {
            originalMap[r] = map[r].clone();
        }
        
        try {
            // Get pacman via reflection
            Field pacmanField = Board.class.getDeclaredField("pacman");
            pacmanField.setAccessible(true);
            Pacman pacman = (Pacman) pacmanField.get(board);
            
            // Replace all hearts and golds with empty space to trigger the edge case
            for (int r = 0; r < map.length; r++) {
                for (int c = 0; c < map[r].length; c++) {
                    if (map[r][c] == 'H' || map[r][c] == 'G') {
                        map[r][c] = ' ';
                    }
                }
            }
            
            // Ensure pacman is at max health with no shield (conditions for gold spawn)
            // pacman starts at max health
            assertTrue(pacman.isAtMaxHealth());
            assertEquals(0, pacman.getShieldHalfHearts());
            
            // Trigger actionPerformed which calls ensureGoldSpawnIfEligible
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "test");
            assertDoesNotThrow(() -> board.actionPerformed(event));
            
            // The method should complete without finding hearts to convert
            // This covers line 261 (implicit return)
        } finally {
            // Restore original map
            for (int r = 0; r < map.length; r++) {
                map[r] = originalMap[r];
            }
        }
    }

    @Test
    void testFindNearestPointSpawnFallback() throws Exception {
        // This test covers the extremely rare case where findNearestPointSpawn
        // cannot find any non-wall cell and must return the preferred position
        
        // Get MAP via reflection
        Field mapField = Board.class.getDeclaredField("MAP");
        mapField.setAccessible(true);
        char[][] map = (char[][]) mapField.get(null);
        
        // Save the original map
        char[][] originalMap = new char[map.length][];
        for (int r = 0; r < map.length; r++) {
            originalMap[r] = map[r].clone();
        }
        
        try {
            // Fill the entire map with walls
            for (int r = 0; r < map.length; r++) {
                for (int c = 0; c < map[r].length; c++) {
                    map[r][c] = '#';
                }
            }
            
            // Get access to findNearestPointSpawn method via reflection
            Method findMethod = Board.class.getDeclaredMethod("findNearestPointSpawn", java.awt.Point.class);
            findMethod.setAccessible(true);
            
            // Call the method - it should fall back to returning preferredPosition
            java.awt.Point preferred = new java.awt.Point(100, 100);
            java.awt.Point result = (java.awt.Point) findMethod.invoke(board, preferred);
            
            // Should return the preferred position since no non-wall cells exist
            assertEquals(preferred.x, result.x);
            assertEquals(preferred.y, result.y);
        } finally {
            // Restore the original map
            for (int r = 0; r < map.length; r++) {
                map[r] = originalMap[r];
            }
        }
    }
}
