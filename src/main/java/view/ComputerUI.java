package view;

import static util.LazySwing.action;
import static util.LazySwing.colorToHex;
import static util.LazySwing.inv;
import static util.LazySwing.runSafely;

import io.IOListener;
import io.ObservableIO;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import model.CPU;
import model.Memory;
import model.ProgramCounter;
import model.ProgramCounterListener;
import model.Registry;
import net.miginfocom.swing.MigLayout;
import util.FileHandler;
import util.ObservableValue;
import view.AbstractSelecter.FocusRequester;
import view.AbstractSelecter.StorageType;
import view.ComputerMenu.MenuCheckboxSetter;
import view.SnapshotDialog.Mode;

public class ComputerUI implements FocusRequester {

  private static final Font HEADLINE_FONT = new Font("Tahoma", Font.BOLD, 14);
  private static final Font HEADER_FONT = new JLabel().getFont().deriveFont(Font.BOLD, 12);

  private static final Color ERROR_HIGHLIGHT_COLOR = new Color(200, 55, 40);
  private static final String ERROR_HIGHLIGHT_COLOR_STRING = colorToHex(ERROR_HIGHLIGHT_COLOR);
  private static final Color INFO_HIGHLIGHT_COLOR = new Color(40, 55, 200);
  private static final String INFO_HIGHLIGHT_COLOR_STRING = colorToHex(INFO_HIGHLIGHT_COLOR);

  private static final String EMPTY_HTML =
      String.format(
          "<html><head><style>"
              + "body { color: black; } "
              + ".error { color: '%s'; font-weight: bold; }"
              + ".info { color: '%s'; font-weight: bold; }"
              + "</style></head><body></body></html>",
          ERROR_HIGHLIGHT_COLOR_STRING, INFO_HIGHLIGHT_COLOR_STRING);

  static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private JFrame frame;
  private Cell[] memCells;
  private Register[] regCells;

  private JTextPane txtOutput;
  private JScrollPane scrollPane;
  private JPanel controlPanel;

  private ObservableValue<Integer> programCounterFocusIdx;
  private AtomicBoolean isExecuting = new AtomicBoolean(false);

  private final Memory memory;
  private final ProgramCounter pc;
  private final CPU cpu;
  private final Registry registry;
  private final ObservableIO io;
  private final CellSelecter cellSelecter;
  private final RegisterSelecter regSelecter;

  private AsciiTable asciiTable;
  private InstructionTable instructionTable;

  private AbstractSelecter currentSelecter;
  private AbstractCell[] currentCells;

  private FileHandler fileHandler;

  public ComputerUI(Memory memory, CPU cpu, ObservableIO io) {
    this.memory = memory;
    this.cpu = cpu;
    this.pc = cpu.getProgramCounter();
    this.registry = cpu.getRegistry();
    this.io = io;

    final SelectionPainter cellPainter =
        (address, isSelected, caretPos, active) ->
            memCells[address].setSelected(isSelected, caretPos, active);
    final SelectionPainter regPainter =
        (address, isSelected, caretPos, active) ->
            regCells[address].setSelected(isSelected, caretPos, active);
    this.cellSelecter = new CellSelecter(memory, cellPainter, this);
    this.regSelecter = new RegisterSelecter(registry, regPainter, this);

    this.programCounterFocusIdx = new ObservableValue<>(pc.getCurrentIndex());
    initialize();
    requestFocus(StorageType.MEMORY);
    configureKeyBindings();

    memCells[programCounterFocusIdx.get()].setProgramCounterFocus();

    frame.pack();
    // frame.setMinimumSize(frame.getSize());

    // Max size is set during initialization, for pack() to work appropriately. Now we remove it.
    scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // Open the Ascii Table and Instructins Description frame by default.
    // toggleAsciiTable(true);
    // toggleInstructions(true);

    // showBorders(frame);

    // Example programs:
    // Print hello: UBJRF8DQ:12:SEVMTE8h
    // Infinite loop: UwdUF4M=:2:hA==:15:gw==
  }

