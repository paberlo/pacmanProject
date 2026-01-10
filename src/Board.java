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
    
    // Sistema de vidas
    private int lives = 3;
    private boolean gameOver = false;
    private boolean gameWon = false;

    // Mapa del nivel actual: 0 = camino vacío, 1 = pared, 2 = punto, 3 = power pellet
    private int[][] levelMap;
    
    // Power mode - cuando Pacman puede comer fantasmas
    private boolean powerMode = false;
    private int powerModeTimer = 0;
    private static final int POWER_MODE_DURATION = 200; // ~8 segundos

    // Definición de los 3 niveles
    // Nivel 1: Diseño simple
    private static final int[][] LEVEL_1 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,3,2,2,2,2,2,2,2,1,2,2,2,2,2,2,2,3,1},
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
        {1,3,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,3,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    // Nivel 2: Diseño de complejidad media
    private static final int[][] LEVEL_2 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,1},
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
        {1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    // Nivel 3: Diseño complejo
    private static final int[][] LEVEL_3 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,3,2,2,1,2,2,2,2,1,2,2,2,2,1,2,2,3,1},
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
        {1,3,2,2,1,2,2,2,2,1,2,2,2,2,1,2,2,3,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    // Contador de puntos restantes
    private int dotsRemaining;

    public Board() {
        setFocusable(true);
        setBackground(Color.BLACK);
        initGame();
        addKeyListener(new PacmanKeyAdapter());
    }
    
    // Inicia un nuevo juego
    private void initGame() {
        lives = 3;
        gameOver = false;
        gameWon = false;
        currentLevel = 1;
        loadLevel(currentLevel);
    }

    // Carga un nivel específico
    public void loadLevel(int level) {
        currentLevel = level;
        powerMode = false;
        powerModeTimer = 0;

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
                if (levelMap[y][x] == 2 || levelMap[y][x] == 3) {
                    dotsRemaining++;
                }
            }
        }
        
        // Posición inicial de Pacman (en un camino seguro)
        int pacmanX = 9 * CELL_SIZE;
        int pacmanY = 3 * CELL_SIZE;
        pacman = new Pacman(pacmanX, pacmanY, this);
        pacman.setDirection(Direction.LEFT);

        // Fantasmas con comportamientos únicos como en el juego original
        ghosts = new Ghost[] {
            new Ghost(9 * CELL_SIZE, 7 * CELL_SIZE, Color.RED, this, Ghost.GhostType.BLINKY),    // Rojo - persigue directamente
            new Ghost(8 * CELL_SIZE, 9 * CELL_SIZE, Color.PINK, this, Ghost.GhostType.PINKY),   // Rosa - embosca
            new Ghost(9 * CELL_SIZE, 9 * CELL_SIZE, Color.CYAN, this, Ghost.GhostType.INKY),    // Cian - errático
            new Ghost(10 * CELL_SIZE, 9 * CELL_SIZE, new Color(255, 184, 82), this, Ghost.GhostType.CLYDE) // Naranja - tímido
        };
        
        // Iniciar o reiniciar el timer
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(33, this); // ~30 FPS
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
    
    // Maneja los atajos (túneles) - teletransporta de un lado al otro
    public int[] handleTunnel(int pixelX, int pixelY) {
        int maxX = (BOARD_WIDTH - 1) * CELL_SIZE;
        int maxY = (BOARD_HEIGHT - 1) * CELL_SIZE;

        // Túnel horizontal (izquierda-derecha)
        if (pixelX < 0) {
            pixelX = maxX;
        } else if (pixelX > maxX) {
            pixelX = 0;
        }

        // Túnel vertical (arriba-abajo)
        if (pixelY < 0) {
            pixelY = maxY;
        } else if (pixelY > maxY) {
            pixelY = 0;
        }

        return new int[]{pixelX, pixelY};
    }

    // Obtiene el ancho del tablero en píxeles
    public int getBoardWidth() {
        return BOARD_WIDTH * CELL_SIZE;
    }

    // Obtiene la altura del tablero en píxeles
    public int getBoardHeight() {
        return BOARD_HEIGHT * CELL_SIZE;
    }

    // Pacman come un punto en la posición dada
    // Retorna: 0 = nada, 1 = punto normal, 2 = power pellet
    public int eatDot(int pixelX, int pixelY) {
        int cellX = (pixelX + CELL_SIZE/2) / CELL_SIZE;
        int cellY = (pixelY + CELL_SIZE/2) / CELL_SIZE;
        
        if (cellX >= 0 && cellX < BOARD_WIDTH && cellY >= 0 && cellY < BOARD_HEIGHT) {
            int cellValue = levelMap[cellY][cellX];
            if (cellValue == 2) {
                levelMap[cellY][cellX] = 0;
                dotsRemaining--;
                return 1; // Punto normal
            } else if (cellValue == 3) {
                levelMap[cellY][cellX] = 0;
                dotsRemaining--;
                activatePowerMode();
                return 2; // Power pellet
            }
        }
        return 0;
    }

    // Activa el modo poder
    private void activatePowerMode() {
        powerMode = true;
        powerModeTimer = POWER_MODE_DURATION;
        for (Ghost ghost : ghosts) {
            ghost.setScared(true);
        }
    }

    // Verifica si está en modo poder
    public boolean isPowerMode() {
        return powerMode;
    }

    // Obtiene el tiempo restante del power mode
    public int getPowerModeTimer() {
        return powerModeTimer;
    }

    // Obtiene la posición X de Pacman
    public int getPacmanX() {
        return pacman.getX();
    }

    // Obtiene la posición Y de Pacman
    public int getPacmanY() {
        return pacman.getY();
    }

    // Obtiene la dirección de Pacman
    public Direction getPacmanDirection() {
        return pacman.getDirection();
    }

    // Obtiene los puntos restantes
    public int getDotsRemaining() {
        return dotsRemaining;
    }
    
    // Obtiene el nivel actual
    public int getCurrentLevel() {
        return currentLevel;
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

        // Dibujar pantalla de Game Over
        if (gameOver) {
            drawGameOver(g);
        }

        // Dibujar pantalla de Victoria
        if (gameWon) {
            drawWin(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String text = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2);
    }

    private void drawWin(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        String text = "¡FELICIDADES!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2);
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
                    // Dibujar punto normal
                    g.setColor(Color.WHITE);
                    int dotSize = 4;
                    int dotOffset = (CELL_SIZE - dotSize) / 2;
                    g.fillOval(pixelX + dotOffset, pixelY + dotOffset, dotSize, dotSize);
                } else if (levelMap[y][x] == 3) {
                    // Dibujar power pellet (más grande y parpadeante)
                    g.setColor(Color.WHITE);
                    int pelletSize = 12;
                    int pelletOffset = (CELL_SIZE - pelletSize) / 2;
                    g.fillOval(pixelX + pelletOffset, pixelY + pelletOffset, pelletSize, pelletSize);
                }
            }
        }
    }
    
    private void drawUI(Graphics g) {
        // Mostrar puntuación, nivel y vidas
        g.setColor(Color.YELLOW);
        g.drawString("Score: " + pacman.getScore(), 10, 395);
        g.drawString("Level: " + currentLevel, 100, 395);
        g.drawString("Dots: " + dotsRemaining, 180, 395);

        // Dibujar vidas como Pacmans pequeños
        g.drawString("Lives: ", 270, 395);
        for (int i = 0; i < lives; i++) {
            g.fillArc(310 + i * 18, 385, 15, 15, 30, 300);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || gameWon) {
            return;
        }

        pacman.move();
        for (Ghost ghost : ghosts) {
            ghost.move();
        }
        
        // Actualizar power mode timer
        if (powerMode) {
            powerModeTimer--;
            if (powerModeTimer <= 0) {
                powerMode = false;
                for (Ghost ghost : ghosts) {
                    ghost.setScared(false);
                }
            }
        }

        // Verificar si se comieron todos los puntos (avanzar al siguiente nivel)
        if (dotsRemaining == 0) {
            if (currentLevel < 3) {
                loadLevel(currentLevel + 1);
            } else {
                // Victoria - todos los niveles completados
                gameWon = true;
                timer.stop();
                JOptionPane.showMessageDialog(this, "¡Felicidades! Has completado todos los niveles.\nPuntuación final: " + pacman.getScore());
                initGame(); // Reiniciar juego
            }
        }
        
        // Verificar colisión con fantasmas
        checkGhostCollision();

        repaint();
    }

    // Verifica colisión con fantasmas
    private void checkGhostCollision() {
        Rectangle pacmanBounds = new Rectangle(pacman.getX() + 2, pacman.getY() + 2, 16, 16);
        for (Ghost ghost : ghosts) {
            if (ghost.isEaten()) continue; // Ignorar fantasmas comidos

            Rectangle ghostBounds = new Rectangle(ghost.getX() + 2, ghost.getY() + 2, 16, 16);
            if (pacmanBounds.intersects(ghostBounds)) {
                if (powerMode && ghost.isScared()) {
                    // Pacman come al fantasma
                    pacman.addScore(200);
                    ghost.setEaten(true);
                } else if (!ghost.isScared()) {
                    // El fantasma atrapa a Pacman
                    lives--;
                    if (lives <= 0) {
                        gameOver = true;
                        timer.stop();
                        int option = JOptionPane.showConfirmDialog(this,
                            "¡Game Over!\nPuntuación: " + pacman.getScore() + "\n¿Jugar de nuevo?",
                            "Game Over", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            initGame();
                        }
                    } else {
                        // Reiniciar posiciones sin perder el progreso del nivel
                        resetPositions();
                    }
                    break;
                }
            }
        }
    }

// Reinicia las posiciones de Pacman y fantasmas sin reiniciar el nivel
    private void resetPositions() {
        powerMode = false;
        powerModeTimer = 0;

        int pacmanX = 9 * CELL_SIZE;
        int pacmanY = 3 * CELL_SIZE;
        pacman.setPosition(pacmanX, pacmanY);
        pacman.setDirection(Direction.LEFT);

        ghosts[0].setPosition(9 * CELL_SIZE, 7 * CELL_SIZE);
        ghosts[0].setScared(false);
        ghosts[1].setPosition(8 * CELL_SIZE, 9 * CELL_SIZE);
        ghosts[1].setScared(false);
        ghosts[2].setPosition(9 * CELL_SIZE, 9 * CELL_SIZE);
        ghosts[2].setScared(false);
        ghosts[3].setPosition(10 * CELL_SIZE, 9 * CELL_SIZE);
        ghosts[3].setScared(false);
    }

    private class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacman.keyPressed(e);
        }
    }
}