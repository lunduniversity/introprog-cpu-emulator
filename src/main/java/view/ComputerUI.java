package view;

import static util.LazySwing.action;
import static util.LazySwing.colorToHex;
import static util.LazySwing.inv;
import static util.LazySwing.runSafely;

import instruction.Instruction;
import instruction.InstructionFactory;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
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
import javax.swing.SwingConstants;
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
import util.ExecutionSpeed;
import util.FileHandler;
import util.LazySwing;
import util.Settings;
import util.SizedLabel;
import view.AbstractSelecter.FocusRequester;
import view.AbstractSelecter.StorageType;
import view.ComputerMenu.MenuCheckboxSetter;
import view.SnapshotDialog.Mode;

public class ComputerUI implements FocusRequester {

  private static final Color ERROR_HIGHLIGHT_COLOR = new Color(200, 55, 40);
  private static final String ERROR_HIGHLIGHT_COLOR_STRING = colorToHex(ERROR_HIGHLIGHT_COLOR);
  private static final Color INFO_HIGHLIGHT_COLOR = new Color(40, 55, 200);
  private static final String INFO_HIGHLIGHT_COLOR_STRING = colorToHex(INFO_HIGHLIGHT_COLOR);

  private static final Dimension SCROLLER_SIZE = new Dimension(4500, 450);

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

  private JPanel memoryPanel;
  private JPanel memoryCellsPanel;

  private JTextPane txtOutput;
  private JScrollPane scrollPane;
  private JPanel controlPanel;

  private InstructionHighlighter instructionHighlighter;
  private AtomicBoolean isExecuting = new AtomicBoolean(false);

  private final Memory memory;
  private final ProgramCounter pc;
  private final CPU cpu;
  private final Registry registry;
  private final ObservableIO io;
  private final CellSelecter cellSelecter;
  private final RegisterSelecter regSelecter;

  private HelpWindow helpWindow;
  private AsciiTable asciiTable;
  private InstructionTable instructionTable;

  private AbstractSelecter currentSelecter;
  private AbstractCell[] currentCells;

  private FileHandler fileHandler;
  private boolean newParagraph = true;
  private JPanel registerPanel;

  private InstructionFactory factory;
  private JTextArea lblDescription;
  private JScrollPane outputScroll;
  private JButton btnRun;
  private ScheduledFuture<?> executionTask;
  private JButton btnStep;
  private JButton btnReset;
  private Settings settings;
  private ComputerMenu menu;

  public ComputerUI(Memory memory, CPU cpu, ObservableIO io) {
    this.memory = memory;
    this.cpu = cpu;
    this.pc = cpu.getProgramCounter();
    this.registry = cpu.getRegistry();
    this.io = io;
    this.factory = new InstructionFactory();
    this.settings = Settings.loadFromFile();

    // Set Swings default font size
    UIManager.put("Label.font", new Font("Tahoma", Font.PLAIN, Settings.DEFAULT_FONT_SIZE));

    final SelectionPainter cellPainter =
        (address, isSelected, caretPos, active) ->
            memCells[address].setSelected(isSelected, caretPos, active);
    final SelectionPainter regPainter =
        (address, isSelected, caretPos, active) ->
            regCells[address].setSelected(isSelected, caretPos, active);
    this.cellSelecter =
        new CellSelecter(memory, cellPainter, this, idx -> memCells[idx].scrollTo());
    this.regSelecter = new RegisterSelecter(registry, regPainter, this);

    this.instructionHighlighter =
        new InstructionHighlighter(
            AbstractCell::setProgramCounterFocus, // PC cell focus
            AbstractCell::clearProgramCounterFocus, // PC cell unfocus
            AbstractCell::setProgramCounterHighlight, // Highlight cell
            AbstractCell::clearProgramCounterHighlight); // Unhighlight cell
    initializeUI();
    requestFocus(StorageType.MEMORY);
    configureKeyBindings();
    setupSettingsListeners();

    instructionHighlighter.switchCells(memCells[0], memCells[0]);

    updateGlobalFontSize();

    inv(
        () -> {
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);

          // Open additional windows if needed
          if (settings.getShowAsciiTableOnStartup()) {
            menu.triggerManuallyAsciiTable();
          }
          if (settings.getShowInstructionsOnStartup()) {
            menu.triggerManuallyInstructions();
          }
          if (settings.getShowHelpOnStartup()) {
            menu.triggerManuallyHelp();
          }
        });

