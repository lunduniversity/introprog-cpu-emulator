package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class LazySwing {
  private static final Random r = new Random();

  public static void inv(Runnable runnable) {
    inv(runnable, true);
  }

  public static void inv(Runnable runnable, boolean forceNewInvocation) {
    if (forceNewInvocation || !SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(runnable);
    } else {
      runnable.run();
    }
  }

  public static void checkEDT() {
    checkEDT(false);
  }

  public static void checkEDT(boolean throwException) {
    if (!SwingUtilities.isEventDispatchThread()) {
      IllegalStateException e = new IllegalStateException("This method must be called on the EDT");
      if (throwException) {
        throw e;
      } else {
        e.printStackTrace();
      }
    }
  }

  public interface ThrowingRunnable<T> {
    T run() throws Exception;
  }

  public static <T> T runSafely(ThrowingRunnable<T> runnable) {
    return runSafely(null, runnable);
  }

  public static <T> T runSafely(JFrame parent, ThrowingRunnable<T> runnable) {
    try {
      return runnable.run();
    } catch (Exception e) {
      // Show a warning message using JOptionPane
      JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
    }
    return null;
  }

  public static boolean isEDT() {
    return SwingUtilities.isEventDispatchThread();
  }

  public static AbstractAction action(ActionListener listener) {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        listener.actionPerformed(e);
      }
    };
  }

  public static String colorToHex(Color color) {
    // Use Integer.toHexString to convert the RGB values to hex
    // and String.format to ensure leading zeros are included if necessary.
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  public static void showBorders(Component component) {
    // Define a simple border
    Border border = BorderFactory.createLineBorder(Color.BLACK, 1);

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

  public static String getKeyStrokeString(KeyStroke keyStroke) {
    // Getting the modifier text, if any
    String modifierText = InputEvent.getModifiersExText(keyStroke.getModifiers());

    // Getting the key text
    String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());

    // Constructing the final string
    if (!modifierText.isEmpty()) {
      return modifierText + "+" + keyText;
    } else {
      return keyText;
    }
  }

  public static void setComponentTreeFontSize(Component comp, int currentFontSize) {
    if (comp instanceof Container) {
      for (Component child : ((Container) comp).getComponents()) {
        setComponentTreeFontSize(child, currentFontSize);
      }
    }

    Font font = comp.getFont();
    if (font == null) {
      return;
    }
    Font newFont = font.deriveFont((float) (currentFontSize));
    comp.setFont(newFont);
  }
}
