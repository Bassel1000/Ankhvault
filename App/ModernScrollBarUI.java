import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernScrollBarUI extends BasicScrollBarUI {
    private final int THUMB_SIZE = 8;

    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = Theme.BORDER_STROKE;
        this.trackColor = Theme.BG_ROOT;
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(THUMB_SIZE * 4, THUMB_SIZE * 4);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        btn.setMinimumSize(new Dimension(0, 0));
        btn.setMaximumSize(new Dimension(0, 0));
        return btn;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Theme.BG_ROOT);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        Theme.setupGraphics(g2);

        boolean isHovered = isThumbRollover();

        g2.setColor(isHovered ? Theme.ACCENT : Theme.BORDER_STROKE);

        int x = thumbBounds.x + 2;
        int y = thumbBounds.y + 2;
        int w = thumbBounds.width - 4;
        int h = thumbBounds.height - 4;

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            w = Math.min(w, THUMB_SIZE);
            x = thumbBounds.x + (thumbBounds.width - w) / 2;
        } else {
            h = Math.min(h, THUMB_SIZE);
            y = thumbBounds.y + (thumbBounds.height - h) / 2;
        }

        g2.fill(new RoundRectangle2D.Float(x, y, w, h, THUMB_SIZE, THUMB_SIZE));
        g2.dispose();
    }
}
