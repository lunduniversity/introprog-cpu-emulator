package util;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SizedLabel extends JLabel {
  private int fontSizeDelta = 0;
  private boolean bold;

  public SizedLabel(String text, int fontSizeDelta, boolean bold, int align) {
    super(text, align);
    this.fontSizeDelta = fontSizeDelta;
    this.bold = bold;
  }

  public SizedLabel(String text, int fontSizeDelta, boolean bold) {
    this(text, fontSizeDelta, bold, SwingConstants.LEADING);
  }

  public SizedLabel(String text, int fontSizeDelta) {
    this(text, fontSizeDelta, false, SwingConstants.LEADING);
  }

  public SizedLabel(String text) {
    this(text, 0, false, SwingConstants.LEADING);
  }

  @Override
  public void setFont(Font font) {
    font = font.deriveFont(bold ? Font.BOLD : Font.PLAIN, (font.getSize() + fontSizeDelta));
    super.setFont(font);
  }
}
