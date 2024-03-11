package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import model.Computer;
import net.miginfocom.swing.MigLayout;

public class ComputerUI {

  private JFrame frame;
  private Cell[] memCells;
  private Register[] regCells;
  private Register pc;
  private int programCounterFocusIdx;
  private JLabel lblErrorMessage;

  private final Computer computer;

  public ComputerUI(Computer computer) {
    this.computer = computer;
    this.programCounterFocusIdx = computer.getProgramCounter();
    initialize();

    memCells[programCounterFocusIdx].setProgramCounterFocus();

    frame.setVisible(true);
  }

  /** Initialize the contents of the frame. */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 800, 725);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame
        .getContentPane()
        .setLayout(new MigLayout("", "[grow][][][][grow]", "[][][][grow][grow][grow]"));

    JLabel lblComputerHeader = new JLabel("Computer");
    lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
    frame.getContentPane().add(lblComputerHeader, "cell 0 0 5 1,grow");

    JLabel lblDescription =
        new JLabel(
            "A simple simulation of a CPU and memory. This computer has an 8-bit processor, with 64"
                + " bytes of memory and 6 registers.");
    frame.getContentPane().add(lblDescription, "cell 0 1 5 1");

    Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
    frame.getContentPane().add(rigidArea, "cell 0 2");

    JPanel memoryPanel = new JPanel();
    frame.getContentPane().add(memoryPanel, "cell 0 3 1 3,grow");
    memoryPanel.setLayout(
        new MigLayout(
            "", "[30px:30px:30px][100px:100px:100px][30px:30px:30px][30px:30px:30px]", "[][][][]"));

    JLabel lblMemoryHeader = new JLabel("Memory");
    memoryPanel.add(lblMemoryHeader, "cell 0 0 4 1");
    lblMemoryHeader.setFont(new Font("Tahoma", Font.BOLD, 14));

    Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 10));
    memoryPanel.add(rigidArea_3, "cell 0 1 2 1");

    JLabel lblAddress = new JLabel("Addr.");
    lblAddress.setBorder(null);
    lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
    memoryPanel.add(lblAddress, "cell 0 2,alignx right");

    JLabel lblValue = new JLabel("Value");
    lblValue.setHorizontalAlignment(SwingConstants.RIGHT);
    memoryPanel.add(lblValue, "cell 1 2,alignx center");

    JLabel lblHex = new JLabel("Hex");
    lblHex.setHorizontalAlignment(SwingConstants.RIGHT);
    memoryPanel.add(lblHex, "cell 2 2,alignx right");

    JLabel lblDec = new JLabel("Dec");
    lblDec.setHorizontalAlignment(SwingConstants.RIGHT);
    memoryPanel.add(lblDec, "cell 3 2,alignx right");

    // Memory cells
    {
      JPanel memoryCellsPanel = new JPanel();
      memoryCellsPanel.setBorder(null);
      memoryPanel.add(memoryCellsPanel, "cell 0 3 4 1,alignx left,aligny top");
      memoryCellsPanel.setLayout(new BoxLayout(memoryCellsPanel, BoxLayout.Y_AXIS));

      memCells = new Cell[computer.memorySize()];
      for (int i = 0; i < computer.memorySize(); i++) {
        final int idx = i;
        memCells[i] =
            new Cell(
                i,
                value -> computer.writeMemory(idx, value),
                new CellNav() {
                  @Override
                  public void prevCell(int xpos) {
                    memCells[(idx - 1 + computer.memorySize()) % computer.memorySize()].focus(xpos);
                  }

                  @Override
                  public void nextCell(int xpos) {
                    memCells[(idx + 1) % computer.memorySize()].focus(xpos);
                  }
                });
        memoryCellsPanel.add(memCells[i]);
      }

      {
        computer.addMemoryListener(
            (address, value) -> memCells[address.getOperand()].setValue(value));
      }
    }

    // Registers and program counter
    {
      JPanel registerPanel = new JPanel();
      frame.getContentPane().add(registerPanel, "cell 4 3,grow");
      registerPanel.setLayout(new MigLayout("", "[][][]", "[][][][][][]"));

      JLabel lblRegisterHeader = new JLabel("Registers");
      lblRegisterHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
      registerPanel.add(lblRegisterHeader, "cell 0 0 3 1");

      Component rigidArea_4 = Box.createRigidArea(new Dimension(20, 10));
      registerPanel.add(rigidArea_4, "cell 0 1");

      // Computer has 6 registers, OP1-OP3 and R1-R3.
      // R1-R3 are general purpose registers, OP1-OP3 are used for operations.
      regCells = new Register[6];
      int offset = 2;
      String[] regNames = {"OP1", "OP2", "RES", "R1", "R2", "R3"};
      for (int i = 0; i < regCells.length; i++) {
        final int idx = i;
        regCells[i] =
            new Register(
                regNames[i],
                value -> computer.writeRegistry(idx, value),
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

      pc =
          new Register(
              "PC",
              value -> computer.setProgramCounter(value),
              new CellNav() {
                @Override
                public void prevCell(int xpos) {}

                @Override
                public void nextCell(int xpos) {}
              });
      registerPanel.add(pc, String.format("cell 0 %d", offset + 6 + 1));

      {
        computer.addRegistryListener(
            (address, value) -> {
              regCells[address.getOperand()].setValue(value);
              regCells[address.getOperand()].highlight();
            });
        computer.addProgramCounterListener(
            value -> {
              memCells[programCounterFocusIdx].clearProgramCounterFocus();
              pc.setValue(value);
              pc.highlight();
              programCounterFocusIdx = value;
              memCells[programCounterFocusIdx].setProgramCounterFocus();
            });
      }
    }

    // Execution controls
    {
      JPanel controlPanel = new JPanel();
      frame.getContentPane().add(controlPanel, "cell 4 5,grow");
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
                computer.step();
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
    pc.unhighlight();
  }

  private void handleError(Exception ex) {
    lblErrorMessage.setText(ex.getMessage());
  }
}
