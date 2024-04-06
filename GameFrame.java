import javax.swing.JFrame;

public class GameFrame extends JFrame {

  GameFrame() {

    this.add(new GamePanel()); // unamed instance again
    this.setTitle("Snake Game Simulation");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.pack(); // window set to fit the size of the components nicely
    this.setVisible(true);
    this.setLocationRelativeTo(null); // window appears in middle of screen
  }

}
