import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class DungeonManager {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SIZE = 30;
    private static final int OBJECT_SIZE = 50;
    private static final int PIXEL_SIZE = 20;

    private Player player;
    private ArrayList<GameObject> gameObjects;
    private GameObject exit;

    private boolean levelComplete = false;
    private int levelCounter = 1;
    private long startTime;

    private static final int[] dx = {-1, 0, 1, 0};
    private static final int[] dy = {0, -1, 0, 1};

    private ArrayList<Point> path;
    private int pathIndex;

    public DungeonManager() {
        player = new Player(WIDTH / 2, HEIGHT / 2);
        gameObjects = new ArrayList<>();
        generateLevel();
        startTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public GameObject getExit() {
        return exit;
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public int getLevelCounter() {
        return levelCounter;
    }

    public ArrayList<Point> getPath() {
        return path;
    }

    public void generateLevel() {
        generateBlocks();
        generateExit();
    }

    private void generateBlocks() {
        Random rand = new Random();
        int numBlocks = (rand.nextInt(30) + WIDTH * HEIGHT) / 14000;

        for (int i = 0; i < numBlocks; i++) {
            int x, y;
            do {
                x = rand.nextInt(WIDTH - OBJECT_SIZE);
                y = rand.nextInt(HEIGHT - OBJECT_SIZE);
            } while (Math.abs(x - player.getX()) < 2 * OBJECT_SIZE && Math.abs(y - player.getY()) < 2 * OBJECT_SIZE);
            gameObjects.add(new GameObject(x, y, OBJECT_SIZE, false));
        }
    }

    private void generateExit() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(WIDTH - OBJECT_SIZE);
            y = rand.nextInt(HEIGHT - OBJECT_SIZE);
        } while (exitCollidesWithBlocks(x, y));
        exit = new GameObject(x, y, OBJECT_SIZE, true);
    }

    private boolean exitCollidesWithBlocks(int x, int y) {
        Rectangle exitRect = new Rectangle(x, y, OBJECT_SIZE, OBJECT_SIZE);
        for (GameObject gameObject : gameObjects) {
            if (exitRect.intersects(gameObject.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    public void restartLevel() {
        gameObjects.clear();
        generateLevel();
        startTime = System.currentTimeMillis();
    }

    public void generateNextLevel() {
        gameObjects.clear();
        generateLevel();
        levelComplete = false;
        levelCounter++;
        startTime = System.currentTimeMillis();
    }

    public void movePlayer(int dx, int dy) {
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
        for (GameObject gameObject : gameObjects) {
            if (gameObject.isBlock() && playerRect.intersects(gameObject.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    private boolean collidesWithExit(int x, int y) {
        Rectangle playerRect = new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
        return playerRect.intersects(exit.getRectangle());
    }

    public void findShortestPath(int targetX, int targetY) {
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
