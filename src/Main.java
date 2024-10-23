import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Main extends JFrame {

    // Ekran sozlamalri bilan ishlash qismi
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int DOT_SIZE = 10;
    private static final int ALL_DOTS = (WIDTH * HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    // Qismlar joylashuvi bilan ishlash
    private int dots;
    private int apply_x;
    private int apply_y;
    private char directions;
    private boolean running;

    public Main() {
        setTitle("Ilon o'yini");
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
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

        startGame();
    }

    public void startGame() {

        running = true;
        dots = 2;
        directions = 'R';

        for (int i = 0; i < dots; i ++) {
            x[i] = 100 - i * DOT_SIZE;
            y[i] = 100;
        }

        spawnApple();

        Timer timer = new Timer(100, new ActionListener() {
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

    // Xo'rak paydo bo'lish qismi
    private void spawnApple() {
        apply_x = new Random().nextInt(WIDTH / DOT_SIZE) * DOT_SIZE;
        apply_y = new Random().nextInt(HEIGHT / DOT_SIZE) * DOT_SIZE;
    }

    private void move() {
        for (int i = dots; i > 0; i --) {
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

        if (x[0] == apply_x && y[0] == apply_y) {
            dots ++;
            spawnApple();
        }
    }

    // O'yindan tashqari xolatlarni tekshirish
    private void checkCollision() {
        if (x[0] < 0 || x[0] > WIDTH || y[0] < 0 || y[0] > HEIGHT) {
            running = false;
        }

        for (int i = 1; i < dots; i ++) {
            if (x[0] == x[i] && y[0] == y[1]) {
                running = false;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (running) {
            g.setColor(Color.BLUE);
            g.fillRect(apply_x, apply_y, DOT_SIZE, DOT_SIZE);

            for (int i = 0; i < dots; i ++) {
                g.setColor(i == 0 ? Color.GREEN : Color.RED);
                g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
            }
        } else {
            showGameOver(g);
        }
    }

    // O'yin tugashini ifodalash qismi
    private void showGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("O'yin tugadi!", WIDTH / 2, HEIGHT / 2);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Main mains = new Main();
            mains.setVisible(true);
        });
    }
}

// feruzbek khamroev