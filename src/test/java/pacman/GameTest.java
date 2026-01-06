package pacman;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import javax.swing.JFrame;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testGameCreation() {
        Game game = new Game();
        assertNotNull(game);
    }

    @Test
    void testGameIsJFrame() {
        Game game = new Game();
        assertTrue(game instanceof JFrame);
    }

    @Test
    void testGameTitle() {
        Game game = new Game();
        assertEquals("Pac-Man", game.getTitle());
    }

    @Test
    void testGameSize() {
        Game game = new Game();
        assertEquals(400, game.getSize().width);
        assertEquals(420, game.getSize().height);
    }

    @Test
    void testGameNotResizable() {
        Game game = new Game();
        assertFalse(game.isResizable());
    }

    @Test
    void testGameDefaultCloseOperation() {
        Game game = new Game();
        assertEquals(JFrame.EXIT_ON_CLOSE, game.getDefaultCloseOperation());
    }

    @Test
    void testGameContainsBoard() {
        Game game = new Game();
        assertTrue(game.getContentPane().getComponentCount() > 0);
        assertTrue(game.getContentPane().getComponent(0) instanceof Board);
    }

    @Test
    void testMainMethodExists() {
        // Verify the main method exists
        try {
            Game.class.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            fail("Main method should exist");
        }
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testMainMethod() {
        // Run main method in a separate thread so we can test it
        Thread mainThread = new Thread(() -> {
            Game.main(new String[]{});
        });
        mainThread.start();
        
        // Give it a moment to start
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Clean up - the game creates a visible window
        // The @Timeout annotation ensures this test doesn't hang
    }
}
