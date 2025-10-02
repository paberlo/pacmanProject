import javax.swing.JFrame;

public class Game extends JFrame {
    public Game() {
        add(new Board());
        setTitle("Pac-Man");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 420);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setVisible(true);
    }
}