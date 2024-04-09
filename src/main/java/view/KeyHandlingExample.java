package view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class KeyHandlingExample {
  private Timer timer;
  private final JFrame frame;

  public KeyHandlingExample() {
    frame = new JFrame("Key Handling Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);
    frame.setFocusable(true);

    // Listen for key events on the frame
    frame.addKeyListener(
        new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            // Check if the key pressed is the desired key (e.g., 'D')
            if (e.getKeyCode() == KeyEvent.VK_D) {
              startMovingCellsDown();
            }
          }

          @Override
          public void keyReleased(KeyEvent e) {
            // Check if the released key is the desired key (e.g., 'D')
            if (e.getKeyCode() == KeyEvent.VK_D) {
              stopMovingCellsDown();
            }
          }
        });
  }

  private void startMovingCellsDown() {
    if (timer == null) {
      timer = new Timer("CellsMover", true); // true to run as a daemon thread
      TimerTask task =
          new TimerTask() {
            @Override
            public void run() {
              moveCellsDown();
            }
          };
      timer.scheduleAtFixedRate(task, 0, 200); // adjust the period as needed
    }
  }

  private void stopMovingCellsDown() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  private void moveCellsDown() {
    // Your logic to move cells down
    System.out.println("Moving cells down");
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          KeyHandlingExample example = new KeyHandlingExample();
          example.frame.setVisible(true);
        });
  }
}
