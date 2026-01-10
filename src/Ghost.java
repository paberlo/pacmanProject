import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Ghost {
    private int x, y;
    private int startX, startY; // Posición inicial para respawn
    private Direction direction;
    private Color color;
    private Random random = new Random();
    private Board board;
    private static final int SIZE = 20;
    private static final int CELL_SIZE = 20;
    private int speed = 4; // Misma base que Pacman
    private boolean scared = false;
    private boolean eaten = false;
    private int eatenTimer = 0;

    // Tipo de fantasma para diferentes comportamientos
    public enum GhostType { BLINKY, PINKY, INKY, CLYDE }
    private GhostType type;

    public Ghost(int x, int y, Color color, Board board, GhostType type) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.color = color;
        this.board = board;
        this.type = type;
        this.direction = Direction.UP;
    }

    // Constructor compatible con el anterior
    public Ghost(int x, int y, Color color, Board board) {
        this(x, y, color, board, GhostType.BLINKY);
    }

    public void draw(Graphics g) {
        if (eaten) {
            // Solo dibujar ojos cuando fue comido
            drawEyes(g, x, y);
            return;
        }

        Color drawColor;
        if (scared) {
            // Parpadeo cuando el power mode está por terminar
            if (board.getPowerModeTimer() < 60 && (board.getPowerModeTimer() / 10) % 2 == 0) {
                drawColor = Color.WHITE;
            } else {
                drawColor = new Color(0, 0, 180);
            }
        } else {
            drawColor = color;
        }

        g.setColor(drawColor);

        // Dibujar cuerpo del fantasma
        g.fillArc(x, y, SIZE, SIZE, 0, 180);
        g.fillRect(x, y + SIZE/2, SIZE, SIZE/2);

        // Dibujar ondas en la parte inferior
        int waveWidth = SIZE / 3;
        for (int i = 0; i < 3; i++) {
            g.fillArc(x + i * waveWidth, y + SIZE - waveWidth/2, waveWidth, waveWidth, 180, 180);
        }

        // Dibujar ojos
        drawEyes(g, x, y);
    }

    private void drawEyes(Graphics g, int px, int py) {
        if (scared && !eaten) {
            // Ojos asustados
            g.setColor(Color.WHITE);
            g.fillOval(px + 4, py + 6, 5, 5);
            g.fillOval(px + 11, py + 6, 5, 5);
        } else {
            // Ojos normales mirando en la dirección del movimiento
            g.setColor(Color.WHITE);
            g.fillOval(px + 3, py + 4, 7, 7);
            g.fillOval(px + 10, py + 4, 7, 7);

            // Pupilas que miran en la dirección del movimiento
            g.setColor(Color.BLUE);
            int pupilOffsetX = 0, pupilOffsetY = 0;
            switch (direction) {
                case LEFT: pupilOffsetX = -1; break;
                case RIGHT: pupilOffsetX = 2; break;
                case UP: pupilOffsetY = -1; break;
                case DOWN: pupilOffsetY = 2; break;
            }
            g.fillOval(px + 5 + pupilOffsetX, py + 6 + pupilOffsetY, 3, 3);
            g.fillOval(px + 12 + pupilOffsetX, py + 6 + pupilOffsetY, 3, 3);
        }
    }

    public void move() {
        if (eaten) {
            // Volver a la base
            moveToBase();
            return;
        }

        // Verificar si está alineado para poder cambiar de dirección
        if (isAligned()) {
            Direction newDir = chooseDirection();
            if (newDir != null) {
                direction = newDir;
            }
        }
        
        int nextX = x;
        int nextY = y;
        
        int currentSpeed = scared ? 2 : speed; // Más lento cuando asustado

        switch (direction) {
            case LEFT: nextX = x - currentSpeed; break;
            case RIGHT: nextX = x + currentSpeed; break;
            case UP: nextY = y - currentSpeed; break;
            case DOWN: nextY = y + currentSpeed; break;
        }
        
        // Aplicar túneles
        int[] tunnelPos = board.handleTunnel(nextX, nextY);
        nextX = tunnelPos[0];
        nextY = tunnelPos[1];

        if (board.canMove(nextX, nextY, SIZE)) {
            x = nextX;
            y = nextY;
        } else {
            alignToGrid();
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

    private void moveToBase() {
        eatenTimer++;
        // Mover rápidamente hacia la base
        int targetX = startX;
        int targetY = startY;

        if (Math.abs(x - targetX) > 2) {
            x += (x < targetX) ? 4 : -4;
        } else if (Math.abs(y - targetY) > 2) {
            y += (y < targetY) ? 4 : -4;
        } else {
            // Llegó a la base
            x = targetX;
            y = targetY;
            eaten = false;
            scared = false;
            eatenTimer = 0;
        }
    }

    private Direction chooseDirection() {
        if (scared) {
            return chooseRandomDirection();
        }

        // Obtener posición objetivo según el tipo de fantasma
        int[] target = getTargetTile();

        // Encontrar la mejor dirección hacia el objetivo
        return chooseBestDirection(target[0], target[1]);
    }

    private int[] getTargetTile() {
        int pacX = board.getPacmanX();
        int pacY = board.getPacmanY();
        Direction pacDir = board.getPacmanDirection();

        switch (type) {
            case BLINKY: // Persigue directamente a Pacman
                return new int[]{pacX, pacY};

            case PINKY: // Apunta 4 celdas delante de Pacman
                int offsetX = 0, offsetY = 0;
                switch (pacDir) {
                    case LEFT: offsetX = -4 * CELL_SIZE; break;
                    case RIGHT: offsetX = 4 * CELL_SIZE; break;
                    case UP: offsetY = -4 * CELL_SIZE; break;
                    case DOWN: offsetY = 4 * CELL_SIZE; break;
                }
                return new int[]{pacX + offsetX, pacY + offsetY};

            case INKY: // Comportamiento más errático
                if (random.nextInt(3) == 0) {
                    return new int[]{pacX, pacY};
                }
                return new int[]{pacX + random.nextInt(100) - 50, pacY + random.nextInt(100) - 50};

            case CLYDE: // Persigue si está lejos, huye si está cerca
                double dist = Math.sqrt(Math.pow(x - pacX, 2) + Math.pow(y - pacY, 2));
                if (dist > 8 * CELL_SIZE) {
                    return new int[]{pacX, pacY};
                } else {
                    // Ir a la esquina inferior izquierda
                    return new int[]{0, board.getBoardHeight()};
                }

            default:
                return new int[]{pacX, pacY};
        }
    }
    
    private Direction chooseBestDirection(int targetX, int targetY) {
        List<Direction> possibleDirs = getPossibleDirections();

        if (possibleDirs.isEmpty()) {
            return direction;
        }

        Direction bestDir = possibleDirs.get(0);
        double bestDist = Double.MAX_VALUE;

        for (Direction dir : possibleDirs) {
            int nextX = x, nextY = y;
            switch (dir) {
                case LEFT: nextX -= CELL_SIZE; break;
                case RIGHT: nextX += CELL_SIZE; break;
                case UP: nextY -= CELL_SIZE; break;
                case DOWN: nextY += CELL_SIZE; break;
            }

            double dist = Math.sqrt(Math.pow(nextX - targetX, 2) + Math.pow(nextY - targetY, 2));
            if (dist < bestDist) {
                bestDist = dist;
                bestDir = dir;
            }
        }

        return bestDir;
    }

    private Direction chooseRandomDirection() {
        List<Direction> possibleDirs = getPossibleDirections();
        if (possibleDirs.isEmpty()) {
            return direction;
        }
        return possibleDirs.get(random.nextInt(possibleDirs.size()));
    }

    private List<Direction> getPossibleDirections() {
        List<Direction> dirs = new ArrayList<>();
        Direction opposite = getOppositeDirection(direction);

        for (Direction dir : Direction.values()) {
            // No puede dar media vuelta (excepto si es la única opción)
            if (dir == opposite) continue;

            int nextX = x, nextY = y;
            switch (dir) {
                case LEFT: nextX -= CELL_SIZE; break;
                case RIGHT: nextX += CELL_SIZE; break;
                case UP: nextY -= CELL_SIZE; break;
                case DOWN: nextY += CELL_SIZE; break;
            }

            if (board.canMove(nextX, nextY, SIZE)) {
                dirs.add(dir);
            }
        }

        // Si no hay direcciones posibles, permitir dar media vuelta
        if (dirs.isEmpty() && opposite != null) {
            int nextX = x, nextY = y;
            switch (opposite) {
                case LEFT: nextX -= CELL_SIZE; break;
                case RIGHT: nextX += CELL_SIZE; break;
                case UP: nextY -= CELL_SIZE; break;
                case DOWN: nextY += CELL_SIZE; break;
            }
            if (board.canMove(nextX, nextY, SIZE)) {
                dirs.add(opposite);
            }
        }

        return dirs;
    }

    private Direction getOppositeDirection(Direction dir) {
        switch (dir) {
            case LEFT: return Direction.RIGHT;
            case RIGHT: return Direction.LEFT;
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            default: return null;
        }
    }

    private boolean isAligned() {
        return (x % CELL_SIZE == 0) && (y % CELL_SIZE == 0);
    }

    private void alignToGrid() {
        x = Math.round((float) x / CELL_SIZE) * CELL_SIZE;
        y = Math.round((float) y / CELL_SIZE) * CELL_SIZE;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setScared(boolean scared) {
        this.scared = scared;
        if (!scared) {
            this.eaten = false;
        }
    }

    public boolean isScared() { return scared; }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
        if (eaten) {
            this.scared = false;
        }
    }

    public boolean isEaten() { return eaten; }
}
