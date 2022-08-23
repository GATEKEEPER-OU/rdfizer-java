package org.apache.ant.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class BundleGenerator {

  public static final Path RESOURCES_PATH = Path.of("src", "main", "resources");

  public static void main(String[] args) throws IOException {
    String ontologyName = args[0];
    Path ontologyPath = Path.of(RESOURCES_PATH.toString(), "mappings", ontologyName);
    Path ontologyPartsPath = Path.of(ontologyPath.toString(), "parts");

    List<File> ttls = getResourceFiles(ontologyPartsPath, "ttl");

    StringBuilder helifitRml = new StringBuilder();
    for (File ttl : ttls) {
      String ttlContent = readFileToString(ttl);
      helifitRml
        .append(ttlContent)
        .append("\n\n");
    }

    File ontologyFile = new File(ontologyPath.toFile(), "helifit.template.ttl");
    Files.writeString(ontologyFile.toPath(), helifitRml.toString());
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private BundleGenerator() {
  }

  private static List<File> getResourceFiles(Path path, String ext) {
    File resourceDir = new File(path.toUri());
    return Stream.of(Objects.requireNonNull(resourceDir.listFiles()))
      .filter(file -> !file.isDirectory())
      .filter(file -> file.getName().endsWith(ext))
      .sorted(Comparator.comparing(File::getName))
      .toList();
  }

  public static String readFileToString(File file) {
    try {
      return Files.readString(file.toPath());

    } catch (IOException e) {
      // @todo Message: resource not found
      e.printStackTrace();
    }

    return null;
  }

}