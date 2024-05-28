import java.awt.*;
import java.util.Random;

public class Dungeon {
    private int width;
    private int height;
    private int[][] tiles;

    public Dungeon(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new int[width][height];
        generateDungeon();
    }

    private void generateDungeon() {
        Random rand = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 && y >= height / 2 - 1 && y <= height / 2 + 2) {
                    tiles[x][y] = 2; // Niebieski kafelek
                } else {
                    tiles[x][y] = rand.nextInt(2); // 0: podłoga, 1: ściana
                }
            }
        }
    }

    public void generateNextLevel() {
        Random rand = new Random();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 && y >= height / 2 - 1 && y <= height / 2 + 2) {
                    tiles[x][y] = 2; // Niebieski kafelek
                } else {
                    tiles[x][y] = rand.nextInt(2); // 0: podłoga, 1: ściana
                }
            }
        }
    }

    public boolean isWalkable(int x, int y) {
        return tiles[x][y] == 0;
    }

    public int getTileType(int x, int y) {
        return tiles[x][y];
    }

    public void toggleTile(int x, int y) {
        if (tiles[x][y] == 1) {
            tiles[x][y] = 0; // Zmiana ściany na podłogę
        } else if (tiles[x][y] == 0) {
            tiles[x][y] = 1; // Zmiana podłogi na ścianę
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
                    g.setColor(Color.BLUE); // Niebieski kafelek
                }
                g.fillRect(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
            }
        }
    }
}
