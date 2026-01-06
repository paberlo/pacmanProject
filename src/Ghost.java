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
    
    public enum GhostBehavior {
        CHASER,    // Persigue a Pacman directamente
        AMBUSHER,  // Intenta emboscar a Pacman
        RANDOM     // Movimiento aleatorio
    }

    public Ghost(int x, int y, Color color, Board board) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.board = board;
        this.direction = Direction.values()[random.nextInt(4)];
        
        // Asignar comportamiento basado en el color
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
        // Dibujar ojos
        g.setColor(Color.WHITE);
        g.fillOval(x + 3, y + 5, 6, 6);
        g.fillOval(x + 11, y + 5, 6, 6);
        g.setColor(Color.BLUE);
        g.fillOval(x + 4, y + 6, 4, 4);
        g.fillOval(x + 12, y + 6, 4, 4);
    }

    public void move() {
        // Cambiar de dirección basado en el comportamiento
        // Aumentada la frecuencia de cambios: de 1/15 a 1/8 para RANDOM
        // CHASER y AMBUSHER cambian más frecuentemente: 1/5
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
        
        // Verificar si puede moverse en esa dirección
        if (board.canMove(nextX, nextY, SIZE)) {
            x = nextX;
            y = nextY;
        } else {
            // Si no puede moverse, elegir una dirección válida
            direction = chooseValidDirection();
        }
    }
    
    // Elige una nueva dirección basada en el comportamiento del fantasma
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
    
    // Persigue directamente a Pacman
    private Direction chaseTarget(int targetX, int targetY) {
        int dx = targetX - x;
        int dy = targetY - y;
        
        // Decidir si moverse horizontal o verticalmente
        // Con más peso a la dirección de mayor distancia
        if (Math.abs(dx) > Math.abs(dy)) {
            // Moverse horizontalmente
            if (dx > 0) return Direction.RIGHT;
            else return Direction.LEFT;
        } else if (Math.abs(dy) > Math.abs(dx)) {
            // Moverse verticalmente
            if (dy > 0) return Direction.DOWN;
            else return Direction.UP;
        } else {
            // Distancias iguales, elegir aleatoriamente
            if (random.nextBoolean()) {
                return dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                return dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }
    
    // Intenta emboscar a Pacman (moverse hacia donde va Pacman)
    private Direction ambushTarget(int targetX, int targetY) {
        // Predecir posición futura de Pacman (aproximadamente 4 celdas adelante)
        Direction pacmanDir = board.getPacmanDirection();
        int predictX = targetX;
        int predictY = targetY;
        
        int prediction = 80; // 4 celdas * 20 pixels
        switch (pacmanDir) {
            case LEFT: predictX -= prediction; break;
            case RIGHT: predictX += prediction; break;
            case UP: predictY -= prediction; break;
            case DOWN: predictY += prediction; break;
        }
        
        // Perseguir la posición predicha
        return chaseTarget(predictX, predictY);
    }
    
    // Elige una dirección válida (no bloqueada por paredes)
    private Direction chooseValidDirection() {
        Direction[] directions = Direction.values();
        Direction[] validDirections = new Direction[4];
        int validCount = 0;
        
        // Encontrar todas las direcciones válidas
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
                validDirections[validCount++] = dir;
            }
        }
        
        // Si hay direcciones válidas, elegir una basada en el comportamiento
        if (validCount > 0) {
            if (behavior == GhostBehavior.RANDOM) {
                // Para RANDOM, elegir aleatoriamente entre las válidas
                return validDirections[random.nextInt(validCount)];
            } else {
                // Para CHASER y AMBUSHER, elegir la mejor dirección válida
                Direction targetDir = chooseNewDirection();
                
                // Verificar si la dirección objetivo es válida
                for (int i = 0; i < validCount; i++) {
                    if (validDirections[i] == targetDir) {
                        return targetDir;
                    }
                }
                
                // Si no, elegir la primera dirección válida
                return validDirections[0];
            }
        }
        
        // Si no hay direcciones válidas, mantener la dirección actual
        return direction;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}