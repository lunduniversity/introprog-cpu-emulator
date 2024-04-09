package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * A custom menu item that displays the accelerator text aligned to the right of the menu item text.
 * The purpose of this existing, rather than simply using the built-in accelerator, is to circumvent
 * the issue of the accelerator being slow when the bound key is pressed and held down. This custom
 * implementation allows the accelerator text to be displayed without any actual key binding, which
 * can instead be handled manually by listening for key pressed and released events, and triggering
 * the menu action periodically while the key is held down.
 */
public class AcceleratorMenuItem extends JMenuItem {

  private static final int SPACE = 20;
  private static final int PADDING = 5;
  private static final Color ACC_DEFAULT_TEXT_COLOR = new Color(120, 150, 220);
  private static final Color ACC_HOVER_TEXT_COLOR = new Color(80, 100, 150);
  private static final float ACC_FONT_DIFF = 1.0f;

  private String acceleratorText;
  private boolean isHovered = false;

  public AcceleratorMenuItem(String text) {
    this(text, null);
  }

  public AcceleratorMenuItem(String text, KeyStroke keyStroke) {
    super(text);
    this.acceleratorText = keyStroke != null ? keyStroke.toString() : null;
    addMouseListener(
        new MouseAdapter() {

          @Override
          public void mouseEntered(MouseEvent e) {
            isHovered = true;
            repaint(); // Important to trigger a repaint when the state changes
          }

          @Override
          public void mouseExited(MouseEvent e) {
            isHovered = false;
            repaint(); // Trigger repaint on state change
          }
        });
  }

  @Override
  public void setAccelerator(KeyStroke keyStroke) {
    // Do nothing, as the accelerator text is not bound to any actual key binding
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (acceleratorText != null) {
      Graphics2D g2d = (Graphics2D) g.create();

      // Set the color for the accelerator text
      g2d.setColor(isHovered ? ACC_HOVER_TEXT_COLOR : ACC_DEFAULT_TEXT_COLOR);

      // Set the font smaller than the component's font
      Font originalFont = g.getFont();
      float smallerSize = originalFont.getSize() - ACC_FONT_DIFF; // Decrease font size
      Font smallerFont = originalFont.deriveFont(smallerSize);
      g2d.setFont(smallerFont);

      // Calculate the position for the accelerator text
      FontMetrics fm = g2d.getFontMetrics();
      Insets insets = getInsets();
      int x =
          getWidth()
              - insets.right
              - fm.stringWidth(acceleratorText)
              - PADDING; // padding from right
      int y = (getHeight() + fm.getAscent() - fm.getLeading() - fm.getDescent()) / 2;

      // Draw the accelerator text
      g2d.drawString(acceleratorText, x, y);

      g2d.dispose();
    }
  }

  @Override
  public Dimension getPreferredSize() {
    Dimension size = super.getPreferredSize();
    if (acceleratorText != null && getGraphics() != null) {
      Graphics g = getGraphics();
      Font originalFont = getFont();
      float smallerSize = originalFont.getSize() - ACC_FONT_DIFF;
      Font smallerFont = originalFont.deriveFont(Font.BOLD, smallerSize);
      FontMetrics fm = g.getFontMetrics(smallerFont);
      size.width += SPACE + fm.stringWidth(acceleratorText) + PADDING;
    }
    return size;
  }
}
