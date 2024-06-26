package view;

import static util.LazySwing.runSafely;

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import util.ExamplesHandler;
import util.ExecutionSpeed;
import util.FileHandler;
import util.MenuFactory;
import util.Settings;

public class ComputerMenu extends JMenuBar {

  private JCheckBoxMenuItem itmShowHelp;
  private JCheckBoxMenuItem itmShowAsciiTable;
  private JCheckBoxMenuItem itmShowInstructions;

  public interface MenuCheckboxSetter {
    void setCheckbox(boolean selected);
  }

  public ComputerMenu(ComputerUI ui, FileHandler fileHandler) {

    JFrame frame = ui.getFrame();
    Settings settings = ui.getSettings();
    MenuFactory mf = new MenuFactory(frame);

    JMenu menuFile = mf.menu("<u>F</u>ile", "alt F");
    JMenu menuEdit = mf.menu("<u>E</u>dit", "alt E");
    JMenu menuSelect = mf.menu("<u>S</u>elect", "alt S");
    JMenu menuExecute = mf.menu("E<u>x</u>ecute", "alt X");
    JMenu menuView = mf.menu("<u>V</u>iew", "alt V");
    JMenu menuExamples = mf.menu("Exa<u>m</u>ples", "alt M");
    JMenu menuSettings = mf.menu("<u>C</u>onfigure", "alt C");
    JMenu menuHelp = mf.menu("<u>H</u>elp", "alt H");
    add(menuFile);
    add(menuEdit);
    add(menuSelect);
    add(menuExecute);
    add(menuView);
    add(menuExamples);
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
    JMenuItem itmClose = mf.item("Close opened file", "ctrl W", e -> ui.handleDeleteAllData());
    itmClose.setEnabled(fileHandler.isFileOpened());
    fileHandler.addPropertyChangeListener(evt -> itmClose.setEnabled(fileHandler.isFileOpened()));
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
        mf.item("Delete all cells (use carefully!)", "ctrl shift M", e -> ui.handleDeleteAllData());
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
    JMenuItem itmRun = mf.item("Run/Stop", "ctrl SPACE", e -> ui.handleRunAndStop());
    JMenuItem itmResetState = mf.item("Reset", "ctrl R", e -> ui.handleResetState());
    JMenuItem itmClearOutput = mf.item("Clear output", "ctrl L", e -> ui.handleClearOutput());
    JMenu menuSpeed = mf.menu("Execution speed (step period)", "");
    ButtonGroup group = new ButtonGroup();
    ExecutionSpeed[] speeds = ExecutionSpeed.values();
    for (int i = 0; i < speeds.length; i++) {
      ExecutionSpeed speed = speeds[i];
      JRadioButtonMenuItem itmSpeed =
          mf.rButton(
              speed.toString(),
              "ctrl " + (i + 1),
              settings.getExecutionSpeed().equals(speed),
              e -> settings.setExecutionSpeed(speed));
      menuSpeed.add(itmSpeed);
      group.add(itmSpeed);
    }

    menuExecute.add(itmStep);
    menuExecute.add(itmRun);
    menuExecute.add(itmResetState);
    menuExecute.add(itmClearOutput);
    menuExecute.addSeparator();
    menuExecute.add(menuSpeed);

    // View menu items
    JMenuItem itmIncFontSize =
        mf.item(
            "Increase font size",
            new String[] {"ctrl PLUS", "ctrl ADD", "ctrl shift EQUALS"},
            e -> settings.increaseFontSize());
    JMenuItem itmDecFontSize =
        mf.item("Decrease font size", "ctrl MINUS", e -> settings.decreaseFontSize());
    JMenuItem itmResetFontSize =
        mf.item("Reset font size", "ctrl 0", e -> settings.resetFontSize());
    JMenuItem itmAutoResize = mf.item("Auto resize window", "F5", e -> ui.autoResizeFrame());
    menuView.add(itmIncFontSize);
    menuView.add(itmDecFontSize);
    menuView.add(itmResetFontSize);
    menuView.addSeparator();
    menuView.add(itmAutoResize);

    // Examples menu items
    List<String> exampleNames = ExamplesHandler.getExampleNames();
    IntStream.range(0, exampleNames.size())
        .mapToObj(
            i ->
                mf.item(
                    exampleNames.get(i),
                    "ctrl alt " + (i + 1),
                    e -> {
                      boolean load = true;
                      if (fileHandler.isFileOpened() || fileHandler.isModified()) {
                        int result =
                            JOptionPane.showConfirmDialog(
                                frame,
                                "Loading an example will overwrite and replace the current"
                                    + " memory.\n"
                                    + "There is no way to undo this action. Do you want to"
                                    + " continue?",
                                "Confirm example load",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (result != JOptionPane.YES_OPTION) {
                          load = false;
                        }
                      }
                      if (load) {
                        ui.setMemorySnapshot(ExamplesHandler.getExample(exampleNames.get(i)));
                      }
                    }))
        .forEach(menuExamples::add);

    // Settings menu items
    JCheckBoxMenuItem itmOpenHelp =
        mf.cBox(
            "Open Help on startup",
            "",
            settings.getShowHelpOnStartup(),
            itemEvent ->
                settings.setShowHelpOnStartup(itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmOpenAscii =
        mf.cBox(
            "Open ASCII table on startup",
            "",
            settings.getShowAsciiTableOnStartup(),
            itemEvent ->
                settings.setShowAsciiTableOnStartup(
                    itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmOpenInstr =
        mf.cBox(
            "Open Instructions on startup",
            "",
            settings.getShowInstructionsOnStartup(),
            itemEvent ->
                settings.setShowInstructionsOnStartup(
                    itemEvent.getStateChange() == ItemEvent.SELECTED));

    JCheckBoxMenuItem itmAnchorAscii =
        mf.cBox(
            "Anchor ASCII table to main window",
            "",
            settings.getAnchorAsciiTable(),
            itemEvent ->
                settings.setAnchorAsciiTable(itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmAnchorInstr =
        mf.cBox(
            "Anchor Instructions to main window",
            "",
            settings.getAnchorInstructions(),
            itemEvent ->
                settings.setAnchorInstructions(itemEvent.getStateChange() == ItemEvent.SELECTED));
    JCheckBoxMenuItem itmMoveCaret =
        mf.cBox(
            "Move caret after input",
            "",
            settings.getMoveCaretAfterInput(),
            itemEvent ->
                settings.setMoveCaretAfterInput(itemEvent.getStateChange() == ItemEvent.SELECTED));
    menuSettings.add(itmOpenHelp);
    menuSettings.add(itmOpenAscii);
    menuSettings.add(itmOpenInstr);
    menuSettings.addSeparator();
    menuSettings.add(itmAnchorAscii);
    menuSettings.add(itmAnchorInstr);
    menuSettings.addSeparator();
    menuSettings.add(itmMoveCaret);

    itmShowHelp =
        mf.cBox(
            "Show help",
            "F1",
            false,
            itemEvent ->
                ui.toggleHelp(
                    itemEvent.getStateChange() == ItemEvent.SELECTED,
                    ((JCheckBoxMenuItem) itemEvent.getItem())::setSelected));
    itmShowAsciiTable =
        mf.cBox(
            "Show ASCII Table",
            "F2",
            false,
            itemEvent ->
                ui.toggleAsciiTable(
                    itemEvent.getStateChange() == ItemEvent.SELECTED,
                    ((JCheckBoxMenuItem) itemEvent.getItem())::setSelected));
    itmShowInstructions =
        mf.cBox(
            "Show Instructions",
            "F3",
            false,
            itemEvent ->
                ui.toggleInstructions(
                    itemEvent.getStateChange() == ItemEvent.SELECTED,
                    ((JCheckBoxMenuItem) itemEvent.getItem())::setSelected));
    menuHelp.add(itmShowHelp);
    menuHelp.add(itmShowAsciiTable);
    menuHelp.add(itmShowInstructions);
  }

  void triggerManuallyAsciiTable() {
    itmShowAsciiTable.doClick();
  }

  void triggerManuallyInstructions() {
    itmShowInstructions.doClick();
  }

  void triggerManuallyHelp() {
    itmShowHelp.doClick();
  }
}
