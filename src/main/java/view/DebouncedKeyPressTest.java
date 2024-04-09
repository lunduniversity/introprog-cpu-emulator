package view;

import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class DebouncedKeyPressTest {
  private static long lastActionTime = 0;
  private static final long DEBOUNCE_DELAY = 200; // milliseconds

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          JFrame frame = new JFrame("Debounced KeyPress Test");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(400, 300);

          JMenuBar menuBar = new JMenuBar();
          frame.setJMenuBar(menuBar);

          JMenu menu = new JMenu("Test Menu");
          menuBar.add(menu);

          JMenuItem menuItem = new JMenuItem("Test Action");
          menuItem.addActionListener(
              e -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastActionTime > DEBOUNCE_DELAY) {
                  lastActionTime = currentTime;
                  System.out.println("Action triggered");
                }
              });

          menuItem.setAccelerator(
              KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
          menu.add(menuItem);

          frame.setVisible(true);
        });
  }
}
