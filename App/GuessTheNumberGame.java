import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GuessTheNumberGame extends JFrame {
    private int randomNumber;
    private int attemptsLeft;
    private JTextField guessField;
    private JTextArea outputArea;
    private JLabel attemptsLabel;
    private ModernButton guessBtn;
    private ModernButton restartBtn;
    private boolean gameOver = false;

    public GuessTheNumberGame() {
        setTitle("Ankhvault - Guess The Number");
        setSize(460, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        Theme.applyRoot(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(Theme.BG_ROOT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Guess The Number");
        title.setFont(Theme.TITLE_H2);
        title.setForeground(Theme.TEXT_PRIMARY);

        attemptsLabel = new JLabel("Attempts: 5/5");
        attemptsLabel.setFont(Theme.TITLE_H3);
        attemptsLabel.setForeground(Theme.ACCENT_CYAN);

        header.add(title, BorderLayout.WEST);
        header.add(attemptsLabel, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // Center Log Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(Theme.BODY);
        outputArea.setBackground(Theme.BG_ELEVATED);
        outputArea.setForeground(Theme.TEXT_PRIMARY);
        outputArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true));
        scrollPane.getViewport().setBackground(Theme.BG_ELEVATED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Input & Actions Panel
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setOpaque(false);

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        inputRow.setOpaque(false);

        JLabel prompt = new JLabel("Your Guess (1 - 20):");
        prompt.setFont(Theme.BODY);
        prompt.setForeground(Theme.TEXT_MUTED);

        guessField = new JTextField(8);
        guessField.setPreferredSize(new Dimension(100, 36));
        guessField.setBackground(Theme.BG_ELEVATED);
        guessField.setForeground(Theme.TEXT_PRIMARY);
        guessField.setCaretColor(Theme.ACCENT);
        guessField.setFont(Theme.BODY);
        guessField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_STROKE, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        guessField.addActionListener(e -> checkGuess());

        guessBtn = new ModernButton("Guess", ModernButton.Style.PRIMARY, 10);
        guessBtn.addActionListener(e -> checkGuess());

        inputRow.add(prompt);
        inputRow.add(guessField);
        inputRow.add(guessBtn);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionRow.setOpaque(false);

        restartBtn = new ModernButton("Play Again", ModernButton.Style.OUTLINE, 10);
        restartBtn.addActionListener(e -> startNewGame());
        actionRow.add(restartBtn);

        southPanel.add(inputRow);
        southPanel.add(actionRow);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        startNewGame();
        setVisible(true);
    }

    private void startNewGame() {
        randomNumber = (int) (Math.random() * 20) + 1;
        attemptsLeft = 5;
        gameOver = false;
        guessField.setText("");
        guessField.setEnabled(true);
        guessBtn.setEnabled(true);
        outputArea.setText("🎮 New Game Started! I'm thinking of a number between 1 and 20.\nCan you guess it in 5 attempts?\n\n");
        updateAttemptsDisplay();
    }

    private void updateAttemptsDisplay() {
        attemptsLabel.setText("Attempts: " + attemptsLeft + "/5");
        if (attemptsLeft <= 2) {
            attemptsLabel.setForeground(Theme.DANGER);
        } else {
            attemptsLabel.setForeground(Theme.ACCENT_CYAN);
        }
    }

    private void checkGuess() {
        if (gameOver) return;

        String input = guessField.getText().trim();
        try {
            int guess = Integer.parseInt(input);
            if (guess < 1 || guess > 20) {
                outputArea.append("⚠️ Please enter a valid number between 1 and 20.\n");
                return;
            }

            attemptsLeft--;
            updateAttemptsDisplay();

            if (guess == randomNumber) {
                outputArea.append("🎉 AMAZING! You guessed the correct number: " + randomNumber + "!\n");
                endGame(true);
            } else if (attemptsLeft == 0) {
                outputArea.append("💀 Game Over! The correct number was " + randomNumber + ".\n");
                endGame(false);
            } else {
                if (guess < randomNumber) {
                    outputArea.append("⬆️ Too Low! Try a HIGHER number. (" + attemptsLeft + " attempts left)\n");
                } else {
                    outputArea.append("⬇️ Too High! Try a LOWER number. (" + attemptsLeft + " attempts left)\n");
                }
            }
        } catch (NumberFormatException ex) {
            outputArea.append("⚠️ Invalid entry. Please enter digits only.\n");
        }
        guessField.setText("");
    }

    private void endGame(boolean win) {
        gameOver = true;
        guessField.setEnabled(false);
        guessBtn.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuessTheNumberGame::new);
    }
}
