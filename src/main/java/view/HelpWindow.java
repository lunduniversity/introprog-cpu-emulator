package view;

import static util.LazySwing.inv;

import java.io.IOException;
import java.net.URL;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import net.miginfocom.swing.MigLayout;
import util.LazySwing;
import util.Settings;

public class HelpWindow extends JDialog {

  public HelpWindow(JFrame parent, Settings settings) {
    setTitle("Help");
    setSize(800, 600);
    setLocationRelativeTo(parent);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setAlwaysOnTop(true);

    // Set up the layout manager
    setLayout(new MigLayout("fill"));

    JEditorPane helpContent = new JEditorPane();
    helpContent.setEditable(false); // Make it non-editable
    helpContent.setContentType("text/html"); // Set the content type as HTML
    helpContent.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

    // Load HTML file
    try {
      URL helpURL = getClass().getResource("/help/help.html");
      helpContent.setPage(helpURL);
    } catch (IOException e1) {
      helpContent.setText("<html><body><h2>Failed to load Help content!</h2></body></html>");
    }

    // Add a scroll pane to allow scrolling
    JScrollPane scrollPane = new JScrollPane(helpContent);
    add(scrollPane, "grow"); // Use "grow" to make the scroll pane fill the window

    inv(
        () -> {
          updateGlobalFontSize(settings.getCurrentFontSize());
          setVisible(true);
        });
  }

  public void updateGlobalFontSize(int newFontSize) {
    LazySwing.setComponentTreeFontSize(this, newFontSize);
  }
}
