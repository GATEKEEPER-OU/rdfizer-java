import java.io.File;
import java.time.Instant;

/**
 * This demo shows how to use the RDFizer class.
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */

public class Main {

  public static void main(String[] args) {

    // Dataset file containing data to parse
    String dataset = "dataset-4.json";

    // Output file where save parsed data
    long timestamp = Instant.now().toEpochMilli();
    String outputFilename = "output-"+timestamp+".n3";
    File outputFile = new File(RDFizer.TMP_DIR, outputFilename);

    // Output format needed
    String format = "ntriples"; // or "turtle"

    try {
      RDFizer.parse(dataset, outputFile, format);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}