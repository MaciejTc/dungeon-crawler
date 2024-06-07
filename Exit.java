import java.awt.*;

public class Exit {
    private int x;
    private int y;
    private final int size;

    public Exit(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, size, size);
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, size, size);
    }
}
