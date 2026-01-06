package pacman;

import java.awt.*;
import java.util.Random;

public class Ghost {
    private static final int SIZE = 20;
    private static final int BODY_OVERLAP = 1;
    private int x, y;
    private Direction direction;
    private final Color color;
    private final int speed;
    private final int damageHalfHearts;
    private final GhostType type;
    private Random random = new Random();

    public Ghost(int x, int y, GhostType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = type.getColor();
        this.speed = type.getSpeed();
        this.damageHalfHearts = type.getDamageHalfHearts();
        this.direction = Direction.values()[random.nextInt(4)];
    }

    public void draw(Graphics g) {
        g.setColor(color);
        int arcHeight = SIZE / 2;
        g.fillArc(x, y, SIZE, SIZE, 0, 180);
        g.fillRect(x, y + arcHeight - BODY_OVERLAP, SIZE, SIZE - arcHeight + BODY_OVERLAP);
        int legWidth = SIZE / 4;
        g.setColor(color.darker());
        g.fillRect(x, y + SIZE - legWidth, legWidth, legWidth);
        g.fillRect(x + legWidth, y + SIZE - legWidth, legWidth, legWidth);
        g.fillRect(x + legWidth * 2, y + SIZE - legWidth, legWidth, legWidth);
        g.fillRect(x + legWidth * 3, y + SIZE - legWidth, legWidth, legWidth);
        g.setColor(Color.WHITE);
        g.fillOval(x + 4, y + 6, 6, 6);
        g.fillOval(x + 12, y + 6, 6, 6);
        g.setColor(Color.BLACK);
        g.fillOval(x + 6, y + 8, 3, 3);
        g.fillOval(x + 14, y + 8, 3, 3);
    }

    public void move() {
        if (random.nextInt(10) == 0) {
            direction = Direction.values()[random.nextInt(4)];
        }
        int oldX = x;
        int oldY = y;
        int newX = x;
        int newY = y;
        switch (direction) {
            case LEFT: newX -= speed; break;
            case RIGHT: newX += speed; break;
            case UP: newY -= speed; break;
            case DOWN: newY += speed; break;
        }
        newX = Board.wrapXIfAllowed(newX, newY, SIZE);
        if (Board.isWallCollision(newX, newY, SIZE)) {
            x = oldX;
            y = oldY;
            direction = Direction.values()[random.nextInt(4)];
        } else {
            x = newX;
            y = newY;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    public int getDamageHalfHearts() {
        return damageHalfHearts;
    }
}