  /** Initialize the contents of the frame. */
  private void initialize() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            handleExit();
          }
        });
    frame.setTitle("SeaPeaEwe");
    frame.getContentPane().setLayout(new MigLayout("", "[]", "[]"));

    fileHandler =
        new FileHandler(
            frame,
            title -> inv(() -> frame.setTitle("SeaPeaEwe" + (title != null ? " - " + title : ""))));
    frame.setJMenuBar(new ComputerMenu(this, fileHandler));

    JLabel lblComputerHeader = new JLabel("SeaPeaEwe 8-bit Computer");
    lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
    lblComputerHeader.setFocusable(false);
    frame.getContentPane().add(lblComputerHeader, "cell 0 0 3 1");

    JTextArea lblDescription =
        new JTextArea(
            String.format(
                "A simple emulator of a CPU and memory. This computer has an 8-bit processor,"
                    + " with %d bytes of memory and (%d+1) registers (including program counter). "
                    + "Note that all registers have names, but are still addressed using their"
                    + " indices 0\u2014%d.",
                memory.size(), Registry.NUM_REGISTERS, Registry.NUM_REGISTERS - 1));
    lblDescription.setLineWrap(true);
    lblDescription.setWrapStyleWord(true);
    lblDescription.setEditable(false);
    lblDescription.setBorder(null);
    lblDescription.setBackground(UIManager.getColor("Label.background"));
    lblDescription.setFocusable(false);
    frame.getContentPane().add(lblDescription, "cell 0 1 3 1, grow");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 10));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    memoryPanel = createCellPanel(false);
    appendHeaderToCellPanel(memoryPanel, "Memory", false);
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 3, top, left, grow, shrink");

    // Memory cells
    {
      memoryCellsPanel = createCellPanel(false);
      // new JPanel(
      //     new MigLayout(
      //         "wrap 6, gap 0 0, insets 0, fill",
      //         "[sg addr][sg value][sg hex][sg dec][sg ascii][sg instr]",
      //         "[]"));
      memoryCellsPanel.setFocusable(true);
      scrollPane =
          new JScrollPane(
              memoryCellsPanel,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      memoryCellsPanel.setBorder(null);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);
      scrollPane.setMaximumSize(new Dimension(450, 450));

      // Remove arrow key bindings for vertical and horizontal scroll bars
      InputMap im = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      im.put(KeyStroke.getKeyStroke("UP"), "none");
      im.put(KeyStroke.getKeyStroke("DOWN"), "none");
      im.put(KeyStroke.getKeyStroke("LEFT"), "none");
      im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

      memoryPanel.add(scrollPane, "span, top, left, grow, shrink, h 400px::");

      memCells = new Cell[memory.size()];
      for (int i = 0; i < memory.size(); i++) {
        final int idx = i;
        memCells[i] =
            new Cell(memoryCellsPanel, i, value -> memory.setValueAt(idx, value), cellSelecter);
        // memoryCellsPanel.add(memCells[i]);
      }

      memory.addListener(
          (startIdx, values) ->
              inv(
                  () -> {
                    boolean executing = isExecuting.get();
                    for (int i = 0; i < values.length; i++) {
                      memCells[startIdx + i].setValue(values[i], executing);
                    }
                  }));

      appendHeaderToCellPanel(memoryCellsPanel, null, false);
    }

    // Registers and program counter
    {
      JPanel registerPanel = createCellPanel(true);
      appendHeaderToCellPanel(registerPanel, "Registers", true);
      frame.getContentPane().add(registerPanel, "cell 1 3, top, left, grow, shrink");

      // JPanel regCellsPanel =
      //     new JPanel(new MigLayout("flowy, gap 0 0, insets 0, fill", "[grow]", "[grow]"));
      // regCellsPanel.setFocusable(true);
      // regCellsPanel.setBorder(null);
      // registerPanel.add(regCellsPanel, "cell 0 3 8 1, top, left, grow");

      // Computer has 8 registers, OP1-OP3 and R1-R3, plus PRT and PC.
      // R1-R3 are general purpose registers, OP1-OP3 are used for operations.
      regCells = new Register[Registry.NUM_REGISTERS];
      // int offset = 3;
      // final String cellFormat = "cell 0 %d";
      for (int i = 0; i < regCells.length - 1; i++) {
        final int idx = i;
        regCells[i] =
            new Register(
                registerPanel,
                i,
                Registry.REGISTER_NAMES[i],
                value -> registry.setValueAt(idx, value),
                regSelecter);
        // registerPanel.add(regCells[i], String.format(cellFormat, offset + idx));
        if (idx == 2) {
          // offset++;
          registerPanel.add(Box.createRigidArea(new Dimension(10, 10)), "wrap");
        }
      }

      registerPanel.add(Box.createRigidArea(new Dimension(20, 10)), "wrap");

      Register pcCell =
          new Register(
              registerPanel, Registry.NUM_REGISTERS - 1, "PC", pc::setCurrentIndex, regSelecter);
      // regCellsPanel.add(pcCell, String.format(cellFormat, offset + Registry.NUM_REGISTERS + 1));
      regCells[Registry.NUM_REGISTERS - 1] = pcCell;

      {
        cpu.addRegistryListener(
            (startIdx, values) ->
                inv(
                    () -> {
                      boolean executing = isExecuting.get();
                      for (int i = 0; i < values.length; i++) {
                        regCells[startIdx + i].setValue(values[i], executing);
                      }
                    }));
        pc.addListener(
            new ProgramCounterListener() {
              @Override
              public void onProgramCounterChanged(int oldIdx, int newIdx) {
                inv(
                    () -> {
                      if (oldIdx >= 0 && oldIdx < memory.size()) {
                        memCells[oldIdx].clearProgramCounterFocus();
                      }
                      pcCell.setValue(newIdx, isExecuting.get());
                      programCounterFocusIdx.set(newIdx);
                      if (newIdx >= 0 && newIdx < memory.size()) {
                        memCells[newIdx].setProgramCounterFocus();
                      } else {
                        pcCell.highlightError();
                      }
                    });
              }

              public void onProgramCounterHalted() {
                pcCell.highlightCompleted();
                appendInfo("Program execution completed.");
              }
            });
      }
    }

    // Divider between registers and controls
    frame.getContentPane().add(Box.createRigidArea(new Dimension(10, 30)), "cell 1 4, top");

    // Execution controls
    {
      controlPanel = new JPanel();
      controlPanel.setBorder(BorderFactory.createTitledBorder(null, "Controls", 0, 0, null));
      frame.getContentPane().add(controlPanel, "cell 1 5, top, grow, shrink");
      controlPanel.setLayout(new MigLayout("", "[][][][][grow,shrink]", "[][][][][]"));

      JLabel lblControlHeader = new JLabel("Controls");
      lblControlHeader.setFont(HEADLINE_FONT);
      controlPanel.add(lblControlHeader, "cell 0 0");

      // Step button
      {
        JButton btnStep = new JButton("Step");
        btnStep.setFocusable(false);
        btnStep.addActionListener(e -> handleStep());
        controlPanel.add(btnStep, "cell 0 1");
      }

      // Run button
      {
        JButton btnRun = new JButton("Run");
        btnRun.setFocusable(false);
        btnRun.addActionListener(e -> handleRun());
        controlPanel.add(btnRun, "cell 1 1");
      }

      // Reset button
      {
        JButton btnReset = new JButton("Reset");
        btnReset.setFocusable(false);
        btnReset.addActionListener(e -> handleResetState());
        controlPanel.add(btnReset, "cell 2 1");
      }
      // Help button
      {
        JButton btnHelp = new JButton("Help (F1)");
        btnHelp.setFocusable(false);
        btnHelp.addActionListener(e -> synchronizeColumnWidths());
        controlPanel.add(btnHelp, "cell 3 1");
      }

      // Small space
      controlPanel.add(Box.createRigidArea(new Dimension(10, 10)), "cell 0 2");

      // Output textbox
      {
        JLabel lblOutput = new JLabel("Output:");
        controlPanel.add(lblOutput, "cell 0 3, top, left");

        JButton btnClearOutput = new JButton("Clear output");
        btnClearOutput.setFocusable(false);
        btnClearOutput.addActionListener(e -> handleClearOutput());
        controlPanel.add(btnClearOutput, "cell 2 3 2 1, right");

        txtOutput = new JTextPane();
        txtOutput.setContentType("text/html");
        txtOutput.setEditorKit(new HTMLEditorKit());
        txtOutput.setFocusable(false);
        // txtOutput.setOpaque(false);
        txtOutput.setEditable(false);
        txtOutput.setMargin(new Insets(0, 0, 0, 0));
        txtOutput.setFont(lblOutput.getFont());
        txtOutput.setBorder(null);
        txtOutput.setText(EMPTY_HTML);
        JScrollPane outputScroll =
            new JScrollPane(
                txtOutput,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outputScroll.setMinimumSize(new Dimension(150, 150));
        controlPanel.add(outputScroll, "cell 0 4 5 1, grow, top, left");

        io.addListener(
            new IOListener() {
              @Override
              public void print(int value) {
                handlePrint(value, false);
              }

              @Override
              public void print(char character) {
                handlePrint(character, true);
              }
            });
      }
    }
  }

  private void configureKeyBindings() {
    // Disabling TAB and Shift+TAB for focus traversal in this JPanel
    Set<AWTKeyStroke> forwardKeys =
        new HashSet<>(frame.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
    forwardKeys.remove(KeyStroke.getKeyStroke("TAB"));
    frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

    Set<AWTKeyStroke> backwardKeys =
        new HashSet<>(frame.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
    backwardKeys.remove(KeyStroke.getKeyStroke("shift TAB"));
    frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

    // Configuring key bindings
    InputMap imap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap amap = frame.getRootPane().getActionMap();

    // Switch between memory and register selecter
    imap.put(KeyStroke.getKeyStroke("TAB"), "switchSelecter");
    imap.put(KeyStroke.getKeyStroke("shift TAB"), "switchSelecter");
    amap.put(
        "switchSelecter",
        action(
            e -> {
              if (currentSelecter == cellSelecter) {
                regSelecter.requestFocus();
              } else if (currentSelecter == regSelecter) {
                cellSelecter.requestFocus();
              }
            }));

    // Handle arrow keys to move caret
    imap.put(KeyStroke.getKeyStroke("UP"), "caretUp");
    imap.put(KeyStroke.getKeyStroke("DOWN"), "caretDown");
    imap.put(KeyStroke.getKeyStroke("LEFT"), "caretLeft");
    imap.put(KeyStroke.getKeyStroke("RIGHT"), "caretRight");
    imap.put(KeyStroke.getKeyStroke("ENTER"), "caretNextCell");
    amap.put("caretUp", action(e -> currentSelecter.moveCaretUp()));
    amap.put("caretDown", action(e -> currentSelecter.moveCaretDown()));
    amap.put("caretLeft", action(e -> currentSelecter.moveCaretLeft()));
    amap.put("caretRight", action(e -> currentSelecter.moveCaretRight()));
    amap.put("caretNextCell", action(e -> currentSelecter.moveCaretToNextCell()));

    // Handle setting bit values (flipping bits are handled in the ComuterMenu class)
    imap.put(KeyStroke.getKeyStroke("0"), "setBit0");
    imap.put(KeyStroke.getKeyStroke("1"), "setBit1");
    amap.put("setBit0", action(e -> setBit(false)));
    amap.put("setBit1", action(e -> setBit(true)));

    // Handle page up and page down to scroll memory cells
    final JScrollBar vscroll = scrollPane.getVerticalScrollBar();
    imap.put(KeyStroke.getKeyStroke("PAGE_UP"), "scrollUp");
    imap.put(KeyStroke.getKeyStroke("PAGE_DOWN"), "scrollDown");
    amap.put("scrollUp", action(e -> vscroll.setValue(vscroll.getValue() - 16 * 4)));
    amap.put("scrollDown", action(e -> vscroll.setValue(vscroll.getValue() + 16 * 4)));
  }

  private void synchronizeColumnWidths() {
    Component[] headerComponents = memoryPanel.getComponents();
    Component[] bodyComponents = memoryCellsPanel.getComponents();

    for (int i = 0; i < headerComponents.length; i++) {
      headerComponents[i].setPreferredSize(
          new Dimension(
              bodyComponents[i].getPreferredSize().width,
              headerComponents[i].getPreferredSize().height));
    }
  }

  @Override
  public void requestFocus(StorageType type) {
    if (type == StorageType.MEMORY) {
      cellSelecter.setActive();
      regSelecter.setInactive();
      currentSelecter = cellSelecter;
      currentCells = memCells;
    } else {
      cellSelecter.setInactive();
      regSelecter.setActive();
      currentSelecter = regSelecter;
      currentCells = regCells;
    }
  }

  // Package protected methods, used internally or by other view classes

  JFrame getFrame() {
    return frame;
  }

  AbstractSelecter getCurrentSelecter() {
    return currentSelecter;
  }

  void toggleHelp(boolean display, MenuCheckboxSetter setter) {}

  void toggleAsciiTable(boolean display, MenuCheckboxSetter setter) {
    if (display) {
      if (asciiTable == null) {
        asciiTable = new AsciiTable(frame);
        asciiTable.addWindowListener(
            new WindowAdapter() {
              @Override
              public void windowClosed(WindowEvent e) {
                setter.setCheckbox(false);
              }
            });
      }
    } else {
      if (asciiTable != null) {
        asciiTable.dispose();
        asciiTable = null;
      }
    }
    frame.requestFocus();
  }

  void toggleInstructions(boolean display, MenuCheckboxSetter setter) {
    if (display) {
      if (instructionTable == null) {
        instructionTable = new InstructionTable(frame, memory, pc);
        instructionTable.addWindowListener(
            new WindowAdapter() {
              @Override
              public void windowClosed(WindowEvent e) {
                setter.setCheckbox(false);
              }
            });
      }
    } else {
      if (instructionTable != null) {
        instructionTable.dispose();
        instructionTable = null;
      }
    }
    frame.requestFocus();
  }

  void toggleHelpOnStartup(boolean value) {}

  void toggleAsciiTableOnStartup(boolean value) {}

  void toggleInstructionsOnStartup(boolean value) {}

  void toggleMoveCaretAfterInput(boolean value) {}

  void flipBit() {
    if (currentSelecter.isMouseSelectingOngoing()) {
      return; // Do not allow editing during selection
    }
    currentCells[currentSelecter.getCaretRow()].flipBit(currentSelecter.getCaretCol());
    currentSelecter.moveCaretRight();
    fileHandler.setIsModified(true);
  }

  void setBit(boolean value) {
    if (currentSelecter.isMouseSelectingOngoing()) {
      return; // Do not allow editing during selection
    }
    currentCells[currentSelecter.getCaretRow()].setBit(currentSelecter.getCaretCol(), value);
    currentSelecter.moveCaretRight();
    fileHandler.setIsModified(true);
  }

  void handleStep() {
    try {
      isExecuting.set(true);
      resetCellColors();
      cpu.step();
    } catch (Exception ex) {
      appendError(ex);
    } finally {
      inv(() -> isExecuting.set(false));
    }
  }

  void handleRun() {
    try {
      isExecuting.set(true);
      resetCellColors();
      cpu.run();
    } catch (Exception ex) {
      appendError(ex);
    } finally {
      inv(() -> isExecuting.set(false));
    }
  }

  void handleResetState() {
    pc.setCurrentIndex(0);
    registry.reset();
    inv(this::resetCellColors);
  }

  void handleClearOutput() {
    // txtOutput.setContentType("text/plain");
    // txtOutput.setText("");
    // txtOutput.setContentType("text/html");

    txtOutput.setText("");

    controlPanel.revalidate();
    controlPanel.repaint();
  }

  void handleDeleteAllData() {
    int result =
        JOptionPane.showConfirmDialog(
            frame,
            "This will delete all data in memory and registers.\n"
                + "This action cannot be undone. Are you sure you want to continue?",
            "Delete all data",
            JOptionPane.WARNING_MESSAGE,
            JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
      cpu.reset();
      memory.reset();
      executor.schedule(() -> inv(this::resetCellColors), 700, TimeUnit.MILLISECONDS);
    }
  }

  void handleExit() {
    if (fileHandler.isModified()) {
      if (fileHandler.isFileOpened()) {
        int result =
            JOptionPane.showConfirmDialog(
                frame,
                "You have unsaved changes. Do you want to save them before closing?",
                "Save changes",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
          runSafely(frame, () -> fileHandler.saveFile(getMemorySnapshot()));
        } else if (result == JOptionPane.NO_OPTION) {
          System.exit(0);
        }
        // If the user chooses to cancel, do nothing.
      } else {
        int result =
            JOptionPane.showConfirmDialog(
                frame,
                "Do you want to save your program before closing?",
                "Save changes",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (result == JOptionPane.YES_OPTION) {
          runSafely(frame, () -> fileHandler.saveFile(getMemorySnapshot()));
        } else if (result == JOptionPane.NO_OPTION) {
          System.exit(0);
        }
        // If the user chooses to cancel, do nothing.
      }
    } else {
      System.exit(0);
    }
  }

  void exportAsBase64() {
    String memorySnapdhot = memory.exportAsBase64();
    if (memorySnapdhot.isEmpty()) {
      memorySnapdhot = "(Memory is empty)";
    }
    SnapshotDialog dialog = new SnapshotDialog(frame, Mode.EXPORT);
    dialog.setText(memorySnapdhot);
    dialog.setVisible(true);
  }

  void importFromBase64() {
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
  }

  String[] getMemorySnapshot() {
    return memory.exportAsBinary();
  }

  void setMemorySnapshot(String[] snapshot) {
    if (snapshot == null || snapshot.length == 0) {
      return;
    }
    memory.importFromBinary(snapshot);
  }

  // Private methods, used internally

  private void resetCellColors() {
    for (Cell c : memCells) {
      c.unhighlight();
    }
    for (Register r : regCells) {
      r.unhighlight();
    }
  }

  private boolean newParagraph = true;
  private JPanel memoryPanel;
  private JPanel memoryCellsPanel;

  private void handlePrint(int value, boolean isAscii) {
    if (isAscii) {
      // Treat value as ASCII character and append to print label
      char c = (char) (value & 0xFF);
      if (c == '\n') {
        newParagraph = true;
      } else {
        if (newParagraph) {
          appendHtmlContent("<p>");
          newParagraph = false;
        }
        appendStringContent(String.valueOf(c));
      }
    } else {
      appendHtmlContent("<p>");
      appendStringContent(String.valueOf(value));
      newParagraph = true;
    }
  }

  private void appendError(Exception ex) {
    newParagraph = true;
    appendHtmlContent(
        String.format(
            "<p class='error'>%s<br>%s</p>", ex.getClass().getSimpleName(), ex.getMessage()));
  }

  private void appendInfo(String msg) {
    newParagraph = true;
    appendHtmlContent(String.format("<p class='info'>%s</p>", msg));
  }

  // Helper method to append HTML content to the JTextPane.
  private void appendHtmlContent(String htmlContent) {
    inv(
        () -> {
          try {
            HTMLDocument doc = (HTMLDocument) txtOutput.getDocument();
            HTMLEditorKit editorKit = (HTMLEditorKit) txtOutput.getEditorKit();
            editorKit.insertHTML(doc, doc.getLength(), htmlContent, 0, 0, null);
          } catch (Exception e) {
            e.printStackTrace();
          }
          controlPanel.revalidate();
          controlPanel.repaint();
        });
  }

  // Helper method to append HTML content to the JTextPane.
  private void appendStringContent(final String content) {
    inv(
        () -> {
          try {
            HTMLDocument doc = (HTMLDocument) txtOutput.getDocument();
            String transformed = content.equals(" ") ? "&nbsp;" : content;

            // Find the last paragraph ('<p>') element.
            ElementIterator iterator = new ElementIterator(doc);
            Element elem;
            Element lastParagraphElement = null;
            while ((elem = iterator.next()) != null) {
              if (elem.getName().equals("p")) {
                lastParagraphElement = elem;
              }
            }

            // Check if a last '<p>' was found and append the content to it.
            if (lastParagraphElement != null) {
              doc.insertBeforeEnd(lastParagraphElement, transformed);
            }
          } catch (BadLocationException | IOException e) {
            e.printStackTrace();
          }

          controlPanel.revalidate();
          controlPanel.repaint();
        });
  }

  private JPanel createCellPanel(boolean includeLabel) {
    JPanel cellPanel = new JPanel();
    cellPanel.setBorder(null);
    int numCols = includeLabel ? 7 : 6;
    cellPanel.setLayout(
        new MigLayout(
            "gap 5 0,insets 0,wrap " + numCols,
            // (includeLabel ? "[30px:30px:30px]" : "")
            //     +
            // "[30px:30px:30px][108px:108px:108px][30px:30px:30px][30px:30px:30px][30px:30px:30px][110px::,grow]",
            // "[][][]"
            "[sg addr, right]"
                + (includeLabel ? "[sg name, right]" : "")
                + "[sg value, center, grow][sg hex, left][sg dec, left][sg ascii, left]10[sg"
                + " instr, left, grow]",
            "[]"));

    return cellPanel;
  }

  private JPanel appendHeaderToCellPanel(JPanel cellPanel, String header, boolean includeLabel) {
    int numCols = includeLabel ? 7 : 6;
    if (header != null) {
      JLabel lblHeader = new JLabel(header);
      cellPanel.add(lblHeader, "left, wrap, span " + numCols);
      lblHeader.setFont(HEADLINE_FONT);

      cellPanel.add(Box.createRigidArea(new Dimension(20, 10)), "wrap");
    }

    cellPanel.add(header("Addr"));
    if (includeLabel) {
      cellPanel.add(header("Name"));
    }
    cellPanel.add(header("Value"));
    cellPanel.add(header("Hex"));
    cellPanel.add(header("Dec"));
    cellPanel.add(header("Ascii"));
    cellPanel.add(header("Instr"));

    return cellPanel;
  }

  private JLabel header(String text) {
    JLabel label = new JLabel(text);
    label.setFont(HEADER_FONT);
    label.setBorder(null);
    return label;
  }
}
