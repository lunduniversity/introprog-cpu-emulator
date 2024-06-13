package util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileHandler {

  // Reuse the same file chooser, so that the last directory is remembered
  private final JFileChooser fileChooser;
  private final JFrame parent;
  private final TitleSetter titleSetter;
  private final Settings settings;
  private final PropertyChangeSupport pcs;

  private File openedFile;
  private boolean isModified;

  public interface TitleSetter {
    void setTitle(String title);
  }

  public FileHandler(JFrame parent, TitleSetter titleSetter, Settings settings) {
    this.parent = parent;
    this.titleSetter = titleSetter;
    this.settings = settings;
    pcs = new PropertyChangeSupport(this);

    fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
    fileChooser.setCurrentDirectory(getExistingDirectory(settings.getLastOpenedFilePath()));

    openedFile = null;
    isModified = false;
  }

  private File getExistingDirectory(String directoryPath) {
    if (directoryPath == null || directoryPath.isEmpty()) {
      return new File(System.getProperty("user.home"));
    }
    File directory = new File(directoryPath);
    while (!directory.exists() || !directory.isDirectory()) {
      File parentDirectory = directory.getParentFile();
      if (parentDirectory == null) {
        directory = new File(System.getProperty("user.home"));
        break;
      }
      directory = parentDirectory;
    }
    return directory;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  private void _setOpenedFile(File openedFile) {
    File oldFile = this.openedFile;
    this.openedFile = openedFile;
    this.isModified = false;
    pcs.firePropertyChange("openedFile", oldFile, openedFile);
  }

  private void _updateTitle() {
    if (openedFile != null) {
      if (isModified) {
        titleSetter.setTitle(openedFile.getName() + " (unsaved changes)");
      } else {
        titleSetter.setTitle(openedFile.getName());
      }
    } else {
      titleSetter.setTitle("");
    }
  }

  public Object closeOpenedFile() {
    _setOpenedFile(null);
    _updateTitle();
    return null;
  }

  /**
   * Opens a file dialog to select a text file, and reads the contents of the file into an array of
   * strings. Each string represents a line in the file, and must contain exactly 8 characters,
   * which are either '0' or '1' (i.e. a 8 bit binary number).
   *
   * @return an array of strings, each representing a 8 bit binary number
   * @throws IllegalArgumentException if the file format is invalid
   * @throws IOException if an I/O error occurs
   */
  public String[] openFile() throws IllegalArgumentException, IOException {
    fileChooser.setDialogTitle("Open File");
    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    int result = fileChooser.showOpenDialog(parent);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      settings.setLastOpenedFilePath(selectedFile.getAbsolutePath());
      try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
        String[] lines =
            reader
                .lines()
                .map(s -> s.split("(//|#|%)", 2)[0])
                .map(s -> s.replace(" ", ""))
                .toArray(String[]::new);
        // verify that each line contains only 0s and 1s, and is 8 characters long
        for (int i = 0; i < lines.length; i++) {
          String line = lines[i];
          if (line.length() != 8 || !line.matches("[01]+")) {
            throw new IllegalArgumentException(
                String.format("Invalid file format at line %d: '%s'", (i + 1), line));
          }
        }
        _setOpenedFile(selectedFile);
        _updateTitle();
        return lines;
      }
    }
    return new String[0];
  }

  /**
   * Opens a file dialog to save the contents of an array of strings into a text file. Each string
   * represents a line in the file and must contain exactly 8 characters, which are either '0' or
   * '1' (i.e. a 8 bit binary number).
   *
   * @param lines an array of strings to be saved
   * @throws IOException if an I/O error occurs
   * @return null
   */
  public Object saveFileAs(String[] lines) throws IOException {
    fileChooser.setDialogTitle("Save As");
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    int result = fileChooser.showSaveDialog(parent);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      // ensure that the file has a .txt extension
      if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
        selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
      }
      try (java.io.PrintWriter writer = new java.io.PrintWriter(selectedFile)) {
        for (String line : lines) {
          writer.println(line);
        }
      }
      _setOpenedFile(selectedFile);
      _updateTitle();
    }
    return null;
  }

  /**
   * Saves the contents of an array of strings into the currently opened file. Each string
   * represents a line in the file and must contain exactly 8 characters, which are either '0' or
   * '1' (i.e. a 8 bit binary number).
   *
   * @param lines an array of strings to be saved
   * @throws IOException if an I/O error occurs
   */
  public Object saveFile(String[] lines) throws IOException {
    if (openedFile == null) {
      saveFileAs(lines);
    } else {
      try (java.io.PrintWriter writer = new java.io.PrintWriter(openedFile)) {
        for (String line : lines) {
          writer.println(line);
        }
      }
      isModified = false;
      _updateTitle();
    }
    return null;
  }

  public boolean isFileOpened() {
    return openedFile != null;
  }

  public boolean isModified() {
    return isModified;
  }

  public void setIsModified(boolean b) {
    if (isModified != b) {
      isModified = b;
      _updateTitle();
    }
  }
}
