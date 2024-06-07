import java.awt.*;

public class Block {
    private int x;
    private int y;
    private final int size;

    public Block(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, size, size);
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, size, size);
    }
}
