package Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SnakeGame extends JFrame implements ActionListener {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int BANNER_HEIGHT = 80;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 75;
    private final int x[] = new int[GAME_UNITS];
    private final int y[] = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private ArrayList<Integer> records = new ArrayList<>();
    private RoundButton startButton;
    private JLabel scoreLabel;
    private JLabel recordLabel;
    private BufferedImage buffer;
    private Graphics bufferGraphics;
    private int maxRecord;
    private JLabel textSnake;
    private JLabel textGameOver;
    private JToolBar banner1;
    private JToolBar banner2;
    private boolean gameOver = false;

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.DARK_GRAY);
        setLayout(null);

        textGameOver = new JLabel("Game Over");
        textGameOver.setBounds(72, 170, 500, 90);
        textGameOver.setFont(new Font("Arial", Font.BOLD, 75));
        textGameOver.setForeground(Color.RED);
        textGameOver.setVisible(false);
        add(textGameOver);

        textSnake = new JLabel("Snake");
        textSnake.setBounds(185, 175, 400, 50);
        textSnake.setFont(new Font("snakeFont", 1, 65));
        textSnake.setForeground(Color.GREEN);
        add(textSnake);
   
        startButton = new RoundButton("Start");
        startButton.setBounds(250, 300, 100, 50);
        startButton.addActionListener(e -> {
            startGame();
        });
        add(startButton);

        scoreLabel = new JLabel("Score: " + applesEaten);
        scoreLabel.setBounds(10, 10, 100, 30);
        scoreLabel.setForeground(Color.WHITE);
        add(scoreLabel);

        recordLabel = new JLabel("Record: " + maxRecord);
        recordLabel.setBounds(510, 10, 100, 30);
        recordLabel.setForeground(Color.WHITE);
        add(recordLabel);

        banner1 = new JToolBar();
        banner1.setRollover(false);
        banner1.setBackground(new Color(20, 20, 20, 100));
        banner1.setSize(SCREEN_WIDTH, 43);
        banner1.setBorder(null);
        add(banner1);

        banner2 = new JToolBar();
        banner2.setRollover(false);
        banner2.setBackground(new Color(20, 20, 20, 100));
        banner2.setSize(SCREEN_WIDTH, BANNER_HEIGHT);
        banner2.setBorder(null);
        banner2.setVisible(false);
        add(banner2);

        addKeyListener(new MyKeyAdapter());
        setFocusable(true);

        buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        bufferGraphics = buffer.getGraphics();
    }
    
    public void updateLabels() {
        scoreLabel.setText("Score: " + applesEaten);
        records.add(applesEaten);
        int maxRecord = records.stream().max(Integer::compareTo).orElse(0);
        recordLabel.setText("Record: " + maxRecord);
    }

    public void startGame() {
        banner1.setVisible(false);
        banner2.setVisible(true);
        startButton.setVisible(false); // Désactiver le bouton
        textSnake.setVisible(false);
        gameOver = false;
        resetGame();
    }

    private void resetGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;
        x[0] = SCREEN_WIDTH / 2;
        y[0] = SCREEN_HEIGHT / 2;
        newApple();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private boolean checkAppleCollisionWithSnake(int x, int y) {
        for (int i = 0; i < bodyParts; i++) {
            if (this.x[i] == x && this.y[i] == y) {
                return true;
            }
        }
        return false;
    }


    public void newApple() {
        do {
            appleX = (int) (Math.random() * ((SCREEN_WIDTH - UNIT_SIZE) / UNIT_SIZE + 1)) * UNIT_SIZE;
            appleY = (int) (Math.random() * ((SCREEN_HEIGHT - UNIT_SIZE - BANNER_HEIGHT) / UNIT_SIZE + 1)) * UNIT_SIZE + BANNER_HEIGHT;
        } while (checkAppleCollisionWithSnake(appleX, appleY));
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollision() {
        for (int i = bodyParts; i > 0; i--) {
            
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 80 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        running = false;
        timer.stop();
        gameOver = true;
        banner2.setVisible(false);
        banner1.setVisible(true);
        textGameOver.setVisible(true);
        recordLabel.setText("Record: " + maxRecord );
        scoreLabel.setText("Score: ");;
        startButton.setVisible(true); // Afficher à nouveau le bouton "Start" après avoir perdu
        startButton.setEnabled(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER && gameOver) {
                    startButton.setVisible(false);
                    startGame(); // Démarrer une nouvelle partie lorsque la touche Entrée est enfoncée
                }
            }
        });
        updateLabels();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        checkApple();
        checkCollision();

        // Effacer le contenu précédent du buffer
        bufferGraphics.setColor(Color.DARK_GRAY);
        bufferGraphics.fillRect(0, 80, SCREEN_WIDTH, SCREEN_HEIGHT); 

        // Dessiner le jeu dans le buffer
        if (running) {
            bufferGraphics.setColor(Color.RED);
            bufferGraphics.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);     

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    bufferGraphics.setColor(Color.GREEN);
                } else {
                    bufferGraphics.setColor(new Color(45, 180, 0));
                }
                bufferGraphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            gameOver(bufferGraphics);
        }

        // Dessiner le buffer sur la fenêtre
        Graphics g = getGraphics();
        g.drawImage(buffer, 0, 0, this);
        g.dispose();

        // Mettre à jour le score sur l'interface utilisateur
        add(banner2);
        updateLabels(); 
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT && direction != 'R') {
                direction = 'L';
            } else if (key == KeyEvent.VK_RIGHT && direction != 'L') {
                direction = 'R';
            } else if (key == KeyEvent.VK_UP && direction != 'D') {
                direction = 'U';
            } else if (key == KeyEvent.VK_DOWN && direction != 'U') {
                direction = 'D';
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame game = new SnakeGame();
            game.setVisible(true);
        });
    }

    private class RoundButton extends JButton {
        private static final long serialVersionUID = 9032198251140247116L;
        public RoundButton(String s) {
            super(s);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusable(false);
        }
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new java.awt.Color(170,170,170));
            g2.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
            if (getModel().isPressed()) {
                g.setColor(new java.awt.Color(100,100,100));
                g2.fillRect(3, 3, getWidth() - 6, getHeight() - 6);
            }
        
            super.paintComponent(g);
            g2.setColor(new Color(30, 30, 30));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new RoundRectangle2D.Double(1, 1, (getWidth() - 3),(getHeight() - 3), 15, 15));
            g2.dispose();
        }
    }
}