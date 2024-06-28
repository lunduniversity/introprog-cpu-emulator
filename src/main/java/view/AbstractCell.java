package view;

import instruction.InstructionFactory;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HexFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import model.Memory;
import model.Registry;
import net.miginfocom.swing.MigLayout;

public abstract class AbstractCell {

  private static final Border PC_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);
  private static final Border PC_NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

  private static final Color DEFAULT_BG_COLOR = UIManager.getColor("TextField.background");
  private static final Color HIGHLIGHT_BG_COLOR = new Color(255, 255, 200);
  private static final Color HIGHLIGHT_PC_BG_COLOR = new Color(1.0f, 0.1f, 1.0f, 0.2f);
  private static final Color HIGHLIGHT_ERROR_BG_COLOR = new Color(240, 150, 150);
  private static final Color HIGHLIGHT_COMPLETED_BG_COLOR = new Color(150, 240, 150);
  private static final Color SELECT_BG_COLOR = new Color(200, 255, 200);
  private static final Color CARET_BG_COLOR = new Color(50, 230, 210);

  private static final Color INACTIVE_SELECT_BG_COLOR = new Color(220, 240, 220);
  private static final Color INACTIVE_CARET_BG_COLOR = new Color(150, 180, 160);

  private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 14);

  private static HexFormat hexFormat = HexFormat.of().withUpperCase();

  private JLabel lblIndex;
  private JLabel lblAddress;
  private JPanel bitPanel;
  private JLabel[] bits;
  private JLabel lblHex;
  private JLabel lblDec;
  private JLabel lblAscii;
  private JLabel lblInstruction;

  private InstructionFactory factory = new InstructionFactory();

  private int currentValue = 0;

  private CellValueListener valueListener;

  private final int index;
  private final Memory mem;
  private final Registry reg;

  protected AbstractCell(
      Container parent,
      final int index,
      String address,
      String label,
      CellValueListener valueListener,
      AbstractSelecter cellSelecter,
      Memory mem,
      Registry reg) {

    this.mem = mem;
    this.reg = reg;
    this.index = index;

    this.valueListener = valueListener;

    lblAddress = new JLabel(address);
    lblAddress.setBorder(null);
    parent.add(lblAddress);

    if (label != null) {
      lblIndex = new JLabel(label);
      lblIndex.setBorder(null);
      parent.add(lblIndex);
    }

    bitPanel = new JPanel();
    bitPanel.setBorder(PC_NO_FOCUS_BORDER);
    bitPanel.setBackground(UIManager.getColor("TextField.background"));
    parent.add(bitPanel);
    bitPanel.setLayout(new MigLayout("flowx, gap 0 0, insets 0", "[sg bit]", ""));
    bitPanel.addMouseListener(
        new MouseAdapter() {

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
        });

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
            new MouseAdapter() {

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
            });
      }
      bitPanel.add(bit);
      bits[i] = bit;
      if (i == bits.length / 2 - 1) {
        bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));
      }
    }
    bitPanel.add(Box.createRigidArea(new Dimension(2, 5)), "gap 0");

    lblHex = new JLabel(hex(0));
    lblHex.setBorder(null);
    parent.add(lblHex);

    lblDec = new JLabel(pad(0));
    lblDec.setBorder(null);
    parent.add(lblDec);

    lblAscii = new JLabel("");
    lblAscii.setBorder(null);
    parent.add(lblAscii);

    lblInstruction = new JLabel();
    lblInstruction.setBorder(null);
    parent.add(lblInstruction);

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
    lblInstruction.setText(factory.createInstruction(value).prettyPrint(mem, reg, index));
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

  public void setValue(int value, boolean isExecuting) {
    for (int i = 0; i < 8; i++) {
      bits[7 - i].setText(Integer.toString(((value >> i) & 1)));
    }
    updateValue();
    if (isExecuting) {
      highlight();
    }
  }

  public int getValue() {
    return currentValue;
  }

  // Highlighting is used to indicate that the cell is being executed
  public AbstractCell highlight() {
    setBgColor(HIGHLIGHT_BG_COLOR);
    return this;
  }

  public void highlightError() {
    setBgColor(HIGHLIGHT_ERROR_BG_COLOR);
  }

  public void highlightCompleted() {
    setBgColor(HIGHLIGHT_COMPLETED_BG_COLOR);
  }

  public AbstractCell unhighlight() {
    setBgColor(DEFAULT_BG_COLOR);
    return this;
  }

  private boolean wasSelected = false;
  private boolean wasCaret = false;

  public AbstractCell setSelected(boolean selected, int caretPos, boolean active) {
    if (selected) {
      wasSelected = true;
      setBgColor(active ? SELECT_BG_COLOR : INACTIVE_SELECT_BG_COLOR);
    } else {
      if (wasSelected || wasCaret) {
        wasSelected = false;
        wasCaret = false;
        setBgColor(DEFAULT_BG_COLOR);
      }
      if (caretPos >= 0) {
        wasCaret = true;
        bits[caretPos].setBackground(active ? CARET_BG_COLOR : INACTIVE_CARET_BG_COLOR);
      }
    }
    return this;
  }

  private void setBgColor(Color color) {
    for (JLabel bit : bits) {
      bit.setBackground(color);
    }
  }

  private void setCellColor(Color color) {
    bitPanel.setBackground(color);
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

  public void setProgramCounterHighlight() {
    setCellColor(HIGHLIGHT_PC_BG_COLOR);
  }

  public void clearProgramCounterHighlight() {
    setCellColor(DEFAULT_BG_COLOR);
  }

  public void scrollTo() {
    scrollTo(0);
  }

  public void scrollTo(int additionalCells) {
    bitPanel.scrollRectToVisible(
        new Rectangle(bitPanel.getSize().width, bitPanel.getSize().height * (1 + additionalCells)));
  }
}
