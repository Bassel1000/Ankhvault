import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class PongGame extends JPanel implements ActionListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 520;
    private static final int BALL_DIAMETER = 18;
    private static final int PADDLE_WIDTH = 12;
    private static final int PADDLE_HEIGHT = 90;
    private static final int PADDLE_SPEED = 9;

    private int ballX = WIDTH / 2 - BALL_DIAMETER / 2;
    private int ballY = HEIGHT / 2 - BALL_DIAMETER / 2;
    private int ballSpeedX = 0;
    private int ballSpeedY = 0;

    private int paddle1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int paddle2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;

    private boolean upPressed1 = false;
    private boolean downPressed1 = false;
    private boolean upPressed2 = false;
    private boolean downPressed2 = false;

    private int player1Score = 0;
    private int player2Score = 0;
    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private String winnerText = "";

    public PongGame() {
        setBackground(Theme.BG_ROOT);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        Timer timer = new Timer(1000 / 60, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (!gameStarted || gameEnded) {
                    if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ENTER) {
                        if (gameEnded) resetGame();
                        gameStarted = true;
                        ballSpeedX = (Math.random() > 0.5 ? 6 : -6);
                        ballSpeedY = (Math.random() > 0.5 ? 5 : -5);
                    }
                }

                if (code == KeyEvent.VK_W) upPressed1 = true;
                if (code == KeyEvent.VK_S) downPressed1 = true;
                if (code == KeyEvent.VK_UP) upPressed2 = true;
                if (code == KeyEvent.VK_DOWN) downPressed2 = true;

                if (!gameStarted && (upPressed1 || downPressed1 || upPressed2 || downPressed2)) {
                    gameStarted = true;
                    ballSpeedX = (Math.random() > 0.5 ? 6 : -6);
                    ballSpeedY = (Math.random() > 0.5 ? 5 : -5);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W) upPressed1 = false;
                if (code == KeyEvent.VK_S) downPressed1 = false;
                if (code == KeyEvent.VK_UP) upPressed2 = false;
                if (code == KeyEvent.VK_DOWN) downPressed2 = false;
            }
        });

        setFocusable(true);
    }

    private void resetGame() {
        player1Score = 0;
        player2Score = 0;
        gameEnded = false;
        winnerText = "";
        resetBall();
    }

    private void resetBall() {
        ballX = WIDTH / 2 - BALL_DIAMETER / 2;
        ballY = HEIGHT / 2 - BALL_DIAMETER / 2;
        ballSpeedX = 0;
        ballSpeedY = 0;
        gameStarted = false;

        if (player1Score >= 5) {
            gameEnded = true;
            winnerText = "Player 1 Wins!";
        } else if (player2Score >= 5) {
            gameEnded = true;
            winnerText = "Player 2 Wins!";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameEnded && gameStarted) {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            // Bounce off top/bottom
            if (ballY <= 0 || ballY >= HEIGHT - BALL_DIAMETER) {
                ballSpeedY *= -1;
            }

            // Bounce off paddles
            if (ballX <= 30 + PADDLE_WIDTH && ballY + BALL_DIAMETER >= paddle1Y && ballY <= paddle1Y + PADDLE_HEIGHT) {
                ballSpeedX = Math.abs(ballSpeedX);
            }
            if (ballX >= WIDTH - 30 - PADDLE_WIDTH - BALL_DIAMETER && ballY + BALL_DIAMETER >= paddle2Y && ballY <= paddle2Y + PADDLE_HEIGHT) {
                ballSpeedX = -Math.abs(ballSpeedX);
            }

            // Scoring
            if (ballX < 0) {
                player2Score++;
                resetBall();
            } else if (ballX > WIDTH - BALL_DIAMETER) {
                player1Score++;
                resetBall();
            }

            // Paddle movements
            if (upPressed1 && paddle1Y > 10) paddle1Y -= PADDLE_SPEED;
            if (downPressed1 && paddle1Y < HEIGHT - PADDLE_HEIGHT - 10) paddle1Y += PADDLE_SPEED;
            if (upPressed2 && paddle2Y > 10) paddle2Y -= PADDLE_SPEED;
            if (downPressed2 && paddle2Y < HEIGHT - PADDLE_HEIGHT - 10) paddle2Y += PADDLE_SPEED;

            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.setupGraphics(g2);

        // Center Net Line
        g2.setColor(Theme.BORDER_STROKE);
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0);
        g2.setStroke(dashed);
        g2.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

        // Player 1 Paddle (Neon Cyan)
        g2.setColor(Theme.ACCENT_CYAN);
        g2.fill(new RoundRectangle2D.Float(30, paddle1Y, PADDLE_WIDTH, PADDLE_HEIGHT, 8, 8));

        // Player 2 Paddle (Electric Indigo)
        g2.setColor(Theme.ACCENT_LIGHT);
        g2.fill(new RoundRectangle2D.Float(WIDTH - 30 - PADDLE_WIDTH, paddle2Y, PADDLE_WIDTH, PADDLE_HEIGHT, 8, 8));

        // Ball
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Float(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER));

        // Scoreboard
        g2.setFont(Theme.TITLE_H1);
        g2.setColor(Theme.ACCENT_CYAN);
        g2.drawString(String.valueOf(player1Score), WIDTH / 2 - 80, 50);

        g2.setColor(Theme.ACCENT_LIGHT);
        g2.drawString(String.valueOf(player2Score), WIDTH / 2 + 50, 50);

        // Controls Label
        g2.setFont(Theme.SMALL);
        g2.setColor(Theme.TEXT_MUTED);
        g2.drawString("P1: W / S", 30, HEIGHT - 15);
        g2.drawString("P2: UP / DOWN", WIDTH - 120, HEIGHT - 15);

        // Start / End Game Overlay
        if (!gameStarted || gameEnded) {
            g2.setColor(new Color(11, 14, 20, 200));
            g2.fillRect(0, 0, WIDTH, HEIGHT);

            g2.setFont(Theme.TITLE_H1);
            g2.setColor(gameEnded ? Theme.SUCCESS : Theme.TEXT_PRIMARY);
            String msg = gameEnded ? winnerText : "NEON PONG";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (WIDTH - fm.stringWidth(msg)) / 2, HEIGHT / 2 - 20);

            g2.setFont(Theme.BODY);
            g2.setColor(Theme.TEXT_MUTED);
            String sub = gameEnded ? "Press SPACE to Play Again" : "Press SPACE or Move Paddle to Serve";
            FontMetrics fmSub = g2.getFontMetrics();
            g2.drawString(sub, (WIDTH - fmSub.stringWidth(sub)) / 2, HEIGHT / 2 + 20);
        }

        g2.dispose();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new PongGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
