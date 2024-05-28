import java.awt.*;
import java.util.*;
import java.util.List;

public class Dungeon {
    private int width;
    private int height;
    private int[][] tiles;
    private List<Point> path;
    private boolean highlightPath;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[width][height];
        this.path = new ArrayList<>();
        this.highlightPath = false;
        generateDungeon();
    }

    private void generateDungeon() {
        Random rand = new Random();

        // Generate random walls and floors
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = rand.nextInt(2); // Randomly generate walls (1) and floors (0)
            }
        }

        // Ensure there's an exit path in the center
        for (int y = height / 2 - 1; y <= height / 2 + 2; y++) {
            tiles[0][y] = 2; // Blue tile indicating the next level entrance
        }

        // Create a simple path from start to exit, changing walls to floors
        createPath();

        // Generate the shortest path using Dijkstra's algorithm
        path = generatePath();
    }

    private void createPath() {
        int startX = 1;
        int startY = 1;
        int endX = 0;
        int endY = height / 2;

        int currentX = startX;
        int currentY = startY;

        while (currentX != endX || currentY != endY) {
            tiles[currentX][currentY] = 0; // Ensure this tile is a floor

            // Decide next step towards the end
            if (currentX > endX) {
                currentX--;
            } else if (currentX < endX) {
                currentX++;
            } else if (currentY > endY) {
                currentY--;
            } else if (currentY < endY) {
                currentY++;
            }
        }
    }

    private List<Point> generatePath() {
        int startX = 1;
        int startY = 1;
        int endX = 0;
        int endY = height / 2;

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
        pq.add(new Node(startX, startY, 0));

        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> costSoFar = new HashMap<>();
        cameFrom.put(new Point(startX, startY), null);
        costSoFar.put(new Point(startX, startY), 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Point currentPoint = new Point(current.x, current.y);

            if (current.x == endX && current.y == endY) {
                return reconstructPath(cameFrom, new Point(endX, endY));
            }

            for (Point next : getNeighbors(current.x, current.y)) {
                int newCost = costSoFar.get(currentPoint) + 1;
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    pq.add(new Node(next.x, next.y, newCost));
                    cameFrom.put(next, currentPoint);
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Point> getNeighbors(int x, int y) {
        List<Point> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isWithinBounds(newX, newY) && tiles[newX][newY] != 1) {
                neighbors.add(new Point(newX, newY));
            }
        }
        return neighbors;
    }

    private List<Point> reconstructPath(Map<Point, Point> cameFrom, Point end) {
        List<Point> path = new ArrayList<>();
        for (Point at = end; at != null; at = cameFrom.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public void generateNextLevel() {
        generateDungeon(); // Reuse the same dungeon generation method
    }

    public boolean isWalkable(int x, int y) {
        return isWithinBounds(x, y) && (tiles[x][y] == 0 || tiles[x][y] == 2);
    }

    public int getTileType(int x, int y) {
        return tiles[x][y];
    }

    public void toggleTile(int x, int y) {
        if (tiles[x][y] == 1) {
            tiles[x][y] = 0; // Change wall to floor
        } else if (tiles[x][y] == 0) {
            tiles[x][y] = 1; // Change floor to wall
        }
    }

    public void draw(Graphics g) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] == 0) {
                    g.setColor(Color.GRAY);
                } else if (tiles[x][y] == 1) {
                    g.setColor(Color.DARK_GRAY);
                } else if (tiles[x][y] == 2) {
                    g.setColor(Color.BLUE); // Blue tile
                }
                g.fillRect(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
            }
        }
        if (highlightPath) {
            g.setColor(Color.YELLOW);
            for (Point p : path) {
                g.fillRect(p.x * Constants.TILE_SIZE, p.y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
            }
        }
    }

    public void toggleHighlightPath() {
        highlightPath = !highlightPath;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private static class Node {
        int x;
        int y;
        int cost;

        Node(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }
    }
}
