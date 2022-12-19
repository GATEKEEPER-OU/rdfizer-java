package org.apache.ant.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
    uniquePrefixChecker(ttls);

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
    // Collection<File> parts = FileUtils.listFiles(resourceDir, exts, true); // NOTE at this stage maven dependencies can't be used here
    Collection<File> parts = listFiles(resourceDir, exts);
    return parts.stream()
      .sorted(Comparator.comparing(File::getName))
      .toList();
  }

  private static Collection<File> listFiles(File rootDir, String[] exts) {
    List<File> files = new LinkedList<>();
    File[] dirContent = rootDir.listFiles();
    for(File file : dirContent) {
      if (file.isDirectory()) {
        Collection<File> subdirContent = listFiles(file, exts);
        files.addAll(subdirContent);
      } else {
        String filename = file.getName();
        String ext = getExtension(filename);
        if (Arrays.asList(exts).contains(ext)) {
          files.add(file);
        }
      }
    }
    return files;
  }

  private static String getExtension(String filename) {
    int lastIndexOf = filename.lastIndexOf(".");
    if (lastIndexOf == -1) {
      return ""; // empty extension
    }
    return filename.substring(lastIndexOf + 1);
  }

  private static void uniquePrefixChecker(List<File> files) {
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