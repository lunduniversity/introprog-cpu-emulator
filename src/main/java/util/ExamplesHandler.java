package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Util class for reading examples from the resources folder. These are example programs that can be
 * loaded into the emulator via the GUI menu.
 */
public class ExamplesHandler {

  private static final String EXAMPLES_DIR = "/examples/";
  private static final String EXAMPLES_LIST_FILE = EXAMPLES_DIR + "_example_list";

  private static Map<String, String[]> exampleMap = null;

  private ExamplesHandler() {}

  private static Map<String, String[]> getExampleMap() {
    if (exampleMap == null) {
      // Read examples in one of two ways:
      // 1. If running from from the source via Gradle, e.g. in your IDE or terminal, list the files
      // in the examples dir and load them.

      try {
        URI uri = ExamplesHandler.class.getResource(EXAMPLES_DIR).toURI();
        // Check if uri points to a file (directory), or just a resource in a JAR
        if (uri.getScheme().equals("file")) {
          exampleMap =
              Files.list(Paths.get(uri))
                  .filter(Files::isRegularFile)
                  .filter(path -> path.getFileName().toString().endsWith(".txt"))
                  .collect(
                      Collectors.toMap(
                          path -> nameFromPath(path.getFileName().toString()),
                          path -> {
                            // Load the file from the file system
                            try (BufferedReader reader =
                                new BufferedReader(new FileReader(path.toFile()))) {
                              return reader.lines().toArray(String[]::new);
                            } catch (Exception e) {
                              return new String[0];
                            }
                          }));
        }
      } catch (URISyntaxException | IOException e) {
        // Ignore
        exampleMap = null;
      }

      // 2. If running from a JAR, files in examples dir cannot be listed. Instead, first read the
      // special file that contains their names, and then load them explicitly.
      if (exampleMap == null) {
        // Read the list of examples from the _example_list file using getResourceAsStream
        try (BufferedReader reader =
            new BufferedReader(
                new InputStreamReader(
                    ExamplesHandler.class.getResourceAsStream(EXAMPLES_LIST_FILE)))) {
          Map<String, String[]> tmpMap = new HashMap<>();
          String line;
          while ((line = reader.readLine()) != null) {
            URL fileURL = ExamplesHandler.class.getResource(EXAMPLES_DIR + line);
            if (fileURL != null) {
              try (BufferedReader fileReader =
                  new BufferedReader(new InputStreamReader(fileURL.openStream()))) {
                tmpMap.put(nameFromPath(line), fileReader.lines().toArray(String[]::new));
              }
            }
          }
          exampleMap = tmpMap;
        } catch (Exception e) {
          // Ignore
          exampleMap = null;
        }
      }

      if (exampleMap == null) {
        exampleMap = Collections.emptyMap();
      }
    }
    return exampleMap;
  }

  private static String nameFromPath(final String filename) {
    // Process the file name to create a readable name
    String name = filename;
    name = name.substring(0, name.lastIndexOf('.'));
    name = name.replace('_', ' ');
    name = name.substring(0, 1).toUpperCase() + name.substring(1);
    return name;
  }

  public static List<String> getExampleNames() {
    return getExampleMap().keySet().stream().sorted().collect(Collectors.toList());
  }

  public static String[] getExample(String name) {
    String[] lines =
        Arrays.stream(getExampleMap().get(name))
            .map(s -> s.split("(//|#|%)", 2)[0])
            .map(s -> s.replace(" ", ""))
            .toArray(String[]::new);
    // verify that each line contains only 0s and 1s, and is 8 characters long
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.length() != 8 || !line.matches("[01]+")) {
        throw new IllegalArgumentException(
            String.format("Invalid file format at line %d: '%s'", (i + 1), line));
      }
    }
    return lines;
  }
}
