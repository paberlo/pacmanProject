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

    public Ghost(int x, int y, Color color, Board board) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.board = board;
        this.direction = Direction.values()[random.nextInt(4)];
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
        // Cambiar de dirección aleatoriamente de vez en cuando
        if (random.nextInt(15) == 0) {
            direction = Direction.values()[random.nextInt(4)];
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
            // Si no puede moverse, elegir otra dirección aleatoria
            direction = Direction.values()[random.nextInt(4)];
        }
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}