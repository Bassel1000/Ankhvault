import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import Snake.code.Snake;

public class Ankhvault {

    public static class Window extends JFrame {
        private final int appWidth;
        private final int appHeight;
        private final String currentUsername;
        private final JPanel contentArea = new JPanel(new CardLayout());
        private final String CARD_STORE = "store";
        private final String CARD_LIBRARY = "library";
        private final String CARD_CART = "cart";
        
        private final ResponsiveWrapGrid storeGrid = new ResponsiveWrapGrid(280, 320, 20);
        private final ResponsiveWrapGrid libraryGrid = new ResponsiveWrapGrid(280, 320, 20);
        private final JPanel emptyLibraryPanel = new JPanel();
        
        private final List<GameCard> storeCards = new ArrayList<>();
        private final List<GameCard> libraryCards = new ArrayList<>();
        
        private NavItem storeNavItem;
        private NavItem libraryNavItem;
        private NavItem cartNavItem;
        private CartPanel cartPanel;
        private JTextField searchField;

        public Window(int width, int height) {
            this(width, height, "Guest");
        }

        public Window(int width, int height, String username) {
            this.appWidth = width;
            this.appHeight = height;
            this.currentUsername = (username != null && !username.trim().isEmpty()) ? username : "Guest";

            setTitle("Ankhvault - Game Store & Launcher");
            setIconImage(Theme.createTransparentLogoIcon(32, 32).getImage());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(width, height);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            Theme.applyRoot(this);
            setLayout(new BorderLayout());

            // Initialize Cart Panel
            cartPanel = new CartPanel(this, currentUsername, this::onCartUpdated);

            // Create Top Header Bar with User Profile
            JPanel topbar = createTopBar();
            add(topbar, BorderLayout.NORTH);

            // Create Sidebar Navigation Menu
            JPanel sidebar = createSidebar();
            add(sidebar, BorderLayout.WEST);

            // Store View Container with Custom Dark ScrollBar
            JScrollPane storeScroll = new JScrollPane(storeGrid);
            storeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            storeScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
            storeScroll.getVerticalScrollBar().setUnitIncrement(18);
            storeScroll.getViewport().setBackground(Theme.BG_ROOT);
            storeScroll.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

            // Library View Container
            JPanel libContainer = new JPanel(new CardLayout());
            libContainer.setBackground(Theme.BG_ROOT);

            JScrollPane libScroll = new JScrollPane(libraryGrid);
            libScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            libScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
            libScroll.getVerticalScrollBar().setUnitIncrement(18);
            libScroll.getViewport().setBackground(Theme.BG_ROOT);
            libScroll.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

            setupEmptyLibraryPanel();

            libContainer.add(libScroll, "grid");
            libContainer.add(emptyLibraryPanel, "empty");

            contentArea.setBackground(Theme.BG_ROOT);
            contentArea.add(storeScroll, CARD_STORE);
            contentArea.add(libContainer, CARD_LIBRARY);
            contentArea.add(cartPanel, CARD_CART);
            add(contentArea, BorderLayout.CENTER);

            // Populate store with catalog
            refreshStoreCards();
            refreshLibraryCards();

            setVisible(true);
        }

