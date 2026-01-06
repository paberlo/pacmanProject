package pacman;

import java.awt.*;

public enum GhostType {
    WEAK(new Color(0, 191, 255), 1, 1), // half heart
    INTERMEDIATE(Color.PINK, 2, 2),
    DIFFICULT(Color.RED, 4, 4),
    EXTREME(Color.BLACK, 6, 6);

    private final Color color;
    private final int speed;
    private final int damageHalfHearts;

    GhostType(Color color, int speed, int damageHalfHearts) {
        this.color = color;
        this.speed = speed;
        this.damageHalfHearts = damageHalfHearts;
    }

    public Color getColor() {
        return color;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDamageHalfHearts() {
        return damageHalfHearts;
    }
}
