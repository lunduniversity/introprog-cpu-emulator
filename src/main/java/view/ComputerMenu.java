package view;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import model.Memory;
import view.SnapshotDialog.Mode;

public class ComputerMenu extends JMenuBar {

  public ComputerMenu(Memory memory, ComputerUI ui) {

    JFrame frame = ui.getFrame();

    JMenu menuFile = new JMenu("File");
    JMenu menuEdit = new JMenu("Edit");
    JMenu menuSelect = new JMenu("Select");
    JMenu menuView = new JMenu("View");
    JMenu menuHelp = new JMenu("Help");
    add(menuFile);
    add(menuEdit);
    add(menuSelect);
    add(menuView);
    add(menuHelp);

    // File menu items
    JMenuItem itmOpen = new JMenuItem("Open/Load");
    JMenuItem itmSave = new JMenuItem("Save");
    JMenuItem itmSaveAs = new JMenuItem("Save As ...");
    JMenuItem itmExport = new JMenuItem("Export base64");
    JMenuItem itmImport = new JMenuItem("Import base64");
    JMenuItem itmReset = new JMenuItem("Reset all data");
    menuFile.add(itmOpen);
    menuFile.add(itmSave);
    menuFile.add(itmSaveAs);
    menuFile.addSeparator();
    menuFile.add(itmExport);
    menuFile.add(itmImport);
    menuFile.addSeparator();
    menuFile.add(itmReset);

    // Edit menu items
    JMenuItem itmUndo = new JMenuItem("Undo"); // ctrl + z
    JMenuItem itmRedo = new JMenuItem("Redo"); // ctrl + y
    JMenuItem itmMoveUp = new JMenuItem("Move selection up"); // alt + up
    JMenuItem itmMoveDown = new JMenuItem("Move selection down"); // alt + down
    JMenuItem itmCopy = new JMenuItem("Copy selection to clipboard"); // ctrl + c
    JMenuItem itmPaste = new JMenuItem("Paste from clipboard"); // ctrl + v
    JMenuItem itmClear = new JMenuItem("Clear selected cells"); // ctrl + delete
    JMenuItem itmDelete = new JMenuItem("Delete selected cells"); // ctrl + shift + delete
    menuEdit.add(itmUndo);
    menuEdit.add(itmRedo);
    menuEdit.addSeparator();
    menuEdit.add(itmMoveUp);
    menuEdit.add(itmMoveDown);
    menuEdit.addSeparator();
    menuEdit.add(itmCopy);
    menuEdit.add(itmPaste);
    menuEdit.addSeparator();
    menuEdit.add(itmClear);
    menuEdit.add(itmDelete);

    // Select menu items
    JMenuItem itmSelectUp = new JMenuItem("Extend selection up"); // shift + up
    JMenuItem itmSelectDown = new JMenuItem("Extend selection down"); // shift + down
    JMenuItem itmClearSelection = new JMenuItem("Clear selection"); // esc
    menuSelect.add(itmSelectUp);
    menuSelect.add(itmSelectDown);
    menuSelect.addSeparator();
    menuSelect.add(itmClearSelection);

    // View menu items

    // Help menu items
    JMenuItem itmAscii = new JCheckBoxMenuItem("Show ASCII Table");
    JMenuItem itmInstructions = new JCheckBoxMenuItem("Show Instructions");
    menuHelp.add(itmAscii);
    menuHelp.add(itmInstructions);

    // Add action listeners to the buttons
    itmExport.addActionListener(
        (e) -> {
          String memorySnapdhot = memory.exportAsBase64();
          if (memorySnapdhot.isEmpty()) {
            memorySnapdhot = "(Memory is empty)";
          }
          SnapshotDialog dialog = new SnapshotDialog(frame, Mode.EXPORT);
          dialog.setText(memorySnapdhot);
          dialog.setVisible(true);
        });
    itmImport.addActionListener(
        (e) -> {
          SnapshotDialog dialog = new SnapshotDialog(frame, Mode.IMPORT);
          dialog.setVisible(true);
          if (dialog.isConfirmed()) {
            String memorySnapshot = dialog.getText();
            try {
              memory.importFromBase64(memorySnapshot);
            } catch (IllegalArgumentException ex) {
              JOptionPane.showMessageDialog(
                  frame,
                  "The given input has the wrong format, and cannot be imported.",
                  "Invalid memory snapdhot",
                  JOptionPane.WARNING_MESSAGE);
            }
          }
        });
    itmReset.addActionListener((e) -> ui.handleResetAllData());
  }
}
