package view;

import static util.LazySwing.action;
import static util.LazySwing.inv;

import io.ObservableIO;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import model.CPU;
import model.Memory;
import model.ProgramCounter;
import model.Registry;
import net.miginfocom.swing.MigLayout;
import util.ObservableValue;
import view.AbstractSelecter.FocusRequester;
import view.AbstractSelecter.StorageType;
import view.SnapshotDialog.Mode;

public class ComputerUI implements FocusRequester {

  private static final Color ERROR_HIGHLIGHT_COLOR = new Color(255, 255, 200);

  static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private JFrame frame;
  private Cell[] memCells;
  private Register[] regCells;
  private Register pcCell;
  private JEditorPane lblPrintOutput;
  private JEditorPane lblErrorMessage;
  private JScrollPane scrollPane;

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

  public ComputerUI(Memory memory, ProgramCounter pc, CPU cpu, ObservableIO io) {
    this.memory = memory;
    this.pc = pc;
    this.cpu = cpu;
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

    memCells[programCounterFocusIdx.get()].setProgramCounterFocus();

    frame.pack();
    frame.setMinimumSize(frame.getSize());
    scrollPane.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // memCells[0].requestFocus();

    // Open the Ascii Table and Instructins Description frame by default.
    // toggleAsciiTable(true);
    // toggleInstructions(true);

    // showBorders(frame);
  }

  // Example program: UBJRF8DQ:12:SEVMTE8h

  /** Initialize the contents of the frame. */
  private void initialize() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setJMenuBar(new ComputerMenu(this));
    frame.getContentPane().setLayout(new MigLayout("", "[]", "[]"));

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
    // imap.put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "shiftPressed");
    // imap.put(KeyStroke.getKeyStroke("released SHIFT"), "shiftReleased");
    // amap.put("shiftPressed", action(e -> isShiftPressed = true));
    // amap.put("shiftReleased", action(e -> isShiftPressed = false));

    // Switch between memory and register selecter
    imap.put(KeyStroke.getKeyStroke("TAB"), "switchSelecter");
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
    amap.put("caretUp", action(e -> currentSelecter.moveCaretUp()));
    amap.put("caretDown", action(e -> currentSelecter.moveCaretDown()));
    amap.put("caretLeft", action(e -> currentSelecter.moveCaretLeft()));
    amap.put("caretRight", action(e -> currentSelecter.moveCaretRight()));

    // // Handle arrow keys to select multiple cells
    // imap.put(KeyStroke.getKeyStroke("shift UP"), "selectUp");
    // imap.put(KeyStroke.getKeyStroke("shift DOWN"), "selectDown");
    // amap.put("selectUp", action(e -> currentSelecter.expandSelectionUp()));
    // amap.put("selectDown", action(e -> currentSelecter.expandSelectionDown()));

    // // Handle arrow keys to move selection
    // imap.put(KeyStroke.getKeyStroke("ctrl UP"), "moveSelectionUp");
    // imap.put(KeyStroke.getKeyStroke("ctrl DOWN"), "moveSelectionDown");
    // amap.put("moveSelectionUp", action(e -> currentSelecter.moveSelectionUp()));
    // amap.put("moveSelectionDown", action(e -> currentSelecter.moveSelectionDown()));

    // // Handle arrow keys to move selected cells
    // imap.put(KeyStroke.getKeyStroke("alt UP"), "moveCellsUp");
    // imap.put(KeyStroke.getKeyStroke("alt DOWN"), "moveCellsDown");
    // amap.put("moveCellsUp", action(e -> currentSelecter.moveCellsUp()));
    // amap.put("moveCellsDown", action(e -> currentSelecter.moveCellsDown()));

    // Handle setting bit values
    imap.put(KeyStroke.getKeyStroke("ENTER"), "flipBit");
    imap.put(KeyStroke.getKeyStroke("SPACE"), "flipBit");
    imap.put(KeyStroke.getKeyStroke("0"), "setBit0");
    imap.put(KeyStroke.getKeyStroke("1"), "setBit1");
    amap.put(
        "flipBit",
        action(
            e -> {
              currentCells[currentSelecter.getCaretRow()].flipBit(currentSelecter.getCaretCol());
            }));
    amap.put(
        "setBit0",
        action(
            e ->
                currentCells[currentSelecter.getCaretRow()].setBit(
                    currentSelecter.getCaretCol(), false)));
    amap.put(
        "setBit1",
        action(
            e ->
                currentCells[currentSelecter.getCaretRow()].setBit(
                    currentSelecter.getCaretCol(), true)));

