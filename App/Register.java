import javax.swing.*;
import java.awt.*;

public class Register extends JFrame {

    private JTextField userField;
    private JTextField emailField;
    private JPasswordField passField;
    private JPasswordField confirmPassField;
    private final int appWidth;
    private final int appHeight;

    public Register(int width, int height) {
        this.appWidth = width;
        this.appHeight = height;

        setTitle("Ankhvault - Create Account");
        setIconImage(Theme.createTransparentLogoIcon(32, 32).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 680);
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
                BorderFactory.createEmptyBorder(28, 36, 28, 36)
        ));

        // Logo Header
        JLabel logo = new JLabel(Theme.createRoundedIcon("Ankhvault Logo.png", 54, 54, 14));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_H1);
        title.setForeground(Theme.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Join Ankhvault to discover and play games", SwingConstants.CENTER);
        subtitle.setFont(Theme.SMALL);
        subtitle.setForeground(Theme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JLabel userLabel = createFieldLabel("Username");
        userField = createTextField();

        JLabel emailLabel = createFieldLabel("Email Address");
        emailField = createTextField();

        JLabel passLabel = createFieldLabel("Password");
        passField = createPasswordField();

        JLabel confirmPassLabel = createFieldLabel("Confirm Password");
        confirmPassField = createPasswordField();

        ModernButton registerBtn = new ModernButton("Create Account", ModernButton.Style.PRIMARY, 12);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        registerBtn.addActionListener(e -> attemptRegister());

        // Login Link
        JPanel loginWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        loginWrap.setOpaque(false);

        JLabel hasAcc = new JLabel("Already have an account?");
        hasAcc.setFont(Theme.SMALL);
        hasAcc.setForeground(Theme.TEXT_MUTED);

        JLabel loginLink = new JLabel("<html><u>Sign In</u></html>");
        loginLink.setFont(Theme.SMALL);
        loginLink.setForeground(Theme.ACCENT_LIGHT);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new Login(appWidth, appHeight).setVisible(true);
            }
        });

        loginWrap.add(hasAcc);
        loginWrap.add(loginLink);

        card.add(logo);
        card.add(Box.createVerticalStrut(10));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));

        card.add(userLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(userField);
        card.add(Box.createVerticalStrut(12));

        card.add(emailLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));

        card.add(passLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));

        card.add(confirmPassLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(confirmPassField);
        card.add(Box.createVerticalStrut(20));

        card.add(registerBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(loginWrap);

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
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setBackground(Theme.BG_ROOT);
        tf.setForeground(Theme.TEXT_PRIMARY);
        tf.setCaretColor(Theme.ACCENT);
        tf.setFont(Theme.BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return tf;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pf.setBackground(Theme.BG_ROOT);
        pf.setForeground(Theme.TEXT_PRIMARY);
        pf.setCaretColor(Theme.ACCENT);
        pf.setFont(Theme.BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return pf;
    }

    private void attemptRegister() {
        String username = userField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String confirmPassword = new String(confirmPassField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = DatabaseManager.getInstance().registerUser(username, email, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Account created successfully! Please sign in.", "Welcome to Ankhvault", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Login(appWidth, appHeight).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}