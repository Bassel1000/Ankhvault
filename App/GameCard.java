import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class GameCard extends JPanel {
    public interface LaunchHandler { void onLaunch(); }
    public interface CartHandler { void onCartAction(); }

    private final GameItem item;
    private final String username;
    private final LaunchHandler onLaunch;
    private final CartHandler onCartAction;

    private boolean isHovered = false;
    private int translateY = 0;
    private final int MAX_TRANSLATE = 6;
    private Timer animTimer;

    public GameCard(GameItem item, String username, LaunchHandler onLaunch, CartHandler onCartAction) {
        this.item = item;
        this.username = username;
        this.onLaunch = onLaunch;
        this.onCartAction = onCartAction;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setPreferredSize(new Dimension(280, 320));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Procedural Brand Artwork Thumbnail
        JPanel thumbContainer = new JPanel(new BorderLayout());
        thumbContainer.setOpaque(false);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setIcon(BrandThemePainter.createBrandBanner(item.getTitle(), 256, 140, 12));
        thumbContainer.add(imageLabel, BorderLayout.CENTER);

        add(thumbContainer, BorderLayout.NORTH);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(item.getTitle());
        titleLabel.setFont(Theme.TITLE_H2);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel genreLabel = new JLabel(item.getGenre() + " • " + item.getFormattedPrice());
        genreLabel.setFont(Theme.SMALL);
        genreLabel.setForeground(Theme.ACCENT_CYAN);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(genreLabel);
        add(infoPanel, BorderLayout.CENTER);

        // Buttons Action Panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actions.setOpaque(false);

        updateActionButtons(actions);
        add(actions, BorderLayout.SOUTH);

        // Mouse elevation listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                animateLift(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                animateLift(false);
            }
        });
    }

    public void updateActionButtons(JPanel actions) {
        actions.removeAll();

        boolean owned = DatabaseManager.getInstance().isGameOwned(username, item.getTitle());
        boolean inCart = DatabaseManager.getInstance().isGameInCart(username, item.getTitle());

        if (owned) {
            ModernButton playButton = new ModernButton("Play Now", ModernButton.Style.SUCCESS, 10);
            if (onLaunch != null) playButton.addActionListener(e -> onLaunch.onLaunch());
            actions.add(playButton);
        } else if (inCart) {
            ModernButton cartBtn = new ModernButton("In Cart 🛒", ModernButton.Style.OUTLINE, 10);
            if (onCartAction != null) cartBtn.addActionListener(e -> onCartAction.onCartAction());
            actions.add(cartBtn);
        } else {
            ModernButton addCartBtn = new ModernButton("+ Cart", ModernButton.Style.OUTLINE, 10);
            addCartBtn.addActionListener(e -> {
                DatabaseManager.getInstance().addToUserCart(username, item.getTitle());
                if (onCartAction != null) onCartAction.onCartAction();
            });

            ModernButton buyBtn = new ModernButton("Buy " + item.getFormattedPrice(), ModernButton.Style.PRIMARY, 10);
            buyBtn.addActionListener(e -> {
                DatabaseManager.getInstance().addToUserCart(username, item.getTitle());
                if (onCartAction != null) onCartAction.onCartAction();
            });

            actions.add(addCartBtn);
            actions.add(buyBtn);
        }

        actions.revalidate();
        actions.repaint();
    }

    public String getGameTitle() {
        return item.getTitle();
    }

    private void animateLift(boolean liftUp) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        animTimer = new Timer(12, e -> {
            if (liftUp && translateY < MAX_TRANSLATE) {
                translateY++;
                repaint();
            } else if (!liftUp && translateY > 0) {
                translateY--;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.setupGraphics(g2);

        int width = getWidth();
        int height = getHeight();
        int yOffset = -translateY;

        g2.setColor(isHovered ? Theme.BG_HOVER : Theme.BG_CARD);
        g2.fill(new RoundRectangle2D.Float(0, yOffset + 4, width, height - 8, 16, 16));

        if (isHovered) {
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new RoundRectangle2D.Float(1, yOffset + 5, width - 2, height - 10, 16, 16));
        } else {
            g2.setColor(Theme.BORDER_STROKE);
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Float(0, yOffset + 4, width - 1, height - 9, 16, 16));
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
