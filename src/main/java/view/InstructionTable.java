package view;

import static util.LazySwing.inv;

import instruction.Instruction;
import instruction.InstructionFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ComponentListener;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import model.Memory;
import model.ProgramCounter;
import model.ProgramCounterListener;
import model.Registry;
import net.miginfocom.swing.MigLayout;

public class InstructionTable extends JFrame {

  private static final Border INSTR_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);
  private static final Border INSTR_NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

  private static final Font plain = new Font("Monospaced", Font.PLAIN, 14);
  private static final Font bold = new Font("Monospaced", Font.BOLD, 14);
  private static final Font hdr = new Font("Tahoma", Font.BOLD, 14);

  private final transient Memory memory;
  private final transient ProgramCounter pc;
  private JScrollPane scrollPane;
  private JPanel table;

  public InstructionTable(JFrame parent, Memory memory, ProgramCounter pc) {

    this.memory = memory;
    this.pc = pc;

    initGUI();

    addComponentListener(
        new ComponentListener() {
          @Override
          public void componentResized(java.awt.event.ComponentEvent e) {
            updateWidth();
          }

          @Override
          public void componentMoved(java.awt.event.ComponentEvent e) {
            updateWidth();
          }

          @Override
          public void componentShown(java.awt.event.ComponentEvent e) {
            updateWidth();
          }

          @Override
          public void componentHidden(java.awt.event.ComponentEvent e) {
            updateWidth();
          }

          private void updateWidth() {
            Dimension tableSize = table.getSize();
            Dimension frameSize = getSize();
            Dimension barSize = scrollPane.getVerticalScrollBar().getSize();
            System.out.println("Table: " + tableSize.width + " -> " + frameSize.width);
            int newWidth = frameSize.width - barSize.width - 40;
            table.setPreferredSize(new Dimension(newWidth, 100));
            table.setMaximumSize(new Dimension(newWidth, Integer.MAX_VALUE));
            table.revalidate();
            table.repaint();
          }
        });

    setSize(new Dimension(600, 850));
    Point parentLocation = parent.getLocation();
    int xCoord = parentLocation.x + parent.getWidth();
    int yCoord = parentLocation.y;
    setLocation(xCoord, yCoord);

    setVisible(true);

    // showBorders(this);
  }

  private void initGUI() {
    setTitle("Instruction Descriptions");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new MigLayout("wrap 1,gapy 20"));

    // Description
    {
      JEditorPane instrDesc = new JEditorPane();
      instrDesc.setContentType("text/html");
      instrDesc.setText(
          "<html><p>All instructions are made up of 4 + 4 bits. The 4 highest (left-most) bits"
              + " is the opcode, which identifies the instruction. The 4 lowest (right-most)"
              + " bits is the operand, which is used as an argument to the instruction. The"
              + " purpose of the operand differs between instructions.</p><p>Note: An"
              + " underlined name, like <u>src</u>, means it's being used as an <b>address</b>,"
              + " rather than a <b>value</b> directly. If src has the value 17, then <u>src</u>"
              + " has the value of the memory slot at index 17.</p></html>");
      instrDesc.setEditable(false);
      instrDesc.setOpaque(false);

      add(instrDesc, "top");
    }

    JPanel contentPane = new JPanel(new MigLayout("wrap 1, gap 5, filly"));
    contentPane.setBorder(null);
    scrollPane =
        new JScrollPane(
            contentPane,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setBorder(null);
    add(scrollPane, "top,left,grow");

    // Instruction table
    {
      table = new JPanel(new MigLayout("wrap 3, gap 7 0, insets 0"));
      contentPane.add(table, "top, left, wrap,grow");

      // Headers
      for (String header : new String[] {"Instr", "Op code", "Description"}) {
        table.add(hdr(header));
      }

      // Instructions
      appendToTable(
          table,
          InstructionFactory.INST_NAME_NOP,
          InstructionFactory.INST_NOP,
          "<b>No Operation</b>: Does nothing.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_ADD,
          InstructionFactory.INST_ADD,
          "<b>Addition</b>: Reads values from registers OP1 and OP2, sums them, and puts the result"
              + " in register RES. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_SUB,
          InstructionFactory.INST_SUB,
          "<b>Subtraction</b>: Reads values from registers OP1 and OP2, subtracts OP2 from OP1, and"
              + " puts the result in register RES. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_CPY,
          InstructionFactory.INST_CPY,
          "<b>Copy</b>: Copies value from source to destination. The first two operand bits are for"
              + " src (constant, memory, or register), next two bits for dst (memory or register"
              + " only). See footnote for addressing types.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_MOV,
          InstructionFactory.INST_MOV,
          "<b>Move</b>: Moves value from source to destination. First two operand bits are for src"
              + " (memory or register), next two bits for dst (memory or register). See footnote"
              + " for addressing types.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_LOD,
          InstructionFactory.INST_LOD,
          "<b>Load</b>: Reads next memory values and loads it into a register. Operand is the"
              + " destination register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ").",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_LDA,
          InstructionFactory.INST_LDA,
          "<b>Load Address</b>: Reads next memory value and interprets it as an address. Then,"
              + " reads the addressed memory values and loads it into a register. Operand is the"
              + " destination register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ").",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_STO,
          InstructionFactory.INST_STO,
          "<b>Store</b>: Stores value from a register into memory. Reads next memory values and"
              + " interprets it as the destination address, at which the register value is stored."
              + " Operand is the source register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ").",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_JMP,
          InstructionFactory.INST_JMP,
          "<b>Jump</b>: Jumps to an address specified by a register. The register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ") is specified by the operand.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_JEQ,
          InstructionFactory.INST_JEQ,
          "<b>Jump if Equal</b> Works like JMP, but only jumps if OP1 and OP2 are equal. Otherwise,"
              + " does nothing.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_JNE,
          InstructionFactory.INST_JNE,
          "<b>Jump if Not Equal</b> Works like JMP, but only jumps if OP1 and OP2 are not equal."
              + " Otherwise, does nothing.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_PRT,
          InstructionFactory.INST_PRT,
          "<b>Print Text</b>: Prints the value in the OUT register, as an ASCII character, to the"
              + " I/O output channel. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_PRD,
          InstructionFactory.INST_PRD,
          "<b>Print Decimal</b>: Prints the value in the OUT register as a decimal number to the"
              + " I/O output channel. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_PRL,
          InstructionFactory.INST_PRL,
          "<b>Print Loop</b>: Prints a series of characters from memory. The memory address of the"
              + " first character to print must be in the OP1 register, and the memory address of"
              + " the last character to print in the OP2 register. The characters are printed in"
              + " the order they appear in memory, and the memory address in the OP1 register is"
              + " incremented by 1 after each character is printed.",
          pc);
      appendToTable(
          table,
          InstructionFactory.INST_NAME_HLT,
          InstructionFactory.INST_HLT,
          "<b>Halt</b>: Halts the program counter, thus terminating program successfully.",
          pc);
      table.add(new JSeparator(), "span 3 1, gapy 3");

      // Legend
      {
        JLabel lblLegend =
            new JLabel(
                "<html>* An addressing <i>type</i> is two bits: 00=constant, 01=register,"
                    + " 10=memory</html>");
        contentPane.add(lblLegend, "gap 5 5");
      }
    }

    // Add a filler to push the above content to the top
    contentPane.add(new JPanel(), "growy");
  }

  private JLabel lbl(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(plain);
    return lbl;
  }

  private JLabel bold(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(bold);
    return lbl;
  }

  private JLabel hdr(String text) {
    JLabel lbl = new JLabel(text);
    lbl.setFont(hdr);
    return lbl;
  }

  private void appendToTable(
      JPanel table, String instr, int opcode, String desc, ProgramCounter pc) {
    JLabel lblInstr = bold(instr);
    lblInstr.setBorder(INSTR_NO_FOCUS_BORDER);
    pc.addListener(
        new ProgramCounterListener() {
          @Override
          public void onProgramCounterChanged(int oldIdx, int newIdx) {
            inv(
                () -> {
                  if (0 <= newIdx && newIdx < memory.size()) {
                    lblInstr.setBorder(
                        (memory.getValueAt(newIdx) & 0xF0) == opcode
                            ? INSTR_FOCUS_BORDER
                            : INSTR_NO_FOCUS_BORDER);
                  }
                });
          }

          @Override
          public void onProgramCounterHalted(int haltReson) {
            // Do nothing
          }
        });
    memory.addListener(
        (startIdx, values) ->
            inv(
                () -> {
                  // If the current PC is in the modified range
                  if (startIdx <= pc.getCurrentIndex()
                      && pc.getCurrentIndex() < startIdx + values.length) {
                    lblInstr.setBorder(
                        (values[pc.getCurrentIndex() - startIdx] & 0xF0) == opcode
                            ? INSTR_FOCUS_BORDER
                            : INSTR_NO_FOCUS_BORDER);
                  }
                }));
    String codeStr = Instruction.toBinaryString(opcode >> 4, 4);
    JLabel lblOpcode = lbl(codeStr);

    String html = "<html><body><p style='margin:0;padding:0;'>%s</p></body></html>";

    JEditorPane lblDesc = new JEditorPane();
    lblDesc.setContentType("text/html");
    lblDesc.setText(String.format(html, desc));
    lblDesc.setOpaque(false);
    lblDesc.setEditable(false);
    lblDesc.setMargin(new Insets(0, 0, 0, 0));

    table.add(new JSeparator(), "growx, span 3 1, gapy 3");
    table.add(lblInstr, "top, left, gap 0");
    table.add(lblOpcode, "top, left, gaptop 2, gapx 2");
    table.add(lblDesc, "top, left, gapx 2");
  }
}
