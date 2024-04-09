package view;

import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class KeyPressTest {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          // Create the main window (frame)
          JFrame frame = new JFrame("KeyPress Test");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(400, 300);

          // Create a menu bar and add it to the frame
          JMenuBar menuBar = new JMenuBar();
          frame.setJMenuBar(menuBar);

          // Create a menu and add it to the menu bar
          JMenu menu = new JMenu("Test Menu");
          menuBar.add(menu);

          // Create a menu item with an action that prints a message
          JMenuItem menuItem = new JMenuItem("Test Action");
          menuItem.addActionListener(e -> System.out.println("Action triggered"));

          // Assign an accelerator key to the menu item (Ctrl+D on Windows/Linux, Cmd+D on macOS)
          menuItem.setAccelerator(
              KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

          // Add the menu item to the menu
          menu.add(menuItem);

          // Display the window
          frame.setVisible(true);
        });
  }
}
