import java.awt.*;
import java.util.Random;

public class Ghost {
    private int x, y;
    private Direction direction;
    private Color color;
    private Random random = new Random();
    private Board board;
    private static final int SIZE = 20;
    private static final int SPEED = 3;
    private GhostBehavior behavior;
    // Reusable array to reduce garbage collection pressure
    private Direction[] validDirectionsBuffer = new Direction[4];
    
    public enum GhostBehavior {
        CHASER,    // Directly pursues Pacman
        AMBUSHER,  // Attempts to ambush Pacman
        RANDOM     // Random movement
    }

    public Ghost(int x, int y, Color color, Board board) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.board = board;
        this.direction = Direction.values()[random.nextInt(4)];
        
        // Assign behavior based on ghost color
        if (color.equals(Color.RED)) {
            this.behavior = GhostBehavior.CHASER;
        } else if (color.equals(Color.PINK)) {
            this.behavior = GhostBehavior.AMBUSHER;
        } else {
            this.behavior = GhostBehavior.RANDOM;
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        // Dibujar cuerpo del fantasma
        g.fillArc(x, y, SIZE, SIZE, 0, 180);
        g.fillRect(x, y + SIZE/2, SIZE, SIZE/2);
        // Dibujar ondas en la parte inferior
        int waveWidth = SIZE / 3;
        for (int i = 0; i < 3; i++) {
            g.fillArc(x + i * waveWidth, y + SIZE - waveWidth/2, waveWidth, waveWidth, 180, 180);
        }
        // Draw eyes
        g.setColor(Color.WHITE);
        g.fillOval(x + 3, y + 5, 6, 6);
        g.fillOval(x + 11, y + 5, 6, 6);
        g.setColor(Color.BLUE);
        g.fillOval(x + 4, y + 6, 4, 4);
        g.fillOval(x + 12, y + 6, 4, 4);
    }

    public void move() {
        // Change direction based on behavior
        // Increased frequency: from 1/15 to 1/8 for RANDOM
        // CHASER and AMBUSHER change more frequently: 1/5
        boolean shouldChangeDirection = false;
        
        switch (behavior) {
            case CHASER:
            case AMBUSHER:
                shouldChangeDirection = random.nextInt(5) == 0;
                break;
            case RANDOM:
                shouldChangeDirection = random.nextInt(8) == 0;
                break;
        }
        
        if (shouldChangeDirection) {
            direction = chooseNewDirection();
        }
        
        int nextX = x;
        int nextY = y;
        
        switch (direction) {
            case LEFT: nextX = x - SPEED; break;
            case RIGHT: nextX = x + SPEED; break;
            case UP: nextY = y - SPEED; break;
            case DOWN: nextY = y + SPEED; break;
        }
        
        // Check if can move in that direction
        if (board.canMove(nextX, nextY, SIZE)) {
            x = nextX;
            y = nextY;
        } else {
            // If can't move, choose a valid direction
            direction = chooseValidDirection();
        }
    }
    
    // Choose a new direction based on the ghost's behavior
    private Direction chooseNewDirection() {
        switch (behavior) {
            case CHASER:
                return chaseTarget(board.getPacmanX(), board.getPacmanY());
            case AMBUSHER:
                return ambushTarget(board.getPacmanX(), board.getPacmanY());
            case RANDOM:
            default:
                return Direction.values()[random.nextInt(4)];
        }
    }
    
    // Directly pursues Pacman
    private Direction chaseTarget(int targetX, int targetY) {
        int dx = targetX - x;
        int dy = targetY - y;
        
        // Decide whether to move horizontally or vertically
        // Prioritize the direction with the greater distance
        if (Math.abs(dx) > Math.abs(dy)) {
            // Move horizontally
            if (dx > 0) return Direction.RIGHT;
            else return Direction.LEFT;
        } else if (Math.abs(dy) > Math.abs(dx)) {
            // Move vertically
            if (dy > 0) return Direction.DOWN;
            else return Direction.UP;
        } else {
            // Equal distances, choose randomly
            if (random.nextBoolean()) {
                return dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                return dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }
    
    // Attempts to ambush Pacman (move toward where Pacman is going)
    private Direction ambushTarget(int targetX, int targetY) {
        // Predict Pacman's future position (approximately 4 cells ahead)
        Direction pacmanDir = board.getPacmanDirection();
        int predictX = targetX;
        int predictY = targetY;
        
        int prediction = 4 * SIZE; // 4 cells ahead
        switch (pacmanDir) {
            case LEFT: predictX -= prediction; break;
            case RIGHT: predictX += prediction; break;
            case UP: predictY -= prediction; break;
            case DOWN: predictY += prediction; break;
        }
        
        // Chase the predicted position
        return chaseTarget(predictX, predictY);
    }
    
    // Choose a valid direction (not blocked by walls)
    private Direction chooseValidDirection() {
        Direction[] directions = Direction.values();
        int validCount = 0;
        
        // Find all valid directions
        for (Direction dir : directions) {
            int testX = x;
            int testY = y;
            
            switch (dir) {
                case LEFT: testX = x - SPEED; break;
                case RIGHT: testX = x + SPEED; break;
                case UP: testY = y - SPEED; break;
                case DOWN: testY = y + SPEED; break;
            }
            
            if (board.canMove(testX, testY, SIZE)) {
                validDirectionsBuffer[validCount++] = dir;
            }
        }
        
        // If there are valid directions, choose one based on behavior
        if (validCount > 0) {
            if (behavior == GhostBehavior.RANDOM) {
                // For RANDOM, choose randomly from valid directions
                return validDirectionsBuffer[random.nextInt(validCount)];
            } else {
                // For CHASER and AMBUSHER, try to choose the best valid direction
                // Calculate target direction directly here to avoid recursion
                int targetX = board.getPacmanX();
                int targetY = board.getPacmanY();
                
                if (behavior == GhostBehavior.AMBUSHER) {
                    // Predict future position for ambusher
                    Direction pacmanDir = board.getPacmanDirection();
                    int prediction = 4 * SIZE;
                    switch (pacmanDir) {
                        case LEFT: targetX -= prediction; break;
                        case RIGHT: targetX += prediction; break;
                        case UP: targetY -= prediction; break;
                        case DOWN: targetY += prediction; break;
                    }
                }
                
                // Calculate best direction toward target
                int dx = targetX - x;
                int dy = targetY - y;
                Direction targetDir;
                
                if (Math.abs(dx) > Math.abs(dy)) {
                    targetDir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
                } else {
                    targetDir = dy > 0 ? Direction.DOWN : Direction.UP;
                }
                
                // Check if target direction is valid
                for (int i = 0; i < validCount; i++) {
                    if (validDirectionsBuffer[i] == targetDir) {
                        return targetDir;
                    }
                }
                
                // If not, choose the first valid direction
                return validDirectionsBuffer[0];
            }
        }
        
        // If no valid directions exist, maintain current direction
        return direction;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}