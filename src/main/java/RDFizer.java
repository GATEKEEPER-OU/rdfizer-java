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
 *
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */
public class RDFizer {

  static final String WORKING_DIR = "datasets";
  static final String DEFAULT_MAPPING_TEMPLATE = "mappings/mapping.template.ttl";
  static final File TMP_DIR = new File("tmp");
//  static final File TMP_DIR = FileUtils.getTempDirectory();

  static Random random = new Random();

  /**
   *
   * @param datasetFilename
   * @param outputFile
   * @param format
   * @throws Exception
   * */
  public static void parse(String datasetFilename, File outputFile, String format)
      throws Exception //, IOException
  {
    parse(datasetFilename, outputFile, format, DEFAULT_MAPPING_TEMPLATE);
  }

  /**
   *
   * @param datasetFilename
   * @param outputFile
   * @param format
   * @param mappingTemplate
   * @throws Exception
   * */
  public static void parse(String datasetFilename, File outputFile, String format, String mappingTemplate)
      throws Exception //, IOException
  {
    // Generate mapping file based on given dataset
    String tempMappingFilename = getMappingFileTempName();
    File tempMappingFile = new File(TMP_DIR, tempMappingFilename);
    InputStream mappingStream = generateMappingStream(datasetFilename, mappingTemplate, tempMappingFile);

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
    FileWriter out = new FileWriter(outputFile);
    result.write(out, format);
    out.close();

    // Remove temporary files
    FileUtils.delete(tempMappingFile);
  }

  /*
   *
   * */
  private static String getMappingFileTempName() {
    int rand = random.nextInt();
    long timestamp = Instant.now().toEpochMilli();
    return "mapping-"+timestamp+"-"+rand+".ttl";
  }

  /*
   *
   * */
  private static InputStream generateMappingStream(String datasetFilename, String mappingTemplate, File mappingFile)
      throws IOException
  {
    // Read mapping template file
    URL mappingTemplateUrl = Resources.getResource(mappingTemplate);
    File templateFile = FileUtils.toFile(mappingTemplateUrl);
    String template = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);

    // Customize mapping file with given data source
    String mappingContent = template.replace("__RML_SRC__", datasetFilename);

    // Save temporary custom mapping file
    FileUtils.writeStringToFile(mappingFile, mappingContent, StandardCharsets.UTF_8);
    InputStream mappingStream = FileUtils.openInputStream(mappingFile);
    return mappingStream;
  }

}