import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Procedural Art & Banner Generator for Ankhvault Games
 * Creates high-tech dark glassmorphic banners for real store games.
 */
public class BrandThemePainter {

    public static ImageIcon createBrandBanner(String gameTitle, int width, int height, int radius) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Theme.setupGraphics(g2);

        // Clip rounded corners
        g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, radius, radius));

        // Derive deterministic color accents from title hash
        int hash = (gameTitle != null) ? Math.abs(gameTitle.hashCode()) : 0;
        Color primaryAccent = getAccentFromHash(hash);
        Color secondaryAccent = getSecondaryAccentFromHash(hash);

        // Dark Gradient Base
        GradientPaint bgGradient = new GradientPaint(0, 0, Theme.BG_ELEVATED, width, height, Theme.BG_ROOT);
        g2.setPaint(bgGradient);
        g2.fillRect(0, 0, width, height);

        // Draw Ambient Sub-Glow
        RadialGradientPaint radialGlow = new RadialGradientPaint(
                new Point2D.Float(width * 0.4f, height * 0.4f),
                width * 0.8f,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(primaryAccent.getRed(), primaryAccent.getGreen(), primaryAccent.getBlue(), 55), new Color(0, 0, 0, 0)}
        );
        g2.setPaint(radialGlow);
        g2.fillRect(0, 0, width, height);

        // Grid overlay
        g2.setColor(new Color(255, 255, 255, 12));
        g2.setStroke(new BasicStroke(1.0f));
        for (int x = 20; x < width; x += 24) g2.drawLine(x, 0, x, height);
        for (int y = 20; y < height; y += 24) g2.drawLine(0, y, width, y);

        // Render High-Tech Vector Shield / Emblem
        int cx = width / 2;
        int cy = height / 2 - 10;

        // Outer Hexagon Shield
        g2.setColor(primaryAccent);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D hex = createHexagonPath(cx, cy, 34);
        g2.draw(hex);

        // Inner Vector Glow Circle
        g2.setColor(secondaryAccent);
        g2.fill(new Ellipse2D.Float(cx - 12, cy - 12, 24, 24));

        // Game Title Text Banner
        g2.setFont(Theme.TITLE_H3);
        g2.setColor(Theme.TEXT_PRIMARY);
        FontMetrics fm = g2.getFontMetrics();
        String displayTitle = (gameTitle != null && !gameTitle.trim().isEmpty()) ? gameTitle : "Ankhvault Game";
        g2.drawString(displayTitle, cx - fm.stringWidth(displayTitle) / 2, height - 18);

        // Subtle Outer Border Frame
        g2.setClip(null);
        g2.setColor(Theme.BORDER_STROKE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, radius, radius));

        g2.dispose();
        return new ImageIcon(image);
    }

    private static Path2D createHexagonPath(float cx, float cy, float radius) {
        Path2D p = new Path2D.Float();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            float x = (float) (cx + radius * Math.cos(angle));
            float y = (float) (cy + radius * Math.sin(angle));
            if (i == 0) p.moveTo(x, y);
            else p.lineTo(x, y);
        }
        p.closePath();
        return p;
    }

    private static Color getAccentFromHash(int hash) {
        Color[] accents = {
                Theme.ACCENT,        // Electric Gold
                Theme.ACCENT_CYAN,   // Scarab Turquoise
                Theme.SUCCESS,       // Emerald Accent
                Theme.ACCENT_LIGHT,  // Light Gold
                new Color(0x8B, 0x5C, 0xF6) // Royal Violet
        };
        return accents[hash % accents.length];
    }

    private static Color getSecondaryAccentFromHash(int hash) {
        Color[] secondary = {
                Theme.ACCENT_CYAN,
                Theme.ACCENT,
                Theme.ACCENT_LIGHT,
                Theme.SUCCESS,
                new Color(0xEC, 0x48, 0x99) // Rose
        };
        return secondary[(hash / 5) % secondary.length];
    }
}
