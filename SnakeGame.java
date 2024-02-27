import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int BOARD_WIDTH = 400;
    private static final int BOARD_HEIGHT = 400;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;
    private final int[] x = new int[GAME_UNITS];
    private final int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;

    public SnakeGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        running = true;
        newApple();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void paintSnake(Graphics g) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(new Color(45, 180, 0));
            }
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void paintApple(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    private void paintScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + applesEaten, 10, 20);
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= BOARD_WIDTH || y[0] < 0 || y[0] >= BOARD_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void newApple() {
        appleX = new Random().nextInt((int) (BOARD_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = new Random().nextInt((int) (BOARD_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String gameOverMsg = "Game Over";
        String retryMsg = "Press R to Retry";
        g.drawString(gameOverMsg, (BOARD_WIDTH - metrics.stringWidth(gameOverMsg)) / 2, BOARD_HEIGHT / 2 - 40);
        g.drawString("Score: " + applesEaten, (BOARD_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, BOARD_HEIGHT / 2);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(retryMsg, (BOARD_WIDTH - metrics.stringWidth(retryMsg)) / 2, BOARD_HEIGHT / 2 + 40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            paintApple(g);
            paintSnake(g);
            paintScore(g);
        } else {
            gameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_R:
                    if (!running) {
                        resetGame();
                    }
                    break;
            }
        }
    }

    private void resetGame() {
        running = true;
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        newApple();
        timer.restart();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new SnakeGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
