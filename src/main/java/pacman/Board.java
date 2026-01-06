package pacman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {
    public static final int TILE_SIZE = 20;
    private static final char WALL = '#';
    private static final char DOT = '.';
    private static final char HEART = 'H';
    private static final char GOLD = 'G';
    private static final char EMPTY = ' ';
    private static final int HALF_HEARTS_PER_HEART = 2;
    private static final int HUD_LIVES_START_X = 100;
    private static final int HUD_LIVES_STEP = 15;
    private static final String[] MAP_TEMPLATE = {
        "####################",
        "#....H.......H.....#",
        "#.######..######...#",
        "#.######..######...#",
        "#..................#",
        "#..####...##...#####",
        "#..####...##...#####",
        "#..H..............H#",
        "#.######..##..######",
        "....................",
        "#...############...#",
        "#..................#",
        "#.######..##..######",
        "#..................#",
        "#..####...##...#####",
        "#......H...........#",
        "#..####...##...#####",
        "#..................#",
        "#....H.........H...#",
        "####################"
    };
    private static final int ROWS = MAP_TEMPLATE.length;
    private static final int COLS = MAP_TEMPLATE[0].length();
    private static final int BOARD_WIDTH = COLS * TILE_SIZE;
    private static final char[][] MAP = new char[ROWS][COLS];
    private Timer timer;
    private Pacman pacman;
    private Ghost[] ghosts;
    // Map and score logic can be defined here

    public Board() {
        setFocusable(true);
        setBackground(Color.BLACK);
        initializeMap();
        pacman = new Pacman(180, 300);
        ensureGoldSpawnIfEligible();
        Point redSpawn = findNearestPointSpawn(new Point(180, 180));
        Point pinkSpawn = findNearestPointSpawn(new Point(60, 60));
        Point weakSpawnA = findNearestPointSpawn(new Point(300, 60));
        Point weakSpawnB = findNearestPointSpawn(new Point(60, 300));
        Point weakSpawnC = findNearestPointSpawn(new Point(300, 300));
        Point extremeSpawn = findNearestPointSpawn(new Point(180, 60));
        ghosts = new Ghost[] {
            new Ghost(weakSpawnA.x, weakSpawnA.y, GhostType.WEAK),
            new Ghost(weakSpawnB.x, weakSpawnB.y, GhostType.WEAK),
            new Ghost(weakSpawnC.x, weakSpawnC.y, GhostType.WEAK),
            new Ghost(pinkSpawn.x, pinkSpawn.y, GhostType.INTERMEDIATE),
            new Ghost(redSpawn.x, redSpawn.y, GhostType.DIFFICULT),
            new Ghost(extremeSpawn.x, extremeSpawn.y, GhostType.EXTREME)
        };
        timer = new Timer(40, this);
        timer.start();
        addKeyListener(new PacmanKeyAdapter());
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
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                char cell = MAP[row][col];
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;
                if (cell == WALL) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                } else if (cell == DOT) {
                    g.setColor(Color.WHITE);
                    g.fillOval(x + TILE_SIZE / 2 - 3, y + TILE_SIZE / 2 - 3, 6, 6);
                } else if (cell == HEART) {
                    g.setColor(Color.PINK);
                    g.fillOval(x + 3, y + 5, 7, 7);
                    g.fillOval(x + 10, y + 5, 7, 7);
                    g.setColor(Color.RED);
                    g.fillPolygon(new int[] {x + 3, x + TILE_SIZE / 2, x + TILE_SIZE - 3}, new int[] {y + 10, y + TILE_SIZE - 4, y + 10}, 3);
                } else if (cell == GOLD) {
                    g.setColor(Color.ORANGE);
                    g.fillOval(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    g.setColor(Color.YELLOW);
                    g.drawOval(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                }
            }
        }
        g.setColor(Color.YELLOW);
        g.drawString("Score: " + pacman.getScore(), 10, 410);
        drawLives(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pacman.move();
        collectItems();
        ensureGoldSpawnIfEligible();
        for (Ghost ghost : ghosts) {
            ghost.move();
            if (intersects(pacman.getBounds(), ghost.getBounds())) {
                pacman.applyDamage(ghost.getDamageHalfHearts());
            }
        }
        if (pacman.isDead()) {
            timer.stop();
        }
        repaint();
    }

    private void initializeMap() {
        for (int r = 0; r < ROWS; r++) {
            MAP[r] = MAP_TEMPLATE[r].toCharArray();
        }
    }

    private void collectItems() {
        Point center = pacman.getCenter();
        int col = center.x / TILE_SIZE;
        int row = center.y / TILE_SIZE;
        if (!isInside(row, col)) {
            return;
        }
        char cell = MAP[row][col];
        if (cell == DOT) {
            MAP[row][col] = EMPTY;
            pacman.addScore(10);
        } else if (cell == HEART) {
            if (!pacman.isAtMaxHealth()) {
                MAP[row][col] = EMPTY;
                pacman.heal(2);
            }
            // Hearts remain when at max; shield spawn handled below
        } else if (cell == GOLD) {
            MAP[row][col] = EMPTY;
            pacman.applyGoldHeart();
        }
        ensureGoldSpawnIfEligible();
    }

    private void drawLives(Graphics g) {
        int startX = HUD_LIVES_START_X;
        int y = 410;
        g.setColor(Color.WHITE);
        g.drawString("Lives:", startX, y);
        int hearts = pacman.getHalfHearts() / HALF_HEARTS_PER_HEART;
        int half = pacman.getHalfHearts() % HALF_HEARTS_PER_HEART;
        int shield = pacman.getShieldHalfHearts();
        int idx = 0;
        for (int i = 0; i < hearts; i++) {
            drawHeartIcon(g, startX + 50 + idx * HUD_LIVES_STEP, y - 12, Color.RED);
            idx++;
        }
        if (half > 0) {
            drawHalfHeartIcon(g, startX + 50 + idx * HUD_LIVES_STEP, y - 12, Color.RED);
            idx++;
        }
        if (shield > 0) {
            g.setColor(Color.YELLOW);
            int shieldHearts = shield / HALF_HEARTS_PER_HEART;
            boolean halfShield = shield % HALF_HEARTS_PER_HEART != 0;
            String shieldText = halfShield ? shieldHearts + ".5♥" : shieldHearts + "♥";
            g.drawString("Shield:" + shieldText, startX + 50 + idx * HUD_LIVES_STEP, y);
        }
    }

    private void drawHeartIcon(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillOval(x, y, 8, 8);
        g.fillOval(x + 6, y, 8, 8);
        g.fillPolygon(new int[] {x, x + 7, x + 14}, new int[] {y + 5, y + 14, y + 5}, 3);
    }

    private void drawHalfHeartIcon(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillOval(x, y, 8, 8);
        g.fillPolygon(new int[] {x, x + 4, x + 8}, new int[] {y + 5, y + 14, y + 5}, 3);
    }

    public static boolean isWallAtPixel(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;
        return !isInside(row, col) || MAP[row][col] == WALL;
    }

    public static boolean isWallCollision(int x, int y, int size) {
        // Corner-based collision detection for tile-sized entities
        return isWallAtPixel(x, y) || isWallAtPixel(x + size - 1, y) || isWallAtPixel(x, y + size - 1) || isWallAtPixel(x + size - 1, y + size - 1);
    }

    private static boolean isInside(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    private Point findNearestPointSpawn(Point preferredPosition) {
        int startCol = preferredPosition.x / TILE_SIZE;
        int startRow = preferredPosition.y / TILE_SIZE;
        if (isPointCell(startCol, startRow)) {
            return toPosition(startCol, startRow);
        }
        int maxRadius = Math.max(ROWS, COLS);
        for (int radius = 1; radius <= maxRadius; radius++) {
            for (int row = startRow - radius; row <= startRow + radius; row++) {
                for (int col = startCol - radius; col <= startCol + radius; col++) {
                    if (Math.abs(row - startRow) + Math.abs(col - startCol) != radius) {
                        continue;
                    }
                    if (isPointCell(col, row)) {
                        return toPosition(col, row);
                    }
                }
            }
        }
        return preferredPosition;
    }

    private boolean isPointCell(int col, int row) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS && MAP[row][col] != WALL;
    }

    private Point toPosition(int col, int row) {
        return new Point(col * TILE_SIZE, row * TILE_SIZE);
    }

    private boolean intersects(Rectangle a, Rectangle b) {
        return a.intersects(b);
    }

    private void ensureGoldSpawnIfEligible() {
        if (!pacman.isAtMaxHealth() || pacman.getShieldHalfHearts() > 0 || goldExists()) {
            return;
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (MAP[row][col] == HEART) {
                    MAP[row][col] = GOLD;
                    return;
                }
            }
        }
    }

    private boolean goldExists() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (MAP[row][col] == GOLD) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int wrapXIfAllowed(int x, int y, int size) {
        int rowTop = y / TILE_SIZE;
        int rowBottom = (y + size - 1) / TILE_SIZE;
        if (rowTop < 0 || rowBottom >= ROWS) {
            return x;
        }
        if (canWrapRow(rowTop) && canWrapRow(rowBottom)) {
            if (x + size <= 0) {
                return BOARD_WIDTH - size;
            }
            if (x >= BOARD_WIDTH) {
                return 0;
            }
        }
        return x;
    }

    private static boolean canWrapRow(int row) {
        return MAP[row][0] != WALL && MAP[row][COLS - 1] != WALL;
    }

    private class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacman.keyPressed(e);
        }
    }
}
