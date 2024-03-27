package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class LazySwing {

  public static void inv(Runnable runnable) {
    inv(runnable, true);
  }

  public static void inv(Runnable runnable, boolean forceNewInvocation) {
    if (forceNewInvocation || !javax.swing.SwingUtilities.isEventDispatchThread()) {
      javax.swing.SwingUtilities.invokeLater(runnable);
    } else {
      runnable.run();
    }
  }

  public static void checkEDT() {
    if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
      new IllegalStateException("This method must be called on the EDT").printStackTrace();
      ;
    }
  }

  public static boolean isEDT() {
    return javax.swing.SwingUtilities.isEventDispatchThread();
  }

  public static AbstractAction action(ActionListener listener) {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        listener.actionPerformed(e);
      }
    };
  }

  public static void showBorders(Component component) {
    // Define a simple border
    Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
    Random r = new Random();

    // Set the border on JComponents
    if (component instanceof JComponent) {
      ((JComponent) component).setBorder(border);
    }
    component.setBackground(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));

    // Recursively set the border on child components if the component is a container
    if (component instanceof Container) {
      for (Component child : ((Container) component).getComponents()) {
        showBorders(child);
      }
    }
  }
}
