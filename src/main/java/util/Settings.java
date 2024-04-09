package util;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class Settings {
  private static final transient String SETTINGS_FILE = "settings.json";
  private static final transient Gson gson = new Gson(); // Shared Gson instance

  private transient File settingsFile = null;

  private boolean showHelpOnStartup;
  private boolean showAsciiTableOnStartup;
  private boolean showInstructionsOnStartup;
  private boolean moveCaretAfterInput;

  // Private constructor to prevent instantiation without loading from file
  private Settings() {
    showHelpOnStartup = true;
    showAsciiTableOnStartup = true;
    showInstructionsOnStartup = true;
    moveCaretAfterInput = true;
  }

  private void setSettingsFile(File settingsFile) {
    this.settingsFile = settingsFile;
  }

  // Static method to load Settings from the settings.json file
  public static Settings loadFromFile() {
    try {
      File settingsFile = createSettingsFile();
      try (FileReader reader = new FileReader(settingsFile)) {
        Settings settings = gson.fromJson(reader, Settings.class);
        settings.setSettingsFile(settingsFile);

        return settings;
      } catch (IOException e) {
        return new Settings();
      }
    } catch (Exception e) {
      // Return a new instance with default settings if the file does not exist or an error occurs
      return new Settings();
    }
  }

  // Non-static method to save the current Settings instance to the settings.json file
  public void save() {
    if (settingsFile == null) {
      return;
    }
    try (FileWriter writer = new FileWriter(settingsFile)) {
      // Serialize the current Settings instance to JSON and save it to the file
      gson.toJson(this, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static File createSettingsFile() throws Exception {
    // Get the path to the directory containing the JAR file
    String jarDir =
        new File(
                Settings.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath())
            .getParent();

    // Construct the path to the settings.json file
    String settingsFilePath = Paths.get(jarDir, SETTINGS_FILE).toString();

    // Create the file if it does not exist
    File settingsFile = new File(settingsFilePath);
    settingsFile.createNewFile();

    return settingsFile;
  }

  // Below are the getters and setters for the settings attributes

  public boolean isShowHelpOnStartup() {
    return showHelpOnStartup;
  }

  public void setShowHelpOnStartup(boolean showHelpOnStartup) {
    this.showHelpOnStartup = showHelpOnStartup;
  }

  public boolean isShowAsciiTableOnStartup() {
    return showAsciiTableOnStartup;
  }

  public void setShowAsciiTableOnStartup(boolean showAsciiTableOnStartup) {
    this.showAsciiTableOnStartup = showAsciiTableOnStartup;
  }

  public boolean isShowInstructionsOnStartup() {
    return showInstructionsOnStartup;
  }

  public void setShowInstructionsOnStartup(boolean showInstructionsOnStartup) {
    this.showInstructionsOnStartup = showInstructionsOnStartup;
  }

  public boolean isMoveCaretAfterInput() {
    return moveCaretAfterInput;
  }

  public void setMoveCaretAfterInput(boolean moveCaretAfterInput) {
    this.moveCaretAfterInput = moveCaretAfterInput;
  }
}
