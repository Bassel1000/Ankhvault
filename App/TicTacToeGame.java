import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class TicTacToeGame extends JFrame {
    private final JButton[][] buttons = new JButton[3][3];
    private char currentPlayer = 'X';
    private boolean gameOver = false;
    private final JLabel statusLabel;
    private final ModernButton restartBtn;

    public TicTacToeGame() {
        setTitle("Ankhvault - Tic Tac Toe");
        setSize(440, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        Theme.applyRoot(this);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 16));
        mainPanel.setBackground(Theme.BG_ROOT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Status Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Tic Tac Toe");
        titleLabel.setFont(Theme.TITLE_H2);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        statusLabel = new JLabel("Player X's Turn");
        statusLabel.setFont(Theme.TITLE_H3);
        statusLabel.setForeground(Theme.ACCENT_CYAN);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 3x3 Grid Board Container
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(Theme.BG_ROOT);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int r = row;
                final int c = col;
                JButton cell = new JButton("") {
                    private boolean isHovered = false;

                    {
                        setContentAreaFilled(false);
                        setFocusPainted(false);
                        setBorderPainted(false);
                        setFont(new Font("Segoe UI", Font.BOLD, 44));
                        setCursor(new Cursor(Cursor.HAND_CURSOR));

                        addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                            public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                        });
                    }

                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        Theme.setupGraphics(g2);

                        int w = getWidth();
                        int h = getHeight();

                        // Cell background
                        g2.setColor(isHovered && !gameOver && getText().isEmpty() ? Theme.BG_HOVER : Theme.BG_ELEVATED);
                        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 14, 14));

                        // Cell border
                        g2.setColor(Theme.BORDER_STROKE);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 14, 14));

                        // Symbol text rendering
                        String symbol = getText();
                        if (!symbol.isEmpty()) {
                            g2.setColor("X".equals(symbol) ? Theme.ACCENT_CYAN : Theme.ACCENT_LIGHT);
                            g2.setFont(getFont());
                            FontMetrics fm = g2.getFontMetrics();
                            int x = (w - fm.stringWidth(symbol)) / 2;
                            int y = (h + fm.getAscent() - fm.getDescent()) / 2;
                            g2.drawString(symbol, x, y);
                        }

                        g2.dispose();
                    }
                };

                cell.addActionListener(e -> handleCellClick(r, c));
                buttons[row][col] = cell;
                boardPanel.add(cell);
            }
        }

        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Footer Action Row
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);

        restartBtn = new ModernButton("Restart Game", ModernButton.Style.PRIMARY, 12);
        restartBtn.addActionListener(e -> resetBoard());
        footer.add(restartBtn);

        mainPanel.add(footer, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
        setVisible(true);
    }

    private void handleCellClick(int r, int c) {
        if (gameOver || !buttons[r][c].getText().isEmpty()) return;

        buttons[r][c].setText(String.valueOf(currentPlayer));

        if (checkForWin()) {
            gameOver = true;
            statusLabel.setText("🎉 Player " + currentPlayer + " Wins!");
            statusLabel.setForeground(Theme.SUCCESS);
        } else if (checkForDraw()) {
            gameOver = true;
            statusLabel.setText("🤝 Game Draw!");
            statusLabel.setForeground(Theme.WARNING);
        } else {
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            statusLabel.setText("Player " + currentPlayer + "'s Turn");
            statusLabel.setForeground(currentPlayer == 'X' ? Theme.ACCENT_CYAN : Theme.ACCENT_LIGHT);
        }
    }

    private boolean checkForWin() {
        String sym = String.valueOf(currentPlayer);
        for (int i = 0; i < 3; i++) {
            if (sym.equals(buttons[i][0].getText()) && sym.equals(buttons[i][1].getText()) && sym.equals(buttons[i][2].getText())) return true;
            if (sym.equals(buttons[0][i].getText()) && sym.equals(buttons[1][i].getText()) && sym.equals(buttons[2][i].getText())) return true;
        }
        if (sym.equals(buttons[0][0].getText()) && sym.equals(buttons[1][1].getText()) && sym.equals(buttons[2][2].getText())) return true;
        if (sym.equals(buttons[0][2].getText()) && sym.equals(buttons[1][1].getText()) && sym.equals(buttons[2][0].getText())) return true;
        return false;
    }

    private boolean checkForDraw() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (buttons[r][c].getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void resetBoard() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                buttons[r][c].setText("");
            }
        }
        currentPlayer = 'X';
        gameOver = false;
        statusLabel.setText("Player X's Turn");
        statusLabel.setForeground(Theme.ACCENT_CYAN);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGame::new);
    }
}
