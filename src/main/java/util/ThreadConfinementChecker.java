package util;

import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * A RepaintManager that is less forgiving than stock Swing. Detects Swing accesses not confined to
 * the EDT.
 *
 * <p>Based on ideas from https://stackoverflow.com/a/17760977
 */
public class ThreadConfinementChecker extends RepaintManager {

  public static void install() {
    RepaintManager.setCurrentManager(new ThreadConfinementChecker());
  }

  @Override
  public synchronized void addInvalidComponent(JComponent component) {
    check();
    super.addInvalidComponent(component);
  }

  @Override
  public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
    check();
    super.addDirtyRegion(component, x, y, w, h);
  }

  private static void check() {

    // If the current thread is the EDT, all is fine.
    // Otherwise, we need to check more carefully.

    if (!SwingUtilities.isEventDispatchThread()) {
      boolean repaintInvoked = false;
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      for (int i = 0; i < stackTrace.length; i++) {
        if (repaintInvoked && stackTrace[i].getClassName().startsWith("javax.swing.")) {
          // If repaint() was invoked from within Swing, we assume it was
          // due to a modification of some kind. That would be an error.

          // By default, the stack frame contains lots and lots of internal
          // Swing calls, which don't really make us any wiser. To avoid this,
          // we scan through the stack frame to the original Swing call, and
          // cut the subsequent ones out.
          while (i < stackTrace.length && stackTrace[i].getClassName().startsWith("javax.swing.")) {
            i++;
          }
          throw new SwingThreadingError(Arrays.copyOfRange(stackTrace, i, stackTrace.length));
        }
        if (stackTrace[i].getMethodName().equals("repaint")) {
          repaintInvoked = true;
        }
      }

      if (!repaintInvoked) {
        // If repaint() was _not_ invoked, we got here because another
        // Swing method was invoked. That is also presumed to be an error.
        throw new SwingThreadingError();
      }
    }
  }
}

// ===========================================================================

@SuppressWarnings("serial")
class SwingThreadingError extends Error {
  public SwingThreadingError() {
    super("Swing accessed from thread '" + Thread.currentThread().getName() + "'");
  }

  public SwingThreadingError(StackTraceElement[] stackTrace) {
    super(
        "Swing accessed from thread '" + Thread.currentThread().getName() + "'", null, false, true);
    setStackTrace(stackTrace);
  }
}
