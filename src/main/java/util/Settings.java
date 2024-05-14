package util;

import com.google.gson.Gson;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Settings implements java.io.Serializable {

  public static final String EXECUTION_SPEED = "executionSpeed";
  public static final String SHOW_HELP_ON_STARTUP = "showHelpOnStartup";
  public static final String SHOW_ASCII_TABLE_ON_STARTUP = "showAsciiTableOnStartup";
  public static final String SHOW_INSTRUCTIONS_ON_STARTUP = "showInstructionsOnStartup";
  public static final String ANCHOR_ASCII_TABLE = "anchorAsciiTable";
  public static final String ANCHOR_INSTRUCTIONS = "anchorInstructions";
  public static final String MOVE_CARET_AFTER_INPUT = "moveCaretAfterInput";
  public static final String CURRENT_FONT_SIZE = "currentFontSize";

  private static final transient String SETTINGS_FILE = "settings.json";
  private static final transient Gson gson = new Gson(); // Shared Gson instance
  private transient File settingsFile = null;
  private transient PropertyChangeSupport pcs;

  // Configurable settings
  private ExecutionSpeed executionSpeed;
  private boolean showHelpOnStartup;
  private boolean showAsciiTableOnStartup;
  private boolean showInstructionsOnStartup;
  private boolean anchorAsciiTable;
  private boolean anchorInstructions;
  private boolean moveCaretAfterInput;
  private int currentFontSize;

  public static final int DEFAULT_FONT_SIZE = 14;
  public static final int MIN_FONT_SIZE = 8;
  public static final int MAX_FONT_SIZE = 30;

  // Private constructor to prevent instantiation without loading from file
  private Settings() {
    // Default settings
    executionSpeed = ExecutionSpeed.MEDIUM;
    showHelpOnStartup = true;
    showAsciiTableOnStartup = true;
    showInstructionsOnStartup = true;
    anchorAsciiTable = true;
    anchorInstructions = true;
    moveCaretAfterInput = true;
    currentFontSize = DEFAULT_FONT_SIZE;

    pcs = new PropertyChangeSupport(this);
  }

  /**
   * Add a PropertyChangeListener for a specific property.
   *
   * @param propertyName The name of the property to listen for changes. Must be one of the
   *     constants defined in this class, e.g. EXECUTION_SPEED.
   * @param listener
   */
  public void addPropertyChangeListener(
      String propertyName, java.beans.PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(
      String propertyName, java.beans.PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(propertyName, listener);
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

  public boolean getShowHelpOnStartup() {
    return showHelpOnStartup;
  }

  public boolean getShowAsciiTableOnStartup() {
    return showAsciiTableOnStartup;
  }

  public boolean getShowInstructionsOnStartup() {
    return showInstructionsOnStartup;
  }

  public boolean getAnchorAsciiTable() {
    return anchorAsciiTable;
  }

  public boolean getAnchorInstructions() {
    return anchorInstructions;
  }

  public boolean getMoveCaretAfterInput() {
    return moveCaretAfterInput;
  }

  public int getCurrentFontSize() {
    return currentFontSize;
  }

  public void setExecutionSpeed(ExecutionSpeed executionSpeed) {
    ExecutionSpeed oldExecutionSpeed = this.executionSpeed;
    this.executionSpeed = executionSpeed;
    pcs.firePropertyChange(EXECUTION_SPEED, oldExecutionSpeed, executionSpeed);
    save();
  }

  public void setShowHelpOnStartup(boolean showHelpOnStartup) {
    boolean oldShowHelpOnStartup = this.showHelpOnStartup;
    this.showHelpOnStartup = showHelpOnStartup;
    pcs.firePropertyChange(SHOW_HELP_ON_STARTUP, oldShowHelpOnStartup, showHelpOnStartup);
    save();
  }

  public void setShowAsciiTableOnStartup(boolean showAsciiTableOnStartup) {
    boolean oldShowAsciiTableOnStartup = this.showAsciiTableOnStartup;
    this.showAsciiTableOnStartup = showAsciiTableOnStartup;
    pcs.firePropertyChange(
        SHOW_ASCII_TABLE_ON_STARTUP, oldShowAsciiTableOnStartup, showAsciiTableOnStartup);
    save();
  }

  public void setShowInstructionsOnStartup(boolean showInstructionsOnStartup) {
    boolean oldShowInstructionsOnStartup = this.showInstructionsOnStartup;
    this.showInstructionsOnStartup = showInstructionsOnStartup;
    pcs.firePropertyChange(
        SHOW_INSTRUCTIONS_ON_STARTUP, oldShowInstructionsOnStartup, showInstructionsOnStartup);
    save();
  }

  public void setAnchorAsciiTable(boolean anchorAsciiTable) {
    boolean oldAnchorAsciiTable = this.anchorAsciiTable;
    this.anchorAsciiTable = anchorAsciiTable;
    pcs.firePropertyChange(ANCHOR_ASCII_TABLE, oldAnchorAsciiTable, anchorAsciiTable);
    save();
  }

  public void setAnchorInstructions(boolean anchorInstructions) {
    boolean oldAnchorInstructions = this.anchorInstructions;
    this.anchorInstructions = anchorInstructions;
    pcs.firePropertyChange(ANCHOR_INSTRUCTIONS, oldAnchorInstructions, anchorInstructions);
    save();
  }

  public void setMoveCaretAfterInput(boolean moveCaretAfterInput) {
    boolean oldMoveCaretAfterInput = this.moveCaretAfterInput;
    this.moveCaretAfterInput = moveCaretAfterInput;
    pcs.firePropertyChange(MOVE_CARET_AFTER_INPUT, oldMoveCaretAfterInput, moveCaretAfterInput);
    save();
  }

  public void setCurrentFontSize(int currentFontSize) {
    int oldFontSize = this.currentFontSize;
    this.currentFontSize = currentFontSize;
    pcs.firePropertyChange(CURRENT_FONT_SIZE, oldFontSize, currentFontSize);
    save();
  }

  // Font size convenience methods
  public void increaseFontSize() {
    if (currentFontSize < MAX_FONT_SIZE) {
      setCurrentFontSize(currentFontSize + 2);
    }
  }

  public void decreaseFontSize() {
    if (currentFontSize > MIN_FONT_SIZE) {
      setCurrentFontSize(currentFontSize - 2);
    }
  }

  public void resetFontSize() {
    setCurrentFontSize(DEFAULT_FONT_SIZE);
  }
}
