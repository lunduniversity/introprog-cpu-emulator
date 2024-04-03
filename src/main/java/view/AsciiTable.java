package view;

import instruction.Instruction;
import java.awt.Font;
import java.awt.Point;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

public class AsciiTable extends JFrame {

  private static final Font plain = new Font("Monospaced", Font.PLAIN, 14);
  private static final Font bold = new Font("Monospaced", Font.BOLD, 14);

  public AsciiTable(JFrame parent) {
    setTitle("ASCII Table");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    JPanel contentPane = new JPanel();
    contentPane.setLayout(
        new MigLayout("flowy,gap 10 5, insets 0", "[grow,shrink,fill]", "5[grow,shrink,80:]5[]5"));
    add(contentPane);

    JEditorPane notice = new JEditorPane();
    notice.setContentType("text/html");
    notice.setText(
        "Note that characters 0-31 and 127 are control characters and thus not printable.");
    // notice.setLineWrap(true);
    // notice.setWrapStyleWord(true);
    notice.setOpaque(false);
    notice.setEditable(false);
    notice.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    notice.setFont(new JLabel().getFont().deriveFont(14f));
    JScrollPane sc =
        new JScrollPane(
            notice,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    sc.setBorder(null);
    contentPane.add(sc, "grow,gap 5 5");

    // Add column headers
    JPanel headerPanel =
        new JPanel(
            new MigLayout(
                "wrap 4, gap 10 3",
                "[sg symbol,40:40:][sg hex,30:30][sg dec,30:30][sg bin,70:]",
                "[grow,fill]"));
    headerPanel.add(hdr("Char"), "al c");
    headerPanel.add(hdr("Hex"), "al c");
    headerPanel.add(hdr("Dec"), "al c");
    headerPanel.add(hdr("Bin"), "al c");
    contentPane.add(headerPanel, "grow 20");

    contentPane.add(new JSeparator(), "");

    // Add table data
    JPanel columnPanel =
        new JPanel(
            new MigLayout(
                "wrap 4, gap 10 3",
                "[sg symbol,40:40:][sg hex,30:30][sg dec,30:30][sg bin,70:]",
                "[grow,fill]"));

    for (int asciiCode = 0; asciiCode < 128; asciiCode++) {
      columnPanel.add(lbl(String.valueOf((char) asciiCode), true), "al c");
      columnPanel.add(lbl(String.format("0x%02X", asciiCode)), "al r");
      columnPanel.add(lbl(String.valueOf(asciiCode)), "al r");
      columnPanel.add(lbl(Instruction.toBinaryString(asciiCode, 8, 4)), "al r");
    }

    JScrollPane scrollPane =
        new JScrollPane(
            columnPanel,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    scrollPane.setBorder(null);
    contentPane.add(scrollPane, "grow");

    pack(); // Adjusts size to contents
    setSize(270, parent.getHeight());
    setVisible(true);

    Point parentLocation = parent.getLocation();
    int xCoord = parentLocation.x - getWidth();
    int yCoord = parentLocation.y;
    setLocation(xCoord, yCoord);
  }

  private JLabel lbl(String text) {
    return lbl(text, false);
  }

  private JLabel lbl(String text, boolean bold) {
    JLabel label = new JLabel(text);
    label.setFont(bold ? AsciiTable.bold : AsciiTable.plain);
    label.setOpaque(true);
    return label;
  }

  private JLabel hdr(String text) {
    JLabel label = new JLabel(text);
    label.setFont(label.getFont().deriveFont(Font.BOLD, 14.0f));
    label.setOpaque(true);
    return label;
  }
}
