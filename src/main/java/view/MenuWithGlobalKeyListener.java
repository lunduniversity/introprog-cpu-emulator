package view;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MenuWithGlobalKeyListener {
  private Timer timer;
  private boolean keyPressed = false;

  public MenuWithGlobalKeyListener() {
    JFrame frame = new JFrame("Menu with Key Listener Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);

    // Menu bar setup
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Actions");
    JMenuItem menuItem = new JMenuItem("Perform Action (Press D)");
    // menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
    // menuItem.addActionListener(e -> performActionOnce());
    menu.add(menuItem);
    menuBar.add(menu);
    frame.setJMenuBar(menuBar);

    // Global key listener
    Toolkit.getDefaultToolkit()
        .addAWTEventListener(
            event -> {
              KeyEvent keyEvent = (KeyEvent) event;
              if (keyEvent.getID() == KeyEvent.KEY_PRESSED
                  && keyEvent.getKeyCode() == KeyEvent.VK_D
                  && !keyPressed) {
                keyPressed = true;
                startAction();
              } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED
                  && keyEvent.getKeyCode() == KeyEvent.VK_D) {
                keyPressed = false;
                stopAction();
              }
            },
            AWTEvent.KEY_EVENT_MASK);

    frame.setVisible(true);
  }

  private void startAction() {
    if (timer == null) {
      System.out.println("Continuous Action Started");
      timer = new Timer(200, e -> performContinuousAction());
      timer.start();
    }
  }

  private void stopAction() {
    if (timer != null) {
      timer.stop();
      timer = null;
      System.out.println("Continuous Action Stopped");
    }
  }

  private void performContinuousAction() {
    // Continuous action logic here
    System.out.println("Performing Continuous Action...");
  }

  private void performActionOnce() {
    // One-time action logic here
    if (!keyPressed) { // Prevents action when key is held
      System.out.println("Performing Action Once...");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MenuWithGlobalKeyListener::new);
  }
}
