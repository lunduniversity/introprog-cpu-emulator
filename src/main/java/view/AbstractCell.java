package view;

import instruction.InstructionFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HexFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

public abstract class AbstractCell extends JPanel {

  private static final long serialVersionUID = 1L;

  private static final Border PC_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);
  private static final Border PC_NO_FOCUS_BORDER =
      BorderFactory.createEmptyBorder(2, 2, 2, 2); // UIManager.getBorder("TextField.border");

  private static final Color DEFAULT_BG_COLOR = UIManager.getColor("TextField.background");
  private static final Color HIGHLIGHT_BG_COLOR = new Color(255, 255, 200);
  private static final Color SELECT_BG_COLOR = new Color(200, 255, 200);
  private static final Color CARET_BG_COLOR = new Color(50, 230, 210);

  private static final Color INACTIVE_DEFAULT_BG_COLOR = new Color(240, 240, 240);
  private static final Color INACTIVE_SELECT_BG_COLOR = new Color(220, 240, 220);
  private static final Color INACTIVE_CARET_BG_COLOR = new Color(150, 180, 160);

  private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 14);

  private static HexFormat hexFormat = HexFormat.of().withUpperCase();

  private JLabel lblIndex;
  private JPanel bitPanel;
  private JLabel[] bits;
  private JLabel lblHex;
  private JLabel lblDec;
  private JLabel lblAscii;
  private JLabel lblInstruction;

  private InstructionFactory factory = new InstructionFactory();

  private int currentValue = 0;

  private CellValueListener valueListener;

  public AbstractCell(
      final int index,
      String label,
      CellValueListener valueListener,
      AbstractSelecter cellSelecter) {

    this.valueListener = valueListener;

    setBorder(null);
    setLayout(
        new MigLayout(
            "gap 5 5, insets 0",
            "[30px:30px:30px][100px:100px:100px][30px:30px:30px][30px:30px:30px][30px:30px:30px][][110px::,grow]",
            "[]"));

    lblIndex = new JLabel(label);
    lblIndex.setBorder(null);
    lblIndex.setHorizontalTextPosition(SwingConstants.RIGHT);
    lblIndex.setPreferredSize(new Dimension(30, 20));
    lblIndex.setHorizontalAlignment(SwingConstants.RIGHT);
    add(lblIndex, "cell 0 0,alignx right");

    bitPanel = new JPanel();
    bitPanel.setBorder(PC_NO_FOCUS_BORDER);
    bitPanel.setBackground(UIManager.getColor("TextField.background"));
    add(bitPanel, "cell 1 0,grow");
    bitPanel.setLayout(new MigLayout("flowx, gap 0 0, insets 0", "[sg bit]", ""));

    bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));
    bits = new JLabel[8];
    for (int i = 0; i < bits.length; i++) {
      final int bitIdx = i;
      JLabel bit = new JLabel("0");
      bit.setBorder(null);
      bit.setOpaque(true);
      bit.setFont(MONOSPACED);
      bit.setBackground(DEFAULT_BG_COLOR);
      bit.setPreferredSize(new Dimension(10, 20));
      bit.setHorizontalAlignment(SwingConstants.CENTER);
      if (cellSelecter != null) {
        bit.addMouseListener(
            new MouseListener() {

              @Override
              public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2
                    || e.getButton() == MouseEvent.BUTTON3) {
                  flipBit(bitIdx);
                }
                cellSelecter.setCaretPosition(index, bitIdx);
                cellSelecter.cancelSelection();
              }

              @Override
              public void mousePressed(MouseEvent e) {
                cellSelecter.requestFocus();
                if (e.getButton() == MouseEvent.BUTTON1) {
                  cellSelecter.startSelection(index);
                }
              }

              @Override
              public void mouseReleased(MouseEvent e) {
                cellSelecter.endSelection();
              }

              @Override
              public void mouseEntered(MouseEvent e) {
                cellSelecter.updateSelection(index);
              }

              @Override
              public void mouseExited(MouseEvent e) {}
            });
      }
      bitPanel.add(bit, String.format("growx", i + 1));
      bits[i] = bit;
      if (i == bits.length / 2 - 1) {
        bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));
      }
    }
    bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));

    lblHex = new JLabel(hex(0));
    lblHex.setBorder(null);
    lblHex.setHorizontalTextPosition(SwingConstants.RIGHT);
    lblHex.setHorizontalAlignment(SwingConstants.RIGHT);
    lblHex.setPreferredSize(new Dimension(30, 20));
    add(lblHex, "cell 2 0,alignx right");

    lblDec = new JLabel(pad(0));
    lblDec.setBorder(null);
    lblDec.setHorizontalTextPosition(SwingConstants.RIGHT);
    lblDec.setHorizontalAlignment(SwingConstants.RIGHT);
    lblDec.setPreferredSize(new Dimension(30, 20));
    add(lblDec, "cell 3 0,alignx right");

    lblAscii = new JLabel("");
    lblAscii.setBorder(null);
    lblAscii.setHorizontalTextPosition(SwingConstants.RIGHT);
    lblAscii.setHorizontalAlignment(SwingConstants.RIGHT);
    lblAscii.setPreferredSize(new Dimension(30, 20));
    add(lblAscii, "cell 4 0,alignx right");

    add(Box.createRigidArea(new Dimension(2, 5)), "cell 5 0");

    lblInstruction = new JLabel();
    lblInstruction.setBorder(null);
    lblInstruction.setHorizontalTextPosition(SwingConstants.LEFT);
    lblInstruction.setHorizontalAlignment(SwingConstants.LEFT);
    lblInstruction.setMinimumSize(new Dimension(60, 20));
    add(lblInstruction, "cell 6 0,alignx left grow");

    updateValue();
  }

  // Inform the rest of the UI that the value has changed
  private void userChangedValue() {
    updateValue();
    valueListener.onCellChanged(currentValue);
  }

  private void updateValue() {
    int value = 0;
    for (int i = 0; i < bits.length; i++) {
      int b = bits[i].getText().equals("1") ? 1 : 0;
      value |= b << (bits.length - i - 1);
    }
    currentValue = value;
    lblHex.setText(hex(value));
    lblDec.setText(pad(value));
    // if value is a printable ascii charcter, display it
    // (0-31 are control characters, 32-126 are printable ascii characters, 127 is DEL)
    if (value >= 32 && value <= 126) lblAscii.setText(Character.toString((char) value));
    else lblAscii.setText("--");
    lblInstruction.setText(factory.createInstruction(value).toString());
  }

  private static String hex(int value) {
    return hex(value, true);
  }

  private static String hex(int value, boolean prefix) {
    String hexDigits = hexFormat.toHexDigits(value);
    if (hexDigits.length() == 1) {
      hexDigits = "0" + hexDigits;
    } else if (hexDigits.length() > 2) {
      hexDigits = hexDigits.substring(hexDigits.length() - 2);
    }
    return prefix ? "0x" + hexDigits : hexDigits;
  }

  static String pad(int value) {
    return String.format("%02d", value);
  }

  public AbstractCell focus(int xpos) {
    // bits[xpos].requestFocusInWindow();
    // bits[xpos].setCaretPosition(0);
    return this;
  }

  public void setValue(int value, boolean isExecuting) {
    for (int i = 0; i < 8; i++) {
      bits[7 - i].setText(Integer.toString(((value >> i) & 1)));
    }
    updateValue();
    if (isExecuting) {
      System.out.println("Highlighting");
      highlight();
    } else {
      System.out.println("Not highlighting");
    }
  }

  public int getValue() {
    return currentValue;
  }

  // Highlighting is used to indicate that the cell is being executed
  public AbstractCell highlight() {
    for (JLabel bit : bits) {
      bit.setBackground(HIGHLIGHT_BG_COLOR);
    }
    return this;
  }

  public AbstractCell unhighlight() {
    for (JLabel bit : bits) {
      bit.setBackground(DEFAULT_BG_COLOR);
    }
    return this;
  }

  private boolean wasSelected = false;
  private boolean wasCaret = false;

  public AbstractCell setSelected(boolean selected, int caretPos, boolean active) {
    if (selected) {
      wasSelected = true;
      _bgColor(active ? SELECT_BG_COLOR : INACTIVE_SELECT_BG_COLOR);
    } else {
      // _bgColor(active ? DEFAULT_BG_COLOR : INACTIVE_DEFAULT_BG_COLOR);
      if (wasSelected || wasCaret) {
        wasSelected = false;
        wasCaret = false;
        _bgColor(DEFAULT_BG_COLOR);
      }
      if (caretPos >= 0) {
        wasCaret = true;
        bits[caretPos].setBackground(active ? CARET_BG_COLOR : INACTIVE_CARET_BG_COLOR);
      }
    }
    return this;
  }

  private void _bgColor(Color color) {
    for (JLabel bit : bits) {
      bit.setBackground(color);
    }
  }

  public AbstractCell setBits(int start, int end, boolean value) {
    for (int i = start; i < end; i++) {
      bits[i].setText(value ? "1" : "0");
    }
    userChangedValue();
    return this;
  }

  public void flipBit(int bitIdx) {
    bits[bitIdx].setText(bits[bitIdx].getText().equals("1") ? "0" : "1");
    userChangedValue();
  }

  public void setBit(int bitIdx, boolean value) {
    bits[bitIdx].setText(value ? "1" : "0");
    userChangedValue();
  }

  public void setProgramCounterFocus() {
    bitPanel.setBorder(PC_FOCUS_BORDER);
  }

  public void clearProgramCounterFocus() {
    bitPanel.setBorder(PC_NO_FOCUS_BORDER);
  }
}
