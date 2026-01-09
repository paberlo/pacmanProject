import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {
    private Timer timer;
    private Pacman pacman;
    private Ghost[] ghosts;

    private final int TILE_SIZE = 20; // px
    private final int ROWS = 20;
    private final int COLS = 18;

    private int[][][] levels;
    private int currentLevel = 0;
    private int[][] map;
    private int pelletsRemaining;

    public Board() {
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE + 20));

        loadLevels();
        loadLevel(0);

        pacman = new Pacman(9 * TILE_SIZE, 15 * TILE_SIZE);
        ghosts = new Ghost[] {
            new Ghost(9 * TILE_SIZE, 9 * TILE_SIZE, Color.RED),
            new Ghost(3 * TILE_SIZE, 3 * TILE_SIZE, Color.PINK),
            new Ghost(13 * TILE_SIZE, 3 * TILE_SIZE, Color.CYAN)
        };

        timer = new Timer(40, this);
        timer.start();
        addKeyListener(new PacmanKeyAdapter());
    }

    private void loadLevels() {
        levels = new int[3][][];

        // Level 0: simple border + some pellets inside
        int[][] l0 = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) {
                    l0[r][c] = 1; // wall
                } else {
                    l0[r][c] = 2; // pellet
                }
            }
        }
        // carve a corridor
        for (int c = 2; c < COLS - 2; c++) {
            l0[10][c] = 0;
        }

        // Level 1: more walls
        int[][] l1 = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                l1[r][c] = (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) ? 1 : 2;
            }
        }
        // add a cross of walls
        for (int r = 3; r < ROWS - 3; r++) {
            l1[r][COLS/2] = 1;
        }
        for (int c = 3; c < COLS - 3; c++) {
            l1[ROWS/2][c] = 1;
        }

        // Level 2: maze-like
        int[][] l2 = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                l2[r][c] = (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) ? 1 : 2;
            }
        }
        for (int r = 2; r < ROWS - 2; r += 2) {
            for (int c = 2; c < COLS - 2; c++) {
                l2[r][c] = 1;
            }
        }

        levels[0] = l0;
        levels[1] = l1;
        levels[2] = l2;
    }

    private void loadLevel(int idx) {
        currentLevel = idx;
        map = new int[ROWS][COLS];
        pelletsRemaining = 0;
        int[][] src = levels[idx];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                map[r][c] = src[r][c];
                if (map[r][c] == 2) pelletsRemaining++;
            }
        }
        // reset positions
        pacman = new Pacman(9 * TILE_SIZE, 15 * TILE_SIZE);
        ghosts = new Ghost[] {
            new Ghost(9 * TILE_SIZE, 9 * TILE_SIZE, Color.RED),
            new Ghost(3 * TILE_SIZE, 3 * TILE_SIZE, Color.PINK),
            new Ghost(13 * TILE_SIZE, 3 * TILE_SIZE, Color.CYAN)
        };
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        pacman.draw(g);
        for (Ghost ghost : ghosts) {
            ghost.draw(g);
        }
    }

    private void drawBoard(Graphics g) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;
                switch (map[r][c]) {
                    case 1: // wall
                        g.setColor(Color.BLUE.darker());
                        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        break;
                    case 2: // pellet
                        g.setColor(Color.WHITE);
                        int px = x + TILE_SIZE/2 - 3;
                        int py = y + TILE_SIZE/2 - 3;
                        g.fillOval(px, py, 6, 6);
                        break;
                    default:
                        // empty
                        break;
                }
            }
        }

        g.setColor(Color.YELLOW);
        g.drawString("Score: " + pacman.getScore(), 10, ROWS * TILE_SIZE + 15);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move pacman with collision check
        pacman.move(this);

        // Check pellet consumption
        int pCol = pacman.getX() / TILE_SIZE;
        int pRow = pacman.getY() / TILE_SIZE;
        if (pRow >= 0 && pRow < ROWS && pCol >= 0 && pCol < COLS) {
            if (map[pRow][pCol] == 2) {
                map[pRow][pCol] = 0;
                pelletsRemaining--;
                pacman.addScore(10);
            }
        }

        // move ghosts
        for (Ghost ghost : ghosts) {
            ghost.move(this);
        }

        // next level when pellets finished
        if (pelletsRemaining <= 0) {
            if (currentLevel < levels.length - 1) {
                loadLevel(currentLevel + 1);
            } else {
                loadLevel(0);
            }
        }

        repaint();
    }

    public boolean isWallAtTile(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return true;
        return map[row][col] == 1;
    }

    public boolean isCollisionBoxWall(int x, int y, int w, int h) {
        // check the four corners
        int left = x;
        int right = x + w - 1;
        int top = y;
        int bottom = y + h - 1;

        int[] cols = new int[] { left / TILE_SIZE, right / TILE_SIZE };
        int[] rows = new int[] { top / TILE_SIZE, bottom / TILE_SIZE };

        for (int r : rows) {
            for (int c : cols) {
                if (isWallAtTile(r, c)) return true;
            }
        }
        return false;
    }

    private class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacman.keyPressed(e);
        }
    }
}
