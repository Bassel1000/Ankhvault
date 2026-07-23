import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ToastManager {
    public static void showToast(JFrame parent, String message) {
        if (parent == null) return;
        JLayeredPane layeredPane = parent.getLayeredPane();
        if (layeredPane == null) return;

        JPanel toast = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.setupGraphics(g2);

                // Glass elevated background
                g2.setColor(new Color(0x16, 0x1B, 0x26, 240));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));

                // Border accent stroke
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        toast.setOpaque(false);
        toast.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 10));

        JLabel label = new JLabel(message);
        label.setFont(Theme.TITLE_H3);
        label.setForeground(Theme.TEXT_PRIMARY);
        toast.add(label);

        int toastW = Math.max(260, label.getPreferredSize().width + 48);
        int toastH = 44;
        int x = (parent.getWidth() - toastW) / 2;
        int y = parent.getHeight() - toastH - 60;

        toast.setBounds(x, y, toastW, toastH);
        layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();

        // Auto remove toast after 2.5 seconds
        Timer timer = new Timer(2500, e -> {
            layeredPane.remove(toast);
            layeredPane.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
}
