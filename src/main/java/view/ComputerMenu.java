package view;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBoxMenuItem;
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

  // private final InputMap inputMap;
  // private final ActionMap actionMap;

  public ComputerMenu(ComputerUI ui, Memory memory, CellSelecter cellSelecter) {

    this.ui = ui;
    this.frame = ui.getFrame();
    this.memory = memory;
    this.cellSelecter = cellSelecter;

    // this.inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    // this.actionMap = frame.getRootPane().getActionMap();

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
    JMenuItem itmMoveUp = new JMenuItem("Move selected cells up"); // alt + up
    JMenuItem itmMoveDown = new JMenuItem("Move selected cells down"); // alt + down
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
    JMenuItem itmAsciiTable = new JCheckBoxMenuItem("Show ASCII Table");
    JMenuItem itmInstructions = new JCheckBoxMenuItem("Show Instructions");
    menuHelp.add(itmAsciiTable);
    menuHelp.add(itmInstructions);

    // Add action listeners to the buttons

    // File menu items
    itmOpen.addActionListener(e -> {});
    itmSave.addActionListener(e -> {});
    itmSaveAs.addActionListener(e -> {});
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

    // Edit menu items
    itmReset.addActionListener((e) -> ui.handleResetAllData());
    itmUndo.addActionListener(e -> {});
    itmRedo.addActionListener(e -> {});
    itmMoveUp.addActionListener(e -> cellSelecter.moveCellsUp());
    itmMoveDown.addActionListener(e -> cellSelecter.moveCellsDown());
    itmCopy.addActionListener(e -> cellSelecter.copySelection());
    itmPaste.addActionListener(e -> cellSelecter.pasteSelection());
    itmClear.addActionListener(e -> cellSelecter.clearSelection());
    itmDelete.addActionListener(e -> cellSelecter.deleteSelection());

    // Select menu items
    itmSelectUp.addActionListener(e -> {});
    itmSelectDown.addActionListener(e -> {});
    itmClearSelection.addActionListener(e -> {});

    // View menu items
    // none for now

    // Help menu items
    itmAsciiTable.addItemListener(
        itemEvent -> {
          ui.toggleAsciiTable(itemEvent.getStateChange() == ItemEvent.SELECTED);
        });
    itmInstructions.addItemListener(
        itemEvent -> ui.toggleInstructions(itemEvent.getStateChange() == ItemEvent.SELECTED));

    // Bind actions
    // itmOpen.setAccelerator(bind("open", "ctrl O", _open()));
    // itmSave.setAccelerator(bind("save", "ctrl S", _save()));
    // itmSaveAs.setAccelerator(bind("saveAs", "ctrl shift S", _saveAs()));
    // itmExport.setAccelerator(bind("export", "ctrl E", _export()));
    // itmImport.setAccelerator(bind("import", "ctrl I", _import()));
    // itmReset.setAccelerator(bind("reset", "ctrl R", _reset()));
    // itmUndo.setAccelerator(bind("undo", "ctrl Z", _undo()));
    // itmRedo.setAccelerator(bind("redo", "ctrl Y", _redo()));
    // itmMoveUp.setAccelerator(bind("moveUp", "alt UP", _moveUp()));
    // itmMoveDown.setAccelerator(bind("moveDown", "alt DOWN", _moveDown()));
    // itmCopy.setAccelerator(bind("copy", "ctrl C", _copy()));
    // itmPaste.setAccelerator(bind("paste", "ctrl V", _paste()));
    // itmClear.setAccelerator(bind("clear", "ctrl DELETE", _clear()));
    // itmDelete.setAccelerator(bind("delete", "ctrl shift DELETE", _delete()));
    // itmSelectUp.setAccelerator(bind("selectUp", "shift UP", _selectUp()));
    // itmSelectDown.setAccelerator(bind("selectDown", "shift DOWN", _selectDown()));
    // itmClearSelection.setAccelerator(bind("clearSelection", "ESCAPE", _clearSelection()));
    // itmAsciiTable.setAccelerator(bind("asciiTable", "F1", _asciiTable()));
    // itmInstructions.setAccelerator(bind("instructins", "F2", _instructions()));

    // Bind shortcut keys
    itmOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    itmSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    itmSaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
    itmExport.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
    itmImport.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
    itmReset.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
    itmUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
    itmRedo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
    itmMoveUp.setAccelerator(KeyStroke.getKeyStroke("alt UP"));
    itmMoveDown.setAccelerator(KeyStroke.getKeyStroke("alt DOWN"));
    itmCopy.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
    itmPaste.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
    itmClear.setAccelerator(KeyStroke.getKeyStroke("ctrl DELETE"));
    itmDelete.setAccelerator(KeyStroke.getKeyStroke("ctrl shift DELETE"));
    itmSelectUp.setAccelerator(KeyStroke.getKeyStroke("shift UP"));
    itmSelectDown.setAccelerator(KeyStroke.getKeyStroke("shift DOWN"));
    itmClearSelection.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));
    itmAsciiTable.setAccelerator(KeyStroke.getKeyStroke("F1"));
    itmInstructions.setAccelerator(KeyStroke.getKeyStroke("F2"));
  }

  // private KeyStroke bind(String actionName, String keyStrokeString, MenuAction action) {
  //   KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeString);
  //   inputMap.put(keyStroke, actionName);
  //   actionMap.put(
  //       actionName,
  //       new AbstractAction() {
  //         @Override
  //         public void actionPerformed(ActionEvent e) {
  //           action.execute();
  //         }
  //       });
  //   return keyStroke;
  // }

  // interface MenuAction {
  //   void execute();
  // }

  // private MenuAction _open() {
  //   return () -> {};
  // }

  // private MenuAction _save() {
  //   return () -> {};
  // }

  // private MenuAction _saveAs() {
  //   return () -> {};
  // }

  // private MenuAction _export() {
  //   return () -> {
  //     String memorySnapdhot = memory.exportAsBase64();
  //     if (memorySnapdhot.isEmpty()) {
  //       memorySnapdhot = "(Memory is empty)";
  //     }
  //     SnapshotDialog dialog = new SnapshotDialog(frame, Mode.EXPORT);
  //     dialog.setText(memorySnapdhot);
  //     dialog.setVisible(true);
  //   };
  // }

  // private MenuAction _import() {
  //   return () -> {
  //     SnapshotDialog dialog = new SnapshotDialog(frame, Mode.IMPORT);
  //     dialog.setVisible(true);
  //     if (dialog.isConfirmed()) {
  //       String memorySnapshot = dialog.getText();
  //       try {
  //         memory.importFromBase64(memorySnapshot);
  //       } catch (IllegalArgumentException ex) {
  //         JOptionPane.showMessageDialog(
  //             frame,
  //             "The given input has the wrong format, and cannot be imported.",
  //             "Invalid memory snapdhot",
  //             JOptionPane.WARNING_MESSAGE);
  //       }
  //     }
  //   };
  // }

  // private MenuAction _reset() {
  //   return () -> {
  //     ui.handleResetAllData();
  //   };
  // }

  // private MenuAction _undo() {
  //   return () -> {};
  // }

  // private MenuAction _redo() {
  //   return () -> {};
  // }

  // private MenuAction _moveUp() {
  //   return () -> {
  //     cellSelecter.moveCellsUp();
  //   };
  // }

  // private MenuAction _moveDown() {
  //   return () -> {};
  // }

  // private MenuAction _copy() {
  //   return () -> {};
  // }

  // private MenuAction _paste() {
  //   return () -> {};
  // }

  // private MenuAction _clear() {
  //   return () -> {};
  // }

  // private MenuAction _delete() {
  //   return () -> {};
  // }

  // private MenuAction _selectUp() {
  //   return () -> {};
  // }

  // private MenuAction _selectDown() {
  //   return () -> {};
  // }

  // private MenuAction _clearSelection() {
  //   return () -> {};
  // }

  // private MenuAction _asciiTable() {
  //   System.out.println("Registering Ascii action");
  //   return () -> {
  //     System.out.println("Triggered Ascii action");
  //     ui.toggleAsciiTable();
  //   };
  // }

  // private MenuAction _instructions() {
  //   return () -> {
  //     ui.toggleInstructions();
  //   };
  // }
}
