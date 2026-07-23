package Snake.code;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 400;
    private final int B_HEIGHT = 400;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 1600;
    private final int RAND_POS = 38;
    private final int DELAY = 120;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;
    private int score = 0;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        initBoard();
    }
    
    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(new Color(0x0B, 0x0E, 0x14));
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {
        try {
            ImageIcon iid = new ImageIcon("Snake/resources/dot.png");
            if (iid.getIconWidth() > 0) ball = iid.getImage();

            ImageIcon iia = new ImageIcon("Snake/resources/apple.png");
            if (iia.getIconWidth() > 0) apple = iia.getImage();

            ImageIcon iih = new ImageIcon("Snake/resources/head.png");
            if (iih.getIconWidth() > 0) head = iih.getImage();
        } catch (Exception ignored) {}
    }

    private void initGame() {
        dots = 3;
        score = 0;
        inGame = true;
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;

        for (int z = 0; z < dots; z++) {
            x[z] = 100 - z * 10;
            y[z] = 100;
        }
        
        locateApple();

        if (timer != null) timer.stop();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Antialiasing setup
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Dark Canvas Grid Lines
        g2.setColor(new Color(0x16, 0x1B, 0x26));
        for (int i = 0; i < B_WIDTH; i += 20) {
            g2.drawLine(i, 0, i, B_HEIGHT);
        }
        for (int j = 0; j < B_HEIGHT; j += 20) {
            g2.drawLine(0, j, B_WIDTH, j);
        }

        if (inGame) {
            // Draw Apple
            if (apple != null) {
                g2.drawImage(apple, apple_x, apple_y, this);
            } else {
                g2.setColor(new Color(0xEF, 0x44, 0x44)); // Neon Red
                g2.fill(new Ellipse2D.Float(apple_x, apple_y, DOT_SIZE, DOT_SIZE));
            }

            // Draw Snake Body
            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    if (head != null) {
                        g2.drawImage(head, x[z], y[z], this);
                    } else {
                        g2.setColor(new Color(0x06, 0xB6, 0xD4)); // Cyan Head
                        g2.fill(new RoundRectangle2D.Float(x[z], y[z], DOT_SIZE, DOT_SIZE, 4, 4));
                    }
                } else {
                    if (ball != null) {
                        g2.drawImage(ball, x[z], y[z], this);
                    } else {
                        g2.setColor(new Color(0x10, 0xB9, 0x81)); // Emerald Body
                        g2.fill(new RoundRectangle2D.Float(x[z], y[z], DOT_SIZE, DOT_SIZE, 3, 3));
                    }
                }
            }

            // Draw Score Banner
            g2.setColor(Color.WHITE);
            g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
            g2.drawString("Score: " + score, 16, 26);

        } else {
            gameOver(g2);
        }

        g2.dispose();
    }

    private void gameOver(Graphics2D g2) {
        g2.setColor(new Color(11, 14, 20, 220));
        g2.fillRect(0, 0, B_WIDTH, B_HEIGHT);

        String msg = "Game Over";
        String scoreMsg = "Final Score: " + score;
        String restartMsg = "Press SPACE to Restart";

        java.awt.Font fontTitle = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 26);
        java.awt.Font fontSub = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);

        FontMetrics fm1 = g2.getFontMetrics(fontTitle);
        FontMetrics fm2 = g2.getFontMetrics(fontSub);

        g2.setFont(fontTitle);
        g2.setColor(new Color(0xEF, 0x44, 0x44));
        g2.drawString(msg, (B_WIDTH - fm1.stringWidth(msg)) / 2, B_HEIGHT / 2 - 20);

        g2.setFont(fontSub);
        g2.setColor(Color.WHITE);
        g2.drawString(scoreMsg, (B_WIDTH - fm2.stringWidth(scoreMsg)) / 2, B_HEIGHT / 2 + 10);

        g2.setColor(new Color(0x94, 0xA3, 0xB8));
        g2.drawString(restartMsg, (B_WIDTH - fm2.stringWidth(restartMsg)) / 2, B_HEIGHT / 2 + 36);
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            score += 10;
            locateApple();
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection)  x[0] -= DOT_SIZE;
        if (rightDirection) x[0] += DOT_SIZE;
        if (upDirection)    y[0] -= DOT_SIZE;
        if (downDirection)  y[0] += DOT_SIZE;
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame && timer != null) {
            timer.stop();
        }
    }

    private void locateApple() {
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (!inGame && key == KeyEvent.VK_SPACE) {
                initGame();
                return;
            }

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }
            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
