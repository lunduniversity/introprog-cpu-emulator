package view;

import instruction.InstructionPrettyPrinter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HexFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;

public abstract class AbstractCell extends JPanel {

  private static final long serialVersionUID = 1L;

  private static final Border PC_FOCUS_BORDER = BorderFactory.createLineBorder(Color.MAGENTA, 2);

  private static final Border PC_NO_FOCUS_BORDER =
      BorderFactory.createEmptyBorder(2, 2, 2, 2); // UIManager.getBorder("TextField.border");

  private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 200);

  private static HexFormat hexFormat = HexFormat.of().withUpperCase();

  private JLabel lblIndex;
  private JPanel bitPanel;
  private JTextField[] bits;
  private JLabel lblHex;
  private JLabel lblDec;
  private JLabel lblAscii;
  private JLabel lblInstruction;

  private CellListener listener;

  private int currentValue = 0;

  /** Create the panel. */
  public AbstractCell(String label, CellListener listener, CellNav cellNav) {
    setBorder(null);
    setLayout(
        new MigLayout(
            "gap 5 5, insets 0",
            "[30px:30px:30px][100px:100px:100px][30px:30px:30px][30px:30px:30px][30px:30px:30px][5px:5px:5px][110px::,grow]",
            "[]"));
    this.listener = listener;

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
    bitPanel.setLayout(new BoxLayout(bitPanel, BoxLayout.X_AXIS));

    bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));
    bits = new JTextField[8];
    for (int i = 0; i < bits.length; i++) {
      final int idx = i;
      JTextField bit = new JTextField("0", 1);
      bit.setBorder(null);
      bit.setPreferredSize(new Dimension(10, 20));
      bit.setHorizontalAlignment(SwingConstants.CENTER);
      bit.addKeyListener(
          new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
              if (e.getKeyChar() == '0' || e.getKeyChar() == '1') {
                bit.setText(Character.toString(e.getKeyChar()));
                userChangedValue();
                if (idx < bits.length - 1) {
                  bits[idx + 1].requestFocusInWindow();
                }
              } else {
                bit.setCaretPosition(0);
              }
              e.consume();
            }

            @Override
            public void keyPressed(KeyEvent e) {
              if (e.getExtendedKeyCode() == KeyEvent.VK_LEFT && idx > 0) {
                bits[idx - 1].requestFocusInWindow();
              }
              if (e.getExtendedKeyCode() == KeyEvent.VK_RIGHT && idx < bits.length - 1) {
                bits[idx + 1].requestFocusInWindow();
              }
              if (e.getExtendedKeyCode() == KeyEvent.VK_UP) {
                cellNav.prevCell(idx);
              }
              if (e.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
                cellNav.nextCell(idx);
              }
              if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                cellNav.nextCell(0);
              }
            }
          });
      bit.addFocusListener(
          new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
              bit.setCaretPosition(0);
            }

            @Override
            public void focusLost(FocusEvent e) {
              if (bit.getText().length() == 0) {
                bit.setText("0");
                updateValue();
              }
            }
          });
      bitPanel.add(bit, String.format("cell %d 0,growx", i + 1));
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

    add(Box.createRigidArea(new Dimension(5, 5)), "cell 5 0");

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
    listener.onCellChanged(currentValue);
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
    lblInstruction.setText(InstructionPrettyPrinter.prettyPrint(value));
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

  public void focus(int xpos) {
    bits[xpos].requestFocusInWindow();
    bits[xpos].setCaretPosition(0);
  }

  public void setValue(int value) {
    for (int i = 0; i < 8; i++) {
      bits[7 - i].setText(Integer.toString(((value >> i) & 1)));
    }
    updateValue();
    highlight();
  }

  public int getValue() {
    return currentValue;
  }

  public void highlight() {
    for (JTextField bit : bits) {
      bit.setBackground(HIGHLIGHT_COLOR);
    }
  }

  public void unhighlight() {
    for (JTextField bit : bits) {
      bit.setBackground(UIManager.getColor("TextField.background"));
    }
  }

  public void setProgramCounterFocus() {
    bitPanel.setBorder(PC_FOCUS_BORDER);
  }

  public void clearProgramCounterFocus() {
    bitPanel.setBorder(PC_NO_FOCUS_BORDER);
  }
}
