import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Main extends JFrame {

    private GamePanel gamePanel;
    private int speedSnake = 100;

    public Main() {
        setTitle("Ilon o'yini");
        setSize(600, 500); // Umumiy oyna kattaligi
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // O'yin panelini yaratish
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER); // Game paneli o'rtada bo'ladi

        // Tezlikni boshqarish qismini shakllantirish
        JPanel buttonPanel = new JPanel();
        JButton speedUp = new JButton("Tezlashtirish");
        JButton normalSpeed = new JButton("Normal");
        JButton speedDown = new JButton("Sekinlashtirish");

        speedUp.addActionListener(e -> {
            gamePanel.increaseSpeed();
            gamePanel.requestFocusInWindow(); // Fokusni o'yin paneliga qaytarish
        });
        normalSpeed.addActionListener(e -> {
            gamePanel.normalSpeed();
            gamePanel.requestFocusInWindow(); // Fokusni o'yin paneliga qaytarish
        });
        speedDown.addActionListener(e -> {
            gamePanel.decreaseSpeed();
            gamePanel.requestFocusInWindow(); // Fokusni o'yin paneliga qaytarish
        });

        buttonPanel.add(speedUp);
        buttonPanel.add(normalSpeed);
        buttonPanel.add(speedDown);

        // Button panelini pastga qo'shish
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mains = new Main();
            mains.setVisible(true);
        });
    }
}

class GamePanel extends JPanel {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int DOT_SIZE = 10;
    private static final int ALL_DOTS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;
    private char directions;
    private boolean running;
    private int score;  // Ho'rak yeganlik hisoblagichi

    private Timer timer;
    private int speedSnake = 100;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);

        // O'yin boshlanishida fokusni olish
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (directions != 'R') directions = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (directions != 'L') directions = 'R';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (directions != 'U') directions = 'D';
                        break;
                    case KeyEvent.VK_UP:
                        if (directions != 'D') directions = 'U';
                        break;
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        startGame();
    }

    public void increaseSpeed() {
        if (speedSnake > 50) {
            speedSnake -= 50;
            timer.setDelay(speedSnake);
        }
    }

    public void decreaseSpeed() {
        if (speedSnake < 500) {
            speedSnake += 50;
            timer.setDelay(speedSnake);
        }
    }

    public void normalSpeed() {
        speedSnake = 100;
        timer.setDelay(speedSnake);
    }

    public void startGame() {
        running = true;
        dots = 2;
        score = 0;  // Hisobni noldan boshlash
        directions = 'R';

        for (int i = 0; i < dots; i++) {
            x[i] = 100 - i * DOT_SIZE;
            y[i] = 100;
        }

        spawnApple();

        timer = new Timer(speedSnake, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    move();
                    checkCollision();
                    repaint();
                }
            }
        });
        timer.start();
    }

    private void spawnApple() {
        apple_x = new Random().nextInt((WIDTH - DOT_SIZE) / DOT_SIZE) * DOT_SIZE;
        apple_y = new Random().nextInt((HEIGHT - DOT_SIZE) / DOT_SIZE) * DOT_SIZE;
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (directions) {
            case 'L':
                x[0] -= DOT_SIZE;
                break;
            case 'R':
                x[0] += DOT_SIZE;
                break;
            case 'U':
                y[0] -= DOT_SIZE;
                break;
            case 'D':
                y[0] += DOT_SIZE;
                break;
        }

        if (x[0] == apple_x && y[0] == apple_y) {
            dots++;
            score++;  // Hisobni oshirish
            spawnApple();
        }
    }

    private void checkCollision() {
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        for (int i = 1; i < dots; i++) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            g.setColor(Color.BLUE);
            g.fillRect(apple_x, apple_y, DOT_SIZE, DOT_SIZE);

            for (int i = 0; i < dots; i++) {
                g.setColor(i == 0 ? Color.GREEN : Color.RED);
                g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
            }

            // Hisob va tezlikni ko'rsatish
            showScoreAndSpeed(g);

        } else {
            showGameOver(g);
        }
    }

    private void showScoreAndSpeed(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Ho'raklar soni: " + score, 10, 20);  // Hisob ko'rsatilishi
        g.drawString("Tezlik: " + speedSnake + " ms", 10, 40);  // Tezlik ko'rsatilishi
    }

    private void showGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("O'yin tugadi!", WIDTH / 2 - 50, HEIGHT / 2);
    }
}
