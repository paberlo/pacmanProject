import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {
    private Timer timer;
    private Pacman pacman;
    private Ghost[] ghosts;
    // Aquí puedes definir el mapa y la lógica de puntos

    public Board() {
        setFocusable(true);
        setBackground(Color.BLACK);
        pacman = new Pacman(180, 300);
        ghosts = new Ghost[] {
            new Ghost(180, 180, Color.RED),
            new Ghost(60, 60, Color.PINK),
            new Ghost(300, 60, Color.CYAN)
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
        // Aquí puedes dibujar el laberinto y los puntos
        g.setColor(Color.YELLOW);
        g.drawString("Score: " + pacman.getScore(), 10, 410);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pacman.move();
        for (Ghost ghost : ghosts) {
            ghost.move();
        }
        // Aquí puedes agregar colisiones y lógica de puntos
        repaint();
    }

    private class PacmanKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            pacman.keyPressed(e);
        }
    }
}