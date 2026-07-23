import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public final class Theme {
    private Theme() {}

    // Palette Tokens
    public static final Color BG_ROOT = new Color(0x0B, 0x0E, 0x14);       // Deep Dark Background
    public static final Color BG_ELEVATED = new Color(0x16, 0x1B, 0x26);   // Card & Component Background
    public static final Color BG_HOVER = new Color(0x1F, 0x27, 0x38);      // Elevated Hover Surface
    public static final Color BG_CARD = new Color(0x13, 0x19, 0x24);       // Game Card Surface
    
    public static final Color ACCENT = new Color(0x63, 0x66, 0xF1);        // Primary Electric Indigo
    public static final Color ACCENT_LIGHT = new Color(0x81, 0x8C, 0xF8);  // Light Accent Glow
    public static final Color ACCENT_CYAN = new Color(0x06, 0xB6, 0xD4);   // Secondary Neon Cyan
    public static final Color SUCCESS = new Color(0x10, 0xB9, 0x81);       // Success Emerald Green
    public static final Color DANGER = new Color(0xEF, 0x44, 0x44);        // Danger Red
    public static final Color WARNING = new Color(0xF5, 0x9E, 0x0B);       // Warning Orange
    
    public static final Color TEXT_PRIMARY = new Color(0xFF, 0xFF, 0xFF);   // Pure White Primary Text
    public static final Color TEXT_MUTED = new Color(0x94, 0xA3, 0xB8);     // Muted Slate Text
    public static final Color BORDER_STROKE = new Color(0x2A, 0x34, 0x47);  // Dark Slate Stroke

    // Typography
    public static final Font TITLE_H1 = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font TITLE_H2 = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font TITLE_H3 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // Global Graphics Antialiasing Setup
    public static void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    public static void applyRoot(JFrame frame) {
        frame.getContentPane().setBackground(BG_ROOT);
    }

    public static void applyPanel(JComponent comp) {
        comp.setBackground(BG_ROOT);
        comp.setForeground(TEXT_PRIMARY);
    }

    public static void applyElevated(JComponent comp) {
        comp.setBackground(BG_ELEVATED);
        comp.setForeground(TEXT_PRIMARY);
    }

    // Resolves image file path safely across working directories
    public static File resolveImageFile(String path) {
        if (path == null || path.isEmpty()) return null;
        File f1 = new File(path);
        if (f1.exists()) return f1;
        File f2 = new File("App", path);
        if (f2.exists()) return f2;
        File f3 = new File("App/Snake/resources", path);
        if (f3.exists()) return f3;
        File f4 = new File("Snake/resources", path);
        if (f4.exists()) return f4;
        return null;
    }

    // Creates a rounded Icon from image file OR falls back to Procedural Brand Theme Graphics
    public static ImageIcon createRoundedIcon(String path, int width, int height, int radius) {
        File imgFile = resolveImageFile(path);
        if (imgFile != null) {
            try {
                ImageIcon raw = new ImageIcon(imgFile.getAbsolutePath());
                if (raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
                    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = output.createGraphics();
                    setupGraphics(g2);
                    g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, radius, radius));
                    g2.drawImage(raw.getImage(), 0, 0, width, height, null);
                    g2.dispose();
                    return new ImageIcon(output);
                }
            } catch (Exception ignored) {}
        }

        // Derive title from filename for brand theme artwork fallback
        String title = "NexaPlay";
        if (path != null) {
            if (path.toLowerCase().contains("tic")) title = "Tic Tac Toe";
            else if (path.toLowerCase().contains("snake")) title = "Snake";
            else if (path.toLowerCase().contains("pong")) title = "Pong";
            else if (path.toLowerCase().contains("hangman")) title = "Hangman";
            else if (path.toLowerCase().contains("guess")) title = "Guess The Number";
        }

        return BrandThemePainter.createBrandBanner(title, width, height, radius);
    }
}
