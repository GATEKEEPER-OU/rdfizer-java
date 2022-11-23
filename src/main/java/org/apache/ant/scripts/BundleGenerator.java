package org.apache.ant.scripts;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    checkerUniquePrefix(ttls);

    StringBuilder helifitRml = new StringBuilder();
    for (File ttl : ttls) {
      String ttlContent = Files.readString(ttl.toPath());
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
    String[] exts = new String[]{ ext };
    Collection<File> parts = FileUtils.listFiles(resourceDir, exts, true);
    return parts.stream()
      .sorted(Comparator.comparing(File::getName))
      .toList();
  }

  private static void checkerUniquePrefix(List<File> files) {
    HashMap<String, String> prefixes = new HashMap<>();
    for (File file: files) {
      String filename = file.getName();
      String[] parts = filename.split("_");
      if (parts.length < 2) {
        throw new IllegalArgumentException("Part filename should have prefix");
      }
      String prefix = parts[0];
      if (prefixes.containsKey(prefix)) {
        String message = String.format("Prefix %s already in use for '%s'", prefix, filename);
        throw new IllegalArgumentException(message);
      }
      prefixes.put(prefix, filename);
    }
  }

}