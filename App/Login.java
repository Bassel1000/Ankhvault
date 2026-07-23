import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private final int appWidth;
    private final int appHeight;

    public Login() {
        this(1280, 800);
    }

    public Login(int width, int height) {
        this.appWidth = width;
        this.appHeight = height;

        setTitle("Ankhvault - Sign In");
        setIconImage(Theme.createTransparentLogoIcon(32, 32).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        Theme.applyRoot(this);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Theme.BG_ROOT);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_ELEVATED);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(32, 36, 32, 36)
        ));

        // Logo Header
        JLabel logo = new JLabel(Theme.createRoundedIcon("Ankhvault Logo.png", 54, 54, 14));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Ankhvault", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_H1);
        title.setForeground(Theme.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to continue to Ankhvault", SwingConstants.CENTER);
        subtitle.setFont(Theme.SMALL);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JLabel userLabel = createFieldLabel("Username");
        userField = createTextField();

        JLabel passLabel = createFieldLabel("Password");
        passField = createPasswordField();

        ModernButton loginBtn = new ModernButton("Sign In", ModernButton.Style.PRIMARY, 12);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> attemptLogin());

        // Register Link
        JPanel registerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerWrap.setOpaque(false);

        JLabel noAcc = new JLabel("Don't have an account?");
        noAcc.setFont(Theme.SMALL);
        noAcc.setForeground(Theme.TEXT_MUTED);

        JLabel regLink = new JLabel("<html><u>Create One</u></html>");
        regLink.setFont(Theme.SMALL);
        regLink.setForeground(Theme.ACCENT_LIGHT);
        regLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new Register(appWidth, appHeight).setVisible(true);
            }
        });

        registerWrap.add(noAcc);
        registerWrap.add(regLink);

        card.add(logo);
        card.add(Box.createVerticalStrut(12));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));

        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(userField);
        card.add(Box.createVerticalStrut(16));

        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passField);
        card.add(Box.createVerticalStrut(24));

        card.add(loginBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(registerWrap);

        mainPanel.add(card);
        add(mainPanel);
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.SMALL);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setBackground(Theme.BG_ROOT);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setCaretColor(Theme.ACCENT);
        tf.setFont(Theme.BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pf.setBackground(Theme.BG_ROOT);
        pf.setForeground(Theme.TEXT_PRIMARY);
        pf.setCaretColor(Theme.ACCENT);
        pf.setFont(Theme.BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    private void attemptLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Sign In Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean valid = DatabaseManager.getInstance().authenticateUser(username, password);
        if (valid) {
            dispose();
            new Ankhvault.Window(appWidth, appHeight, username).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}