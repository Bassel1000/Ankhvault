import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class CartPanel extends JPanel {
    private final JFrame parentFrame;
    private final String username;
    private final Runnable onCartUpdated;

    private final JPanel itemsListPanel = new JPanel();
    private final JLabel subtotalLabel = new JLabel("$0.00");
    private final JLabel totalLabel = new JLabel("$0.00");
    private final List<GameItem> currentCartItems = new ArrayList<>();

    public CartPanel(JFrame parentFrame, String username, Runnable onCartUpdated) {
        this.parentFrame = parentFrame;
        this.username = username;
        this.onCartUpdated = onCartUpdated;

        setLayout(new BorderLayout(24, 0));
        setBackground(Theme.BG_ROOT);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left Area: Cart Items List inside ScrollPane
        itemsListPanel.setLayout(new BoxLayout(itemsListPanel, BoxLayout.Y_AXIS));
        itemsListPanel.setBackground(Theme.BG_ROOT);

        JScrollPane scrollPane = new JScrollPane(itemsListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Theme.BG_ROOT);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        add(scrollPane, BorderLayout.CENTER);

        // Right Area: Order Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.EAST);

        refreshCart();
    }

    public void refreshCart() {
        itemsListPanel.removeAll();
        currentCartItems.clear();

        List<GameItem> allGames = getStoreCatalog();
        java.util.Set<String> cartTitles = DatabaseManager.getInstance().getUserCart(username);

        for (GameItem g : allGames) {
            if (cartTitles.contains(g.getTitle())) {
                currentCartItems.add(g);
            }
        }

        if (currentCartItems.isEmpty()) {
            JPanel emptyBox = new JPanel();
            emptyBox.setLayout(new BoxLayout(emptyBox, BoxLayout.Y_AXIS));
            emptyBox.setOpaque(false);

            JLabel icon = new JLabel("🛒");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel msg = new JLabel("Your Shopping Cart is Empty");
            msg.setFont(Theme.TITLE_H2);
            msg.setForeground(Theme.TEXT_PRIMARY);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyBox.add(Box.createVerticalGlue());
            emptyBox.add(icon);
            emptyBox.add(Box.createVerticalStrut(10));
            emptyBox.add(msg);
            emptyBox.add(Box.createVerticalGlue());

            itemsListPanel.add(emptyBox);
        } else {
            for (GameItem item : currentCartItems) {
                itemsListPanel.add(createCartItemCard(item));
                itemsListPanel.add(Box.createVerticalStrut(12));
            }
        }

        double total = currentCartItems.stream().mapToDouble(GameItem::getPrice).sum();
        subtotalLabel.setText(String.format("$%.2f", total));
        totalLabel.setText(String.format("$%.2f", total));

        itemsListPanel.revalidate();
        itemsListPanel.repaint();
    }

    private JPanel createCartItemCard(GameItem item) {
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.setupGraphics(g2);
                g2.setColor(Theme.BG_ELEVATED);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(Theme.BORDER_STROKE);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(500, 80));
        card.setMaximumSize(new Dimension(800, 80));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 14));

        // Thumbnail
        JLabel img = new JLabel(Theme.createRoundedIcon(item.getLogoPath(), 80, 60, 8));
        card.add(img, BorderLayout.WEST);

        // Title & Genre
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel title = new JLabel(item.getTitle());
        title.setFont(Theme.TITLE_H3);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel genre = new JLabel(item.getGenre());
        genre.setFont(Theme.SMALL);
        genre.setForeground(Theme.TEXT_MUTED);

        info.add(title);
        info.add(Box.createVerticalStrut(4));
        info.add(genre);
        card.add(info, BorderLayout.CENTER);

        // Price & Remove Action
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        right.setOpaque(false);

        JLabel price = new JLabel(item.getFormattedPrice());
        price.setFont(Theme.TITLE_H3);
        price.setForeground(Theme.ACCENT_CYAN);

        ModernButton removeBtn = new ModernButton("Remove", ModernButton.Style.GHOST, 8);
        removeBtn.addActionListener(e -> {
            DatabaseManager.getInstance().removeFromUserCart(username, item.getTitle());
            ToastManager.showToast(parentFrame, item.getTitle() + " removed from cart.");
            refreshCart();
            if (onCartUpdated != null) onCartUpdated.run();
        });

        right.add(price);
        right.add(removeBtn);
        card.add(right, BorderLayout.EAST);

        return card;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.setupGraphics(g2);
                g2.setColor(Theme.BG_ELEVATED);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(Theme.BORDER_STROKE);
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 360));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel title = new JLabel("Order Summary");
        title.setFont(Theme.TITLE_H2);
        title.setForeground(Theme.TEXT_PRIMARY);

        content.add(title);
        content.add(Box.createVerticalStrut(16));

        // Subtotal row
        content.add(createSummaryRow("Subtotal", subtotalLabel));
        content.add(Box.createVerticalStrut(8));
        content.add(createSummaryRow("Estimated Tax", new JLabel("$0.00")));
        content.add(Box.createVerticalStrut(12));
        content.add(new JSeparator());
        content.add(Box.createVerticalStrut(12));
        content.add(createSummaryRow("Total", totalLabel));
        content.add(Box.createVerticalStrut(20));

        // Checkout Button
        ModernButton checkoutBtn = new ModernButton("Proceed to Checkout", ModernButton.Style.PRIMARY, 12);
        checkoutBtn.addActionListener(e -> {
            if (currentCartItems.isEmpty()) {
                ToastManager.showToast(parentFrame, "Your cart is empty! Add games from the Store first.");
                return;
            }
            double total = currentCartItems.stream().mapToDouble(GameItem::getPrice).sum();
            CryptoCheckoutDialog dialog = new CryptoCheckoutDialog(parentFrame, username, currentCartItems, total, () -> {
                refreshCart();
                if (onCartUpdated != null) onCartUpdated.run();
            });
            dialog.setVisible(true);
        });

        content.add(checkoutBtn);
        panel.add(content, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createSummaryRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(Theme.BODY);
        label.setForeground(Theme.TEXT_MUTED);

        valueLabel.setFont(Theme.TITLE_H3);
        valueLabel.setForeground(Theme.TEXT_PRIMARY);

        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    public static List<GameItem> getStoreCatalog() {
        List<GameItem> list = new ArrayList<>();
        list.add(new GameItem("tic", "Tic Tac Toe", "Tic Tac Toe Logo.jpeg", 0.00, "Puzzle / Classic", "Classic strategy grid game for two players."));
        list.add(new GameItem("snake", "Snake", "Snake Game Logo.jpeg", 4.99, "Arcade / Retro", "Retro snake game with smooth movement and live scoring."));
        list.add(new GameItem("guess", "Guess The Number", "Guess the Number Logo.jpeg", 4.99, "Casual", "Challenge your mind to guess the hidden number in 5 tries."));
        list.add(new GameItem("pong", "Pong", "Pong Game Logo.jpeg", 9.99, "Arcade", "Neon 2-player arcade pong with responsive paddle controls."));
        list.add(new GameItem("hangman", "Hangman", "Hangman Logo.jpeg", 14.99, "Word / Trivia", "Test your vocabulary skills against the gallows."));
        return list;
    }
}
