package view;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class ManualAcceleratorDisplay {
  private Timer timer;

  public ManualAcceleratorDisplay() {
    JFrame frame = new JFrame("Manual Accelerator Display Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);

    // Create menu bar and add it to the frame
    JMenuBar menuBar = new JMenuBar();

    // Create a menu
    JMenu menu = new JMenu("Actions");
    menuBar.add(menu);

    // Manually set the menu item text to include the 'accelerator' key hint
    JMenuItem menuItem = new JMenuItem("Perform Action (Ctrl+D)");

    // Normally, you would use setAccelerator() here, but we're avoiding that to manually handle key
    // events
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));

    menu.add(menuItem);
    frame.setJMenuBar(menuBar);

    // Setup the global key listener
    Toolkit.getDefaultToolkit()
        .addAWTEventListener(
            event -> {
              KeyEvent keyEvent = (KeyEvent) event;
              if (keyEvent.getID() == KeyEvent.KEY_PRESSED
                  && keyEvent.getKeyCode() == KeyEvent.VK_D
                  && (keyEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) {
                startAction();
              } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED
                  && keyEvent.getKeyCode() == KeyEvent.VK_D) {
                stopAction();
              }
            },
            AWTEvent.KEY_EVENT_MASK);

    frame.setVisible(true);
  }

  private void startAction() {
    if (timer == null) {
      System.out.println("Action Started");
      timer = new Timer(200, e -> performAction());
      timer.start();
    }
  }

  private void stopAction() {
    if (timer != null) {
      timer.stop();
      timer = null;
      System.out.println("Action Stopped");
    }
  }

  private void performAction() {
    // Action logic here
    System.out.println("Performing Action...");
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(ManualAcceleratorDisplay::new);
  }
}
