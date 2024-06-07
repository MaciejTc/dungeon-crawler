import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Dungeon extends JPanel implements KeyListener, MouseListener {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SIZE = 30;
    private static final int PLAYER_SPEED = 10;
    private static final int BLOCK_SIZE = 50;
    private static final int EXIT_SIZE = 50;
    private static final int PIXEL_SIZE = 20;

    private Player player;
    private ArrayList<Block> blocks;
    private Exit exit;

    private boolean levelComplete = false;
    private int levelCounter = 1;
    private long startTime;

    private static final int[] dx = {-1, 0, 1, 0};
    private static final int[] dy = {0, -1, 0, 1};

    private ArrayList<Point> path;
    private int pathIndex;

    public Dungeon() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        player = new Player(WIDTH / 2, HEIGHT / 2);
        blocks = new ArrayList<>();
        generateLevel();

        startTime = System.currentTimeMillis();

        Timer timer = new Timer(1000 / 60, e -> repaint());
        timer.start();
    }

    private void generateLevel() {
        generateBlocks();
        generateExit();
    }

    private void generateBlocks() {
        Random rand = new Random();
        int numBlocks = (rand.nextInt(30) + WIDTH * HEIGHT) / 14000;

        for (int i = 0; i < numBlocks; i++) {
            int x, y;
            do {
                x = rand.nextInt(WIDTH - BLOCK_SIZE);
                y = rand.nextInt(HEIGHT - BLOCK_SIZE);
            } while (Math.abs(x - player.getX()) < 2 * BLOCK_SIZE && Math.abs(y - player.getY()) < 2 * BLOCK_SIZE);
            blocks.add(new Block(x, y, BLOCK_SIZE));
        }
    }

    private void generateExit() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(WIDTH - EXIT_SIZE);
            y = rand.nextInt(HEIGHT - EXIT_SIZE);
        } while (exitCollidesWithBlocks(x, y));
        exit = new Exit(x, y, EXIT_SIZE);
    }

    private boolean exitCollidesWithBlocks(int x, int y) {
        Rectangle exitRect = new Rectangle(x, y, EXIT_SIZE, EXIT_SIZE);
        for (Block block : blocks) {
            if (exitRect.intersects(block.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private void restartLevel() {
        blocks.clear();
        generateLevel();
        startTime = System.currentTimeMillis();
    }

    private void generateNextLevel() {
        blocks.clear();
        generateLevel();
        levelComplete = false;
        levelCounter++;
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (Block block : blocks) {
            block.draw(g);
        }
        exit.draw(g);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + String.valueOf(levelCounter), 20, 20);

        if (levelComplete) {
            g.setColor(Color.WHITE);
            g.drawString("Level Complete! Press Enter for next level.", WIDTH / 2 - 120, HEIGHT / 2);
        }

        if (path != null && !path.isEmpty()) {
            g.setColor(Color.YELLOW);
            for (int i = 0; i < path.size() - 1; i++) {
                Point p1 = path.get(i);
                Point p2 = path.get(i + 1);
                g.drawLine(p1.x * PIXEL_SIZE + PIXEL_SIZE / 2, p1.y * PIXEL_SIZE + PIXEL_SIZE / 2,
                        p2.x * PIXEL_SIZE + PIXEL_SIZE / 2, p2.y * PIXEL_SIZE + PIXEL_SIZE / 2);
            }
        }

        if (levelCounter > 5) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.WHITE);
            g.drawString("Congrats, you made it through!!!", WIDTH / 2 - 70, HEIGHT / 2);
        }
    }

    private void movePlayer(int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (newX >= 0 && newX <= WIDTH - PLAYER_SIZE && newY >= 0 && newY <= HEIGHT - PLAYER_SIZE) {
            if (!collidesWithBlocks(newX, newY)) {
                if (collidesWithExit(newX, newY)) {
                    levelComplete = true;
                } else {
                    player.move(dx, dy);
                }
            }
        }
    }

    private boolean collidesWithBlocks(int x, int y) {
        Rectangle playerRect = new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
        for (Block block : blocks) {
            if (playerRect.intersects(block.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private boolean collidesWithExit(int x, int y) {
        Rectangle playerRect = new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
        return playerRect.intersects(exit.getRectangle());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key= e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                movePlayer(0, -PLAYER_SPEED);
                break;
            case KeyEvent.VK_DOWN:
                movePlayer(0, PLAYER_SPEED);
                break;
            case KeyEvent.VK_LEFT:
                movePlayer(-PLAYER_SPEED, 0);
                break;
            case KeyEvent.VK_RIGHT:
                movePlayer(PLAYER_SPEED, 0);
                break;
            case KeyEvent.VK_ENTER:
                if (levelComplete) {
                    generateNextLevel();
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
            findShortestPath(mouseX / PIXEL_SIZE, mouseY / PIXEL_SIZE);
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

    private void findShortestPath(int targetX, int targetY) {
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        int[][] distance = new int[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                visited[i][j] = false;
                distance[i][j] = Integer.MAX_VALUE;
            }
        }

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(player.getX() / PIXEL_SIZE, player.getY() / PIXEL_SIZE));
        visited[player.getX() / PIXEL_SIZE][player.getY() / PIXEL_SIZE] = true;
        distance[player.getX() / PIXEL_SIZE][player.getY() / PIXEL_SIZE] = 0;

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int x = current.x;
            int y = current.y;

            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                if (newX >= 0 && newX < WIDTH / PIXEL_SIZE && newY >= 0 && newY < HEIGHT / PIXEL_SIZE
                        && !collidesWithBlocks(newX * PIXEL_SIZE, newY * PIXEL_SIZE) && !visited[newX][newY]) {
                    queue.add(new Point(newX, newY));
                    visited[newX][newY] = true;
                    distance[newX][newY] = distance[x][y] + 1;
                }
            }
        }

        int shortestDistance = distance[targetX][targetY];
        if (shortestDistance != Integer.MAX_VALUE && !collidesWithBlocks(targetX * PIXEL_SIZE, targetY * PIXEL_SIZE)) {
            path = new ArrayList<>();
            int x = targetX;
            int y = targetY;
            while (distance[x][y] != 0) {
                path.add(new Point(x, y));
                for (int i = 0; i < 4; i++) {
                    int newX = x + dx[i];
                    int newY = y + dy[i];
                    if (newX >= 0 && newX < WIDTH / PIXEL_SIZE && newY >= 0 && newY < HEIGHT / PIXEL_SIZE
                            && distance[newX][newY] == distance[x][y] - 1) {
                        x = newX;
                        y = newY;
                        break;
                    }
                }
            }
            pathIndex = path.size() - 1;
        }
    }
}

