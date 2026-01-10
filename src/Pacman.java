import java.awt.*;
import java.awt.event.*;

public class Pacman {
    private int x, y;
    private Direction direction = Direction.LEFT;
    private Direction nextDirection = Direction.LEFT;
    private int score = 0;
    private Board board;
    private static final int SIZE = 20;
    private static final int SPEED = 4;
    private static final int CELL_SIZE = 20;

    // Animación de la boca
    private int mouthAngle = 45;
    private int mouthDirection = 10;

    public Pacman(int x, int y, Board board) {
        this.x = x;
        this.y = y;
        this.board = board;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        int startAngle = direction.getAngle() + mouthAngle / 2;
        int arcAngle = 360 - mouthAngle;
        g.fillArc(x, y, SIZE, SIZE, startAngle, arcAngle);

        // Animar boca
        mouthAngle += mouthDirection;
        if (mouthAngle >= 45 || mouthAngle <= 5) {
            mouthDirection = -mouthDirection;
        }
    }

    public void move() {
        // Intentar cambiar de dirección si está alineado
        if (x % CELL_SIZE == 0 && y % CELL_SIZE == 0) {
            if (canMoveInDirection(nextDirection)) {
                direction = nextDirection;
            }
        }

        // Mover en la dirección actual
        int newX = x;
        int newY = y;

        switch (direction) {
            case LEFT: newX = x - SPEED; break;
            case RIGHT: newX = x + SPEED; break;
            case UP: newY = y - SPEED; break;
            case DOWN: newY = y + SPEED; break;
        }

        // Manejar túneles
        int[] pos = board.handleTunnel(newX, newY);
        newX = pos[0];
        newY = pos[1];

        // Verificar si puede moverse
        if (board.canMove(newX, newY, SIZE)) {
            x = newX;
            y = newY;
        }
        
        // Comer puntos
        int dotType = board.eatDot(x, y);
        if (dotType == 1) {
            score += 10;
        } else if (dotType == 2) {
            score += 50;
        }
    }

    private boolean canMoveInDirection(Direction dir) {
        int testX = x;
        int testY = y;

        switch (dir) {
            case LEFT: testX = x - SPEED; break;
            case RIGHT: testX = x + SPEED; break;
            case UP: testY = y - SPEED; break;
            case DOWN: testY = y + SPEED; break;
        }

        return board.canMove(testX, testY, SIZE);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                nextDirection = Direction.LEFT; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                nextDirection = Direction.RIGHT; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                nextDirection = Direction.UP; break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                nextDirection = Direction.DOWN; break;
        }
    }

    public int getScore() { return score; }
    public void addScore(int points) { score += points; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Direction getDirection() { return direction; }

    public void setDirection(Direction dir) {
        this.direction = dir;
        this.nextDirection = dir;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Direction getDirection() {
        return direction;
    }
}