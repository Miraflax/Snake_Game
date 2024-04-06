import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

  static final int SCREEN_WIDTH = 1000;
  static final int SCREEN_HEIGHT = 800;
  static final int UNIT_SIZE = 40; // how big a base "unit" is
  static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE);
  static final int DELAY = 69; // game speed
  static final int COLOR_SHIFT_DELAY = 1000;
  int x[] = new int[GAME_UNITS]; // x coordinates of snake
  int y[] = new int[GAME_UNITS];
  int bodyParts = 56; // initial size
  int applesEaten;
  int appleX; // x coordinate of randomly generated apples
  int appleY;
  int powerUpX;
  int powerUpY;
  int junkX[] = new int[GAME_UNITS];
  int junkY[] = new int[GAME_UNITS];
  char direction = 'R'; // start direction
  boolean running = false;
  Timer timer;
  Timer colorTimer;
  Random random; // declare

  GamePanel() {
    random = new Random(); // define
    this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    this.setBackground(Color.black);
    this.setFocusable(true);
    this.addKeyListener(new MyKeyAdapter()); // game panel receives events from keyboard inputs
    startGame();
  }

  public void startGame() {
    newApple();
    newPowerUp();
    running = true;
    timer = new Timer(DELAY, this);
    timer.start();
    colorTimer = new Timer(COLOR_SHIFT_DELAY, this);
    colorTimer.start();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {

    if (running) {
      g.setColor(Color.yellow);
      g.drawLine(0, 2, SCREEN_WIDTH, 2); // top
      g.drawLine(1, 0, 1, SCREEN_HEIGHT); // left
      g.drawLine(0, SCREEN_HEIGHT - 1, SCREEN_WIDTH, SCREEN_HEIGHT - 1); // bottom
      g.drawLine(SCREEN_WIDTH - 1, 0, SCREEN_WIDTH - 1, SCREEN_HEIGHT); // right
      /*
       * g.setColor(Color.lightGray);
       * for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
       * g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT); // vertical
       * }
       * for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
       * g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE); // horizontal
       * }
       */
      g.setColor(Color.orange);
      g.fillOval(powerUpX, powerUpY, UNIT_SIZE, UNIT_SIZE);

      g.setColor(Color.red);
      g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

      // draw the snake
      int redStart = 199; // Starting color (magenta)
      int greenStart = 21;
      int blueStart = 133;

      int redEnd = 0; // Ending color (blue)
      int greenEnd = 240;
      int blueEnd = 30;

      for (int i = 0; i < bodyParts; i++) {
        // Calculate interpolated color for each body part
        int red = transitionColor(redStart, redEnd, i, bodyParts);
        int green = transitionColor(greenStart, greenEnd, i, bodyParts);
        int blue = transitionColor(blueStart, blueEnd, i, bodyParts);

        g.setColor(new Color(red, green, blue, 240));

        if (x[i] < SCREEN_WIDTH && y[i] < SCREEN_HEIGHT)
          ;
        g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
      }
      /*
       * for (int i = 0; i < bodyParts; i++) {
       * if (i == 0) {
       * // head
       * g.setColor(Color.green);
       * g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
       * } else {
       * // body
       * if (blueGradient < 255) {
       * blueColor = blueGradient;
       * } else {
       * blueColor = 512 - blueGradient;
       * }
       * blueGradient = (blueGradient + 8) % (511);
       * g.setColor(new Color(150, 150, blueColor, 255));
       * // g.setColor(new
       * // Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
       * g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
       * }
       * }
       */
    } else {
      gameOver(g);
    }
  }

  public int transitionColor(int start, int end, int step, int totalSteps) {
    return start + ((end - start) * step) / totalSteps;
  }

  public void newApple() {
    appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
    appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    // make sure it doesn't generate inside of the snake?
  }

  public void newPowerUp() {
    powerUpX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
    powerUpY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
  }

  public void move() {
    for (int i = bodyParts; i > 0; i--) {
      x[i] = x[i - 1];
      y[i] = y[i - 1];
    }

    switch (direction) {
      case 'U':
        // y[0] = y-coordinate of the head of the snake
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

  public void fillJunk() {
    for (int i = 0; i < bodyParts; i++) {
      junkX[i] = SCREEN_WIDTH + 1;
      junkY[i] = SCREEN_HEIGHT + 1;
    }
  }

  public void checkPowerUp() {
    if ((x[0] == powerUpX) && (y[0] == powerUpY)) {
      int xPos = x[0];
      int yPos = y[0];
      fillJunk();
      x = junkX;
      y = junkY;
      x[0] = xPos;
      y[0] = yPos;
      /*
       * Brief Pause
       * timer.stop();
       * try {
       * Thread.sleep(5);
       * } catch (InterruptedException e) {
       * e.printStackTrace();
       * }
       * timer.restart();
       */
      newPowerUp();
    }
  }

  public void checkCollisions() {
    // checks if head collides with body
    for (int i = bodyParts; i > 0; i--) {
      if ((x[0] == x[i]) && (y[0] == y[i])) {
        running = false;
      }
    }
    // check if head touches left border
    if (x[0] < 0) {
      x[0] = SCREEN_WIDTH - UNIT_SIZE;
    }
    // check if head touches right border
    if (x[0] >= SCREEN_WIDTH) {
      x[0] = 0;
    }
    // check if head touches top border
    if (y[0] < 0) {
      y[0] = SCREEN_HEIGHT - UNIT_SIZE;
    }
    // check if head touches bottom border
    if (y[0] >= SCREEN_HEIGHT) {
      y[0] = 0;
    }

    if (!running) {
      timer.stop();
      colorTimer.stop();
    }
  }

  public void gameOver(Graphics g) {
    // Game Over text
    g.setColor(Color.red);
    g.setFont(new Font("Helvetica", Font.BOLD, 120));
    FontMetrics metrics1 = getFontMetrics(g.getFont());
    g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2,
        SCREEN_HEIGHT / 2);

    // Score
    g.setColor(Color.gray);
    g.setFont(new Font("Helvetica", Font.BOLD, 80));
    FontMetrics metrics2 = getFontMetrics(g.getFont());
    g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2,
        (SCREEN_HEIGHT / 2) + metrics1.getAscent() / 2 + 10);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (running) {
      move();
      checkApple();
      checkPowerUp();
      checkCollisions();
    }
    repaint();
  }

  public class MyKeyAdapter extends KeyAdapter {
    @Override // we're overridng keyPressed
    public void keyPressed(KeyEvent keypress) {
      switch (keypress.getKeyCode()) {
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
      }
    }
  }
}