package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
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

  private JFrame frame;
  private Cell[] memCells;
  private Register[] regCells;
  private Register pcCell;
  private int programCounterFocusIdx;
  private JLabel lblErrorMessage;

  private final Memory memory;
  private final ProgramCounter pc;
  private final CPU cpu;
  private final Registry registry;

  public ComputerUI(Memory memory, ProgramCounter pc, CPU cpu) {
    this.memory = memory;
    this.pc = pc;
    this.cpu = cpu;
    this.registry = cpu.getRegistry();

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
    frame.getContentPane().setLayout(new MigLayout("", "[][][grow,shrink]", "[][][][][][][grow]"));

    JLabel lblComputerHeader = new JLabel("SeaPeaEwe 8-bit Computer");
    lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
    frame.getContentPane().add(lblComputerHeader, "cell 0 0 3 1");

    JTextArea lblDescription =
        new JTextArea(
            "A simple simulation of a CPU and memory."
                + System.lineSeparator()
                + "This computer has an 8-bit processor, with 64 bytes of memory and (7+1)"
                + " registers (including program counter).");
    lblDescription.setLineWrap(true);
    lblDescription.setWrapStyleWord(true);
    lblDescription.setEditable(false);
    lblDescription.setBorder(null);
    lblDescription.setBackground(UIManager.getColor("Label.background"));
    frame.getContentPane().add(lblDescription, "cell 0 1 3 1, grow");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    JPanel memoryPanel = createCellPanel("Memory");
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 3, top, left");

    // Memory cells
    {
      JPanel memoryCellsPanel = new JPanel();
      memoryCellsPanel.setBorder(null);
      memoryPanel.add(memoryCellsPanel, "cell 0 3,alignx left,aligny top");
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
      frame.getContentPane().add(registerPanel, "cell 1 3,grow");

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
      registerPanel.add(rigidArea_6, String.format("cell 0 %d", offset + 6));

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
      registerPanel.add(pcCell, String.format("cell 0 %d", offset + 6 + 1));

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
                    memCells[programCounterFocusIdx].clearProgramCounterFocus();
                    pcCell.setValue(value);
                    pcCell.highlight();
                    programCounterFocusIdx = value;
                    memCells[programCounterFocusIdx].setProgramCounterFocus();
                  });
            });
      }
    }

    // Divider between registers and controls
    {
      frame.getContentPane().add(Box.createRigidArea(new Dimension(10, 30)), "cell 1 4");
    }

    // Execution controls
    {
      JPanel controlPanel = new JPanel();
      controlPanel.setBorder(BorderFactory.createTitledBorder(null, "Controls", 0, 0, null));
      frame.getContentPane().add(controlPanel, "cell 1 5,growx,aligny top");
      controlPanel.setLayout(new MigLayout("", "[][]", "[][][]"));

      JLabel lblControlHeader = new JLabel("Controls");
      lblControlHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
      controlPanel.add(lblControlHeader, "cell 0 0");

      // Step button
      {
        JLabel lblStep = new JLabel("Step");
        controlPanel.add(lblStep, "cell 0 1");

        JButton btnStep = new JButton("Step");
        btnStep.addActionListener(
            e -> {
              try {
                resetCellColors();
                cpu.step();
              } catch (Exception ex) {
                handleError(ex);
                ex.printStackTrace();
              }
            });
        controlPanel.add(btnStep, "cell 1 1");
      }

      // Error message textbox
      {
        JLabel lblError = new JLabel("Error");
        controlPanel.add(lblError, "cell 0 2");

        lblErrorMessage = new JLabel("");
        controlPanel.add(lblErrorMessage, "cell 1 2");
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
  }

  private void handleError(Exception ex) {
    lblErrorMessage.setText(ex.getMessage());
  }

  private JPanel createCellPanel(String header) {
    JPanel cellPanel = new JPanel();
    cellPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
    cellPanel.setLayout(
        new MigLayout(
            "gap 5 5",
            "[30px:30px:30px][108px:108px:108px][30px:30px:30px][30px:30px:30px][30px:30px:30px][5px:5px:5px][110px::,grow]",
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

    cellPanel.add(Box.createRigidArea(new Dimension(5, 5)), "cell 5 2");

    JLabel lblInstruction = new JLabel("Instr");
    lblInstruction.setHorizontalAlignment(SwingConstants.LEFT);
    cellPanel.add(lblInstruction, "cell 6 2,alignx left");

    return cellPanel;
  }
}
