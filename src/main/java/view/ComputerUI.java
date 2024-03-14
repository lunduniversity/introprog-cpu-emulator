package view;

import instruction.InstructionFactory;
import io.ObservableIO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import model.CPU;
import model.Memory;
import model.ProgramCounter;
import model.Registry;
import net.miginfocom.swing.MigLayout;

public class ComputerUI {

  private static final Color ERROR_HIGHLIGHT_COLOR = new Color(255, 255, 200);

  private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private JFrame frame;
  private Cell[] memCells;
  private Register[] regCells;
  private Register pcCell;
  private int programCounterFocusIdx;
  private JLabel lblPrintOutput;
  private JLabel lblErrorMessage;

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
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /** Initialize the contents of the frame. */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 800, 725);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.getContentPane().setLayout(new MigLayout("", "[][][][]", "[][][][][][grow]"));

    JLabel lblComputerHeader = new JLabel("SeaPeaEwe 8-bit Computer");
    lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
    frame.getContentPane().add(lblComputerHeader, "cell 0 0 3 1");

    JTextArea lblDescription =
        new JTextArea(
            String.format(
                "A simple simulation of a CPU and memory.%nThis computer has an 8-bit processor,"
                    + " with %d bytes of memory and (%d+1) registers (including program counter).",
                memory.size(), Registry.NUM_REGISTERS));
    lblDescription.setLineWrap(true);
    lblDescription.setWrapStyleWord(true);
    lblDescription.setEditable(false);
    lblDescription.setBorder(null);
    lblDescription.setBackground(UIManager.getColor("Label.background"));
    frame.getContentPane().add(lblDescription, "cell 0 1 3 1, grow");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    JPanel memoryPanel = createCellPanel("Memory");
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 9, top, left");

    // Memory cells
    {
      JPanel memoryCellsPanel = new JPanel();
      JScrollPane scrollPane =
          new JScrollPane(
              memoryCellsPanel,
              JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
              JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      memoryCellsPanel.setBorder(null);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);

      // Remove arrow key bindings for vertical and horizontal scroll bars
      InputMap im = scrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      im.put(KeyStroke.getKeyStroke("UP"), "none");
      im.put(KeyStroke.getKeyStroke("DOWN"), "none");
      im.put(KeyStroke.getKeyStroke("LEFT"), "none");
      im.put(KeyStroke.getKeyStroke("RIGHT"), "none");

      memoryPanel.add(
          scrollPane, "cell 0 3,alignx left,aligny top, grow, w 370px::, h 400px:600px:800px");
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
      registerPanel.setBorder(null);
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
      controlPanel.setLayout(new MigLayout("", "[][][grow,fill][][]", "[][][]"));

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

      // Reset button
      {
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(
            e -> {
              cpu.reset();
              memory.reset();
              memCells[0].focus(0);

              executor.schedule(
                  () -> SwingUtilities.invokeLater(() -> resetCellColors()),
                  700,
                  TimeUnit.MILLISECONDS);
            });
        controlPanel.add(btnReset, "cell 3 1");
      }

      // Small space
      controlPanel.add(Box.createRigidArea(new Dimension(10, 10)), "cell 0 2");

      // Print output textbox
      {
        JLabel lblOutput = new JLabel("Output:");
        controlPanel.add(lblOutput, "cell 0 3,right");

        lblPrintOutput = new JLabel();
        lblPrintOutput.setOpaque(true);
        controlPanel.add(lblPrintOutput, "cell 1 3 3 1, grow");

        io.addListener((value) -> handlePrint(value));
      }

      // Error message textbox
      {
        JLabel lblError = new JLabel("Error:");
        controlPanel.add(lblError, "cell 0 4,right");

        lblErrorMessage = new JLabel("");
        lblErrorMessage.setOpaque(true);
        controlPanel.add(lblErrorMessage, "cell 1 4 3 1, grow");
      }
    }

    // Divider before right-side panel. Vertical line or border that fills all vertical space.
    {
      JSeparator rightDivider = new JSeparator(SwingConstants.VERTICAL);
      frame.getContentPane().add(rightDivider, "cell 2 3 1 9, growy, gaptop 45");
    }

    // Right-side panel with instruction descriptions
    {
      JPanel instructionPanel = new JPanel();
      frame.getContentPane().add(instructionPanel, "cell 3 3 1 5, top, grow");
      instructionPanel.setLayout(new MigLayout("", "[fill,grow]", "[]"));

      JLabel lblInstructionHeader = new JLabel("Instruction Descriptions");
      lblInstructionHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
      instructionPanel.add(lblInstructionHeader, "cell 0 0");

      instructionPanel.add(Box.createRigidArea(new Dimension(20, 10)), "cell 0 1");

      JTextArea instrDesc =
          new JTextArea(
              "All instructions are made up of 4 + 4 bits. The 4 highest (left-most) bits is the"
                  + " opcode, which identifies the instruction. The 4 lowest (right-most) bits is"
                  + " the operand, which is used as an argument to the instruction. The purpose of"
                  + " the operand differs between instructions.");
      instrDesc.setLineWrap(true);
      instrDesc.setWrapStyleWord(true);
      instrDesc.setEditable(false);
      instrDesc.setOpaque(false);
      instrDesc.setMinimumSize(new Dimension(400, 30));
      instructionPanel.add(instrDesc, "cell 0 2");

      instructionPanel.add(Box.createRigidArea(new Dimension(20, 10)), "cell 0 3");

      // Instruction table
      {
        JPanel table =
            new JPanel(new MigLayout("wrap 4, gap 10px", "[][][grow 50][grow 50]", "[]"));
        table.setBorder(null);
        instructionPanel.add(table, "cell 0 4, grow, gap 0");
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
              "Add OP1 and OP2, put result in RES.");
          appendToTable(
              table,
              InstructionFactory.INST_NAME_SUB,
              InstructionFactory.INST_SUB,
              "--",
              "Subtract OP2 from OP1, put result in RES.");
          appendToTable(
              table,
              InstructionFactory.INST_NAME_CPY,
              InstructionFactory.INST_CPY,
              "ab is src address*,\ncd is dst address*",
              "Reads the next two memory values (src and dst) and copies src to the dst.");
          appendToTable(
              table, InstructionFactory.INST_NAME_MOV, InstructionFactory.INST_MOV, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME__LD, InstructionFactory.INST__LD, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME_LDA, InstructionFactory.INST_LDA, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME__ST, InstructionFactory.INST__ST, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME_JMP, InstructionFactory.INST_JMP, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME__JE, InstructionFactory.INST__JE, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME_JNE, InstructionFactory.INST_JNE, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME_PRT, InstructionFactory.INST_PRT, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME_PRL, InstructionFactory.INST_PRL, "todo", "todo");
          appendToTable(
              table, InstructionFactory.INST_NAME_HLT, InstructionFactory.INST_HLT, "todo", "todo");
        }

        // Legend
        {
          JLabel lblLegend =
              new JLabel("* An address is two bits: 00=constant, 01=register, 10=memory");
          instructionPanel.add(lblLegend, "cell 0 5");
        }
      }
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
    lblPrintOutput.setText("");
    lblPrintOutput.setBackground(UIManager.getColor("Panel.background"));
    lblErrorMessage.setText("");
    lblErrorMessage.setBackground(UIManager.getColor("Panel.background"));
  }

  private void handlePrint(int value) {
    // Treat value as ASCII character and appen to print label
    char c = (char) (value & 0xFF);
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
            "[][][][]"));

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

  private void appendToTable(JPanel table, String instr, int opcode, String operand, String desc) {
    JLabel lblInstr = new JLabel(instr);
    String bin = Integer.toBinaryString((opcode >> 4) & 0xF);
    String codeStr = String.format("%4s", bin).replace(' ', '0');
    JLabel lblOpcode = new JLabel(codeStr);

    JTextArea lblOperand = new JTextArea(operand);
    lblOperand.setLineWrap(true);
    lblOperand.setWrapStyleWord(true);
    lblOperand.setOpaque(false);
    lblOperand.setEditable(false);

    JTextArea lblDesc = new JTextArea(desc);
    lblDesc.setLineWrap(true);
    lblDesc.setWrapStyleWord(true);
    lblDesc.setOpaque(false);
    lblDesc.setEditable(false);

    table.add(lblInstr);
    table.add(lblOpcode);
    table.add(lblOperand);
    table.add(lblDesc, "grow");
  }
}
