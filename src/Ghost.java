import java.awt.*;
import java.util.Random;

public class Ghost {
    private int x, y;
    private Direction direction;
    private Color color;
    private Random random = new Random();

    public Ghost(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.direction = Direction.values()[random.nextInt(4)];
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, 20, 20);
    }

    public void move() {
        if (random.nextInt(10) == 0) {
            direction = Direction.values()[random.nextInt(4)];
        }
        switch (direction) {
            case LEFT: x -= 4; break;
            case RIGHT: x += 4; break;
            case UP: y -= 4; break;
            case DOWN: y += 4; break;
        }
        // Aquí puedes agregar lógica de colisiones con el laberinto
    }
}