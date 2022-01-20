import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */
public class RDFizer {

  static final String DEFAULT_MAPPING_TEMPLATE = "mappings/mapping.template.ttl";
  static final String TMP_MAPPING_FILENAME = "tmp/mapping.tmp.ttl";
  static final String WORKING_DIR = "datasets";

  /**
   *
   * */
  public static void parse(String datasetFilename, String outputFilename, String format)
      throws Exception //, IOException
  {
    parse(datasetFilename, outputFilename, format, DEFAULT_MAPPING_TEMPLATE);
  }

  /**
   *
   * */
  public static void parse(String datasetFilename, String outputFilename, String format, String mappingFilename)
      throws Exception //, IOException
  {
    InputStream mappingStream = generateMappingFile(datasetFilename, DEFAULT_MAPPING_TEMPLATE);

    // Set up the basepath for the records factory, i.e., the basepath for the (local file) data sources
    File mappingFile = new File(TMP_MAPPING_FILENAME);
    RecordsFactory factory = new RecordsFactory(WORKING_DIR);

    // Load the mapping in a QuadStore
    QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

    // Set up the outputstore (needed when you want to output something else than nquads
    QuadStore outputStore = new RDF4JStore();

    // Create the Executor
    Executor executor = new Executor(rmlStore, factory, null, outputStore, Utils.getBaseDirectiveTurtle(mappingStream));

    // Execute the mapping
    QuadStore result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));

    // Output the result
    File outputFile = new File(outputFilename); // @todo finire
//    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
    FileWriter out = new FileWriter(outputFile);
    result.write(out, format);
    out.close();
  }

  private static InputStream generateMappingFile(String datasetFilename, String mappingFilename)
      throws IOException
  {
    // Read template
    URL mappingUrl = Resources.getResource(mappingFilename);
    File templateFile = FileUtils.toFile(mappingUrl);
    String template = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);

    // Customize mapping file with given data source
    String mappingContent = template.replace("__RML_SRC__", datasetFilename);

    // Save temporary custom mapping
    File mappingFile = new File(TMP_MAPPING_FILENAME);
    FileUtils.writeStringToFile(mappingFile, mappingContent, StandardCharsets.UTF_8);
    InputStream mappingStream = FileUtils.openInputStream(mappingFile);
    return mappingStream;
  }

}