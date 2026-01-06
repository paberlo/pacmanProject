package pacman;

import org.junit.jupiter.api.Test;
import java.awt.Color;
import static org.junit.jupiter.api.Assertions.*;

class GhostTypeTest {

    @Test
    void testWeakGhostTypeColor() {
        assertEquals(new Color(0, 191, 255), GhostType.WEAK.getColor());
    }

    @Test
    void testWeakGhostTypeSpeed() {
        assertEquals(1, GhostType.WEAK.getSpeed());
    }

    @Test
    void testWeakGhostTypeDamage() {
        assertEquals(1, GhostType.WEAK.getDamageHalfHearts());
    }

    @Test
    void testIntermediateGhostTypeColor() {
        assertEquals(Color.PINK, GhostType.INTERMEDIATE.getColor());
    }

    @Test
    void testIntermediateGhostTypeSpeed() {
        assertEquals(2, GhostType.INTERMEDIATE.getSpeed());
    }

    @Test
    void testIntermediateGhostTypeDamage() {
        assertEquals(2, GhostType.INTERMEDIATE.getDamageHalfHearts());
    }

    @Test
    void testDifficultGhostTypeColor() {
        assertEquals(Color.RED, GhostType.DIFFICULT.getColor());
    }

    @Test
    void testDifficultGhostTypeSpeed() {
        assertEquals(4, GhostType.DIFFICULT.getSpeed());
    }

    @Test
    void testDifficultGhostTypeDamage() {
        assertEquals(4, GhostType.DIFFICULT.getDamageHalfHearts());
    }

    @Test
    void testExtremeGhostTypeColor() {
        assertEquals(Color.BLACK, GhostType.EXTREME.getColor());
    }

    @Test
    void testExtremeGhostTypeSpeed() {
        assertEquals(6, GhostType.EXTREME.getSpeed());
    }

    @Test
    void testExtremeGhostTypeDamage() {
        assertEquals(6, GhostType.EXTREME.getDamageHalfHearts());
    }

    @Test
    void testAllGhostTypesCount() {
        assertEquals(4, GhostType.values().length);
    }

    @Test
    void testValueOf() {
        assertEquals(GhostType.WEAK, GhostType.valueOf("WEAK"));
        assertEquals(GhostType.INTERMEDIATE, GhostType.valueOf("INTERMEDIATE"));
        assertEquals(GhostType.DIFFICULT, GhostType.valueOf("DIFFICULT"));
        assertEquals(GhostType.EXTREME, GhostType.valueOf("EXTREME"));
    }
}
