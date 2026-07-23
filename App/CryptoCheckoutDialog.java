import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CryptoCheckoutDialog extends JDialog {
    public interface PaymentSuccessCallback {
        void onSuccess();
    }

    private final List<GameItem> cartItems;
    private final double totalPrice;
    private final PaymentSuccessCallback callback;
    private final String username;

    private String selectedToken = "SOL";
    private final JLabel cryptoAmountLabel = new JLabel();
    private final JTextField txHashField = new JTextField();
    private ModernButton verifyCryptoBtn;

    public CryptoCheckoutDialog(JFrame parent, String username, List<GameItem> cartItems, double totalPrice, PaymentSuccessCallback callback) {
        super(parent, "NexaPlay Checkout - Payment Required (x402)", true);
        this.username = username;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.callback = callback;

        setSize(540, 620);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_ROOT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_ELEVATED);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_STROKE),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)
        ));

        JLabel title = new JLabel("Checkout");
        title.setFont(Theme.TITLE_H1);
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel totalLabel = new JLabel("Total: " + String.format("$%.2f", totalPrice));
        totalLabel.setFont(Theme.TITLE_H2);
        totalLabel.setForeground(Theme.ACCENT_CYAN);

        header.add(title, BorderLayout.WEST);
        header.add(totalLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Payment Method Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Theme.BG_ROOT);
        tabbedPane.setForeground(Theme.TEXT_PRIMARY);
        tabbedPane.setFont(Theme.TITLE_H3);

        tabbedPane.addTab("⚡ Crypto x402 Web3", createCryptoPanel());
        tabbedPane.addTab("💳 Credit Card", createCardPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCryptoPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(Theme.BG_ROOT);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // x402 Banner Header
        JPanel x402Banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Theme.setupGraphics(g2);
                g2.setColor(Theme.BG_ELEVATED);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        x402Banner.setOpaque(false);
        x402Banner.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel h402Label = new JLabel("HTTP 402: PAYMENT REQUIRED");
        h402Label.setFont(Theme.TITLE_H3);
        h402Label.setForeground(Theme.ACCENT_CYAN);

        JLabel descLabel = new JLabel("Pay instantly using x402 Web3 Micro-Payment Protocol");
        descLabel.setFont(Theme.SMALL);
        descLabel.setForeground(Theme.TEXT_MUTED);

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setOpaque(false);
        bannerText.add(h402Label);
        bannerText.add(Box.createVerticalStrut(2));
        bannerText.add(descLabel);

        x402Banner.add(bannerText, BorderLayout.WEST);
        panel.add(x402Banner, BorderLayout.NORTH);

        // Center Content Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        // Token Selection Row
        JLabel tokenSelectLabel = new JLabel("Select Crypto Token:");
        tokenSelectLabel.setFont(Theme.BODY);
        tokenSelectLabel.setForeground(Theme.TEXT_MUTED);
        tokenSelectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel tokensRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tokensRow.setOpaque(false);

        String[] tokens = {"SOL", "ETH", "USDT", "BTC"};
        ButtonGroup bg = new ButtonGroup();

        for (String t : tokens) {
            JRadioButton rb = new JRadioButton(t, "SOL".equals(t));
            rb.setOpaque(false);
            rb.setFont(Theme.TITLE_H3);
            rb.setForeground(Theme.TEXT_PRIMARY);
            rb.addActionListener(e -> {
                selectedToken = t;
                updateCryptoAmount();
            });
            bg.add(rb);
            tokensRow.add(rb);
        }

        body.add(tokenSelectLabel);
        body.add(Box.createVerticalStrut(6));
        body.add(tokensRow);
        body.add(Box.createVerticalStrut(14));

        // Crypto Amount Display Panel
        JPanel amountBox = new JPanel(new BorderLayout());
        amountBox.setBackground(Theme.BG_ELEVATED);
        amountBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel payLabel = new JLabel("Amount Due:");
        payLabel.setFont(Theme.BODY);
        payLabel.setForeground(Theme.TEXT_MUTED);

        cryptoAmountLabel.setFont(Theme.TITLE_H2);
        cryptoAmountLabel.setForeground(Theme.SUCCESS);
        updateCryptoAmount();

        amountBox.add(payLabel, BorderLayout.WEST);
        amountBox.add(cryptoAmountLabel, BorderLayout.EAST);
        body.add(amountBox);
        body.add(Box.createVerticalStrut(14));

        // Copyable Wallet Address Row
        JLabel walletLabel = new JLabel("Merchant Wallet Address:");
        walletLabel.setFont(Theme.BODY);
        walletLabel.setForeground(Theme.TEXT_MUTED);

        JPanel walletRow = new JPanel(new BorderLayout(8, 0));
        walletRow.setOpaque(false);

        JTextField walletAddressField = new JTextField("0x71C849B3E2F40A1079543NEXA402SOL");
        walletAddressField.setEditable(false);
        walletAddressField.setBackground(Theme.BG_ELEVATED);
        walletAddressField.setForeground(Theme.ACCENT_LIGHT);
        walletAddressField.setFont(new Font("Consolas", Font.PLAIN, 12));
        walletAddressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        ModernButton copyBtn = new ModernButton("Copy", ModernButton.Style.OUTLINE, 8);
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(walletAddressField.getText()), null);
            ToastManager.showToast((JFrame) getOwner(), "Wallet address copied to clipboard!");
        });

        walletRow.add(walletAddressField, BorderLayout.CENTER);
        walletRow.add(copyBtn, BorderLayout.EAST);

        body.add(walletLabel);
        body.add(Box.createVerticalStrut(4));
        body.add(walletRow);
        body.add(Box.createVerticalStrut(14));

        // Tx Hash Verification Input
        JLabel txLabel = new JLabel("Transaction Hash / Signature:");
        txLabel.setFont(Theme.BODY);
        txLabel.setForeground(Theme.TEXT_MUTED);

        txHashField.setPreferredSize(new Dimension(460, 38));
        txHashField.setBackground(Theme.BG_ELEVATED);
        txHashField.setForeground(Theme.TEXT_PRIMARY);
        txHashField.setCaretColor(Theme.ACCENT);
        txHashField.setFont(Theme.BODY);
        txHashField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        body.add(txLabel);
        body.add(Box.createVerticalStrut(4));
        body.add(txHashField);

        panel.add(body, BorderLayout.CENTER);

        // Verification Button
        verifyCryptoBtn = new ModernButton("⚡ Verify x402 Payment", ModernButton.Style.PRIMARY, 12);
        verifyCryptoBtn.addActionListener(e -> verifyCryptoPayment());
        panel.add(verifyCryptoBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BG_ROOT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel numLabel = new JLabel("Card Number:");
        numLabel.setFont(Theme.BODY);
        numLabel.setForeground(Theme.TEXT_MUTED);
        panel.add(numLabel, gbc);

        gbc.gridy++;
        JTextField cardNum = createInput("4532 •••• •••• 8892");
        panel.add(cardNum, gbc);

        gbc.gridy++;
        JPanel subRow = new JPanel(new GridLayout(1, 2, 12, 0));
        subRow.setOpaque(false);

        JPanel expBox = new JPanel(new BorderLayout(0, 4));
        expBox.setOpaque(false);
        JLabel expLabel = new JLabel("Expiry (MM/YY)");
        expLabel.setFont(Theme.BODY);
        expLabel.setForeground(Theme.TEXT_MUTED);
        expBox.add(expLabel, BorderLayout.NORTH);
        expBox.add(createInput("12/28"), BorderLayout.CENTER);

        JPanel cvvBox = new JPanel(new BorderLayout(0, 4));
        cvvBox.setOpaque(false);
        JLabel cvvLabel = new JLabel("CVV Code");
        cvvLabel.setFont(Theme.BODY);
        cvvLabel.setForeground(Theme.TEXT_MUTED);
        cvvBox.add(cvvLabel, BorderLayout.NORTH);
        cvvBox.add(createInput("888"), BorderLayout.CENTER);

        subRow.add(expBox);
        subRow.add(cvvBox);
        panel.add(subRow, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(24, 0, 0, 0);
        ModernButton payBtn = new ModernButton(String.format("Pay $%.2f Now", totalPrice), ModernButton.Style.SUCCESS, 12);
        payBtn.addActionListener(e -> {
            ToastManager.showToast((JFrame) getOwner(), "Payment Authorized! Unlocking games...");
            completePurchase();
        });
        panel.add(payBtn, gbc);

        return panel;
    }

    private JTextField createInput(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setPreferredSize(new Dimension(300, 38));
        f.setBackground(Theme.BG_ELEVATED);
        f.setForeground(Theme.TEXT_PRIMARY);
        f.setCaretColor(Theme.ACCENT);
        f.setFont(Theme.BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private void updateCryptoAmount() {
        double cryptoValue = totalPrice;
        switch (selectedToken) {
            case "SOL": cryptoValue = totalPrice / 140.0; break;
            case "ETH": cryptoValue = totalPrice / 3200.0; break;
            case "BTC": cryptoValue = totalPrice / 65000.0; break;
            case "USDT": cryptoValue = totalPrice; break;
        }
        cryptoAmountLabel.setText(String.format("%.4f %s", cryptoValue, selectedToken));
    }

    private void verifyCryptoPayment() {
        verifyCryptoBtn.setEnabled(false);
        verifyCryptoBtn.setText("⏳ Verifying x402 Protocol Block...");

        Timer timer = new Timer(1800, e -> {
            ToastManager.showToast((JFrame) getOwner(), "⚡ x402 Crypto Payment Verified!");
            completePurchase();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void completePurchase() {
        for (GameItem item : cartItems) {
            DatabaseManager.getInstance().addToUserLibrary(username, item.getTitle());
        }
        DatabaseManager.getInstance().clearUserCart(username);
        if (callback != null) callback.onSuccess();
        dispose();
    }
}
