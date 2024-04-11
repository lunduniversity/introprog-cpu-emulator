package util;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LookAndFeelUtils {

  public static void setLookAndFeel(String laf) {
    try {
      UIManager.setLookAndFeel(laf);
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  public static void setPlatformSpecificLookAndFeel() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("linux")) {
      findLookAndFeel("GTK").ifPresent(LookAndFeelUtils::setLookAndFeel);
    } else if (osName.contains("win")) {
      findLookAndFeel("Windows").ifPresent(LookAndFeelUtils::setLookAndFeel);
    } else if (osName.contains("mac")) {
      findLookAndFeel("Mac OS X").ifPresent(LookAndFeelUtils::setLookAndFeel);
    } else {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException
          | InstantiationException
          | IllegalAccessException
          | UnsupportedLookAndFeelException e) {
        e.printStackTrace();
      }
    }
  }

  private static java.util.Optional<String> findLookAndFeel(String namePart) {
    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
      if (info.getName().contains(namePart)) {
        return java.util.Optional.of(info.getClassName());
      }
    }
    return java.util.Optional.empty();
  }
}
