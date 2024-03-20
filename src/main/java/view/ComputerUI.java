package view;

import instruction.Instruction;
import instruction.InstructionFactory;
import io.ObservableIO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import model.CPU;
import model.Memory;
import model.ProgramCounter;
import model.Registry;
import net.miginfocom.swing.MigLayout;
import view.SnapshotDialog.Mode;

public class ComputerUI {

  private static final Color ERROR_HIGHLIGHT_COLOR = new Color(255, 255, 200);

  private static final Border INSTR_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);

  private static final Border INSTR_NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

  private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private JFrame frame;
  private Cell[] memCells;
  private Register[] regCells;
  private Register pcCell;
  private int programCounterFocusIdx;
  private JEditorPane lblPrintOutput;
  private JEditorPane lblErrorMessage;
  private JScrollPane scrollPane;

  private final Memory memory;
  private final ProgramCounter pc;
  private final CPU cpu;
  private final Registry registry;
  private final ObservableIO io;

  public ComputerUI(Memory memory, ProgramCounter pc, CPU cpu, ObservableIO io) {
    this.memory = memory;
    this.pc = pc;
    this.cpu = cpu;
    this.registry = cpu.getRegistry();
    this.io = io;

    this.programCounterFocusIdx = pc.getCurrentIndex();
    initialize();

    memCells[programCounterFocusIdx].setProgramCounterFocus();

    frame.pack();
    frame.setMinimumSize(frame.getSize());
    scrollPane.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    // _showBorders(frame);
  }

  @SuppressWarnings("unused")
  private void _showBorders(Component component) {
    // Define a simple border
    Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
    Random r = new Random();

    // Set the border on JComponents
    if (component instanceof JComponent) {
      ((JComponent) component).setBorder(border);
    }
    component.setBackground(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));

    // Recursively set the border on child components if the component is a container
    if (component instanceof Container) {
      for (Component child : ((Container) component).getComponents()) {
        _showBorders(child);
      }
    }
  }

  /** Initialize the contents of the frame. */
  private void initialize() {
    frame = new JFrame();
    // frame.setBounds(100, 100, 800, 725);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // frame.setResizable(false);

    // Add menu bar
    JMenuBar menuBar = new JMenuBar();
    JMenu myMenu = new JMenu("Tools");
    menuBar.add(myMenu);

    // Add menu items
    JMenuItem mnuExport = new JMenuItem("Export memory snapshot");
    JMenuItem mnuImport = new JMenuItem("Import memory snapshot");
    JMenuItem mnuReset = new JMenuItem("Reset all data");
    myMenu.add(mnuExport);
    myMenu.add(mnuImport);
    myMenu.addSeparator();
    myMenu.add(mnuReset);

    // Add action listeners to the buttons  UBJRF8DQ:12:SEVMTE8h
    mnuExport.addActionListener(
        (e) -> {
          String memorySnapdhot = memory.exportAsBase64();
          if (memorySnapdhot.isEmpty()) {
            memorySnapdhot = "(Memory is empty)";
          }
          SnapshotDialog dialog = new SnapshotDialog(frame, Mode.EXPORT);
          dialog.setText(memorySnapdhot);
          dialog.setVisible(true);
        });
    mnuImport.addActionListener(
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
    mnuReset.addActionListener((e) -> handleResetAllData());

    frame.setJMenuBar(menuBar);

    frame.getContentPane().setLayout(new MigLayout("", "[][][][]", "[][][][][][]"));

    JLabel lblComputerHeader = new JLabel("SeaPeaEwe 8-bit Computer");
    lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
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
    frame.getContentPane().add(lblDescription, "cell 0 1 3 1, grow");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 10));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    JPanel memoryPanel = createCellPanel("Memory");
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 3, top, left, grow");

    // Memory cells
    {
      JPanel memoryCellsPanel = new JPanel();
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
                value -> memory.setValueAt(idx, value),
                new CellNav() {
                  @Override
                  public void prevCell(int xpos) {
                    memCells[(idx - 1 + memory.size()) % memory.size()].focus(xpos);
                  }

                  @Override
                  public void nextCell(int xpos) {
                    memCells[(idx + 1) % memory.size()].focus(xpos);
                  }
                });
        memoryCellsPanel.add(memCells[i]);
      }

      {
        memory.addListener(
            (address, value) ->
                SwingUtilities.invokeLater(() -> memCells[address].setValue(value)));
      }
    }

    // Registers and program counter
    {
      JPanel registerPanel = createCellPanel("Registers");
      frame.getContentPane().add(registerPanel, "cell 1 3, top");

      // Computer has 6 registers, OP1-OP3 and R1-R3.
      // R1-R3 are general purpose registers, OP1-OP3 are used for operations.
      regCells = new Register[Registry.NUM_REGISTERS];
      int offset = 3;
      for (int i = 0; i < regCells.length; i++) {
        final int idx = i;
        regCells[i] =
            new Register(
                Registry.REGISTER_NAMES[i],
                value -> registry.setRegister(idx, value),
                new CellNav() {
                  @Override
                  public void prevCell(int xpos) {
                    regCells[(idx - 1 + 6) % (6)].focus(xpos);
                  }

                  @Override
                  public void nextCell(int xpos) {
                    regCells[(idx + 1) % (6)].focus(xpos);
                  }
                });
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
              "PC",
              value -> pc.setCurrentIndex(value),
              new CellNav() {
                @Override
                public void prevCell(int xpos) {}

                @Override
                public void nextCell(int xpos) {}
              });
      registerPanel.add(pcCell, String.format("cell 0 %d", offset + Registry.NUM_REGISTERS + 1));

      {
        cpu.addRegistryListener(
            (address, value) -> {
              SwingUtilities.invokeLater(
                  () -> {
                    regCells[address].setValue(value);
                    regCells[address].highlight();
                  });
            });
        pc.addListener(
            value -> {
              SwingUtilities.invokeLater(
                  () -> {
                    if (programCounterFocusIdx >= 0 && programCounterFocusIdx < memory.size()) {
                      memCells[programCounterFocusIdx].clearProgramCounterFocus();
                    }
                    pcCell.setValue(value);
                    pcCell.highlight();
                    programCounterFocusIdx = value;
                    if (value >= 0 && value < memory.size()) {
                      memCells[programCounterFocusIdx].setProgramCounterFocus();
                    }
                  });
            });
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
        btnStep.addActionListener(
            e -> {
              try {
                resetCellColors();
                cpu.step();
              } catch (Exception ex) {
                handleError(ex);
              }
            });
        controlPanel.add(btnStep, "cell 0 1");
      }

      // Run button
      {
        JButton btnRun = new JButton("Run");
        btnRun.addActionListener(
            e -> {
              try {
                resetCellColors();
                cpu.run();
              } catch (Exception ex) {
                handleError(ex);
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
        btnReset.addActionListener(
            e -> {
              lblPrintOutput.setText("");
              lblErrorMessage.setText("");
              pc.setCurrentIndex(0);
              registry.reset();
              SwingUtilities.invokeLater(() -> resetCellColors());
            });
        controlPanel.add(btnReset, "cell 0 6 2 1");
      }
    }

    // Divider before right-side panel. Vertical line or border that fills all vertical space.
    {
      JSeparator rightDivider = new JSeparator(SwingConstants.VERTICAL);
      frame.getContentPane().add(rightDivider, "cell 2 3 1 3, growy");
    }

    // Right-side panel with instruction descriptions
    {
      JPanel instructionPanel = new JPanel();
      frame.getContentPane().add(instructionPanel, "cell 3 3 1 3, top, shrink");
      instructionPanel.setLayout(new MigLayout("", "[fill,grow]", "[shrink]"));

      JLabel lblInstructionHeader = new JLabel("Instruction Descriptions");
      lblInstructionHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
      instructionPanel.add(lblInstructionHeader, "cell 0 0");

      instructionPanel.add(Box.createRigidArea(new Dimension(20, 10)), "cell 0 1");

      JTextArea instrDesc =
          new JTextArea(
              "All instructions are made up of 4 + 4 bits. The 4 highest (left-most) bits is"
                  + " the opcode, which identifies the instruction. The 4 lowest (right-most) bits"
                  + " is the operand, which is used as an argument to the instruction. The purpose"
                  + " of the operand differs between instructions.");
      instrDesc.setLineWrap(true);
      instrDesc.setWrapStyleWord(true);
      instrDesc.setEditable(false);
      instrDesc.setOpaque(false);
      instrDesc.setMinimumSize(new Dimension(400, 30));
      instructionPanel.add(instrDesc, "cell 0 2");

      JLabel addreessNote =
          new JLabel(
              "<html>Note: An underlined name, like <u>src</u>, means it's being used as an"
                  + " <b>address</b>, rather than a<br><b>value</b> directly. If src has the value"
                  + " 17, then <u>src</u> has the value of the memory slot at index 17.</html>");
      addreessNote.setFont(instrDesc.getFont());
      instructionPanel.add(addreessNote, "cell 0 3, gaptop 5, gapbottom 15");

      // instructionPanel.add(Box.createRigidArea(new Dimension(20, 10)), "cell 0 3");

      // Instruction table
      {
        JPanel table =
            new JPanel(
                new MigLayout("wrap 4, gap 5px 0, insets 0", "[][][grow 50][grow 50]", "[]"));
        table.setBorder(null);
        instructionPanel.add(table, "cell 0 4, gap 0");
        // Headers
        {
          for (String hdr : new String[] {"Instr", "Opcode", "Operand (abcd)", "Description"}) {
            table.add(new JLabel(hdr));
          }
        }

        // Instructions
        {
          appendToTable(
              table,
              InstructionFactory.INST_NAME_ADD,
              InstructionFactory.INST_ADD,
              "--",
              "Add OP1 and OP2, put result in RES.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_SUB,
              InstructionFactory.INST_SUB,
              "--",
              "Subtract OP2 from OP1, put result in RES.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_CPY,
              InstructionFactory.INST_CPY,
              "<b>ab</b> is src type*.<br><b>cd</b> is dst type*.",
              "Reads the next two memory values (src and dst) and copies <u>src</u> to"
                  + " <u>dst</u>.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_MOV,
              InstructionFactory.INST_MOV,
              "<b>ab</b> is src type*.<br><b>cd</b> is dst type*.",
              "Reads the next two memory values (src and dst) and moves <u>src</u> to <u>dst</u>."
                  + " Afterwards, <u>src</u> is set to 0.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME__LD,
              InstructionFactory.INST__LD,
              "Specifies destination register.",
              "Reads next memory <b>value</b> and loads it into a register.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_LDA,
              InstructionFactory.INST_LDA,
              "Specifies destination register.",
              "Reads next memory <b>address</b> and loads the addressed value into a register.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME__ST,
              InstructionFactory.INST__ST,
              "Specifies source regsiter.",
              "Reads next memory <b>address</b> and stores register value at it.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_JMP,
              InstructionFactory.INST_JMP,
              "Specifies source register.",
              "Jumps to address given by a register.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME__JE,
              InstructionFactory.INST__JE,
              "Specifies source register.",
              "Jumps to address given by a register IF OP1 and OP2 are equal.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_JNE,
              InstructionFactory.INST_JNE,
              "Specifies source register.",
              "Jumps to address given by a register IF OP1 and OP2 are NOT equal.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_PRT,
              InstructionFactory.INST_PRT,
              "--",
              "Reads value in PRT and sends to I/O output channel.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_PRL,
              InstructionFactory.INST_PRL,
              "--",
              "Reads memory address from OP1, loads value at that address into PRT and sends it to"
                  + " I/O output channel, and increments OP1. Increments PC only if OP1 and OP2 are"
                  + " equal.",
              pc);
          appendToTable(
              table,
              InstructionFactory.INST_NAME_HLT,
              InstructionFactory.INST_HLT,
              "--",
              "Halts PC, thus terminating program successfully.",
              pc);
          table.add(new JSeparator(), "growx, span 4 1, gapy 3");
        }

        // Legend
        {
          JLabel lblLegend =
              new JLabel(
                  "<html>* An addressing <i>type</i> is two bits: 00=constant, 01=register,"
                      + " 10=memory</html>");
          instructionPanel.add(lblLegend, "cell 0 5");
        }
      }
    }

    // Divider after right-side panel. Vertical line or border that fills all vertical space.
    {
      JSeparator rightDivider = new JSeparator(SwingConstants.VERTICAL);
      frame.getContentPane().add(rightDivider, "cell 4 3 1 3, growy");
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

  private void handleResetAllData() {
    cpu.reset();
    memory.reset();
    memCells[0].focus(0);

    executor.schedule(
        () -> SwingUtilities.invokeLater(() -> resetCellColors()), 700, TimeUnit.MILLISECONDS);
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

  private void appendToTable(
      JPanel table, String instr, int opcode, String operand, String desc, ProgramCounter pc) {
    JLabel lblInstr = new JLabel(instr);
    lblInstr.setBorder(INSTR_NO_FOCUS_BORDER);
    pc.addListener(
        pcValue -> {
          if (pcValue >= 0 && pcValue < memory.size()) {
            lblInstr.setBorder(
                (memory.getValueAt(pcValue) & 0xF0) == opcode
                    ? INSTR_FOCUS_BORDER
                    : INSTR_NO_FOCUS_BORDER);
          }
        });
    String codeStr = Instruction.toBinaryString(opcode >> 4, 4);
    JLabel lblOpcode = new JLabel(codeStr);

    String html = "<html>%s</html>";
    JEditorPane lblOperand = new JEditorPane();
    lblOperand.setContentType("text/html");
    lblOperand.setText(String.format(html, operand));
    lblOperand.setOpaque(false);
    lblOperand.setEditable(false);
    lblOperand.setHighlighter(null);
    lblOperand.setMaximumSize(new Dimension(130, 400));
    lblOperand.setPreferredSize(new Dimension(130, 20));
    lblOperand.setMargin(new Insets(0, 0, 0, 0));

    JEditorPane lblDesc = new JEditorPane();
    lblDesc.setContentType("text/html");
    lblDesc.setText(String.format(html, desc));
    lblDesc.setOpaque(false);
    lblDesc.setEditable(false);
    lblDesc.setHighlighter(null);
    lblDesc.setMaximumSize(new Dimension(300, 600));
    lblDesc.setPreferredSize(new Dimension(300, 20));
    lblDesc.setMargin(new Insets(0, 0, 0, 0));

    table.add(new JSeparator(), "growx, span 4 1, gapy 3");
    table.add(lblInstr, "aligny top, gap 0");
    table.add(lblOpcode, "aligny top, gaptop 2, gapx 2");
    table.add(lblOperand, "growx, shrinky, aligny top, gapx 2");
    table.add(lblDesc, "growx, shrinky, aligny top, gapx 2");
  }
}
