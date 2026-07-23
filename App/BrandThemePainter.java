import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class BrandThemePainter {

    public static ImageIcon createBrandBanner(String gameTitle, int width, int height, int radius) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Theme.setupGraphics(g2);

        // Clip rounded corners
        g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, radius, radius));

        // Dark Gradient Base
        GradientPaint bgGradient = new GradientPaint(0, 0, Theme.BG_ELEVATED, width, height, Theme.BG_ROOT);
        g2.setPaint(bgGradient);
        g2.fillRect(0, 0, width, height);

        // Draw Ambient Sub-Glow
        RadialGradientPaint radialGlow = new RadialGradientPaint(
                new Point2D.Float(width / 2f, height / 2f),
                width / 1.5f,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(0x63, 0x66, 0xF1, 40), new Color(0, 0, 0, 0)}
        );
        g2.setPaint(radialGlow);
        g2.fillRect(0, 0, width, height);

        // Render Game-Specific Procedural Artwork
        switch (gameTitle) {
            case "Tic Tac Toe":
                drawTicTacToeArt(g2, width, height);
                break;
            case "Snake":
                drawSnakeArt(g2, width, height);
                break;
            case "Pong":
                drawPongArt(g2, width, height);
                break;
            case "Hangman":
                drawHangmanArt(g2, width, height);
                break;
            case "Guess The Number":
                drawGuessArt(g2, width, height);
                break;
            default:
                drawGenericNexaArt(g2, width, height, gameTitle);
                break;
        }

        // Subtle Outer Border Frame
        g2.setClip(null);
        g2.setColor(Theme.BORDER_STROKE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, radius, radius));

        g2.dispose();
        return new ImageIcon(image);
    }

    private static void drawTicTacToeArt(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2;

        // Draw Grid Lines
        g2.setColor(new Color(0x2A, 0x34, 0x47, 180));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 30, cy - 45, cx - 30, cy + 45);
        g2.drawLine(cx + 30, cy - 45, cx + 30, cy + 45);
        g2.drawLine(cx - 75, cy - 15, cx + 75, cy - 15);
        g2.drawLine(cx - 75, cy + 15, cx + 75, cy + 15);

        // Draw Glowing 'X' (Cyan)
        g2.setColor(Theme.ACCENT_CYAN);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 60, cy - 40, cx - 40, cy - 20);
        g2.drawLine(cx - 40, cy - 40, cx - 60, cy - 20);

        // Draw Glowing 'O' (Orange/Indigo)
        g2.setColor(Theme.ACCENT_LIGHT);
        g2.draw(new Ellipse2D.Float(cx + 40, cy + 20, 22, 22));

        // Draw Center 'X'
        g2.setColor(Theme.SUCCESS);
        g2.drawLine(cx - 10, cy - 10, cx + 10, cy + 10);
        g2.drawLine(cx + 10, cy - 10, cx - 10, cy + 10);
    }

    private static void drawSnakeArt(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2;

        // Background Grid Pattern
        g2.setColor(new Color(0x1F, 0x27, 0x38, 120));
        for (int x = 20; x < w; x += 20) g2.drawLine(x, 0, x, h);
        for (int y = 20; y < h; y += 20) g2.drawLine(0, y, w, y);

        // Draw Winding Snake Body (Emerald Green)
        g2.setColor(Theme.SUCCESS);
        g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D snakePath = new Path2D.Float();
        snakePath.moveTo(cx - 70, cy + 20);
        snakePath.curveTo(cx - 30, cy + 40, cx - 10, cy - 30, cx + 30, cy - 10);
        snakePath.curveTo(cx + 50, cy + 10, cx + 60, cy - 20, cx + 70, cy - 30);
        g2.draw(snakePath);

        // Snake Head
        g2.setColor(Theme.ACCENT_CYAN);
        g2.fill(new Ellipse2D.Float(cx + 65, cy - 36, 14, 14));

        // Apple (Glowing Red)
        g2.setColor(Theme.DANGER);
        g2.fill(new Ellipse2D.Float(cx + 30, cy + 25, 14, 14));
    }

    private static void drawPongArt(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2;

        // Center Dashed Net Line
        g2.setColor(Theme.BORDER_STROKE);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0));
        g2.drawLine(cx, 15, cx, h - 15);

        // Left Paddle (Cyan)
        g2.setColor(Theme.ACCENT_CYAN);
        g2.fill(new RoundRectangle2D.Float(cx - 80, cy - 30, 10, 60, 6, 6));

        // Right Paddle (Indigo)
        g2.setColor(Theme.ACCENT_LIGHT);
        g2.fill(new RoundRectangle2D.Float(cx + 70, cy - 10, 10, 60, 6, 6));

        // Glowing Ball & Trail
        g2.setColor(new Color(255, 255, 255, 60));
        g2.fill(new Ellipse2D.Float(cx - 35, cy - 8, 14, 14));
        g2.fill(new Ellipse2D.Float(cx - 20, cy - 3, 14, 14));

        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Float(cx - 5, cy + 2, 14, 14));
    }

    private static void drawHangmanArt(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2;

        // Vector Gallows
        g2.setColor(Theme.TEXT_MUTED);
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - 40, cy + 40, cx + 20, cy + 40); // Base
        g2.drawLine(cx - 20, cy + 40, cx - 20, cy - 40); // Post
        g2.drawLine(cx - 20, cy - 40, cx + 30, cy - 40); // Top
        g2.drawLine(cx + 30, cy - 40, cx + 30, cy - 25); // Rope

        // Stick Figure
        g2.setColor(Theme.ACCENT_CYAN);
        g2.draw(new Ellipse2D.Float(cx + 21, cy - 25, 18, 18)); // Head
        g2.drawLine(cx + 30, cy - 7, cx + 30, cy + 15); // Body
        g2.drawLine(cx + 30, cy, cx + 15, cy + 12); // Left arm
        g2.drawLine(cx + 30, cy, cx + 45, cy + 12); // Right arm

        // Question Badge
        g2.setColor(Theme.ACCENT_LIGHT);
        g2.setFont(Theme.TITLE_H1);
        g2.drawString("?", cx + 55, cy - 10);
    }

    private static void drawGuessArt(Graphics2D g2, int w, int h) {
        int cx = w / 2;
        int cy = h / 2;

        // Glowing Target Rings
        g2.setColor(new Color(0x63, 0x66, 0xF1, 80));
        g2.setStroke(new BasicStroke(3f));
        g2.draw(new Ellipse2D.Float(cx - 45, cy - 45, 90, 90));

        g2.setColor(Theme.ACCENT_CYAN);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new Ellipse2D.Float(cx - 30, cy - 30, 60, 60));

        // Floating Digits
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(Color.WHITE);
        g2.drawString("7", cx - 12, cy + 8);

        g2.setFont(Theme.TITLE_H3);
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString("13", cx - 65, cy - 15);
        g2.drawString("20", cx + 45, cy + 25);
        g2.drawString("1", cx + 40, cy - 20);
        g2.drawString("?", cx - 55, cy + 30);
    }

    private static void drawGenericNexaArt(Graphics2D g2, int w, int h, String titleText) {
        int cx = w / 2;
        int cy = h / 2;

        // Gamepad Emblem
        g2.setColor(Theme.ACCENT_CYAN);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        FontMetrics fmIcon = g2.getFontMetrics();
        String icon = "🎮";
        g2.drawString(icon, cx - fmIcon.stringWidth(icon) / 2, cy - 5);

        // Title Text
        g2.setFont(Theme.TITLE_H3);
        g2.setColor(Theme.TEXT_PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(titleText, cx - fm.stringWidth(titleText) / 2, cy + 35);
    }
}
