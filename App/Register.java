import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Register extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Register(int width, int height) {
        setTitle("NexaPlay - Create Account");
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
        card.setPreferredSize(new Dimension(420, 540));
        card.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Header
        JLabel logo = new JLabel(Theme.createRoundedIcon("NexaPlay Icon.png", 54, 54, 14));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_H1);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Join NexaPlay to discover and play games", SwingConstants.CENTER);
        subtitle.setFont(Theme.BODY);
        subtitle.setForeground(Theme.TEXT_MUTED);

        JPanel headBox = new JPanel();
        headBox.setLayout(new BoxLayout(headBox, BoxLayout.Y_AXIS));
        headBox.setOpaque(false);
        headBox.add(logo);
        headBox.add(Box.createVerticalStrut(10));
        headBox.add(title);
        headBox.add(Box.createVerticalStrut(4));
        headBox.add(subtitle);

        card.add(headBox, gbc);

        // Email Field
        gbc.gridy++;
        gbc.insets = new Insets(12, 0, 2, 0);
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.BODY);
        emailLabel.setForeground(Theme.TEXT_MUTED);
        card.add(emailLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        emailField = createStyledTextField();
        card.add(emailField, gbc);

        // Username Field
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 2, 0);
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(Theme.BODY);
        userLabel.setForeground(Theme.TEXT_MUTED);
        card.add(userLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        usernameField = createStyledTextField();
        card.add(usernameField, gbc);

        // Password Field
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 2, 0);
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(Theme.BODY);
        passLabel.setForeground(Theme.TEXT_MUTED);
        card.add(passLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 16, 0);
        passwordField = createStyledPasswordField();
        card.add(passwordField, gbc);

        // Register Action Button
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 8, 0);
        ModernButton registerBtn = new ModernButton("Register", ModernButton.Style.PRIMARY, 12);
        registerBtn.addActionListener(e -> attemptRegister(width, height));
        card.add(registerBtn, gbc);

        // Switch to Login Button
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        ModernButton loginNavBtn = new ModernButton("Already have an account?", ModernButton.Style.OUTLINE, 12);
        loginNavBtn.addActionListener(e -> {
            new Login(width, height).setVisible(true);
            dispose();
        });
        card.add(loginNavBtn, gbc);

        rootPanel.add(card);
        getContentPane().add(rootPanel);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(340, 38));
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
        field.setPreferredSize(new Dimension(340, 38));
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

    private void attemptRegister(int width, int height) {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            ToastManager.showToast(this, "Please fill out all fields");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            ToastManager.showToast(this, "Invalid email format!");
            return;
        }

        if (password.length() < 8) {
            ToastManager.showToast(this, "Password must be at least 8 characters!");
            return;
        }

        boolean success = DatabaseManager.getInstance().registerUser(username, email, password);
        if (success) {
            ToastManager.showToast(this, "Registration Successful! Account created.");
            new Login(width, height).setVisible(true);
            dispose();
        } else {
            ToastManager.showToast(this, "Username already exists! Choose another.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle bounds = env.getMaximumWindowBounds();
            new Register(bounds.width, bounds.height).setVisible(true);
        });
    }
}