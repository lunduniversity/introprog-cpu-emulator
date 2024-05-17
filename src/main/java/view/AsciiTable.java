package view;

import static util.LazySwing.inv;

import instruction.Instruction;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import net.miginfocom.swing.MigLayout;
import util.BackgroundLabel;
import util.Settings;
import util.SizedLabel;

public class AsciiTable extends AnchoredFrame {

  private static final Color GRAY_DARK = new Color(200, 200, 200); // #C8C8C8
  private static final Color BLUE_DARK = new Color(150, 160, 230); // #96A0E6
  private static final Color GREEN_DARK = new Color(100, 210, 100); // #64D264
  private static final Color RED_DARK = new Color(210, 130, 130); // #D28282
  private static final Color YELLOW_DARK = new Color(210, 210, 100); // #D2D264

  private static final Color GRAY_LIGHT = new Color(220, 220, 220); // #DCDCDC
  private static final Color BLUE_LIGHT = new Color(170, 180, 250); // #AAB4FA
  private static final Color GREEN_LIGHT = new Color(120, 230, 120); // #78E678
  private static final Color RED_LIGHT = new Color(230, 150, 150); // #E69696
  private static final Color YELLOW_LIGHT = new Color(230, 230, 120); // #E6E678

  private static final Border border = BorderFactory.createEmptyBorder(2, 10, 2, 10);
  private JPanel headerPanel;
  private JPanel columnPanel;
  private JEditorPane notice;
  private JScrollPane scrollPane;

  private Component headerFiller;

  public AsciiTable(JFrame parent, Settings settings) {
    super("ASCII Table", parent, AnchorSide.LEFT);

    JPanel contentPane = new JPanel();
    contentPane.setLayout(
        new MigLayout("flowy,gap 10 5, insets 0", "[grow,shrink,fill]", "5[grow,shrink]5[]5"));
    add(contentPane);

    notice = new JEditorPane();
    notice.setContentType("text/html");
    notice.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    notice.setText(
        "Note that characters 0-31 and 127 are control characters and thus not printable. <b>32 is"
            + " the space character</b>.");
    notice.setOpaque(false);
    notice.setEditable(false);
    notice.setFont(new JLabel().getFont());
    contentPane.add(notice, "top,grow,gap 5 5");

    headerPanel =
        new JPanel(
            new MigLayout(
                "wrap 4, gap 10 3, insets 0",
                "[sg symbol,grow][sg hex,grow][sg dec,grow][sg bin,grow][sg filler]",
                "[]"));
    headerPanel.add(hdr("Char"), "al c");
    headerPanel.add(hdr("Hex"), "al c");
    headerPanel.add(hdr("Dec"), "al c");
    headerPanel.add(hdr("Bin"), "al c");

    // The filler compensates for the width of the vertical scrollbar
    headerFiller = Box.createRigidArea(new Dimension(10, 0));
    headerPanel.add(headerFiller);
    contentPane.add(headerPanel, "top,grow");

    contentPane.add(new JSeparator(), "");

    columnPanel =
        new JPanel(
            new MigLayout(
                "wrap 4, gap 0 2, insets 0",
                "[sg symbol,grow][sg hex,grow][sg dec,grow][sg bin,grow]",
                "[]"));
    columnPanel.setBackground(Color.BLACK);

    for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
      Color bgColor = getColorForAscii(asciiCode);
      String constraints = "growx";
      columnPanel.add(clbl(String.valueOf((char) asciiCode), true, bgColor), constraints);
      columnPanel.add(rlbl(String.format("0x%02X", asciiCode), false, bgColor), constraints);
      columnPanel.add(rlbl(String.valueOf(asciiCode), false, bgColor), constraints);
      columnPanel.add(
          rlbl(Instruction.toBinaryString(asciiCode, 8, 4), false, bgColor), constraints);
    }

    scrollPane =
        new JScrollPane(
            columnPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setBorder(null);
    contentPane.add(scrollPane, "top,grow");

    headerFiller.setPreferredSize(new Dimension(scrollPane.getVerticalScrollBar().getWidth(), 0));

    inv(
        () -> {
          updateGlobalFontSize(settings.getCurrentFontSize());
          fitToParent();
          setVisible(true);
          inv(parent::requestFocus);
        });
  }

  @Override
  protected void fitContent() {
    for (int i = 0; i < headerPanel.getComponentCount(); i++) {
      headerPanel.getComponent(i).setPreferredSize(null);
    }
    headerPanel.revalidate();

    notice.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
    pack(); // Adjusts size to contents
    synchronizeColumnWidths();
    pack();
    notice.setMaximumSize(null);
  }

  private JLabel clbl(String text, boolean bold, Color bgColor) {
    JLabel label = new BackgroundLabel(text, 0, bold, SwingConstants.CENTER, bgColor);
    label.setBorder(border);
    return label;
  }

  private JLabel rlbl(String text, boolean bold, Color bgColor) {
    JLabel label = new BackgroundLabel(text, 0, bold, SwingConstants.TRAILING, bgColor);
    label.setBorder(border);
    return label;
  }

  private JLabel hdr(String text) {
    return new SizedLabel(text, 2, true, SwingConstants.CENTER);
  }

  private Color getColorForAscii(int asciiCode) {
    boolean dark = asciiCode % 2 == 0;
    // Digits
    if (asciiCode >= 48 && asciiCode <= 57) {
      return dark ? BLUE_DARK : BLUE_LIGHT;
    }
    // Uppercase letters
    if (asciiCode >= 65 && asciiCode <= 90) {
      return dark ? GREEN_DARK : GREEN_LIGHT;
    }
    // Lowercase letters
    if (asciiCode >= 97 && asciiCode <= 122) {
      return dark ? RED_DARK : RED_LIGHT;
    }
    // Special characters
    if ((asciiCode >= 32 && asciiCode <= 47)
        || (asciiCode >= 58 && asciiCode <= 64)
        || (asciiCode >= 91 && asciiCode <= 96)
        || (asciiCode >= 123 && asciiCode <= 126)) {
      return dark ? YELLOW_DARK : YELLOW_LIGHT;
    }
    // Control characters and others
    return dark ? GRAY_DARK : GRAY_LIGHT;
  }

  private void synchronizeColumnWidths() {
    for (int i = 0; i < headerPanel.getComponentCount() - 1; i++) {
      Component c = headerPanel.getComponent(i);
      int newWidth = Math.max(c.getWidth(), columnPanel.getComponent(i).getWidth());
      c.setPreferredSize(new Dimension(newWidth, c.getPreferredSize().height));
    }
    headerPanel.revalidate();
    headerPanel.repaint();
  }
}
