package view;

import static util.LazySwing.inv;

import instruction.Instruction;
import instruction.InstructionFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import model.Memory;
import model.ProgramCounter;
import net.miginfocom.swing.MigLayout;

public class InstructionTable extends JFrame {

  private static final Border INSTR_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);
  private static final Border INSTR_NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

  private static final Font plain = new Font("Monospaced", Font.PLAIN, 14);
  private static final Font bold = new Font("Monospaced", Font.BOLD, 14);
  private static final Font hdr = new Font("Tahoma", Font.BOLD, 14);

  private final Memory memory;
  private final ProgramCounter pc;

  public InstructionTable(JFrame parent, Memory memory, ProgramCounter pc) {

    this.memory = memory;
    this.pc = pc;

    initGUI();

    setSize(new Dimension(600, 850));
    setVisible(true);

    Point parentLocation = parent.getLocation();
    int xCoord = parentLocation.x + parent.getWidth();
    int yCoord = parentLocation.y;
    setLocation(xCoord, yCoord);

    // showBorders(this);
  }

  private void initGUI() {
    setTitle("Instruction Descriptions");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    getContentPane()
        .setLayout(new MigLayout("wrap 1,gapy 20", "[grow,shrink,fill]", "[grow,shrink,fill]"));

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

      add(instrDesc, "");
    }

    JPanel contentPane =
        new JPanel(new MigLayout("wrap 1, gap 5", "[grow,shrink,fill]", "[grow,shrink,fill]"));
    contentPane.setBorder(null);
    // contentPane.setPreferredSize(new Dimension(600, 800));
    add(contentPane, "grow");

    // Instruction table
    {
      JPanel table =
          new JPanel(new MigLayout("wrap 4, gap 7 0, insets 0", "[][][grow][grow]", "[grow]"));
      table.setBorder(null);
      contentPane.add(table, "grow");

      // Headers
      for (String hdr : new String[] {"Instr", "Opcode", "Operand (abcd)", "Description"}) {
        table.add(hdr(hdr));
      }

      // Instructions
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

      // Legend
      {
        JLabel lblLegend =
            new JLabel(
                "<html>* An addressing <i>type</i> is two bits: 00=constant, 01=register,"
                    + " 10=memory</html>");
        contentPane.add(lblLegend, "gap 5 5");
      }
    }
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
      JPanel table, String instr, int opcode, String operand, String desc, ProgramCounter pc) {
    JLabel lblInstr = bold(instr);
    lblInstr.setBorder(INSTR_NO_FOCUS_BORDER);
    pc.addListener(
        (pcValue, newIdx) ->
            inv(
                () -> {
                  if (pcValue >= 0 && pcValue < memory.size()) {
                    lblInstr.setBorder(
                        (memory.getValueAt(pcValue) & 0xF0) == opcode
                            ? INSTR_FOCUS_BORDER
                            : INSTR_NO_FOCUS_BORDER);
                  }
                }));
    memory.addListener(
        (address, value) ->
            inv(
                () -> {
                  if (address == pc.getCurrentIndex()) {
                    lblInstr.setBorder(
                        (value & 0xF0) == opcode ? INSTR_FOCUS_BORDER : INSTR_NO_FOCUS_BORDER);
                  }
                }));
    String codeStr = Instruction.toBinaryString(opcode >> 4, 4);
    JLabel lblOpcode = lbl(codeStr);

    String html = "<html>%s</html>";
    JEditorPane lblOperand = new JEditorPane();
    lblOperand.setContentType("text/html");
    // lblOperand.setLineWrap(true);
    // lblOperand.setWrapStyleWord(true);
    lblOperand.setText(String.format(html, operand));
    lblOperand.setOpaque(false);
    lblOperand.setEditable(false);
    lblOperand.setHighlighter(null);
    // lblOperand.setPreferredSize(new Dimension(0, 10));
    lblOperand.setMargin(new Insets(0, 0, 0, 0));
    JScrollPane sc1 =
        new JScrollPane(
            lblOperand,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    sc1.setBorder(null);

    JEditorPane lblDesc = new JEditorPane();
    lblDesc.setContentType("text/html");
    // lblDesc.setLineWrap(true);
    // lblDesc.setWrapStyleWord(true);
    lblDesc.setText(String.format(html, desc));
    lblDesc.setOpaque(false);
    lblDesc.setEditable(false);
    lblDesc.setHighlighter(null);
    // lblDesc.setPreferredSize(new Dimension(0, 10));
    lblDesc.setMargin(new Insets(0, 0, 0, 0));
    JScrollPane sc2 =
        new JScrollPane(
            lblDesc, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    sc2.setBorder(null);

    table.add(new JSeparator(), "growx, span 4 1, gapy 3");
    table.add(lblInstr, "aligny top, gap 0");
    table.add(lblOpcode, "aligny top, gaptop 2, gapx 2");
    table.add(sc1, "aligny top, gapx 2");
    table.add(sc2, "grow,aligny top, gapx 2");
  }

  public static void main(String[] args) {
    // Ensuring GUI creation is done in the Event Dispatch Thread
    javax.swing.SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            new InstructionTable(null, null, null);
          }
        });
  }
}
