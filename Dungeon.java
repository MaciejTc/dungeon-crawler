import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Dungeon extends JPanel implements KeyListener, MouseListener {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SPEED = 10;
    private static final int PIXEL_SIZE = 20;
    private static final long PATH_DISPLAY_DURATION = 3000;

    private DungeonManager manager;
    private long pathDisplayStartTime;

    public Dungeon() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        manager = new DungeonManager();

        Timer timer = new Timer(1000 / 60, e -> repaint());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        manager.getPlayer().draw(g);
        for (GameObject gameObject : manager.getGameObjects()) {
            gameObject.draw(g);
        }
        manager.getExit().draw(g);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + manager.getLevelCounter(), 20, 20);

        if (manager.isLevelComplete()) {
            g.setColor(Color.WHITE);
            g.drawString("Level Complete! Press Enter for next level.", WIDTH / 2 - 120, HEIGHT / 2);
        }

        if (System.currentTimeMillis() - pathDisplayStartTime <= PATH_DISPLAY_DURATION) {
            ArrayList<Point> path = manager.getPath();
            if (path != null && !path.isEmpty()) {
                g.setColor(Color.YELLOW);
                for (int i = 0; i < path.size() - 1; i++) {
                    Point p1 = path.get(i);
                    Point p2 = path.get(i + 1);
                    g.drawLine(p1.x * PIXEL_SIZE + PIXEL_SIZE / 2, p1.y * PIXEL_SIZE + PIXEL_SIZE / 2,
                            p2.x * PIXEL_SIZE + PIXEL_SIZE / 2, p2.y * PIXEL_SIZE + PIXEL_SIZE / 2);
                }
            }
        }

        if (manager.getLevelCounter() > 5) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString("Congrats, you made it through!!!", WIDTH / 2 - 70, HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                manager.movePlayer(0, -PLAYER_SPEED);
                break;
            case KeyEvent.VK_DOWN:
                manager.movePlayer(0, PLAYER_SPEED);
                break;
            case KeyEvent.VK_LEFT:
                manager.movePlayer(-PLAYER_SPEED, 0);
                break;
            case KeyEvent.VK_RIGHT:
                manager.movePlayer(PLAYER_SPEED, 0);
                break;
            case KeyEvent.VK_ENTER:
                if (manager.isLevelComplete()) {
                    manager.generateNextLevel();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            manager.findShortestPath(mouseX / PIXEL_SIZE, mouseY / PIXEL_SIZE);
            pathDisplayStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
