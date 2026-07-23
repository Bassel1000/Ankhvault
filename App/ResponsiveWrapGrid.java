import javax.swing.*;
import java.awt.*;

public class ResponsiveWrapGrid extends JPanel {
    private int cardWidth = 280;
    private int cardHeight = 320;
    private int gap = 20;

    public ResponsiveWrapGrid() {
        this(280, 320, 20);
    }

    public ResponsiveWrapGrid(int cardWidth, int cardHeight, int gap) {
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.gap = gap;
        setLayout(null);
        Theme.applyPanel(this);
    }

    @Override
    public void doLayout() {
        int width = getWidth();
        if (width <= 0) return;

        int columns = Math.max(1, (width - gap) / (cardWidth + gap));
        int totalGridWidth = columns * cardWidth + (columns - 1) * gap;
        int startX = Math.max(gap, (width - totalGridWidth) / 2);

        Component[] comps = getComponents();
        int row = 0, col = 0;

        for (Component c : comps) {
            if (!c.isVisible()) continue;

            int x = startX + col * (cardWidth + gap);
            int y = gap + row * (cardHeight + gap);
            c.setBounds(x, y, cardWidth, cardHeight);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }

        int visibleCount = 0;
        for (Component c : comps) {
            if (c.isVisible()) visibleCount++;
        }

        int totalRows = (int) Math.ceil((double) visibleCount / columns);
        int totalHeight = Math.max(200, totalRows * (cardHeight + gap) + gap * 2);
        setPreferredSize(new Dimension(width, totalHeight));
    }
}