    // showBorders(frame);

    // Example programs:
    // Print hello: UBJRF8DQ:12:SEVMTE8h
    // Infinite loop: UwdUF4M=:2:hA==:15:gw==
  }

  /** Initialize the contents of the frame. */
  private void initializeUI() {
    frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            handleExit();
          }
        });
    frame.setTitle("c3pu");
    frame.getContentPane().setLayout(new MigLayout("fillx", "[][][]", "[]"));

    fileHandler =
        new FileHandler(
            frame,
            title -> inv(() -> frame.setTitle("c3pu" + (title != null ? " - " + title : ""))));
    menu = new ComputerMenu(this, fileHandler);
    frame.setJMenuBar(menu);

    JLabel lblApplicationHeader = new JLabel("c3pu 8-bit Computer");
    lblApplicationHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
    lblApplicationHeader.setFocusable(false);
    frame.getContentPane().add(lblApplicationHeader, "cell 0 0 3 1");

    lblDescription =
        new JTextArea(
            String.format(
                "A simple emulator of a CPU and memory. This computer has an 8-bit processor,"
                    + " with %d bytes of memory and (%d+1) registers (including program counter). "
                    + "Note that all registers have names, but are still addressed using their"
                    + " indices 0\u2014%d.",
                memory.size(), Registry.NUM_REGISTERS - 1, Registry.NUM_REGISTERS - 1));
    lblDescription.setLineWrap(true);
    lblDescription.setWrapStyleWord(true);
    lblDescription.setEditable(false);
    lblDescription.setBorder(null);
    lblDescription.setBackground(UIManager.getColor("Label.background"));
    lblDescription.setFocusable(false);
    frame.getContentPane().add(lblDescription, "cell 0 1 3 1, grow, shrink");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 10));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    memoryPanel = createCellPanel(false);
    appendHeaderToCellPanel(memoryPanel, "Memory", false, false);
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 3, top, left, grow");

    // Memory cells
    {
      memoryCellsPanel = createCellPanel(false);
      memoryCellsPanel.setFocusable(true);
      scrollPane =
          new JScrollPane(
              memoryCellsPanel,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      memoryCellsPanel.setBorder(null);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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
      }

      memory.addListener(
          (startIdx, values) ->
              inv(
                  () -> {
                    boolean executing = isExecuting.get();
                    for (int i = 0; i < values.length; i++) {
                      memCells[startIdx + i].setValue(values[i], executing);
                    }
                    // If any of the changed cells are highlighted, redo highlighting
                    AbstractCell[] cells =
                        Arrays.copyOfRange(memCells, startIdx, startIdx + values.length);
                    if (instructionHighlighter.isAffected(cells)) {
                      highlightInstructions();
                    }
                  }));

      appendHeaderToCellPanel(memoryCellsPanel, null, false, true);
    }

    // Divider between memory and registers
    frame.getContentPane().add(Box.createRigidArea(new Dimension(10, 30)), "cell 1 3");

    // Registers and program counter
    {
      registerPanel = createCellPanel(true);
      appendHeaderToCellPanel(registerPanel, "Registers", true, false);
      frame.getContentPane().add(registerPanel, "cell 2 3, top, left, grow, shrink");

      // Computer has 8 registers, OP1-OP3 and R1-R3, plus PRT and PC.
      // R1-R3 are general purpose registers, OP1-OP3 are used for operations.
      regCells = new Register[Registry.NUM_REGISTERS];
      for (int i = 0; i < regCells.length - 1; i++) {
        final int idx = i;
        regCells[i] =
            new Register(
                registerPanel,
                i,
                Registry.REGISTER_NAMES[i],
                value -> registry.setValueAt(idx, value),
                regSelecter);
        if (idx == 2) {
          registerPanel.add(Box.createRigidArea(new Dimension(10, 10)), "wrap");
        }
      }

      registerPanel.add(Box.createRigidArea(new Dimension(20, 10)), "wrap");

      Register pcCell =
          new Register(
              registerPanel, Registry.NUM_REGISTERS - 1, "PC", pc::setCurrentIndex, regSelecter);
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
                      // If any of the changed cells are highlighted, redo highlighting
                      AbstractCell[] cells =
                          Arrays.copyOfRange(regCells, startIdx, startIdx + values.length);
                      if (instructionHighlighter.isAffected(cells)) {
                        highlightInstructions();
                      }
                    }));
        pc.addListener(
            new ProgramCounterListener() {
              @Override
              public void onProgramCounterChanged(int oldIdx, int newIdx) {
                inv(
                    () -> {
                      for (Cell c : memCells) {
                        c.clearProgramCounterFocus();
                      }
                      for (Register r : regCells) {
                        r.clearProgramCounterFocus();
                      }

                      pcCell.setValue(newIdx, isExecuting.get());
                      if (!highlightInstructions()) {
                        instructionHighlighter.clearCells();
                        pcCell.highlightError();
                      }
                      memoryCellsPanel.revalidate();
                      memoryCellsPanel.repaint();
                      registerPanel.revalidate();
                      registerPanel.repaint();
                    });
              }

              public void onProgramCounterHalted(int haltReson) {
                inv(
                    () -> {
                      pcCell.highlightCompleted();
                      registerPanel.revalidate();
                      registerPanel.repaint();
                      if (haltReson == ProgramCounter.NORMAL_HALT) {
                        appendInfo("Program execution completed.");
                      } else if (haltReson == ProgramCounter.END_OF_MEMORY) {
                        appendError("Program reached end of memory.");
                      } else {
                        appendError("Program halted due to an error.");
                      }
                    });
              }
            });
      }
    }

    // Divider between registers and controls
    frame.getContentPane().add(Box.createRigidArea(new Dimension(10, 30)), "cell 2 4, top");

    // Execution controls
    {
      controlPanel = new JPanel();
      controlPanel.setBorder(BorderFactory.createTitledBorder(null, "Controls", 0, 0, null));
      frame.getContentPane().add(controlPanel, "cell 2 5, top, left, grow, shrink");
      controlPanel.setLayout(new MigLayout("fillx", "[][][][][grow,shrink]", "[]"));

      JLabel lblControlHeader = new SizedLabel("Controls", 2, true);
      controlPanel.add(lblControlHeader, "cell 0 0 5 1, top, left, wrap");

      // Step button
      {
        btnStep = new JButton("Step");
        btnStep.setFocusable(false);
        btnStep.addActionListener(e -> handleStep());
        controlPanel.add(btnStep, "cell 0 1");
      }

      // Run button
      {
        btnRun = new JButton("Run");
        btnRun.setFocusable(false);
        btnRun.addActionListener(e -> handleRunAndStop());
        controlPanel.add(btnRun, "cell 1 1");
      }

      // Reset button
      {
        btnReset = new JButton("Reset");
        btnReset.setFocusable(false);
        btnReset.addActionListener(e -> handleResetState());
        controlPanel.add(btnReset, "cell 2 1");
      }
      // Help button
      {
        JButton btnHelp = new JButton("Help (F1)");
        btnHelp.setFocusable(false);
        btnHelp.addActionListener(e -> menu.triggerManuallyHelp());
        controlPanel.add(btnHelp, "cell 3 1");
      }

      // Small space
      controlPanel.add(Box.createRigidArea(new Dimension(10, 10)), "cell 0 2");

      // Output textbox
      {
        JLabel lblOutput = new JLabel("Output:");
        controlPanel.add(lblOutput, "cell 0 3 2 1, top, left");

        JButton btnClearOutput = new JButton("Clear output (Ctrl + L)");
        btnClearOutput.setFocusable(false);
        btnClearOutput.addActionListener(e -> handleClearOutput());
        controlPanel.add(btnClearOutput, "cell 2 3 3 1, right");

        txtOutput = new JTextPane();
        txtOutput.setContentType("text/html");
        txtOutput.setEditorKit(new HTMLEditorKit());
        txtOutput.setFocusable(true);
        txtOutput.setEditable(false);
        txtOutput.setMargin(new Insets(0, 0, 0, 0));
        txtOutput.setFont(lblOutput.getFont());
        txtOutput.setBorder(null);
        txtOutput.setText(EMPTY_HTML);
        txtOutput.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        outputScroll =
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
    final String tabAction = "switchSelecter";
    imap.put(KeyStroke.getKeyStroke("TAB"), tabAction);
    imap.put(KeyStroke.getKeyStroke("shift TAB"), tabAction);
    amap.put(
        tabAction,
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
    amap.put(
        "scrollUp",
        action(
            e ->
                vscroll.setValue(
                    vscroll.getValue() - (20 + settings.getCurrentFontSize() / 2) * 10)));
    amap.put(
        "scrollDown",
        action(
            e ->
                vscroll.setValue(
                    vscroll.getValue() + (20 + settings.getCurrentFontSize() / 2) * 10)));
  }

  void updateGlobalFontSize() {
    int currentFontSize = settings.getCurrentFontSize();
    LazySwing.setComponentTreeFontSize(frame, currentFontSize);
    inv(
        () -> {
          // Not sure why this is needed, but it is. When changing multiple font sizes at once, i.e.
          // going from large to default, the frame size is not updated correctly. But the second
          // call to autoResizeFrame() fixes it. Delaying the call further does not work.
          autoResizeFrame();
          autoResizeFrame();
        });
    if (helpWindow != null) {
      helpWindow.updateGlobalFontSize(currentFontSize);
    }
    if (asciiTable != null) {
      asciiTable.updateGlobalFontSize(currentFontSize);
    }
    if (instructionTable != null) {
      instructionTable.updateGlobalFontSize(currentFontSize);
    }
  }

  private void setupSettingsListeners() {
    settings.addPropertyChangeListener(
        Settings.ANCHOR_ASCII_TABLE,
        evt -> {
          if (asciiTable != null) {
            if (((boolean) evt.getNewValue())) {
              asciiTable.anchorToParent();
            } else {
              asciiTable.releaseFromParent();
            }
          }
        });
    settings.addPropertyChangeListener(
        Settings.ANCHOR_INSTRUCTIONS,
        evt -> {
          if (instructionTable != null) {
            if (((boolean) evt.getNewValue())) {
              instructionTable.anchorToParent();
            } else {
              instructionTable.releaseFromParent();
            }
          }
        });
    settings.addPropertyChangeListener(Settings.CURRENT_FONT_SIZE, evt -> updateGlobalFontSize());
  }

  Settings getSettings() {
    return settings;
  }

  private void synchronizeColumnWidths() {
    Component[] headerComponents =
        Arrays.stream(memoryPanel.getComponents())
            .filter(JLabel.class::isInstance)
            .skip(1) // Ingore the title
            .toArray(JLabel[]::new);

    int[] maxWidths = new int[headerComponents.length];
    int maxHeight = 0;
    for (int i = 0; i < memoryCellsPanel.getComponentCount(); i++) {
      int compWidth = memoryCellsPanel.getComponent(i).getPreferredSize().width;
      int compHeight = memoryCellsPanel.getComponent(i).getPreferredSize().height;
      int currentMax = maxWidths[i % maxWidths.length];
      if (compWidth > currentMax) {
        maxWidths[i % maxWidths.length] = compWidth;
      }
      if (compHeight > maxHeight) {
        maxHeight = compHeight;
      }
    }

    for (int i = 0; i < headerComponents.length; i++) {
      Dimension headerSize = new Dimension(maxWidths[i], maxHeight);
      headerComponents[i].setMinimumSize(headerSize);
      headerComponents[i].setPreferredSize(headerSize);
    }
    memoryPanel.revalidate();
    memoryPanel.repaint();
  }

  void autoResizeFrame() {
    Component[] components = {
      frame, memoryPanel, controlPanel, scrollPane, txtOutput, outputScroll, registerPanel
    };

    // Lock memory to small height and call pack() to adjust the frame size
    // Lock description to small width and call pack() to adjust the frame size
    scrollPane.setMaximumSize(SCROLLER_SIZE);
    lblDescription.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
    outputScroll.setMaximumSize(new Dimension(100, 200));
    for (Component c : components) {
      c.revalidate();
      c.repaint();
    }
    frame.pack();

    lblDescription.setMaximumSize(null);
    for (Component c : components) {
      c.revalidate();
      c.repaint();
    }
    frame.pack();
    scrollPane.setMaximumSize(null);

    // Set the maximum size of the control panel to the width of the register panel
    controlPanel.setMaximumSize(
        new Dimension(registerPanel.getPreferredSize().width, Integer.MAX_VALUE));

    Dimension size = frame.getSize();
    frame.setMaximumSize(new Dimension(size.width, size.height));
    frame.pack();
    outputScroll.setMaximumSize(null);
    frame.setMaximumSize(null);

    synchronizeColumnWidths();

    for (Component c : components) {
      c.revalidate();
      c.repaint();
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

  void toggleHelp(boolean display, MenuCheckboxSetter setter) {
    if (display) {
      if (helpWindow == null) {
        inv(
            () -> {
              helpWindow = new HelpWindow(frame, settings);
              helpWindow.addWindowListener(
                  new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                      setter.setCheckbox(false);
                    }
                  });
            });
      }
    } else {
      if (helpWindow != null) {
        helpWindow.dispose();
        helpWindow = null;
      }
    }
  }

  void toggleAsciiTable(boolean display, MenuCheckboxSetter setter) {
    if (display) {
      if (asciiTable == null) {
        asciiTable = new AsciiTable(frame, settings);
        if (settings.getAnchorAsciiTable()) {
          asciiTable.anchorToParent();
        }
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
        instructionTable = new InstructionTable(frame, memory, pc, settings);
        if (settings.getAnchorInstructions()) {
          instructionTable.anchorToParent();
        }
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

  void flipBit() {
    if (currentSelecter.isMouseSelectingOngoing()) {
      return; // Do not allow editing during selection
    }
    currentCells[currentSelecter.getCaretRow()].flipBit(currentSelecter.getCaretCol());
    if (settings.getMoveCaretAfterInput()) {
      currentSelecter.moveCaretRight();
    }
    fileHandler.setIsModified(true);
  }

  void setBit(boolean value) {
    if (currentSelecter.isMouseSelectingOngoing()) {
      return; // Do not allow editing during selection
    }
    currentCells[currentSelecter.getCaretRow()].setBit(currentSelecter.getCaretCol(), value);
    if (settings.getMoveCaretAfterInput()) {
      currentSelecter.moveCaretRight();
    }
    fileHandler.setIsModified(true);
  }

  void handleStep() {
    if (isExecuting.get()) {
      if (executionTask != null) {
        executionTask.cancel(true);
        isExecuting.set(false);
        toggleExecutionControls(false);
      }
      return;
    }
    isExecuting.set(true);
    resetCellColors();
    executor.schedule(
        () -> {
          try {
            cpu.step();
          } catch (Exception ex) {
            appendError(ex);
          } finally {
            inv(() -> isExecuting.set(false));
          }
        },
        0,
        TimeUnit.MILLISECONDS);
  }

  void handleRunAndStop() {
    if (!isExecuting.get()) {
      toggleExecutionControls(true);
      isExecuting.set(true);
      resetCellColors();
      ExecutionSpeed speed = settings.getExecutionSpeed();

      if (speed.getDelay() > 0) {
        executionTask =
            executor.scheduleAtFixedRate(getStepper(), 0, speed.getDelay(), TimeUnit.MILLISECONDS);
      } else {
        executionTask = executor.schedule(getRunner(), 0, TimeUnit.MILLISECONDS);
      }
    } else {
      if (executionTask != null) {
        executionTask.cancel(true);
      }
      isExecuting.set(false);
      toggleExecutionControls(false);
    }
  }

  private Runnable getStepper() {
    return () -> {
      try {
        cpu.step();
        if (pc.isHalted()) {
          inv(
              () -> {
                isExecuting.set(false);
                toggleExecutionControls(false);
              });
          executionTask.cancel(false);
        }
      } catch (Exception ex) {
        inv(
            () -> {
              appendError(ex);
              isExecuting.set(false);
              toggleExecutionControls(false);
            });
        executionTask.cancel(false);
      }
    };
  }

  private Runnable getRunner() {
    return () -> {
      try {
        cpu.run();
      } catch (Exception ex) {
        inv(() -> appendError(ex));
      } finally {
        inv(
            () -> {
              isExecuting.set(false);
              toggleExecutionControls(false);
            });
      }
    };
  }

  private void toggleExecutionControls(boolean isExecuting) {
    inv(
        () -> {
          btnStep.setEnabled(!isExecuting);
          btnReset.setEnabled(!isExecuting);
          btnRun.setText(isExecuting ? "Stop" : "Run");
        });
  }

  void handleResetState() {
    pc.reset();
    registry.reset();
    inv(this::resetCellColors);
  }

  void handleClearOutput() {
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
      runSafely(fileHandler::closeOpenedFile);
      cellSelecter.reset();
      regSelecter.reset();
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
    handleResetState();
  }

  // Private methods, used internally

  private boolean highlightInstructions() {
    if (pc.getCurrentIndex() < 0 || pc.getCurrentIndex() >= memory.size()) {
      return false;
    }
    Instruction instr = factory.createInstruction(memory.getValueAt(pc.getCurrentIndex()));
    int[] affectedMemory = instr.getAffectedMemoryCells(memory, registry, pc);
    int[] affectedRegisters = instr.getAffectedRegisters(memory, registry, pc);
    AbstractCell[] cells =
        Stream.concat(
                Arrays.stream(affectedMemory).mapToObj(i -> this.memCells[i]),
                Arrays.stream(affectedRegisters).mapToObj(i -> this.regCells[i]))
            .toArray(AbstractCell[]::new);
    AbstractCell pcCell = this.memCells[pc.getCurrentIndex()];
    instructionHighlighter.switchCells(pcCell, cells);
    inv(
        () -> {
          int scrollToIdx = affectedMemory[0];
          int additionalCells = affectedMemory.length - 1;
          if (scrollToIdx > 0) {
            scrollToIdx--;
            additionalCells += 2;
          } else {
            additionalCells++;
          }
          if (scrollToIdx + additionalCells >= memory.size()) {
            additionalCells = memory.size() - scrollToIdx - 1;
          }
          memCells[scrollToIdx].scrollTo(additionalCells);
          memoryCellsPanel.revalidate();
          memoryCellsPanel.repaint();
          registerPanel.revalidate();
          registerPanel.repaint();
        });
    return true;
  }

  private void resetCellColors() {
    for (Cell c : memCells) {
      c.unhighlight();
    }
    for (Register r : regCells) {
      r.unhighlight();
    }
  }

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

  private void appendError(String msg) {
    newParagraph = true;
    appendHtmlContent(String.format("<p class='error'>%s</p>", msg));
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
            "gap 10 0,insets 0,fillx,wrap " + numCols,
            // (includeLabel ? "[30px:30px:30px]" : "")
            //     +
            // "[30px:30px:30px][108px:108px:108px][30px:30px:30px][30px:30px:30px][30px:30px:30px][110px::,grow]",
            // "[][][]"
            "[sg addr, right]"
                + (includeLabel ? "[sg name, right]" : "")
                + "[sg value, center][sg hex, left][sg dec, left][sg ascii, left]10[sg"
                + " instr, left, grow, shrink]10",
            "[]"));

    return cellPanel;
  }

  private JPanel appendHeaderToCellPanel(
      JPanel cellPanel, String header, boolean includeLabel, boolean hidden) {
    int numCols = includeLabel ? 7 : 6;
    if (header != null) {
      JLabel lblHeader = new SizedLabel(header, 2, true);
      cellPanel.add(lblHeader, "left, wrap, span " + numCols);

      cellPanel.add(Box.createRigidArea(new Dimension(20, 10)), "wrap");
    }

    cellPanel.add(header("Addr", hidden));
    if (includeLabel) {
      cellPanel.add(header("Name", hidden));
    }
    cellPanel.add(header("Value", hidden, SwingConstants.CENTER));
    cellPanel.add(header("Hex", hidden));
    cellPanel.add(header("Dec", hidden));
    cellPanel.add(header("Ascii", hidden));
    cellPanel.add(header("Instr             ", hidden));

    return cellPanel;
  }

  private JLabel header(String text, boolean hidden) {
    return header(text, hidden, SwingConstants.LEADING);
  }

  private JLabel header(String text, boolean hidden, int alignment) {
    JLabel label = new SizedLabel(text, 2, true, alignment);
    if (hidden) {
      // Set text color to background color to hide the text
      label.setForeground(UIManager.getColor("Panel.background"));
    }
    return label;
  }
}
