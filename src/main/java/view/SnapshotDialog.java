package view;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;

public class SnapshotDialog extends JDialog {
  private JTextArea textArea;
  private JButton actionButton;
  private JButton cancelButton;
  private boolean confirmed = false;

  // Enum to represent dialog mode
  public enum Mode {
    IMPORT,
    EXPORT
  }

  public SnapshotDialog(JFrame parent, Mode mode) {
    super(parent, true); // Modal dialog
    initializeComponents(mode);

    pack(); // Adjust dialog to components
    setResizable(false); // Make the dialog non-resizeable
    setLocationRelativeTo(parent); // Center dialog on the screen
  }

  private void initializeComponents(Mode mode) {
    setLayout(new MigLayout("", "[][grow]", "[][][grow][]"));

    // Set dialog title and description based on mode
    String title, description;
    if (mode == Mode.IMPORT) {
      title = "Import memory snapshot";
      description = "Paste a memory snapshot in Base64 format below to restore it:";
    } else {
      title = "Export memory snapshot";
      description = "Below is a memory snapshot in Base64 format. Copy it to save it:";
    }
    setTitle(title);

    add(new JLabel(description), "span, wrap");

    textArea = new JTextArea(10, 30);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true); // Enable wrapping in the middle of a word
    JScrollPane scrollPane = new JScrollPane(textArea);
    add(scrollPane, "span, grow, wrap");

    JPanel buttonPanel = new JPanel(new MigLayout("rtl"));
    if (mode == Mode.IMPORT) {
      actionButton = new JButton("Import");
      actionButton.addActionListener(
          e -> {
            confirmed = true;
            setVisible(false);
          });
      cancelButton = new JButton("Cancel");
      buttonPanel.add(cancelButton, "split 2, sg 1");
      buttonPanel.add(actionButton, "sg 1");
    } else {
      cancelButton = new JButton("Close");
      buttonPanel.add(cancelButton);
      textArea.setEditable(false);
    }
    cancelButton.addActionListener(
        e -> {
          confirmed = false;
          setVisible(false);
        });
    add(buttonPanel, "span, growx, pushx, align right");

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public String getText() {
    return textArea.getText();
  }

  public void setText(String text) {
    textArea.setText(text);
  }
}
