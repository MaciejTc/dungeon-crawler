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
        this.lastDirectionY = -1; // Initial direction - up
    }

    public void handleKey(int keyCode) {
        int newX = x;
        int newY = y;

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
            case KeyEvent.VK_H:
                dungeon.toggleHighlightPath(); // Toggle path highlighting
                break;
        }

        if (isWithinBounds(newX, newY) && dungeon.isWalkable(newX, newY)) {
            x = newX;
            y = newY;
            checkNextLevel(newX, newY);
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < Constants.FLOOR_WIDTH && y >= 0 && y < Constants.FLOOR_HEIGHT;
    }

    private void checkNextLevel(int newX, int newY) {
        if (dungeon.getTileType(newX, newY) == 2) {
            dungeon.generateNextLevel();
            x = 1; // Reset player to starting position
            y = 1;
        }
    }

    public void handleMouseClick(Point point) {
        int targetX = point.x / Constants.TILE_SIZE;
        int targetY = point.y / Constants.TILE_SIZE;
        if (isWithinBounds(targetX, targetY) && dungeon.isWalkable(targetX, targetY)) {
            x = targetX;
            y = targetY;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x * Constants.TILE_SIZE, y * Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE);
    }
}
