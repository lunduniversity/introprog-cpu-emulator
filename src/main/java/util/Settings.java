package util;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Settings implements java.io.Serializable {

  private static final transient String SETTINGS_FILE = "settings.json";
  private static final transient Gson gson = new Gson(); // Shared Gson instance

  private transient File settingsFile = null;

  private ExecutionSpeed executionSpeed;

  private boolean showHelpOnStartup;
  private boolean showAsciiTableOnStartup;
  private boolean showInstructionsOnStartup;

  private boolean anchorAsciiTable;
  private boolean anchorInstructions;

  private boolean moveCaretAfterInput;

  // Private constructor to prevent instantiation without loading from file
  private Settings() {
    executionSpeed = ExecutionSpeed.MEDIUM;

    showHelpOnStartup = true;
    showAsciiTableOnStartup = true;
    showInstructionsOnStartup = true;

    anchorAsciiTable = true;
    anchorInstructions = true;

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
        if (settings == null) {
          settings = new Settings();
        }
        settings.setSettingsFile(settingsFile);

        return settings;
      }
    } catch (IOException | URISyntaxException e) {
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

  private static File createSettingsFile() throws IOException, URISyntaxException {
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
    if (!settingsFile.createNewFile()) {
      // If the file already exists, still return it
    }

    return settingsFile;
  }

  // Below are the getters and setters for the settings attributes

  public ExecutionSpeed getExecutionSpeed() {
    return executionSpeed;
  }

  public void setExecutionSpeed(ExecutionSpeed executionSpeed) {
    this.executionSpeed = executionSpeed;
  }

  public boolean showHelpOnStartup() {
    return showHelpOnStartup;
  }

  public void setShowHelpOnStartup(boolean showHelpOnStartup) {
    this.showHelpOnStartup = showHelpOnStartup;
  }

  public boolean showAsciiTableOnStartup() {
    return showAsciiTableOnStartup;
  }

  public void setShowAsciiTableOnStartup(boolean showAsciiTableOnStartup) {
    this.showAsciiTableOnStartup = showAsciiTableOnStartup;
  }

  public boolean showInstructionsOnStartup() {
    return showInstructionsOnStartup;
  }

  public void setShowInstructionsOnStartup(boolean showInstructionsOnStartup) {
    this.showInstructionsOnStartup = showInstructionsOnStartup;
  }

  public boolean anchorAsciiTable() {
    return anchorAsciiTable;
  }

  public void setAnchorAsciiTable(boolean anchorAsciiTable) {
    this.anchorAsciiTable = anchorAsciiTable;
  }

  public boolean anchorInstructions() {
    return anchorInstructions;
  }

  public void setAnchorInstructions(boolean anchorInstructions) {
    this.anchorInstructions = anchorInstructions;
  }

  public boolean moveCaretAfterInput() {
    return moveCaretAfterInput;
  }

  public void setMoveCaretAfterInput(boolean moveCaretAfterInput) {
    this.moveCaretAfterInput = moveCaretAfterInput;
  }
}