        private JPanel createTopBar() {
            JPanel topbar = new JPanel(new BorderLayout());
            topbar.setBackground(Theme.BG_ELEVATED);
            topbar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_STROKE),
                    BorderFactory.createEmptyBorder(12, 24, 12, 24)
            ));

            // Left side brand logo & title with 100% transparent vector emblem
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            left.setOpaque(false);

            JLabel logoLabel = new JLabel(Theme.createTransparentLogoIcon(42, 42));

            JPanel titleWrap = new JPanel();
            titleWrap.setLayout(new BoxLayout(titleWrap, BoxLayout.Y_AXIS));
            titleWrap.setOpaque(false);

            JLabel title = new JLabel("Ankhvault");
            title.setFont(Theme.TITLE_H1);
            title.setForeground(Theme.ACCENT);

            JLabel subtitle = new JLabel("Desktop Game Launcher & Store (Duat Studio)");
            subtitle.setFont(Theme.SMALL);
            subtitle.setForeground(Theme.TEXT_MUTED);

            titleWrap.add(title);
            titleWrap.add(subtitle);

            left.add(logoLabel);
            left.add(titleWrap);

            // Right side: Search field, User profile badge & Logout
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
            right.setOpaque(false);

            searchField = new JTextField();
            searchField.setPreferredSize(new Dimension(280, 38));
            searchField.setBackground(Theme.BG_ROOT);
            searchField.setForeground(Theme.TEXT_PRIMARY);
            searchField.setCaretColor(Theme.ACCENT);
            searchField.setFont(Theme.BODY);
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            
            // Live Search Listener
            searchField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { filterGames(); }
                public void removeUpdate(DocumentEvent e) { filterGames(); }
                public void changedUpdate(DocumentEvent e) { filterGames(); }
            });

            // User Profile Badge
            JPanel userBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Theme.setupGraphics(g2);
                    g2.setColor(Theme.BG_ROOT);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.setColor(Theme.BORDER_STROKE);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            userBadge.setOpaque(false);

            JLabel avatarLabel = new JLabel("👤");
            avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            JLabel userTextLabel = new JLabel(currentUsername);
            userTextLabel.setFont(Theme.TITLE_H3);
            userTextLabel.setForeground(Theme.ACCENT_LIGHT);

            userBadge.add(avatarLabel);
            userBadge.add(userTextLabel);

            ModernButton logout = new ModernButton("Logout", ModernButton.Style.OUTLINE, 10);
            logout.addActionListener(e -> {
                ToastManager.showToast(this, "Logged out successfully");
                dispose();
                new Login(appWidth, appHeight).setVisible(true);
            });

            right.add(searchField);
            right.add(userBadge);
            right.add(logout);

            topbar.add(left, BorderLayout.WEST);
            topbar.add(right, BorderLayout.EAST);
            return topbar;
        }

        private JPanel createSidebar() {
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(Theme.BG_ELEVATED);
            sidebar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER_STROKE),
                    BorderFactory.createEmptyBorder(20, 14, 20, 14)
            ));

            JLabel navTitle = new JLabel("NAVIGATION");
            navTitle.setFont(Theme.SMALL);
            navTitle.setForeground(Theme.TEXT_MUTED);
            navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            storeNavItem = new NavItem("Store", true, () -> showCard(CARD_STORE));
            libraryNavItem = new NavItem("Library", false, () -> showCard(CARD_LIBRARY));
            
            int cartCount = DatabaseManager.getInstance().getUserCart(currentUsername).size();
            String cartLabel = cartCount > 0 ? "Cart (" + cartCount + ")" : "Cart";
            cartNavItem = new NavItem(cartLabel, false, () -> showCard(CARD_CART));

            sidebar.add(navTitle);
            sidebar.add(Box.createVerticalStrut(12));
            sidebar.add(storeNavItem);
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(libraryNavItem);
            sidebar.add(Box.createVerticalStrut(8));
            sidebar.add(cartNavItem);
            sidebar.add(Box.createVerticalGlue());

            return sidebar;
        }

        private void setupEmptyLibraryPanel() {
            emptyLibraryPanel.setLayout(new GridBagLayout());
            emptyLibraryPanel.setBackground(Theme.BG_ROOT);

            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setOpaque(false);

            JLabel icon = new JLabel("☥");
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
            icon.setForeground(Theme.ACCENT);
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel msg = new JLabel("Your Vault Library is Empty");
            msg.setFont(Theme.TITLE_H2);
            msg.setForeground(Theme.TEXT_PRIMARY);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = new JLabel("Explore the Store and unlock games for your Ankhvault library.");
            sub.setFont(Theme.BODY);
            sub.setForeground(Theme.TEXT_MUTED);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            ModernButton browseBtn = new ModernButton("Browse Store", ModernButton.Style.PRIMARY, 12);
            browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            browseBtn.addActionListener(e -> showCard(CARD_STORE));

            center.add(icon);
            center.add(Box.createVerticalStrut(12));
            center.add(msg);
            center.add(Box.createVerticalStrut(6));
            center.add(sub);
            center.add(Box.createVerticalStrut(20));
            center.add(browseBtn);

            emptyLibraryPanel.add(center);
        }

        private void showCard(String name) {
            CardLayout cl = (CardLayout) contentArea.getLayout();
            cl.show(contentArea, name);
            storeNavItem.setActive(CARD_STORE.equals(name));
            libraryNavItem.setActive(CARD_LIBRARY.equals(name));
            cartNavItem.setActive(CARD_CART.equals(name));

            if (CARD_CART.equals(name)) {
                cartPanel.refreshCart();
            }
        }

        private void onCartUpdated() {
            int cartCount = DatabaseManager.getInstance().getUserCart(currentUsername).size();
            String cartLabel = cartCount > 0 ? "Cart (" + cartCount + ")" : "Cart";
            cartNavItem = new NavItem(cartLabel, cartNavItem.isActive(), () -> showCard(CARD_CART));

            refreshStoreCards();
            refreshLibraryCards();
        }

        private void filterGames() {
            String query = searchField.getText().trim().toLowerCase();
            for (GameCard card : storeCards) {
                card.setVisible(query.isEmpty() || card.getGameTitle().toLowerCase().contains(query));
            }
            storeGrid.doLayout();
            storeGrid.revalidate();
            storeGrid.repaint();
        }

        private void refreshStoreCards() {
            storeGrid.removeAll();
            storeCards.clear();

            List<GameItem> catalog = CartPanel.getStoreCatalog();
            for (GameItem item : catalog) {
                GameCard card = new GameCard(
                        item,
                        currentUsername,
                        () -> launchGame(item.getTitle()),
                        () -> {
                            ToastManager.showToast(this, item.getTitle() + " added to Cart!");
                            onCartUpdated();
                            showCard(CARD_CART);
                        }
                );
                storeCards.add(card);
                storeGrid.add(card);
            }
            storeGrid.doLayout();
            storeGrid.revalidate();
            storeGrid.repaint();
        }

        private void refreshLibraryCards() {
            libraryGrid.removeAll();
            libraryCards.clear();

            Set<String> owned = DatabaseManager.getInstance().getUserLibrary(currentUsername);
            List<GameItem> catalog = CartPanel.getStoreCatalog();

            for (GameItem item : catalog) {
                if (owned.contains(item.getTitle()) || item.getPrice() == 0.0) {
                    DatabaseManager.getInstance().addToUserLibrary(currentUsername, item.getTitle());
                    
                    GameCard libCard = new GameCard(
                            item,
                            currentUsername,
                            () -> launchGame(item.getTitle()),
                            null
                    );
                    libraryCards.add(libCard);
                    libraryGrid.add(libCard);
                }
            }

            JPanel libContainer = (JPanel) contentArea.getComponent(1);
            CardLayout cl = (CardLayout) libContainer.getLayout();

            if (libraryCards.isEmpty()) {
                cl.show(libContainer, "empty");
            } else {
                cl.show(libContainer, "grid");
            }

            libraryGrid.doLayout();
            libraryGrid.revalidate();
            libraryGrid.repaint();
        }

        private void launchGame(String title) {
            ToastManager.showToast(this, "Launching " + title + "...");
            switch (title) {
                case "Tic Tac Toe":
                    new TicTacToeGame();
                    break;
                case "Guess The Number":
                    new GuessTheNumberGame();
                    break;
                case "Hangman":
                    new HangmanGame();
                    break;
                case "Snake":
                    JFrame snakeFrame = new Snake();
                    snakeFrame.setVisible(true);
                    break;
                case "Pong":
                    JFrame pongFrame = new JFrame("Pong");
                    pongFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    pongFrame.getContentPane().add(new PongGame());
                    pongFrame.pack();
                    pongFrame.setLocationRelativeTo(null);
                    pongFrame.setVisible(true);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle bounds = env.getMaximumWindowBounds();
            new Window(bounds.width, bounds.height, "Guest");
        });
    }
}