    JLabel lblComputerHeader = new JLabel("SeaPeaEwe 8-bit Computer");
    lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
    lblComputerHeader.setFocusable(false);
    frame.getContentPane().add(lblComputerHeader, "cell 0 0 3 1");

    JTextArea lblDescription =
        new JTextArea(
            String.format(
                "A simple simulation of a CPU and memory.%nThis computer has an 8-bit processor,"
                    + " with %d bytes of memory and (%d+1) registers (including program counter).\n"
                    + "Note that all registers have names, but are still addressed using their"
                    + " indices 0\u20147.",
                memory.size(), Registry.NUM_REGISTERS));
    lblDescription.setLineWrap(true);
    lblDescription.setWrapStyleWord(true);
    lblDescription.setEditable(false);
    lblDescription.setBorder(null);
    lblDescription.setBackground(UIManager.getColor("Label.background"));
    lblDescription.setFocusable(false);
    frame.getContentPane().add(lblDescription, "cell 0 1 3 1, grow");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 10));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    JPanel memoryPanel = createCellPanel("Memory");
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 3, top, left, grow");

    // Memory cells
    {
      JPanel memoryCellsPanel = new JPanel();
      memoryCellsPanel.setFocusable(true);
      scrollPane =
          new JScrollPane(
              memoryCellsPanel,
              JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      memoryCellsPanel.setBorder(null);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);
      scrollPane.setMaximumSize(new Dimension(400, 700));

      // Remove arrow key bindings for vertical and horizontal scroll bars
      InputMap im = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      im.put(KeyStroke.getKeyStroke("UP"), "none");
      im.put(KeyStroke.getKeyStroke("DOWN"), "none");
      im.put(KeyStroke.getKeyStroke("LEFT"), "none");
      im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

      memoryPanel.add(scrollPane, "cell 0 3, top, left, grow, w 370px:370px:, h 400px::");
      memoryCellsPanel.setLayout(new BoxLayout(memoryCellsPanel, BoxLayout.Y_AXIS));

      memCells = new Cell[memory.size()];
      for (int i = 0; i < memory.size(); i++) {
        final int idx = i;
        memCells[i] =
            new Cell(
                i,
                value -> {
                  memory.setValueAt(idx, value);
                },
                cellSelecter);
        memoryCellsPanel.add(memCells[i]);
      }

      {
        memory.addListener(
            (address, value) -> inv(() -> memCells[address].setValue(value, isExecuting.get())));
      }
    }

    // Registers and program counter
    {
      JPanel registerPanel = createCellPanel("Registers");
      registerPanel.setFocusable(true);
      frame.getContentPane().add(registerPanel, "cell 1 3, top");

      // Computer has 8 registers, OP1-OP3 and R1-R3, plus PRT and PC.
      // R1-R3 are general purpose registers, OP1-OP3 are used for operations.
      regCells = new Register[Registry.NUM_REGISTERS];
      int offset = 3;
      for (int i = 0; i < regCells.length - 1; i++) {
        final int idx = i;
        regCells[i] =
            new Register(
                i,
                Registry.REGISTER_NAMES[i],
                value -> registry.setRegister(idx, value),
                regSelecter);
        registerPanel.add(regCells[i], String.format("cell 0 %d", offset + idx));
        if (idx == 2) {
          offset++;
          Component rigidArea_5 = Box.createRigidArea(new Dimension(10, 10));
          registerPanel.add(rigidArea_5, String.format("cell 0 %d", offset + idx));
        }
      }

      Component rigidArea_6 = Box.createRigidArea(new Dimension(20, 10));
      registerPanel.add(rigidArea_6, String.format("cell 0 %d", offset + Registry.NUM_REGISTERS));

      pcCell =
          new Register(
              Registry.NUM_REGISTERS - 1, "PC", value -> pc.setCurrentIndex(value), regSelecter);
      registerPanel.add(pcCell, String.format("cell 0 %d", offset + Registry.NUM_REGISTERS + 1));
      regCells[Registry.NUM_REGISTERS - 1] = pcCell;

      {
        cpu.addRegistryListener(
            (address, value) -> {
              inv(
                  () -> {
                    regCells[address].setValue(value, isExecuting.get());
                  });
            });
        pc.addListener(
            (oldIdx, newIdx) -> {
              inv(
                  () -> {
                    if (oldIdx >= 0 && oldIdx < memory.size()) {
                      memCells[oldIdx].clearProgramCounterFocus();
                    }
                    pcCell.setValue(newIdx, isExecuting.get());
                    programCounterFocusIdx.set(newIdx);
                    if (newIdx >= 0 && newIdx < memory.size()) {
                      memCells[newIdx].setProgramCounterFocus();
                    }
                  });
            });
        memory.addListener((address, value) -> inv(() -> {}));
      }
    }

    // Divider between registers and controls
    {
      frame.getContentPane().add(Box.createRigidArea(new Dimension(10, 30)), "cell 1 4, top");
    }

    // Execution controls
    {
      JPanel controlPanel = new JPanel();
      controlPanel.setBorder(BorderFactory.createTitledBorder(null, "Controls", 0, 0, null));
      frame.getContentPane().add(controlPanel, "cell 1 5,growx, top");
      controlPanel.setLayout(new MigLayout("", "[][][grow,fill]", "[][][][][]"));

      JLabel lblControlHeader = new JLabel("Controls");
      lblControlHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
      controlPanel.add(lblControlHeader, "cell 0 0");

      // Step button
      {
        JButton btnStep = new JButton("Step");
        btnStep.setFocusable(false);
        btnStep.addActionListener(
            e -> {
              try {
                isExecuting.set(true);
                resetCellColors();
                cpu.step();
              } catch (Exception ex) {
                handleError(ex);
              } finally {
                inv(() -> isExecuting.set(false));
              }
            });
        controlPanel.add(btnStep, "cell 0 1");
      }

      // Run button
      {
        JButton btnRun = new JButton("Run");
        btnRun.setFocusable(false);
        btnRun.addActionListener(
            e -> {
              try {
                isExecuting.set(true);
                resetCellColors();
                cpu.run();
              } catch (Exception ex) {
                handleError(ex);
              } finally {
                inv(() -> isExecuting.set(false));
              }
            });
        controlPanel.add(btnRun, "cell 1 1");
      }

      // Small space
      controlPanel.add(Box.createRigidArea(new Dimension(10, 10)), "cell 0 2");

      // Print output textbox  UwdUF4M=:2:hA==:15:gw==
      {
        JLabel lblOutput = new JLabel("Output:");
        controlPanel.add(lblOutput, "cell 0 3, top, right");

        lblPrintOutput = new JEditorPane();
        lblPrintOutput.setFocusable(false);
        lblPrintOutput.setOpaque(false);
        lblPrintOutput.setEditable(false);
        lblPrintOutput.setMargin(new Insets(0, 0, 0, 0));
        lblPrintOutput.setFont(lblOutput.getFont());
        lblPrintOutput.setBorder(null);
        JScrollPane outputScroll =
            new JScrollPane(
                lblPrintOutput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outputScroll.setMaximumSize(new Dimension(600, 150));
        controlPanel.add(outputScroll, "cell 1 3 2 1, grow, top, left");

        io.addListener((value) -> handlePrint(value));
      }

      // Error message textbox
      {
        JLabel lblError = new JLabel("Error:");
        controlPanel.add(lblError, "cell 0 4, top, right");

        lblErrorMessage = new JEditorPane();
        lblErrorMessage.setFocusable(false);
        lblErrorMessage.setOpaque(false);
        lblErrorMessage.setEditable(false);
        lblErrorMessage.setMargin(new Insets(0, 0, 0, 0));
        lblErrorMessage.setFont(lblError.getFont());
        lblErrorMessage.setBorder(null);
        JScrollPane errorScroll =
            new JScrollPane(
                lblErrorMessage,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        errorScroll.setMaximumSize(new Dimension(600, 150));
        controlPanel.add(errorScroll, "cell 1 4 2 1, grow, top, left");
      }

      // Small space
      controlPanel.add(Box.createRigidArea(new Dimension(10, 10)), "cell 0 5");

      // Reset button
      {
        JButton btnReset = new JButton("Reset program");
        btnReset.setFocusable(false);
        btnReset.addActionListener(e -> handleResetState());
        controlPanel.add(btnReset, "cell 0 6 2 1");
      }
    }

    // Divider. Vertical line or border that fills all vertical space.
    {
      JSeparator rightDivider = new JSeparator(SwingConstants.VERTICAL);
      frame.getContentPane().add(rightDivider, "cell 2 3 1 3, growy");
    }
  }

  private void resetCellColors() {
    for (Cell c : memCells) {
      c.unhighlight();
    }
    for (Register r : regCells) {
      r.unhighlight();
    }
    pcCell.unhighlight();
    lblPrintOutput.setBackground(UIManager.getColor("Panel.background"));
    lblErrorMessage.setText("");
    lblErrorMessage.setBackground(UIManager.getColor("Panel.background"));
  }

  void handleResetState() {
    {
      lblPrintOutput.setText("");
      lblErrorMessage.setText("");
      pc.setCurrentIndex(0);
      registry.reset();
      inv(() -> resetCellColors());
    }
  }

  void handleResetAllData() {
    cpu.reset();
    memory.reset();
    memCells[0].focus(0);

    executor.schedule(() -> inv(() -> resetCellColors()), 700, TimeUnit.MILLISECONDS);
  }

  JFrame getFrame() {
    return frame;
  }

  private void handlePrint(int value) {
    // Treat value as ASCII character and appen to print label
    char c = (char) (value & 0xFF);
    System.out.println("Printing: " + (char) value);
    lblPrintOutput.setText(lblPrintOutput.getText() + c);
  }

  private void handleError(Exception ex) {
    lblErrorMessage.setText(ex.getMessage());
    lblErrorMessage.setBackground(ERROR_HIGHLIGHT_COLOR);
  }

  private JPanel createCellPanel(String header) {
    JPanel cellPanel = new JPanel();
    cellPanel.setBorder(null);
    cellPanel.setLayout(
        new MigLayout(
            "gap 5 5",
            "[30px:30px:30px][108px:108px:108px][30px:30px:30px][30px:30px:30px][30px:30px:30px][110px::,grow]",
            "[][][]"));

    JLabel lblHeader = new JLabel(header);
    cellPanel.add(lblHeader, "cell 0 0 4 1");
    lblHeader.setFont(new Font("Tahoma", Font.BOLD, 14));

    cellPanel.add(Box.createRigidArea(new Dimension(20, 10)), "cell 0 1 2 1");

    JLabel lblAddress = new JLabel("Addr");
    lblAddress.setBorder(null);
    lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
    cellPanel.add(lblAddress, "cell 0 2,alignx right");

    JLabel lblValue = new JLabel("Value");
    lblValue.setHorizontalAlignment(SwingConstants.CENTER);
    cellPanel.add(lblValue, "cell 1 2,alignx center");

    JLabel lblHex = new JLabel("Hex");
    lblHex.setHorizontalAlignment(SwingConstants.LEFT);
    cellPanel.add(lblHex, "cell 2 2,alignx left");

    JLabel lblDec = new JLabel("Dec");
    lblDec.setHorizontalAlignment(SwingConstants.LEFT);
    cellPanel.add(lblDec, "cell 3 2,alignx left");

    JLabel lblAscii = new JLabel("Ascii");
    lblAscii.setHorizontalAlignment(SwingConstants.LEFT);
    cellPanel.add(lblAscii, "cell 4 2,alignx left");

    JLabel lblInstruction = new JLabel("Instr");
    lblInstruction.setHorizontalAlignment(SwingConstants.LEFT);
    cellPanel.add(lblInstruction, "cell 5 2,alignx left");

    return cellPanel;
  }

  public void toggleAsciiTable(boolean display) {
    if (display) {
      if (asciiTable == null) {
        asciiTable = new AsciiTable(frame);
      }
    } else {
      if (asciiTable != null) {
        asciiTable.dispose();
        asciiTable = null;
      }
    }
    frame.requestFocus();
  }

  public void toggleInstructions(boolean display) {
    if (display) {
      if (instructionTable == null) {
        instructionTable = new InstructionTable(frame, memory, pc);
      }
    } else {
      if (instructionTable != null) {
        instructionTable.dispose();
        instructionTable = null;
      }
    }
    frame.requestFocus();
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

  AbstractSelecter getCurrentSelecter() {
    return currentSelecter;
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
}
