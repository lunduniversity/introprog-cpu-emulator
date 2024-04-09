package util;

import static util.LazySwing.action;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuFactory {

  private final InputMap imap;
  private final ActionMap amap;

  public MenuFactory(JFrame frame) {
    this.imap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    this.amap = frame.getRootPane().getActionMap();
  }

  public JMenuItem item(String text, String keyStroke, ActionListener listener) {
    KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
    JMenuItem item = new AcceleratorMenuItem(text, ks);

    imap.put(ks, text);
    amap.put(text, action(listener));

    return item;
  }

  public JCheckBoxMenuItem cBox(String text, String keyStroke, ItemListener listener) {
    KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
    JCheckBoxMenuItem item = new AcceleratorCheckboxMenuItem(text, ks);
    item.addItemListener(listener);

    imap.put(ks, text);
    amap.put(text, action(event -> item.setSelected(!item.isSelected())));

    return item;
  }
}
