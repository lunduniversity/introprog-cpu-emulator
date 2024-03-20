package view;

import instruction.Instruction;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

public class AsciiTable extends JFrame {

  private static final Font plain = new Font("Monospaced", Font.PLAIN, 14);
  private static final Font bold = new Font("Monospaced", Font.BOLD, 14);

  public AsciiTable() {
    setTitle("ASCII Table");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new MigLayout("gap 10 0, insets 0", "[grow,shrink]", "[shrink][grow]"));
    JScrollPane scrollPane = new JScrollPane(contentPane);
    scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    add(scrollPane);

    JLabel notice =
        new JLabel(
            "Note that characters 0-31 and 127 are control characters and thus not printable.",
            SwingConstants.CENTER);
    notice.setFont(notice.getFont().deriveFont(14f));
    contentPane.add(notice, "wrap, gapleft 20, gaptop 10");

    JPanel asciiPanel = new JPanel(new MigLayout("", "[]", "[]"));

    asciiPanel.add(new JSeparator(SwingConstants.VERTICAL), "growy, gaptop 30");
    for (int col = 0; col < 4; col++) {
      JPanel columnPanel =
          new JPanel(
              new MigLayout(
                  "wrap 4, gap 10 3", "[sg symbol][sg hex][sg dec][sg bin]", "[grow,fill]"));

      // Add column headers
      columnPanel.add(lbl("Char", true), "al c");
      columnPanel.add(lbl("Hex", true), "al r");
      columnPanel.add(lbl("Dec", true), "al r");
      columnPanel.add(lbl("Bin", true), "al r");

      for (int row = 0; row < 32; row++) {
        int asciiCode = col * 32 + row;
        columnPanel.add(lbl(String.valueOf((char) asciiCode), true), "al c");
        columnPanel.add(lbl(String.format("0x%02X", asciiCode)), "al r");
        columnPanel.add(lbl(String.valueOf(asciiCode)), "al r");
        columnPanel.add(lbl(Instruction.toBinaryString(asciiCode, 8, 4)), "al r");
      }

      asciiPanel.add(columnPanel, "grow");
      asciiPanel.add(new JSeparator(SwingConstants.VERTICAL), "growy, gaptop 30");
    }
    contentPane.add(asciiPanel, "grow");

    pack(); // Adjusts size to contents
    setLocationRelativeTo(null); // Center window
    setVisible(true);
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

  public static void main(String[] args) {
    // Ensuring GUI creation is done in the Event Dispatch Thread
    javax.swing.SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            new AsciiTable();
          }
        });
  }
}
