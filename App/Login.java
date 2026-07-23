import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login(int width, int height) {
        setTitle("NexaPlay - Sign In");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Theme.resolveImageFile("NexaPlay Icon.png") != null ? Theme.resolveImageFile("NexaPlay Icon.png").getAbsolutePath() : ""));
        setLocationRelativeTo(null);
        Theme.applyRoot(this);

        // Centered Wrapper Panel
        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(Theme.BG_ROOT);

        // Centered Glass Card Container
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.setupGraphics(g2);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(Theme.BG_ELEVATED);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));

                g2.setColor(Theme.BORDER_STROKE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 20, 20));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 480));
        card.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Header Logo & Text
        JLabel logo = new JLabel(Theme.createRoundedIcon("NexaPlay Icon.png", 54, 54, 14));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_H1);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Sign in to continue to NexaPlay", SwingConstants.CENTER);
        subtitle.setFont(Theme.BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);

        JPanel headBox = new JPanel();
        headBox.setLayout(new BoxLayout(headBox, BoxLayout.Y_AXIS));
        headBox.setOpaque(false);
        headBox.add(logo);
        headBox.add(Box.createVerticalStrut(12));
        headBox.add(title);
        headBox.add(Box.createVerticalStrut(4));
        headBox.add(subtitle);

        card.add(headBox, gbc);

        // Username Field
        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 4, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.BODY);
        userLabel.setForeground(Theme.TEXT_MUTED);
        card.add(userLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        usernameField = createStyledTextField();
        card.add(usernameField, gbc);

        // Password Field
        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 4, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.BODY);
        passLabel.setForeground(Theme.TEXT_MUTED);
        card.add(passLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        passwordField = createStyledPasswordField();
        card.add(passwordField, gbc);

        // Login Action Button
        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 8, 0);
        ModernButton loginBtn = new ModernButton("Sign In", ModernButton.Style.PRIMARY, 12);
        loginBtn.addActionListener(e -> attemptLogin(width, height));
        card.add(loginBtn, gbc);

        // Create Account Switch Button
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        ModernButton registerBtn = new ModernButton("Create an Account", ModernButton.Style.OUTLINE, 12);
        registerBtn.addActionListener(e -> {
            new Register(width, height).setVisible(true);
            dispose();
        });
        card.add(registerBtn, gbc);

        rootPanel.add(card);
        getContentPane().add(rootPanel);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(340, 40));
        field.setBackground(Theme.BG_ROOT);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setCaretColor(Theme.ACCENT);
        field.setFont(Theme.BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(340, 40));
        field.setBackground(Theme.BG_ROOT);
        field.setForeground(Theme.TEXT_PRIMARY);
        field.setCaretColor(Theme.ACCENT);
        field.setFont(Theme.BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return field;
    }

    private void attemptLogin(int width, int height) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            ToastManager.showToast(this, "Please enter both username and password");
            return;
        }

        if (DatabaseManager.getInstance().authenticateUser(username, password)) {
            ToastManager.showToast(this, "Welcome back, " + username + "!");
            new NexaPlay.Window(width, height, username).setVisible(true);
            dispose();
        } else {
            ToastManager.showToast(this, "Invalid credentials! Please try again or Register.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle bounds = env.getMaximumWindowBounds();
            new Login(bounds.width, bounds.height).setVisible(true);
        });
    }
}