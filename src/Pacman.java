import java.awt.*;
import java.awt.event.*;

public class Pacman {
    private int x, y;
    private Direction direction = Direction.LEFT;
    private int score = 0;
    private final int SIZE = 20;
    private final int STEP = 4;

    public Pacman(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillArc(x, y, SIZE, SIZE, direction.getAngle(), 300);
    }

    public void move(Board board) {
        int nextX = x;
        int nextY = y;
        switch (direction) {
            case LEFT: nextX -= STEP; break;
            case RIGHT: nextX += STEP; break;
            case UP: nextY -= STEP; break;
            case DOWN: nextY += STEP; break;
        }
        // check collision with walls via board
        if (!board.isCollisionBoxWall(nextX, nextY, SIZE, SIZE)) {
            x = nextX;
            y = nextY;
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: direction = Direction.LEFT; break;
            case KeyEvent.VK_RIGHT: direction = Direction.RIGHT; break;
            case KeyEvent.VK_UP: direction = Direction.UP; break;
            case KeyEvent.VK_DOWN: direction = Direction.DOWN; break;
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int v) {
        score += v;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
