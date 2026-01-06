import java.awt.*;
import java.awt.event.*;

public class Pacman {
    private int x, y;
    private Direction direction = Direction.DOWN;
    private Direction nextDirection = Direction.DOWN;
    private int score = 0;
    private Board board;
    private static final int SIZE = 20;
    private static final int SPEED = 4;

    public Pacman(int x, int y, Board board) {
        this.x = x;
        this.y = y;
        this.board = board;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillArc(x, y, SIZE, SIZE, direction.getAngle(), 300);
    }
    
    // Calcula la nueva posición basada en la dirección
    private int[] calculateNewPosition(Direction dir) {
        int newX = x;
        int newY = y;
        switch (dir) {
            case LEFT: newX = x - SPEED; break;
            case RIGHT: newX = x + SPEED; break;
            case UP: newY = y - SPEED; break;
            case DOWN: newY = y + SPEED; break;
        }
        return new int[]{newX, newY};
    }

    public void move() {
        // Intentar cambiar a la dirección deseada
        int[] nextPos = calculateNewPosition(nextDirection);
        
        // Si puede moverse en la dirección deseada, cambiar dirección
        if (board.canMove(nextPos[0], nextPos[1], SIZE)) {
            direction = nextDirection;
            x = nextPos[0];
            y = nextPos[1];
        } else {
            // Intentar continuar en la dirección actual
            int[] currentPos = calculateNewPosition(direction);
            
            if (board.canMove(currentPos[0], currentPos[1], SIZE)) {
                x = currentPos[0];
                y = currentPos[1];
            }
        }
        
        // Intentar comer un punto
        if (board.eatDot(x, y)) {
            score += 10;
        }
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: nextDirection = Direction.LEFT; break;
            case KeyEvent.VK_RIGHT: nextDirection = Direction.RIGHT; break;
            case KeyEvent.VK_UP: nextDirection = Direction.UP; break;
            case KeyEvent.VK_DOWN: nextDirection = Direction.DOWN; break;
        }
    }

    public int getScore() {
        return score;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public Direction getDirection() {
        return direction;
    }
}