import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {
    private Timer timer;
    private Pacman pacman;
    private Ghost[] ghosts;
    
    // Tamaño de celda para el mapa
    private static final int CELL_SIZE = 20;
    private static final int BOARD_WIDTH = 19;
    private static final int BOARD_HEIGHT = 19;
    
    // Nivel actual (1, 2 o 3)
    private int currentLevel = 1;
    
    // Mapa del nivel actual: 0 = camino vacío, 1 = pared, 2 = punto
    private int[][] levelMap;
    
    // Definición de los 3 niveles
    // Nivel 1: Diseño simple
    private static final int[][] LEVEL_1 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,2,1,1,1,1,1,2,1,2,1,1,2,1},
        {1,2,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1},
        {1,1,1,1,2,1,1,1,0,1,0,1,1,1,2,1,1,1,1},
        {0,0,0,1,2,1,0,0,0,0,0,0,0,1,2,1,0,0,0},
        {1,1,1,1,2,1,0,1,1,0,1,1,0,1,2,1,1,1,1},
        {0,0,0,0,2,0,0,1,0,0,0,1,0,0,2,0,0,0,0},
        {1,1,1,1,2,1,0,1,1,1,1,1,0,1,2,1,1,1,1},
        {0,0,0,1,2,1,0,0,0,0,0,0,0,1,2,1,0,0,0},
        {1,1,1,1,2,1,0,1,1,1,1,1,0,1,2,1,1,1,1},
        {1,2,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,1,1,2,1,2,1,1,1,2,1,1,2,1},
        {1,2,2,1,2,2,2,2,2,0,2,2,2,2,2,1,2,2,1},
        {1,1,2,1,2,1,2,1,1,1,1,1,2,1,2,1,2,1,1},
        {1,2,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    // Nivel 2: Diseño de complejidad media
    private static final int[][] LEVEL_2 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
        {1,2,1,0,0,1,2,2,2,2,2,2,2,1,0,0,1,2,1},
        {1,2,1,0,0,1,2,1,1,1,1,1,2,1,0,0,1,2,1},
        {1,2,1,1,1,1,2,1,0,0,0,1,2,1,1,1,1,2,1},
        {1,2,2,2,2,2,2,1,0,0,0,1,2,2,2,2,2,2,1},
        {1,2,1,1,1,1,2,1,1,0,1,1,2,1,1,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,0,2,2,2,2,2,2,2,2,1},
        {1,1,1,2,1,1,1,1,2,0,2,1,1,1,1,2,1,1,1},
        {1,2,2,2,2,2,2,2,2,0,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,1,1,2,1,1,0,1,1,2,1,1,1,1,2,1},
        {1,2,2,2,2,2,2,1,0,0,0,1,2,2,2,2,2,2,1},
        {1,2,1,1,1,1,2,1,0,0,0,1,2,1,1,1,1,2,1},
        {1,2,1,0,0,1,2,1,1,1,1,1,2,1,0,0,1,2,1},
        {1,2,1,0,0,1,2,2,2,2,2,2,2,1,0,0,1,2,1},
        {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    // Nivel 3: Diseño complejo
    private static final int[][] LEVEL_3 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,2,2,2,1,2,2,2,2,1,2,2,2,2,1,2,2,2,1},
        {1,2,1,2,1,2,1,1,2,1,2,1,1,2,1,2,1,2,1},
        {1,2,1,2,2,2,2,1,2,2,2,1,2,2,2,2,1,2,1},
        {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,2,1,2,1,1,0,1,1,2,1,2,1,1,2,1},
        {1,2,2,2,2,1,2,1,0,0,0,1,2,1,2,2,2,2,1},
        {1,1,1,1,2,1,2,1,0,0,0,1,2,1,2,1,1,1,1},
        {0,0,0,1,2,2,2,1,1,1,1,1,2,2,2,1,0,0,0},
        {1,1,1,1,2,1,2,2,2,2,2,2,2,1,2,1,1,1,1},
        {1,2,2,2,2,1,2,1,1,1,1,1,2,1,2,2,2,2,1},
        {1,2,1,1,2,1,2,1,0,0,0,1,2,1,2,1,1,2,1},
        {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
        {1,2,1,1,1,1,2,1,1,1,1,1,2,1,1,1,1,2,1},
        {1,2,1,2,2,2,2,1,2,2,2,1,2,2,2,2,1,2,1},
        {1,2,1,2,1,1,2,1,2,1,2,1,2,1,1,2,1,2,1},
        {1,2,2,2,1,2,2,2,2,1,2,2,2,2,1,2,2,2,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    // Contador de puntos restantes
    private int dotsRemaining;

    public Board() {
        setFocusable(true);
        setBackground(Color.BLACK);
        loadLevel(currentLevel);
        addKeyListener(new PacmanKeyAdapter());
    }
    
    // Carga un nivel específico
    public void loadLevel(int level) {
        currentLevel = level;
        
        // Copia el mapa del nivel correspondiente
        int[][] sourceMap;
        switch (level) {
            case 1: sourceMap = LEVEL_1; break;
            case 2: sourceMap = LEVEL_2; break;
            case 3: sourceMap = LEVEL_3; break;
            default: sourceMap = LEVEL_1; break;
        }
        
        // Crear copia del mapa para poder modificarlo
        levelMap = new int[BOARD_HEIGHT][BOARD_WIDTH];
        dotsRemaining = 0;
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                levelMap[y][x] = sourceMap[y][x];
                if (levelMap[y][x] == 2) {
                    dotsRemaining++;
                }
            }
        }
        
        // Posición inicial de Pacman según el nivel (en celda válida con camino)
        // Fila 3, columna 9 es un camino con puntos en todos los niveles
        int pacmanX = 9 * CELL_SIZE;
        int pacmanY = 3 * CELL_SIZE;
        pacman = new Pacman(pacmanX, pacmanY, this);
        
        // Fantasmas en posiciones válidas para todos los niveles
        // Posiciones (9,7), (6,7), (12,7) son caminos en todos los niveles
        ghosts = new Ghost[] {
            new Ghost(9 * CELL_SIZE, 7 * CELL_SIZE, Color.RED, this),
            new Ghost(6 * CELL_SIZE, 7 * CELL_SIZE, Color.PINK, this),
            new Ghost(12 * CELL_SIZE, 7 * CELL_SIZE, Color.CYAN, this)
        };
        
        // Iniciar o reiniciar el timer
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(40, this);
        timer.start();
    }
    
    // Verifica si hay una pared en la posición dada (en coordenadas de pixel)
    public boolean isWall(int pixelX, int pixelY) {
        int cellX = pixelX / CELL_SIZE;
        int cellY = pixelY / CELL_SIZE;
        
        // Verificar límites del mapa
        if (cellX < 0 || cellX >= BOARD_WIDTH || cellY < 0 || cellY >= BOARD_HEIGHT) {
            return true; // Fuera de límites se considera pared
        }
        
        return levelMap[cellY][cellX] == 1;
    }
    
    // Verifica si el movimiento es válido para una entidad (Pacman o Ghost)
    public boolean canMove(int pixelX, int pixelY, int size) {
        // Verificar las 4 esquinas del sprite
        return !isWall(pixelX, pixelY) && 
               !isWall(pixelX + size - 1, pixelY) && 
               !isWall(pixelX, pixelY + size - 1) && 
               !isWall(pixelX + size - 1, pixelY + size - 1);
    }
    
    // Pacman come un punto en la posición dada
    public boolean eatDot(int pixelX, int pixelY) {
        int cellX = (pixelX + CELL_SIZE/2) / CELL_SIZE;
        int cellY = (pixelY + CELL_SIZE/2) / CELL_SIZE;
        
        if (cellX >= 0 && cellX < BOARD_WIDTH && cellY >= 0 && cellY < BOARD_HEIGHT) {
            if (levelMap[cellY][cellX] == 2) {
                levelMap[cellY][cellX] = 0;
                dotsRemaining--;
                return true;
            }
        }
        return false;
    }
    
    // Obtiene los puntos restantes
    public int getDotsRemaining() {
        return dotsRemaining;
    }
    
    // Obtiene el nivel actual
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    // Obtiene la posición X de Pacman (para la IA de los fantasmas)
    public int getPacmanX() {
        return pacman.getX();
    }
    
    // Obtiene la posición Y de Pacman (para la IA de los fantasmas)
    public int getPacmanY() {
        return pacman.getY();
    }
    
    // Obtiene la dirección actual de Pacman (para la IA de los fantasmas)
    public Direction getPacmanDirection() {
        return pacman.getDirection();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        pacman.draw(g);
        for (Ghost ghost : ghosts) {
            ghost.draw(g);
        }
        drawUI(g);
    }

    private void drawBoard(Graphics g) {
        // Dibujar el laberinto y los puntos
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                int pixelX = x * CELL_SIZE;
                int pixelY = y * CELL_SIZE;
                
                if (levelMap[y][x] == 1) {
                    // Dibujar pared
                    g.setColor(Color.BLUE);
                    g.fillRect(pixelX, pixelY, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.CYAN);
                    g.drawRect(pixelX, pixelY, CELL_SIZE - 1, CELL_SIZE - 1);
                } else if (levelMap[y][x] == 2) {
                    // Dibujar punto
                    g.setColor(Color.WHITE);
                    int dotSize = 4;
                    int dotOffset = (CELL_SIZE - dotSize) / 2;
                    g.fillOval(pixelX + dotOffset, pixelY + dotOffset, dotSize, dotSize);
                }
            }
        }
    }
    
    private void drawUI(Graphics g) {
        // Mostrar puntuación y nivel
        g.setColor(Color.YELLOW);
        g.drawString("Score: " + pacman.getScore(), 10, 395);
        g.drawString("Level: " + currentLevel, 150, 395);
        g.drawString("Dots: " + dotsRemaining, 250, 395);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pacman.move();
        for (Ghost ghost : ghosts) {
            ghost.move();
        }
        
        // Verificar si se comieron todos los puntos (avanzar al siguiente nivel)
        if (dotsRemaining == 0) {
            if (currentLevel < 3) {
                loadLevel(currentLevel + 1);
            } else {
                // Victoria - todos los niveles completados
                timer.stop();
                JOptionPane.showMessageDialog(this, "¡Felicidades! Has completado todos los niveles.\nPuntuación final: " + pacman.getScore());
            }
        }
        
        // Verificar colisión con fantasmas
        Rectangle pacmanBounds = new Rectangle(pacman.getX(), pacman.getY(), 20, 20);
        for (Ghost ghost : ghosts) {
            Rectangle ghostBounds = new Rectangle(ghost.getX(), ghost.getY(), 20, 20);
            if (pacmanBounds.intersects(ghostBounds)) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "¡Game Over!\nPuntuación: " + pacman.getScore());
                loadLevel(1); // Reiniciar desde el nivel 1
                break;
            }
        }
        
        repaint();
    }

    private class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacman.keyPressed(e);
        }
    }
}