import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {
    private Dungeon dungeon;
    private Player player;
    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.handleKey(e.getKeyCode());
                repaint();
            }
        });

        dungeon = new Dungeon(Constants.FLOOR_WIDTH, Constants.FLOOR_HEIGHT);
        player = new Player(dungeon);

        timer = new Timer(16, this); // 60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dungeon.draw(g);
        player.draw(g);
    }
}
