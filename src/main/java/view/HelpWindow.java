package view;

import static util.LazySwing.checkEDT;
import static util.LazySwing.inv;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.text.html.HTMLDocument;
import net.miginfocom.swing.MigLayout;
import util.LazySwing;
import util.Settings;

public class HelpWindow extends JDialog {

  public HelpWindow(JFrame parent, Settings settings) {
    checkEDT(true);
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
    helpContent.setText("<html><body><h2>Loading...</h2></body></html>");

    URL helpUrl = getClass().getResource("/help/help.html");
    ((HTMLDocument) helpContent.getDocument()).setBase(helpUrl);

    // Add a scroll pane to allow scrolling
    JScrollPane scrollPane = new JScrollPane(helpContent);
    add(scrollPane, "grow"); // Use "grow" to make the scroll pane fill the window

    updateGlobalFontSize(settings.getCurrentFontSize());
    setVisible(true);

    // Schedule a non-EDT task to load the html content
    new Thread(
            () -> {
              Path path;
              try {
                path = Paths.get(helpUrl.toURI());
                byte[] fileBytes = Files.readAllBytes(path);
                inv(
                    () -> {
                      helpContent.setText(new String(fileBytes, StandardCharsets.UTF_8));
                      helpContent.setCaretPosition(0);
                    });
              } catch (URISyntaxException | IOException e) {
                inv(
                    () ->
                        helpContent.setText(
                            "<html><body><h2>Failed to load Help content!</h2></body></html>"));
              }
            })
        .start();
  }

  public void updateGlobalFontSize(int newFontSize) {
    LazySwing.setComponentTreeFontSize(this, newFontSize);
  }

  public static void main(String[] args) {
    inv(() -> new HelpWindow(null, Settings.getDefault()));
  }
}
