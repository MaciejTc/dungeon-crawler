import java.awt.*;

public class GameObject {
    private int x;
    private int y;
    private final int size;
    private final boolean isExit;

    public GameObject(int x, int y, int size, boolean isExit) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.isExit = isExit;
    }

    public void draw(Graphics g) {
        if (isExit) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.GRAY);
        }
        g.fillRect(x, y, size, size);
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, size, size);
    }

    public boolean isExit() {
        return isExit;
    }

    public boolean isBlock() {
        return !isExit;
    }
}
