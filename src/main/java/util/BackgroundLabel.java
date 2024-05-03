package util;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.SwingConstants;

public class BackgroundLabel extends SizedLabel {
  private Color backgroundColor;

  public BackgroundLabel(String text, int fontSizeDelta, boolean bold, int align, Color bgColor) {
    super(text, 0, bold, align);
    backgroundColor = bgColor;
    setOpaque(false); // The label itself shouldn't handle the opacity
  }

  public BackgroundLabel(String text, int fontSizeDelta, boolean bold, Color bgColor) {
    this(text, fontSizeDelta, bold, SwingConstants.LEADING, bgColor);
  }

  public BackgroundLabel(String text, int fontSizeDelta, Color bgColor) {
    this(text, fontSizeDelta, false, SwingConstants.LEADING, bgColor);
  }

  public BackgroundLabel(String text, Color bgColor) {
    this(text, 0, false, SwingConstants.LEADING, bgColor);
  }

  @Override
  protected void paintComponent(Graphics g) {
    if (backgroundColor != null) {
      g.setColor(backgroundColor);
      g.fillRect(0, 0, getWidth(), getHeight());
    }
    super.paintComponent(g);
  }
}
