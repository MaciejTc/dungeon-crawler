import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {
    private int x;
    private int y;
    private Dungeon dungeon;
    private int lastDirectionX;
    private int lastDirectionY;

    public Player(Dungeon dungeon) {
        this.dungeon = dungeon;
        this.x = 1; // Starting position
        this.y = 1;
        this.lastDirectionX = 0;
        this.lastDirectionY = -1; // Początkowy kierunek - góra
    }

    public void handleKey(int keyCode) {
        int newX = x;
        int newY = y;
        boolean toggleMode = false;

        switch (keyCode) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                newY--;
                lastDirectionX = 0;
                lastDirectionY = -1;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                newY++;
                lastDirectionX = 0;
                lastDirectionY = 1;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                newX--;
                lastDirectionX = -1;
                lastDirectionY = 0;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                newX++;
                lastDirectionX = 1;
                lastDirectionY = 0;
                break;
            case KeyEvent.VK_SPACE:
                toggleMode = true;
                break;
        }

        if (toggleMode) {
            toggleAdjacentTile();
        } else if (dungeon.isWalkable(newX, newY)) {
            x = newX;
            y = newY;
            checkNextLevel(newX, newY); // Sprawdzanie przechodzenia na kolejny poziom
        }
    }

    private void toggleAdjacentTile() {
        int adjacentX = x + lastDirectionX;
        int adjacentY = y + lastDirectionY;
        if (adjacentX >= 0 && adjacentX < Constants.FLOOR_WIDTH && adjacentY >= 0 && adjacentY < Constants.FLOOR_HEIGHT) {
            dungeon.toggleTile(adjacentX, adjacentY);
        }
    }

    private void checkNextLevel(int newX, int newY) {
        if (dungeon.getTileType(newX, newY) == 2) {
            dungeon.generateNextLevel();
            x = 1; // Przywróć gracza do początkowej pozycji
            y = 1;
        }
    }

    public void handleMouseClick(Point point) {
        int targetX = point.x / Constants.TILE_SIZE;
        int targetY = point.y / Constants.TILE_SIZE;
        if (dungeon.isWalkable(targetX, targetY)) {
            x = targetX;
            y = targetY;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
    }
}
