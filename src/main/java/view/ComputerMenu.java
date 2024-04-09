package view;

import static util.LazySwing.runSafely;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import util.FileHandler;
import util.MenuFactory;

public class ComputerMenu extends JMenuBar {

  public interface MenuCheckboxSetter {
    void setCheckbox(boolean selected);
  }

  public ComputerMenu(ComputerUI ui, FileHandler fileHandler) {

    JFrame frame = ui.getFrame();
    MenuFactory mf = new MenuFactory(frame);

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
    JMenuItem itmOpen =
        mf.item("Open/Load", "ctrl O", e -> ui.setMemorySnapshot(runSafely(fileHandler::openFile)));
    JMenuItem itmSave =
        mf.item(
            "Save", "ctrl S", e -> runSafely(() -> fileHandler.saveFile(ui.getMemorySnapshot())));
    JMenuItem itmSaveAs =
        mf.item(
            "Save As ...",
            "ctrl shift S",
            e -> runSafely(() -> fileHandler.saveFileAs(ui.getMemorySnapshot())));
    JMenuItem itmClose =
        mf.item(
            "Close opened file",
            "ctrl W",
            e -> {
              ui.handleResetAllData();
              runSafely(fileHandler::closeOpenedFile);
            });
    JMenuItem itmExport = mf.item("Export base64", "ctrl E", e -> ui.exportAsBase64());
    JMenuItem itmImport = mf.item("Import base64", "ctrl I", e -> ui.importFromBase64());
    JMenuItem itmExit = mf.item("Exit", "ctrl Q", e -> ui.handleExit());
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
    JMenuItem itmFlipBit = mf.item("Flip selected bit", "F", e -> ui.flipBit());
    JMenuItem itmUndo =
        mf.item(
            "Undo",
            "ctrl Z",
            e -> JOptionPane.showMessageDialog(frame, "Undo not implemented yet"));
    JMenuItem itmRedo =
        mf.item(
            "Redo",
            "ctrl Y",
            e -> JOptionPane.showMessageDialog(frame, "Redo not implemented yet"));
    JMenuItem itmMoveUp =
        mf.item("Move selected cells up", "alt UP", e -> ui.getCurrentSelecter().moveCellsUp());
    JMenuItem itmMoveDown =
        mf.item(
            "Move selected cells down", "alt DOWN", e -> ui.getCurrentSelecter().moveCellsDown());
    JMenuItem itmCopy =
        mf.item(
            "Copy selection to clipboard", "ctrl C", e -> ui.getCurrentSelecter().copySelection());
    JMenuItem itmPaste =
        mf.item("Paste from clipboard", "ctrl V", e -> ui.getCurrentSelecter().pasteSelection());
    JMenuItem itmClear =
        mf.item(
            "Clear selected cells",
            "ctrl DELETE",
            e -> ui.getCurrentSelecter().clearSelectedCells());
    JMenuItem itmDelete =
        mf.item(
            "Delete selected cells",
            "ctrl shift DELETE",
            e -> ui.getCurrentSelecter().deleteSelectedCells());
    JMenuItem itmResetData =
        mf.item("Delete all cells (use carefully!)", "ctrl shift M", e -> ui.handleResetAllData());
    menuEdit.add(itmFlipBit);
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
    menuEdit.addSeparator();
    menuEdit.add(itmResetData);

    // Select menu items
    JMenuItem itmSelectUp =
        mf.item(
            "Extend selection up", "shift UP", e -> ui.getCurrentSelecter().expandSelectionUp());
    JMenuItem itmSelectDown =
        mf.item(
            "Extend selection down",
            "shift DOWN",
            e -> ui.getCurrentSelecter().expandSelectionDown());
    JMenuItem itmMoveSelectionUp =
        mf.item("Move selection up", "ctrl UP", e -> ui.getCurrentSelecter().moveSelectionUp());
    JMenuItem itmMoveSelectionDown =
        mf.item(
            "Move selection down", "ctrl DOWN", e -> ui.getCurrentSelecter().moveSelectionDown());
    JMenuItem itmClearSelection =
        mf.item("Clear selection", "ESCAPE", e -> ui.getCurrentSelecter().clearSelection());
    menuSelect.add(itmSelectUp);
    menuSelect.add(itmSelectDown);
    menuSelect.addSeparator();
    menuSelect.add(itmMoveSelectionUp);
    menuSelect.add(itmMoveSelectionDown);
    menuSelect.addSeparator();
    menuSelect.add(itmClearSelection);

    // Run menu items (include "step" and "run" options)
    JMenuItem itmStep = mf.item("Step", "SPACE", e -> ui.handleStep());
    JMenuItem itmRun = mf.item("Run", "ctrl SPACE", e -> ui.handleRun());
    JMenuItem itmResetState = mf.item("Reset", "ctrl R", e -> ui.handleResetState());

    menuExecute.add(itmStep);
    menuExecute.add(itmRun);
    menuExecute.add(itmResetState);

    // View menu items

    // Settings menu items
    JCheckBoxMenuItem itmOpenHelp =
        mf.cBox(
            "Open Help on startup",
            "",
            itemEvent -> ui.toggleHelpOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmOpenAscii =
        mf.cBox(
            "Open ASCII table on startup",
            "",
            itemEvent ->
                ui.toggleAsciiTableOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmOpenInstr =
        mf.cBox(
            "Open Instructions on startup",
            "",
            itemEvent ->
                ui.toggleInstructionsOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmMoveCaret =
        mf.cBox(
            "Move caret after input",
            "",
            itemEvent ->
                ui.toggleMoveCaretAfterInput(itemEvent.getStateChange() == ItemEvent.SELECTED));
    menuSettings.add(itmOpenHelp);
    menuSettings.add(itmOpenAscii);
    menuSettings.add(itmOpenInstr);
    menuSettings.add(itmMoveCaret);

    // Help menu items
    JCheckBoxMenuItem itmShowHelp =
        mf.cBox(
            "Show help",
            "F1",
            itemEvent ->
                ui.toggleHelp(
                    itemEvent.getStateChange() == ItemEvent.SELECTED,
                    ((JCheckBoxMenuItem) itemEvent.getItem())::setSelected));
    JCheckBoxMenuItem itmShowAsciiTable =
        mf.cBox(
            "Show ASCII Table",
            "F2",
            itemEvent ->
                ui.toggleAsciiTable(
                    itemEvent.getStateChange() == ItemEvent.SELECTED,
                    ((JCheckBoxMenuItem) itemEvent.getItem())::setSelected));
    JCheckBoxMenuItem itmShowInstructions =
        mf.cBox(
            "Show Instructions",
            "F3",
            itemEvent ->
                ui.toggleInstructions(
                    itemEvent.getStateChange() == ItemEvent.SELECTED,
                    ((JCheckBoxMenuItem) itemEvent.getItem())::setSelected));
    menuHelp.add(itmShowHelp);
    menuHelp.add(itmShowAsciiTable);
    menuHelp.add(itmShowInstructions);

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
    itmShowAsciiTable.addItemListener(
        itemEvent ->
            ui.toggleAsciiTable(
                itemEvent.getStateChange() == ItemEvent.SELECTED, itmShowAsciiTable::setSelected));
    itmShowInstructions.addItemListener(
        itemEvent ->
            ui.toggleInstructions(
                itemEvent.getStateChange() == ItemEvent.SELECTED,
                itmShowInstructions::setSelected));

    // Bind shortcut keys
    // // File menu items
    // itmOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    // itmSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    // itmSaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
    // itmClose.setAccelerator(KeyStroke.getKeyStroke("ctrl W"));
    // itmExport.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
    // itmImport.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
    // itmExit.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));

    // // Edit menu items
    // itmFlipBit.setAccelerator(KeyStroke.getKeyStroke("F"));
    // itmUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
    // itmRedo.setAccelerator(KeyStroke.getKeyStroke("ctrl Y"));
    // itmMoveUp.setAccelerator(KeyStroke.getKeyStroke("alt UP"));
    // itmMoveDown.setAccelerator(KeyStroke.getKeyStroke("alt DOWN"));
    // itmCopy.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
    // itmPaste.setAccelerator(KeyStroke.getKeyStroke("ctrl V"));
    // itmClear.setAccelerator(KeyStroke.getKeyStroke("ctrl DELETE"));
    // itmDelete.setAccelerator(KeyStroke.getKeyStroke("ctrl shift DELETE"));
    // itmResetData.setAccelerator(KeyStroke.getKeyStroke("ctrl shift M"));

    // // Select menu items
    // itmSelectUp.setAccelerator(KeyStroke.getKeyStroke("shift UP"));
    // itmSelectDown.setAccelerator(KeyStroke.getKeyStroke("shift DOWN"));
    // itmMoveSelectionUp.setAccelerator(KeyStroke.getKeyStroke("ctrl UP"));
    // itmMoveSelectionDown.setAccelerator(KeyStroke.getKeyStroke("ctrl DOWN"));
    // itmClearSelection.setAccelerator(KeyStroke.getKeyStroke("ESCAPE"));

    // // Run menu items
    // itmStep.setAccelerator(KeyStroke.getKeyStroke("SPACE"));
    // itmRun.setAccelerator(KeyStroke.getKeyStroke("ctrl SPACE"));
    // itmResetState.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));

    // // Help menu items
    // itmShowAsciiTable.setAccelerator(KeyStroke.getKeyStroke("F1"));
    // itmShowInstructions.setAccelerator(KeyStroke.getKeyStroke("F2"));
  }
}
