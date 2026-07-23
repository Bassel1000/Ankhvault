import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class NavItem extends JPanel {
    private boolean active = false;
    private boolean isHovered = false;
    private final String label;
    private final Runnable onClick;

    public NavItem(String label, boolean active, Runnable onClick) {
        this.label = label;
        this.active = active;
        this.onClick = onClick;

        setPreferredSize(new Dimension(180, 44));
        setMaximumSize(new Dimension(240, 44));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.setupGraphics(g2);

        int w = getWidth();
        int h = getHeight();

        // Background highlight
        if (active) {
            g2.setColor(Theme.BG_ELEVATED);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 12, 12));

            // Left glowing indicator bar
            g2.setColor(Theme.ACCENT);
            g2.fill(new RoundRectangle2D.Float(2, 8, 5, h - 16, 4, 4));
        } else if (isHovered) {
            g2.setColor(Theme.BG_HOVER);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 12, 12));
        }

        // Text rendering
        g2.setFont(active ? Theme.TITLE_H3 : Theme.BODY);
        g2.setColor(active ? Theme.TEXT_PRIMARY : (isHovered ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED));
        
        FontMetrics fm = g2.getFontMetrics();
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
        int tx = active ? 24 : 16;
        g2.drawString(label, tx, ty);

        g2.dispose();
    }
}
