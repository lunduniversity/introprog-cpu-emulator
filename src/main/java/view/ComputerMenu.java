package view;

import static util.LazySwing.decreaseFontSize;
import static util.LazySwing.increaseFontSize;
import static util.LazySwing.inv;
import static util.LazySwing.resetFontSize;
import static util.LazySwing.runSafely;

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import util.FileHandler;
import util.MenuFactory;
import util.TemplatesHandler;

public class ComputerMenu extends JMenuBar {

  public interface MenuCheckboxSetter {
    void setCheckbox(boolean selected);
  }

  public ComputerMenu(ComputerUI ui, FileHandler fileHandler) {

    JFrame frame = ui.getFrame();
    MenuFactory mf = new MenuFactory(frame);

    JMenu menuFile = mf.menu("<u>F</u>ile", "alt F");
    JMenu menuEdit = mf.menu("<u>E</u>dit", "alt E");
    JMenu menuSelect = mf.menu("<u>S</u>elect", "alt S");
    JMenu menuExecute = mf.menu("E<u>x</u>cute", "alt X");
    JMenu menuView = mf.menu("<u>V</u>iew", "alt V");
    JMenu menuTemplates = mf.menu("<u>T</u>emplates", "alt T");
    JMenu menuSettings = mf.menu("<u>C</u>onfigure", "alt C");
    JMenu menuHelp = mf.menu("<u>H</u>elp", "alt H");
    add(menuFile);
    add(menuEdit);
    add(menuSelect);
    add(menuExecute);
    add(menuView);
    add(menuTemplates);
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
              ui.handleDeleteAllData();
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
    JMenuItem itmRun = mf.item("Run", "ctrl SPACE", e -> ui.handleRun());
    JMenuItem itmResetState = mf.item("Reset", "ctrl R", e -> ui.handleResetState());

    menuExecute.add(itmStep);
    menuExecute.add(itmRun);
    menuExecute.add(itmResetState);

    // View menu items
    JMenuItem itmIncFontSize =
        mf.item(
            "Increase font size",
            new String[] {"ctrl PLUS", "ctrl ADD", "ctrl shift EQUALS"},
            e -> {
              increaseFontSize(frame);
              inv(ui::autoResizeFrame);
            });
    JMenuItem itmDecFontSize =
        mf.item(
            "Decrease font size",
            "ctrl MINUS",
            e -> {
              decreaseFontSize(frame);
              inv(ui::autoResizeFrame);
            });
    JMenuItem itmResetFontSize =
        mf.item(
            "Reset font size",
            "ctrl 0",
            e -> {
              resetFontSize(frame);
              inv(ui::autoResizeFrame);
            });
    menuView.add(itmIncFontSize);
    menuView.add(itmDecFontSize);
    menuView.add(itmResetFontSize);

    // Templates menu items
    List<String> templateNames = TemplatesHandler.getTemplateNames();
    IntStream.range(0, templateNames.size())
        .mapToObj(
            i ->
                mf.item(
                    templateNames.get(i),
                    "ctrl alt " + (i + 1),
                    e -> {
                      boolean load = true;
                      if (fileHandler.isFileOpened() || fileHandler.isModified()) {
                        int result =
                            JOptionPane.showConfirmDialog(
                                frame,
                                "Loading a template will overwrite and replace the current"
                                    + " memory.\n"
                                    + "There is no way to undo this action. Do you want to"
                                    + " continue?",
                                "Confirm template load",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (result != JOptionPane.YES_OPTION) {
                          load = false;
                        }
                      }
                      if (load) {
                        ui.setMemorySnapshot(TemplatesHandler.getTemplate(templateNames.get(i)));
                      }
                    }))
        .forEach(menuTemplates::add);

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
  }
}
