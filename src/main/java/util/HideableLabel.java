package util;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class HideableLabel extends JLabel {

  private static final String HIDDEN_SYMBOL = "\u2298"; // Crossed circle

  private boolean isHideable;
  private String originalText;
  private boolean isHidden;
  private JPopupMenu popupMenu;

  public HideableLabel(String text) {
    super(text);
    this.originalText = text;
    this.isHidden = false;
  }

  @Override
  public void setText(String text) {
    this.originalText = text;
    if (!isHidden) {
      super.setText(text);
    }
  }

  public void setHideable(boolean hideable) {
    this.isHideable = hideable;
    if (hideable) {
      // Initialize the popup menu
      popupMenu = new JPopupMenu();
      JMenuItem toggleItem = new JMenuItem("Hide");
      toggleItem.addActionListener(e -> toggleVisibility());
      popupMenu.add(toggleItem);

      // Add mouse listener to show popup menu on right-click
      addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
              if (e.isPopupTrigger()) {
                toggleItem.setText(isHidden ? "Reveal" : "Hide");
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
              }
            }

            @Override
            public void mousePressed(MouseEvent e) {
              if (e.isPopupTrigger()) {
                toggleItem.setText(isHidden ? "Reveal" : "Hide");
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
              }
            }
          });
    } else {
      // Remove popup menu and mouse listener
      popupMenu = null;
      for (MouseListener listener : getMouseListeners()) {
        removeMouseListener(listener);
      }
    }
  }

  private void toggleVisibility() {
    if (!isHideable) {
      return;
    }
    if (isHidden) {
      super.setText(originalText);
      setForeground(Color.BLACK);
      isHidden = false;
    } else {
      super.setText(HIDDEN_SYMBOL); // Use any desired hidden symbol
      setForeground(Color.GRAY);
      isHidden = true;
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("HidableLabel Demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new FlowLayout());
    HideableLabel l1 = new HideableLabel("This text can be hidden");
    l1.setHideable(true);
    frame.add(l1);
    HideableLabel l2 = new HideableLabel("This text cannot be hidden");
    l2.setHideable(false);
    frame.add(l2);
    frame.setSize(300, 200);
    frame.setVisible(true);
  }
}
