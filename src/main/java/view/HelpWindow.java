package view;

import java.io.IOException;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;

public class HelpWindow extends JFrame {

  public HelpWindow(JFrame parent) {
    setTitle("Help");
    setSize(800, 600);
    setLocationRelativeTo(parent);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // Set up the layout manager
    setLayout(new MigLayout("fill"));

    // Create the JEditorPane to display HTML content
    JEditorPane helpContent = new JEditorPane();
    helpContent.setEditable(false); // Make it non-editable
    helpContent.setContentType("text/html"); // Set the content type as HTML

    // Load HTML file
    try {
      URL helpURL = getClass().getResource("/help/help.html"); // Adjust path as necessary
      helpContent.setPage(helpURL);
    } catch (IOException e) {
      helpContent.setText("<html><body><h2>Failed to load Help content!</h2></body></html>");
    }

    // Add a scroll pane to allow scrolling
    JScrollPane scrollPane = new JScrollPane(helpContent);
    add(scrollPane, "grow"); // Use "grow" to make the scroll pane fill the window

    setVisible(true);
  }
}
