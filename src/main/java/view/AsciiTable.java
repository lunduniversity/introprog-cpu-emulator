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
    int d = (asciiCode % 2) * 20;
    // Digits
    if (asciiCode >= 48 && asciiCode <= 57) {
      return new Color(150 + d, 160 + d, 230 + d); // Gradient of blue
    }
    // Uppercase letters
    else if (asciiCode >= 65 && asciiCode <= 90) {
      return new Color(100 + d, 210 + d, 100 + d); // Gradient of green
    }
    // Lowercase letters
    else if (asciiCode >= 97 && asciiCode <= 122) {
      return new Color(210 + d, 130 + d, 130 + d); // Gradient of red
    }
    // Special characters
    else if ((asciiCode >= 32 && asciiCode <= 47)
        || (asciiCode >= 58 && asciiCode <= 64)
        || (asciiCode >= 91 && asciiCode <= 96)
        || (asciiCode >= 123 && asciiCode <= 126)) {
      return new Color(210 + d, 210 + d, 100 + d); // Gradient of yellow
    }
    // Control characters and others
    else {
      return new Color(200 + d, 200 + d, 200 + d); // Shade of gray
    }
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
