package view;

import static util.LazySwing.inv;

import instruction.Instruction;
import instruction.InstructionFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import model.Memory;
import model.ProgramCounter;
import model.ProgramCounterListener;
import model.Registry;
import model.StorageListener;
import net.miginfocom.swing.MigLayout;
import util.LazySwing;

public class InstructionTable extends AnchoredFrame {

  private static final Border INSTR_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);
  private static final Border INSTR_NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

  private static final Font plain = new Font("Monospaced", Font.PLAIN, 14);
  private static final Font bold = new Font("Monospaced", Font.BOLD, 14);
  private static final Font hdr = new Font("Tahoma", Font.BOLD, 14);

  private final transient Memory memory;
  private final transient ProgramCounter pc;
  private JScrollPane scrollPane;
  private JPanel instrTablePanel;
  private JPanel headerPanel;
  private JPanel instructionPanel;
  private JEditorPane instrDesc;

  public InstructionTable(JFrame parent, Memory memory, ProgramCounter pc) {
    super("Instruction Descriptions", parent, AnchorSide.RIGHT);
    this.memory = memory;
    this.pc = pc;

    initUIContent();

    addComponentListener(
        new ComponentListener() {
          @Override
          public void componentResized(java.awt.event.ComponentEvent e) {
            Dimension frameSize = getSize();
            Dimension barSize = scrollPane.getVerticalScrollBar().getSize();
            int newWidth = frameSize.width - barSize.width - 40;
            instrTablePanel.setPreferredSize(new Dimension(newWidth, 100));
            instrTablePanel.setMaximumSize(new Dimension(newWidth, Integer.MAX_VALUE));

            instructionPanel.revalidate();
            instructionPanel.repaint();
            instrTablePanel.revalidate();
            instrTablePanel.repaint();
            scrollPane.revalidate();
            scrollPane.repaint();
          }

          @Override
          public void componentMoved(java.awt.event.ComponentEvent e) {
            // Not needed
          }

          @Override
          public void componentShown(java.awt.event.ComponentEvent e) {
            // Not needed
          }

          @Override
          public void componentHidden(java.awt.event.ComponentEvent e) {
            // Not needed
          }
        });

    setVisible(true);

    // if (isAnchored) {
    inv(this::anchorToParent);
    // } else {
    //   inv(this::fitToParent);
    // }

    // showBorders(this);
  }

  private void initUIContent() {
    getContentPane().setLayout(new MigLayout("fill,wrap 1,gapy 20", "", "[][][][grow]"));

    // Description
    {
      instrDesc = new JEditorPane();
      instrDesc.setContentType("text/html");
      instrDesc.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      instrDesc.setText(
          "<html><body><p>All instructions are made up of 4 + 4 bits. The 4 highest (left-most)"
              + " bits is the opcode, which identifies the instruction. The 4 lowest (right-most)"
              + " bits is the operand, which is used as an argument to the instruction. The purpose"
              + " of the operand differs between instructions.</p></body></html>");
      instrDesc.setEditable(false);
      instrDesc.setOpaque(false);

      add(instrDesc, "top");
    }

    // Legend
    {
      JLabel lblLegend =
          new JLabel(
              "<html><b>*</b> An addressing <i>type</i> is two bits: 00=constant, 01=register,"
                  + " 10=memory</html>");
      add(lblLegend, "top,gap 5 5");
    }

    Component headerFiller = Box.createRigidArea(new Dimension(10, 0));
    // Header for the instruction table
    {
      headerPanel = new JPanel(new MigLayout("gap 7 0, insets 0"));
      add(headerPanel, "top");

      // Headers
      for (String header : new String[] {"Instr", "Op code", "Description"}) {
        headerPanel.add(hdr(header));
      }
      headerPanel.add(headerFiller);
    }

    // Instruction table
    {
      instructionPanel = new JPanel(new MigLayout("filly,wrap 1,insets 0"));
      scrollPane =
          new JScrollPane(
              instructionPanel,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);
      scrollPane.setBorder(null);
      add(scrollPane, "top,grow");
      instrTablePanel = new JPanel(new MigLayout("wrap 3, gap 7 0, insets 0"));
      instructionPanel.add(instrTablePanel, "top,grow");

      headerFiller.setPreferredSize(new Dimension(scrollPane.getVerticalScrollBar().getWidth(), 0));

      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_NOP,
          InstructionFactory.INST_NOP,
          "<b>No Operation</b>: Does nothing.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_ADD,
          InstructionFactory.INST_ADD,
          "<b>Addition</b>: Reads values from registers OP1 and OP2, sums them, and puts the result"
              + " in register RES. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_SUB,
          InstructionFactory.INST_SUB,
          "<b>Subtraction</b>: Reads values from registers OP1 and OP2, subtracts OP2 from OP1, and"
              + " puts the result in register RES. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_CPY,
          InstructionFactory.INST_CPY,
          "<b>Copy</b>: Copies value from source to destination. The first two operand bits are for"
              + " src (constant, memory, or register), next two bits for dst (memory or register"
              + " only). See footnote for addressing types.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_MOV,
          InstructionFactory.INST_MOV,
          "<b>Move</b>: Moves value from source to destination. First two operand bits are for src"
              + " (memory or register), next two bits for dst (memory or register). See footnote"
              + " for addressing types.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_LOD,
          InstructionFactory.INST_LOD,
          "<b>Load</b>: Reads next memory values and loads it into a register. Operand is the"
              + " destination register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ").",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_LDA,
          InstructionFactory.INST_LDA,
          "<b>Load Address</b>: Reads next memory value and interprets it as an address. Then,"
              + " reads the addressed memory values and loads it into a register. Operand is the"
              + " destination register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ").",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_STO,
          InstructionFactory.INST_STO,
          "<b>Store</b>: Stores value from a register into memory. Reads next memory values and"
              + " interprets it as the destination address, at which the register value is stored."
              + " Operand is the source register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ").",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_JMP,
          InstructionFactory.INST_JMP,
          "<b>Jump</b>: Jumps to an address specified by a register. The register index (0-"
              + (Registry.NUM_REGISTERS - 1)
              + ") is specified by the operand.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_JEQ,
          InstructionFactory.INST_JEQ,
          "<b>Jump if Equal</b> Works like JMP, but only jumps if OP1 and OP2 are equal. Otherwise,"
              + " does nothing.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_JNE,
          InstructionFactory.INST_JNE,
          "<b>Jump if Not Equal</b> Works like JMP, but only jumps if OP1 and OP2 are not equal."
              + " Otherwise, does nothing.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_PRT,
          InstructionFactory.INST_PRT,
          "<b>Print Text</b>: Prints the value in the OUT register, as an ASCII character, to the"
              + " I/O output channel. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_PRD,
          InstructionFactory.INST_PRD,
          "<b>Print Decimal</b>: Prints the value in the OUT register as a decimal number to the"
              + " I/O output channel. Operand has no purpose and is ignored.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_PRL,
          InstructionFactory.INST_PRL,
          "<b>Print Loop</b>: Prints a series of characters from memory. The memory address of the"
              + " first character to print must be in the OP1 register, and the memory address of"
              + " the last character to print in the OP2 register. The characters are printed in"
              + " the order they appear in memory, and the memory address in the OP1 register is"
              + " incremented by 1 after each character is printed.",
          pc);
      appendToTable(
          instrTablePanel,
          InstructionFactory.INST_NAME_HLT,
          InstructionFactory.INST_HLT,
          "<b>Halt</b>: Halts the program counter, thus terminating program successfully.",
          pc);
    }
  }

  @Override
  protected void fitContent() {
    LazySwing.checkEDT();
    for (int i = 0; i < headerPanel.getComponentCount(); i++) {
      headerPanel.getComponent(i).setPreferredSize(null);
    }
    headerPanel.revalidate();

    int newContentWidth = 160 + (LazySwing.getCurrentFontSize() - LazySwing.MIN_FONT_SIZE) * 6;
    instrDesc.setMaximumSize(new Dimension(newContentWidth, Integer.MAX_VALUE));
    instrTablePanel.setMaximumSize(new Dimension(newContentWidth, Integer.MAX_VALUE));
    instrDesc.setPreferredSize(new Dimension(newContentWidth, instrDesc.getPreferredSize().height));
    instrTablePanel.setPreferredSize(
        new Dimension(newContentWidth, instrTablePanel.getPreferredSize().height));
    instructionPanel.revalidate();
    instrTablePanel.revalidate();
    scrollPane.revalidate();
    pack(); // Adjusts size to contents
    synchronizeColumnWidths();
    pack();
    instrDesc.setMaximumSize(null);
    instrTablePanel.setMaximumSize(null);
    instrDesc.setPreferredSize(null);
    instrTablePanel.setPreferredSize(null);
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
    pc.addListener(createPCListener(opcode, lblInstr));
    memory.addListener(createMemoryListener(opcode, pc, lblInstr));
    String codeStr = Instruction.toBinaryString(opcode >> 4, 4);
    JLabel lblOpcode = lbl(codeStr);

    String html = "<html><body><p style='margin:0;padding:0;'>%s</p></body></html>";

    JEditorPane lblDesc = new JEditorPane();
    lblDesc.setContentType("text/html");
    lblDesc.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    lblDesc.setText(String.format(html, desc));
    lblDesc.setOpaque(false);
    lblDesc.setEditable(false);
    lblDesc.setMargin(new Insets(0, 0, 0, 0));

    table.add(new JSeparator(), "growx, span 3 1, gapy 3");
    table.add(lblInstr, "top, left, gap 0");
    table.add(lblOpcode, "top, left, gaptop 2, gapx 2");
    table.add(lblDesc, "top, left, gapx 2");
  }

  private void synchronizeColumnWidths() {
    // Note to self: There are 3 columns in the instruction table, but 4 in the header panel due to
    // the filler that accounts for the vertical scrollbar width. Also, the first component in the
    // instruction table is a separator, so the first column component is at index 1.
    for (int i = 0; i < headerPanel.getComponentCount() - 1; i++) {
      Component c = headerPanel.getComponent(i);
      int newWidth = Math.max(c.getWidth(), instrTablePanel.getComponent(i + 1).getWidth());
      c.setPreferredSize(new Dimension(newWidth, c.getPreferredSize().height));
    }
    headerPanel.revalidate();
    headerPanel.repaint();
  }

  private ProgramCounterListener createPCListener(final int opcode, final JLabel lblInstr) {
    return new ProgramCounterListener() {
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
    };
  }

  private StorageListener createMemoryListener(int opcode, ProgramCounter pc, JLabel lblInstr) {
    return (startIdx, values) ->
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
            });
  }
}
