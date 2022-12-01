package lib.tests.helpers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class TestUtils {

  /**
   * @todo description
   */
  public static File loadResource(String filename) {
    String pathname = classLoader.getResource(filename).getFile();
//    System.out.println("-----> loadResource().pathname: " + pathname); // DEBUG
    return new File(pathname);
  }

  /**
   * @todo description
   */
  public static File createOutputFile(String prepend, String ext) {
    long timestamp = Instant.now().toEpochMilli();
    String outputFilename = prepend + "-" + timestamp + "." + ext;
//    System.out.println("-----> outputFilename " + RDFizer.TMP_DIR +"/"+ outputFilename); // DEBUG
    return new File(TMP_DIR, outputFilename);
  }

  /**
   * @todo description
   */
  public static void removeAllLinesFromFile(File file, String needle) throws IOException {
    List<String> out = Files.lines(file.toPath())
      .filter(line -> !line.contains(needle))
      .collect(Collectors.toList());
    Files.write(file.toPath(), out, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
  }

  private static ClassLoader classLoader = TestUtils.class.getClassLoader();
  private static final File TMP_DIR = FileUtils.getTempDirectory();

}