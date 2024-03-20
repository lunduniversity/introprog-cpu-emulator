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
    setTitle("ASCII Table Display");
    setDefaultCloseOperation(
        JFrame.DISPOSE_ON_CLOSE); // Close the window without terminating the app
    setLayout(new MigLayout("wrap 1", "[grow,fill]", "[grow,fill]"));

    JPanel asciiPanel = new JPanel(new MigLayout("", "[]10[]", "[]"));

    asciiPanel.add(new JSeparator(SwingConstants.VERTICAL), "growx");
    for (int col = 0; col < 4; col++) {
      JPanel columnPanel = new JPanel(new MigLayout("wrap 1, gap 0", "[grow,fill]", "[grow,fill]"));

      for (int row = 0; row < 32; row++) {
        int asciiCode = col * 32 + row;

        JPanel charPanel = new JPanel(new MigLayout("insets 0", "[20px][25px][25px][40px]"));
        charPanel.add(lbl(String.valueOf((char) asciiCode), true), "align right");
        charPanel.add(lbl(String.format("0x%02X", asciiCode)), "align right");
        charPanel.add(lbl(String.valueOf(asciiCode)), "align right");
        charPanel.add(lbl(Instruction.toBinaryString(asciiCode, 8)), "align right");

        columnPanel.add(charPanel, "growx");
      }

      asciiPanel.add(columnPanel, "grow");
      asciiPanel.add(new JSeparator(SwingConstants.VERTICAL), "growx");
    }

    JScrollPane scrollPane = new JScrollPane(asciiPanel);
    add(scrollPane);

    pack(); // Adjusts size to contents
    setLocationRelativeTo(null); // Center window
    setResizable(false);
    setVisible(true);
  }

  private JLabel lbl(String text) {
    return lbl(text, false);
  }

  private JLabel lbl(String text, boolean bold) {
    JLabel label = new JLabel(text);
    label.setFont(bold ? AsciiTable.bold : AsciiTable.plain);
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
