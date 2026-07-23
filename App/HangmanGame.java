import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Random;

public class HangmanGame extends JFrame {
    private final String[] wordPool = {"LAUNCHER", "GAMING", "STORE", "SWING", "GRAPHICS", "PROJECT", "DEVELOPER", "ANTIGRAVITY"};
    private String secretWord;
    private char[] hiddenChars;
    private int mistakesCount = 0;
    private final int MAX_MISTAKES = 6;

    private final JLabel wordDisplayLabel;
    private final JLabel statusLabel;
    private final HangmanCanvas canvas;
    private final JTextField letterField;
    private final ModernButton guessBtn;

    public HangmanGame() {
        setTitle("Ankhvault - Hangman");
        setSize(520, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        Theme.applyRoot(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 14));
        mainPanel.setBackground(Theme.BG_ROOT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header Status
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Hangman");
        title.setFont(Theme.TITLE_H2);
        title.setForeground(Theme.TEXT_PRIMARY);

        statusLabel = new JLabel("Mistakes: 0 / 6");
        statusLabel.setFont(Theme.TITLE_H3);
        statusLabel.setForeground(Theme.ACCENT_CYAN);

        header.add(title, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Center Area: Custom Antialiased Hangman Gallows & Word Display
        JPanel centerPanel = new JPanel(new BorderLayout(0, 16));
        centerPanel.setOpaque(false);

        canvas = new HangmanCanvas();
        centerPanel.add(canvas, BorderLayout.CENTER);

        wordDisplayLabel = new JLabel("_ _ _ _ _", SwingConstants.CENTER);
        wordDisplayLabel.setFont(new Font("Consolas", Font.BOLD, 28));
        wordDisplayLabel.setForeground(Theme.TEXT_PRIMARY);
        centerPanel.add(wordDisplayLabel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // South Input & Action Row
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        southPanel.setOpaque(false);

        JLabel prompt = new JLabel("Letter:");
        prompt.setFont(Theme.BODY);
        prompt.setForeground(Theme.TEXT_MUTED);

        letterField = new JTextField(4);
        letterField.setPreferredSize(new Dimension(50, 36));
        letterField.setBackground(Theme.BG_ELEVATED);
        letterField.setForeground(Theme.TEXT_PRIMARY);
        letterField.setCaretColor(Theme.ACCENT);
        letterField.setFont(Theme.TITLE_H3);
        letterField.setHorizontalAlignment(JTextField.CENTER);
        letterField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        letterField.addActionListener(e -> checkLetter());

        guessBtn = new ModernButton("Guess", ModernButton.Style.PRIMARY, 10);
        guessBtn.addActionListener(e -> checkLetter());

        ModernButton restartBtn = new ModernButton("New Game", ModernButton.Style.OUTLINE, 10);
        restartBtn.addActionListener(e -> startNewGame());

        southPanel.add(prompt);
        southPanel.add(letterField);
        southPanel.add(guessBtn);
        southPanel.add(restartBtn);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        startNewGame();
        setVisible(true);
    }

    private void startNewGame() {
        secretWord = wordPool[new Random().nextInt(wordPool.length)];
        hiddenChars = new char[secretWord.length()];
        Arrays.fill(hiddenChars, '_');
        mistakesCount = 0;

        letterField.setEnabled(true);
        guessBtn.setEnabled(true);
        letterField.setText("");

        updateDisplay();
    }

    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        for (char c : hiddenChars) {
            sb.append(c).append(" ");
        }
        wordDisplayLabel.setText(sb.toString().trim());

        statusLabel.setText("Mistakes: " + mistakesCount + " / " + MAX_MISTAKES);
        if (mistakesCount >= 4) {
            statusLabel.setForeground(Theme.DANGER);
        } else {
            statusLabel.setForeground(Theme.ACCENT_CYAN);
        }

        canvas.setMistakes(mistakesCount);
    }

    private void checkLetter() {
        if (mistakesCount >= MAX_MISTAKES || Arrays.equals(hiddenChars, secretWord.toCharArray())) return;

        String text = letterField.getText().trim().toUpperCase();
        letterField.setText("");
        if (text.isEmpty()) return;

        char guess = text.charAt(0);
        boolean found = false;

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == guess && hiddenChars[i] == '_') {
                hiddenChars[i] = guess;
                found = true;
            }
        }

        if (!found) {
            mistakesCount++;
        }

        updateDisplay();

        if (Arrays.equals(hiddenChars, secretWord.toCharArray())) {
            statusLabel.setText("🎉 VICTORY! Word Guessed!");
            statusLabel.setForeground(Theme.SUCCESS);
            letterField.setEnabled(false);
            guessBtn.setEnabled(false);
        } else if (mistakesCount >= MAX_MISTAKES) {
            statusLabel.setText("💀 GAME OVER! Word: " + secretWord);
            statusLabel.setForeground(Theme.DANGER);
            wordDisplayLabel.setText(secretWord.replace("", " ").trim());
            letterField.setEnabled(false);
            guessBtn.setEnabled(false);
        }
    }

    // Custom Antialiased Vector Canvas for Gallows & Stick Figure
    private static class HangmanCanvas extends JPanel {
        private int mistakes = 0;

        public HangmanCanvas() {
            setOpaque(false);
            setPreferredSize(new Dimension(240, 200));
        }

        public void setMistakes(int mistakes) {
            this.mistakes = mistakes;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            Theme.setupGraphics(g2);

            int cx = getWidth() / 2 - 40;
            int cy = getHeight() - 20;

            // Draw Dark Elevated Base Card
            g2.setColor(Theme.BG_ELEVATED);
            g2.fillRoundRect(cx - 50, 10, 180, 180, 16, 16);
            g2.setColor(Theme.BORDER_STROKE);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(cx - 50, 10, 180, 180, 16, 16);

            // Draw Gallows
            g2.setColor(Theme.TEXT_MUTED);
            g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.draw(new Line2D.Float(cx - 30, cy - 20, cx + 30, cy - 20)); // Base
            g2.draw(new Line2D.Float(cx - 10, cy - 20, cx - 10, cy - 140)); // Vertical Post
            g2.draw(new Line2D.Float(cx - 10, cy - 140, cx + 50, cy - 140)); // Top Beam
            g2.draw(new Line2D.Float(cx + 50, cy - 140, cx + 50, cy - 120)); // Rope

            // Draw Body Parts based on mistakes
            g2.setColor(Theme.ACCENT_CYAN);

            if (mistakes >= 1) g2.draw(new Ellipse2D.Float(cx + 38, cy - 120, 24, 24)); // Head
            if (mistakes >= 2) g2.draw(new Line2D.Float(cx + 50, cy - 96, cx + 50, cy - 60)); // Body
            if (mistakes >= 3) g2.draw(new Line2D.Float(cx + 50, cy - 90, cx + 30, cy - 70)); // Left Arm
            if (mistakes >= 4) g2.draw(new Line2D.Float(cx + 50, cy - 90, cx + 70, cy - 70)); // Right Arm
            if (mistakes >= 5) g2.draw(new Line2D.Float(cx + 50, cy - 60, cx + 30, cy - 35)); // Left Leg
            if (mistakes >= 6) g2.draw(new Line2D.Float(cx + 50, cy - 60, cx + 70, cy - 35)); // Right Leg

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new);
    }
}
