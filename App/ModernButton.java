import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class ModernButton extends JButton {
    public enum Style {
        PRIMARY,
        SUCCESS,
        OUTLINE,
        GHOST
    }

    private final Style style;
    private final int cornerRadius;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        this(text, Style.PRIMARY, 12);
    }

    public ModernButton(String text, Style style) {
        this(text, style, 12);
    }

    public ModernButton(String text, Style style, int cornerRadius) {
        super(text);
        this.style = style;
        this.cornerRadius = cornerRadius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(Theme.TITLE_H3);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 18, 10, 18));

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
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.setupGraphics(g2);

        int width = getWidth();
        int height = getHeight();

        Color bg;
        Color fg = Theme.TEXT_PRIMARY;
        Color border = null;

        switch (style) {
            case SUCCESS:
                bg = isPressed ? Theme.SUCCESS.darker() : (isHovered ? Theme.SUCCESS.brighter() : Theme.SUCCESS);
                fg = Color.WHITE;
                break;
            case OUTLINE:
                bg = isPressed ? Theme.BG_HOVER : (isHovered ? Theme.BG_ELEVATED : new Color(0, 0, 0, 0));
                border = isHovered ? Theme.ACCENT_LIGHT : Theme.BORDER_STROKE;
                fg = isHovered ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED;
                break;
            case GHOST:
                bg = isPressed ? Theme.BG_HOVER : (isHovered ? Theme.BG_ELEVATED : new Color(0, 0, 0, 0));
                fg = isHovered ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED;
                break;
            case PRIMARY:
            default:
                bg = isPressed ? Theme.ACCENT.darker() : (isHovered ? Theme.ACCENT_LIGHT : Theme.ACCENT);
                fg = Color.WHITE;
                break;
        }

        // Fill background
        if (bg.getAlpha() > 0) {
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
        }

        // Draw border if needed
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, cornerRadius, cornerRadius));
        }

        // Text rendering
        setForeground(fg);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        Rectangle bounds = fm.getStringBounds(getText(), g2).getBounds();

        int x = (width - bounds.width) / 2;
        int y = (height + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(fg);
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}
