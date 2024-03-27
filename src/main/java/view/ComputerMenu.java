package view;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class ComputerMenu extends JMenuBar {

  public ComputerMenu(ComputerUI ui) {

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
    menuFile.add(itmOpen);
    menuFile.add(itmSave);
    menuFile.add(itmSaveAs);
    menuFile.addSeparator();
    menuFile.add(itmExport);
    menuFile.add(itmImport);

    // Edit menu items
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
    itmExport.addActionListener((e) -> ui.exportAsBase64());
    itmImport.addActionListener((e) -> ui.importFromBase64());

    // Edit menu items
    itmResetState.addActionListener((e) -> ui.handleResetState());
    itmUndo.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Undo not implemented yet"));
    itmRedo.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Redo not implemented yet"));
    itmMoveUp.addActionListener(e -> ui.getCurrentSelecter().moveCellsUp());
    itmMoveDown.addActionListener(e -> ui.getCurrentSelecter().moveCellsDown());
    itmCopy.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Copy not implemented yet"));
    itmPaste.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Paste not implemented yet"));
    itmClear.addActionListener(e -> ui.getCurrentSelecter().clearSelection());
    itmDelete.addActionListener(
        e -> JOptionPane.showMessageDialog(frame, "Delete not implemented yet"));
    itmResetData.addActionListener((e) -> ui.handleResetAllData());

    // Select menu items
    itmSelectUp.addActionListener(e -> ui.getCurrentSelecter().expandSelectionUp());
    itmSelectDown.addActionListener(e -> ui.getCurrentSelecter().expandSelectionDown());
    itmMoveSelectionUp.addActionListener(e -> ui.getCurrentSelecter().moveSelectionUp());
    itmMoveSelectionDown.addActionListener(e -> ui.getCurrentSelecter().moveSelectionDown());
    itmClearSelection.addActionListener(e -> ui.getCurrentSelecter().clearSelection());

    // View menu items
    // none for now

    // Help menu items
    itmAsciiTable.addItemListener(
        itemEvent -> {
          ui.toggleAsciiTable(itemEvent.getStateChange() == ItemEvent.SELECTED);
        });
    itmInstructions.addItemListener(
        itemEvent -> ui.toggleInstructions(itemEvent.getStateChange() == ItemEvent.SELECTED));

    // Bind shortcut keys
    itmOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    itmSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    itmSaveAs.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
    itmExport.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
    itmImport.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
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
    itmAsciiTable.setAccelerator(KeyStroke.getKeyStroke("F1"));
    itmInstructions.setAccelerator(KeyStroke.getKeyStroke("F2"));
  }
}
