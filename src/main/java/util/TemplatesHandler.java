package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Util class for reading templates from the resources folder. Templates are simply example programs
 * that can be loaded into the emulator.
 */
public class TemplatesHandler {

  private static final String TEMPLATES_PATH = "/templates/";

  private static Map<String, Path> templateMap = null;

  private TemplatesHandler() {}

  private static Map<String, Path> getTemplateMap() {
    if (templateMap == null) {
      try {
        templateMap =
            Arrays.stream(
                    Objects.requireNonNull(
                        new File(TemplatesHandler.class.getResource(TEMPLATES_PATH).toURI())
                            .listFiles()))
                .map(File::toPath) // Convert File to Path
                .collect(
                    Collectors.toMap(
                        path -> {
                          // Process the file name to create a readable name
                          String name = path.getFileName().toString();
                          name = name.substring(0, name.lastIndexOf('.'));
                          name = name.replace('_', ' ');
                          name = name.substring(0, 1).toUpperCase() + name.substring(1);
                          return name;
                        },
                        path -> path // Use the Path object itself as the map value
                        ));
      } catch (URISyntaxException e) {
        e.printStackTrace();
        templateMap = Collections.emptyMap();
      }
    }
    return templateMap;
  }

  public static List<String> getTemplateNames() {
    return getTemplateMap().keySet().stream().sorted().collect(Collectors.toList());
  }

  public static String[] getTemplate(String name) {
    Path path = getTemplateMap().get(name);
    if (path == null) {
      return new String[0];
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
      String[] lines =
          reader
              .lines()
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
      System.out.println("Template read successfully: " + name + " (" + lines.length + " lines");
      return lines;
    } catch (Exception e) {
      System.out.println("Error reading template: " + name);
      e.printStackTrace();
      return new String[0];
    }
  }
}
