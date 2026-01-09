import java.awt.*;
import java.util.Random;

public class Ghost {
    private int x, y;
    private Direction direction;
    private Color color;
    private Random random = new Random();
    private final int SIZE = 20;
    private final int STEP = 4;

    public Ghost(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.direction = Direction.values()[random.nextInt(4)];
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, SIZE, SIZE);
    }

    public void move(Board board) {
        // randomly change direction occasionally
        if (random.nextInt(10) == 0) {
            direction = Direction.values()[random.nextInt(4)];
        }

        int nextX = x;
        int nextY = y;
        switch (direction) {
            case LEFT: nextX -= STEP; break;
            case RIGHT: nextX += STEP; break;
            case UP: nextY -= STEP; break;
            case DOWN: nextY += STEP; break;
        }

        // if collision, pick a new direction
        if (board.isCollisionBoxWall(nextX, nextY, SIZE, SIZE)) {
            direction = Direction.values()[random.nextInt(4)];
        } else {
            x = nextX;
            y = nextY;
        }
    }
}
