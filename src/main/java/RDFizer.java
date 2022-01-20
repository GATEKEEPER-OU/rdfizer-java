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
import java.time.Instant;
import java.util.Random;

/**
 * This class helps the integration of the <a href="https://github.com/RMLio/rmlmapper-java">RML Mapper</a> library.
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */
public class RDFizer {

  static final String WORKING_DIR = "datasets";
  static final String DEFAULT_MAPPING_TEMPLATE = "mappings/mapping.template.ttl";
  static final File TMP_DIR = new File("tmp");
//  static final File TMP_DIR = FileUtils.getTempDirectory();

  static Random random = new Random();

  /**
   * Parse a dataset using the default mapping rules.
   * @param dataset path of a dataset to parse
   * @param output file where save the results
   * @param format format of the output
   *   <ul><li>turtle</li>
   *       <li>ntriples</li>
   *       <li>nquads</li>
   *       <li>jsonld</li>
   *       <li>trig</li>
   *       <li>trix</li><ul>
   * */
  public static void parse(String dataset, File output, String format)
      throws Exception //, IOException
  {
    parse(dataset, output, format, DEFAULT_MAPPING_TEMPLATE);
  }

  /**
   * Parse a dataset using specific mapping rules given.
   * @param dataset path of a dataset to parse
   * @param output file where save the results
   * @param format format of the output
   * @param mappingTemplate mapping rules template path
   * */
  public static void parse(String dataset, File output, String format, String mappingTemplate)
      throws Exception //, IOException
  {
    // Generate mapping file based on given dataset
    String tempMappingFilename = getMappingFileTempName();
    File tempMappingFile = new File(TMP_DIR, tempMappingFilename);
    InputStream mappingStream = generateMappingStream(dataset, mappingTemplate, tempMappingFile);

    // Set up the basepath for the records factory, i.e., the basepath for the (local file) data sources
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
    FileWriter out = new FileWriter(output);
    result.write(out, format);
    out.close();

    // Remove temporary files
    FileUtils.delete(tempMappingFile);
  }

  /*
   * Generate a random mapping file name.
   * */
  private static String getMappingFileTempName() {
    int rand = random.nextInt();
    long timestamp = Instant.now().toEpochMilli();
    return "mapping-"+timestamp+"-"+rand+".ttl";
  }

  /*
   * Generates a new mapping file to a specific dataset (described by datasetPath) starting from a mapping template
   * @param datasetPath path of the dataset to replace in the template
   * @param mappingTemplate template containing the mapping rules
   * @param mappingFile map
   * @return a new FileInputStream for the specified mapping file
   * */
  private static InputStream generateMappingStream(String datasetPath, String mappingTemplate, File mappingFile)
      throws IOException
  {
    // Read mapping template file
    URL mappingTemplateUrl = Resources.getResource(mappingTemplate);
    File templateFile = FileUtils.toFile(mappingTemplateUrl);
    String template = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);

    // Customize mapping file with given data source
    String mappingContent = template.replace("__RML_SRC__", datasetPath);

    // Save temporary custom mapping file
    FileUtils.writeStringToFile(mappingFile, mappingContent, StandardCharsets.UTF_8);
    InputStream mappingStream = FileUtils.openInputStream(mappingFile);
    return mappingStream;
  }

}