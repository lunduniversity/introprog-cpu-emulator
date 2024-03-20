package view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import model.Memory;
import view.SnapshotDialog.Mode;

public class ComputerMenu extends JMenuBar {

  private final ComputerUI ui;
  private final JFrame frame;
  private final Memory memory;
  private final CellSelecter cellSelecter;

  private final InputMap inputMap;
  private final ActionMap actionMap;

  public ComputerMenu(ComputerUI ui, Memory memory, CellSelecter cellSelecter) {

    this.ui = ui;
    this.frame = ui.getFrame();
    this.memory = memory;
    this.cellSelecter = cellSelecter;

    this.inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    this.actionMap = frame.getRootPane().getActionMap();

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

    // Bind actions
    bind("open", KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK, _open());
    bind("save", KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, _save());
    bind("saveAs", KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK, _saveAs());
    bind("export", KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK, _export());
    bind("import", KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK, _import());
    bind("reset", KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK, _reset());
    bind("undo", KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, _undo());
    bind("redo", KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK, _redo());
    bind("moveUp", KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK, _moveUp());
    bind("moveDown", KeyEvent.VK_DOWN, KeyEvent.ALT_DOWN_MASK, _moveDown());
    bind("copy", KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, _copy());
    bind("paste", KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, _paste());
    bind("clear", KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK, _clear());
    bind(
        "delete",
        KeyEvent.VK_DELETE,
        KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK,
        _delete());
    bind("selectUp", KeyEvent.VK_UP, KeyEvent.SHIFT_DOWN_MASK, _selectUp());
    bind("selectDown", KeyEvent.VK_DOWN, KeyEvent.SHIFT_DOWN_MASK, _selectDown());
    bind("clearSelection", KeyEvent.VK_ESCAPE, _clearSelection());
    bind("asciiTable", KeyEvent.VK_F1, _asciiTable());
    bind("instructins", KeyEvent.VK_F2, _instructins());
  }

  private void bind(String actionName, int keyCode, MenuAction action) {
    bind(actionName, keyCode, 0, action);
  }

  private void bind(String actionName, int keyCode, int modifiers, MenuAction action) {
    KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
    inputMap.put(keyStroke, actionName);
    actionMap.put(
        actionName,
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            action.execute();
          }
        });
  }

  interface MenuAction {
    void execute();
  }

  private MenuAction _open() {
    return () -> {};
  }

  private MenuAction _save() {
    return () -> {};
  }

  private MenuAction _saveAs() {
    return () -> {};
  }

  private MenuAction _export() {
    return () -> {
      String memorySnapdhot = memory.exportAsBase64();
      if (memorySnapdhot.isEmpty()) {
        memorySnapdhot = "(Memory is empty)";
      }
      SnapshotDialog dialog = new SnapshotDialog(frame, Mode.EXPORT);
      dialog.setText(memorySnapdhot);
      dialog.setVisible(true);
    };
  }

  private MenuAction _import() {
    return () -> {
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
    };
  }

  private MenuAction _reset() {
    return () -> {
      ui.handleResetAllData();
    };
  }

  private MenuAction _undo() {
    return () -> {};
  }

  private MenuAction _redo() {
    return () -> {};
  }

  private MenuAction _moveUp() {
    return () -> {
      cellSelecter.moveCellsUp(memory);
    };
  }

  private MenuAction _moveDown() {
    return () -> {};
  }

  private MenuAction _copy() {
    return () -> {};
  }

  private MenuAction _paste() {
    return () -> {};
  }

  private MenuAction _clear() {
    return () -> {};
  }

  private MenuAction _delete() {
    return () -> {};
  }

  private MenuAction _selectUp() {
    return () -> {};
  }

  private MenuAction _selectDown() {
    return () -> {};
  }

  private MenuAction _clearSelection() {
    return () -> {};
  }

  private MenuAction _asciiTable() {
    return () -> {};
  }

  private MenuAction _instructins() {
    return () -> {};
  }
}
