package util;

import static util.LazySwing.action;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class MenuFactory {

  private final InputMap imap;
  private final ActionMap amap;

  public MenuFactory(JFrame frame) {
    this.imap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    this.amap = frame.getRootPane().getActionMap();
  }

  public JMenu menu(String text, String keyStroke) {
    KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
    JMenu item = new JMenu("<html>" + text + "</html>");

    imap.put(ks, text);
    amap.put(
        text,
        action(
            e -> {
              item.doClick();
              item.requestFocusInWindow();
            }));

    return item;
  }

  public JMenuItem item(String text, String[] keyStrokes, ActionListener listener) {
    KeyStroke ks = KeyStroke.getKeyStroke(keyStrokes[0]);
    JMenuItem item = new AcceleratorMenuItem(text, ks);
    item.addActionListener(listener);

    for (String keyStroke : keyStrokes) {
      ks = KeyStroke.getKeyStroke(keyStroke);
      imap.put(ks, text);
    }
    amap.put(text, action(listener));

    return item;
  }

  public JMenuItem item(String text, String keyStroke, ActionListener listener) {
    KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
    JMenuItem item = new AcceleratorMenuItem(text, ks);
    item.addActionListener(listener);

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

  public JRadioButtonMenuItem rButton(String text, String keyStroke, ItemListener listener) {
    KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
    JRadioButtonMenuItem item = new AcceleratorRadioButtonMenuItem(text, ks);
    item.addItemListener(listener);

    imap.put(ks, text);
    amap.put(text, action(event -> item.setSelected(!item.isSelected())));

    return item;
  }
}
