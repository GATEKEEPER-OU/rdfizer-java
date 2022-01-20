import java.time.Instant;

/**
 *
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */
public class Main {

  /**
   *
   * */
  public static void main(String[] args) {
    String dataset = "dataset-4.json";
    long timestamp = Instant.now().getEpochSecond();
    String output = "output-"+timestamp+".n3";
    try {
      // Formats accepted:
      // - turtle
      // - ntriples
      // - nquads
      // - jsonld
      // - trig
      // - trix
      RDFizer.parse(dataset, output, "ntriples");
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

}