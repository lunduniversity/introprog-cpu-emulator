package view;

import static util.LazySwing.runSafely;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import util.FileHandler;

public class ComputerMenu extends JMenuBar {

  public ComputerMenu(ComputerUI ui, FileHandler fileHandler) {

    JFrame frame = ui.getFrame();

    JMenu menuFile = new JMenu("File");
    JMenu menuEdit = new JMenu("Edit");
    JMenu menuSelect = new JMenu("Select");
    JMenu menuExecute = new JMenu("Execute");
    JMenu menuView = new JMenu("View");
    JMenu menuSettings = new JMenu("Settings");
    JMenu menuHelp = new JMenu("Help");
    add(menuFile);
    add(menuEdit);
    add(menuSelect);
    add(menuExecute);
    add(menuView);
    add(menuSettings);
    add(menuHelp);

    // File menu items
    JMenuItem itmOpen = new JMenuItem("Open/Load");
    JMenuItem itmSave = new JMenuItem("Save");
    JMenuItem itmSaveAs = new JMenuItem("Save As ...");
    JMenuItem itmClose = new JMenuItem("Close opened file");
    JMenuItem itmExport = new JMenuItem("Export base64");
    JMenuItem itmImport = new JMenuItem("Import base64");
    JMenuItem itmExit = new JMenuItem("Exit");
    menuFile.add(itmOpen);
    menuFile.add(itmSave);
    menuFile.add(itmSaveAs);
    menuFile.add(itmClose);
    menuFile.addSeparator();
    menuFile.add(itmExport);
    menuFile.add(itmImport);
    menuFile.addSeparator();
    menuFile.add(itmExit);

    // Edit menu items
    JMenuItem itmFlipBit = new JMenuItem("Flip selected bit"); // f
    JMenuItem itmUndo = new JMenuItem("Undo"); // ctrl + z
    JMenuItem itmRedo = new JMenuItem("Redo"); // ctrl + y
    JMenuItem itmResetState = new JMenuItem("Reset program"); // ctrl + r
    JMenuItem itmMoveUp = new JMenuItem("Move selected cells up"); // alt + up
    JMenuItem itmMoveDown = new JMenuItem("Move selected cells down"); // alt + down
    JMenuItem itmCopy = new JMenuItem("Copy selection to clipboard"); // ctrl + c
    JMenuItem itmPaste = new JMenuItem("Paste from clipboard"); // ctrl + v
    JMenuItem itmClear = new JMenuItem("Clear selected cells"); // ctrl + delete
    JMenuItem itmDelete = new JMenuItem("Delete selected cells"); // ctrl + shift + delete
    JMenuItem itmResetData = new JMenuItem("Delete all cells (use carefully!)"); // ctrl + shift + m
    menuEdit.add(itmFlipBit);
    menuEdit.add(itmUndo);
    menuEdit.add(itmRedo);
    menuEdit.add(itmResetState);
    menuEdit.addSeparator();
    menuEdit.add(itmMoveUp);
    menuEdit.add(itmMoveDown);
    menuEdit.addSeparator();
    menuEdit.add(itmCopy);
    menuEdit.add(itmPaste);
    menuEdit.addSeparator();
    menuEdit.add(itmClear);
    menuEdit.add(itmDelete);
    menuEdit.addSeparator();
    menuEdit.add(itmResetData);

    // Select menu items
    JMenuItem itmSelectUp = new JMenuItem("Extend selection up"); // shift + up
    JMenuItem itmSelectDown = new JMenuItem("Extend selection down"); // shift + down
    JMenuItem itmMoveSelectionUp = new JMenuItem("Move selection up"); // ctrl + up
    JMenuItem itmMoveSelectionDown = new JMenuItem("Move selection down"); // ctrl + down
    JMenuItem itmClearSelection = new JMenuItem("Clear selection"); // esc
    menuSelect.add(itmSelectUp);
    menuSelect.add(itmSelectDown);
    menuSelect.addSeparator();
    menuSelect.add(itmMoveSelectionUp);
    menuSelect.add(itmMoveSelectionDown);
    menuSelect.addSeparator();
    menuSelect.add(itmClearSelection);

    // Run menu items (include "step" and "run" options)
    JMenuItem itmStep = new JMenuItem("Step"); // space
    JMenuItem itmRun = new JMenuItem("Run"); // ctrl + space
    menuExecute.add(itmStep);
    menuExecute.add(itmRun);

    // View menu items

    // Settings menu items
    JMenuItem itmOpenHelp = new JCheckBoxMenuItem("Open Help on startup");
    JMenuItem itmOpenAscii = new JCheckBoxMenuItem("Open ASCII table on startup");
    JMenuItem itmOpenInstr = new JCheckBoxMenuItem("Open Instructions on startup");
    JMenuItem itmMoveCaret = new JCheckBoxMenuItem("Move caret after input");
    menuSettings.add(itmOpenHelp);
    menuSettings.add(itmOpenAscii);
    menuSettings.add(itmOpenInstr);
    menuSettings.add(itmMoveCaret);

    // Help menu items
    JMenuItem itmAsciiTable = new JCheckBoxMenuItem("Show ASCII Table");
    JMenuItem itmInstructions = new JCheckBoxMenuItem("Show Instructions");
    menuHelp.add(itmAsciiTable);
    menuHelp.add(itmInstructions);

    // Add action listeners to the buttons

    // File menu items
    itmOpen.addActionListener(e -> ui.setMemorySnapshot(runSafely(fileHandler::openFile)));
    itmSave.addActionListener(e -> runSafely(() -> fileHandler.saveFile(ui.getMemorySnapshot())));
    itmSaveAs.addActionListener(
        e -> runSafely(() -> fileHandler.saveFileAs(ui.getMemorySnapshot())));
    itmClose.addActionListener(
        e -> {
          ui.handleResetAllData();
          runSafely(fileHandler::closeOpenedFile);
        });
    itmExport.addActionListener(e -> ui.exportAsBase64());
    itmImport.addActionListener(e -> ui.importFromBase64());
    itmExit.addActionListener(e -> ui.handleExit());

    itmClose.setEnabled(false);
    fileHandler.addPropertyChangeListener(
        evt -> {
          if (evt.getPropertyName().equals("openedFile")) {
            itmClose.setEnabled(evt.getNewValue() != null);
          }
        });

    // Edit menu items
    itmFlipBit.addActionListener(e -> ui.flipBit());
    itmUndo.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Undo not implemented yet"));
    itmRedo.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Redo not implemented yet"));
    itmResetState.addActionListener(e -> ui.handleResetState());
    itmMoveUp.addActionListener(e -> ui.getCurrentSelecter().moveCellsUp());
    itmMoveDown.addActionListener(e -> ui.getCurrentSelecter().moveCellsDown());
    itmCopy.addActionListener(e -> ui.getCurrentSelecter().copySelection());
    itmPaste.addActionListener(e -> ui.getCurrentSelecter().pasteSelection());
    itmClear.addActionListener(e -> ui.getCurrentSelecter().clearSelectedCells());
    itmDelete.addActionListener(e -> ui.getCurrentSelecter().deleteSelectedCells());
    itmResetData.addActionListener(e -> ui.handleResetAllData());

    // Select menu items
    itmSelectUp.addActionListener(e -> ui.getCurrentSelecter().expandSelectionUp());
    itmSelectDown.addActionListener(e -> ui.getCurrentSelecter().expandSelectionDown());
    itmMoveSelectionUp.addActionListener(e -> ui.getCurrentSelecter().moveSelectionUp());
    itmMoveSelectionDown.addActionListener(e -> ui.getCurrentSelecter().moveSelectionDown());
    itmClearSelection.addActionListener(e -> ui.getCurrentSelecter().clearSelection());

    // Run menu items
    itmStep.addActionListener(e -> ui.handleStep());
    itmRun.addActionListener(e -> ui.handleRun());

    // View menu items

    // Settings menu items
    itmOpenHelp.addItemListener(
        itemEvent -> ui.toggleHelpOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    itmOpenAscii.addItemListener(
        itemEvent ->
            ui.toggleAsciiTableOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    itmOpenInstr.addItemListener(
        itemEvent ->
            ui.toggleInstructionsOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    itmMoveCaret.addItemListener(
        itemEvent ->
            ui.toggleMoveCaretAfterInput(itemEvent.getStateChange() == ItemEvent.SELECTED));

    // Help menu items
    itmAsciiTable.addItemListener(
        itemEvent -> ui.toggleAsciiTable(itemEvent.getStateChange() == ItemEvent.SELECTED));
    itmInstructions.addItemListener(
        itemEvent -> ui.toggleInstructions(itemEvent.getStateChange() == ItemEvent.SELECTED));

    // Bind shortcut keys
    itmOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    itmSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    itmSaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
    itmClose.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
    itmExport.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
    itmImport.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
    itmExit.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
    itmFlipBit.setAccelerator(KeyStroke.getKeyStroke("F"));
    itmUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
    itmRedo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
    itmResetState.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
    itmMoveUp.setAccelerator(KeyStroke.getKeyStroke("alt UP"));
    itmMoveDown.setAccelerator(KeyStroke.getKeyStroke("alt DOWN"));
    itmCopy.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
    itmPaste.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
    itmClear.setAccelerator(KeyStroke.getKeyStroke("ctrl DELETE"));
    itmDelete.setAccelerator(KeyStroke.getKeyStroke("ctrl shift DELETE"));
    itmResetData.setAccelerator(KeyStroke.getKeyStroke("ctrl shift M"));
    itmSelectUp.setAccelerator(KeyStroke.getKeyStroke("shift UP"));
    itmSelectDown.setAccelerator(KeyStroke.getKeyStroke("shift DOWN"));
    itmMoveSelectionUp.setAccelerator(KeyStroke.getKeyStroke("ctrl UP"));
    itmMoveSelectionDown.setAccelerator(KeyStroke.getKeyStroke("ctrl DOWN"));
    itmClearSelection.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));
    itmStep.setAccelerator(KeyStroke.getKeyStroke("SPACE"));
    itmRun.setAccelerator(KeyStroke.getKeyStroke("ctrl SPACE"));
    itmAsciiTable.setAccelerator(KeyStroke.getKeyStroke("F1"));
    itmInstructions.setAccelerator(KeyStroke.getKeyStroke("F2"));
  }
}
