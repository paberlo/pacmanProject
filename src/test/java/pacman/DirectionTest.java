package pacman;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void testLeftDirection() {
        Direction direction = Direction.LEFT;
        assertEquals(180, direction.getAngle());
    }

    @Test
    void testRightDirection() {
        Direction direction = Direction.RIGHT;
        assertEquals(0, direction.getAngle());
    }

    @Test
    void testUpDirection() {
        Direction direction = Direction.UP;
        assertEquals(90, direction.getAngle());
    }

    @Test
    void testDownDirection() {
        Direction direction = Direction.DOWN;
        assertEquals(270, direction.getAngle());
    }

    @Test
    void testAllDirectionsCount() {
        assertEquals(4, Direction.values().length);
    }

    @Test
    void testValueOf() {
        assertEquals(Direction.LEFT, Direction.valueOf("LEFT"));
        assertEquals(Direction.RIGHT, Direction.valueOf("RIGHT"));
        assertEquals(Direction.UP, Direction.valueOf("UP"));
        assertEquals(Direction.DOWN, Direction.valueOf("DOWN"));
    }
}
