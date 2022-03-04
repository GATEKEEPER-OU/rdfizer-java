package org.ou.gatekeeper.rdf;

import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;

import org.apache.commons.io.FileUtils;
import org.ou.gatekeeper.rdf.enums.MappingTemplate;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.writers.BlazegraphWriter;
import org.ou.gatekeeper.rdf.writers.FileWriter;
import org.ou.gatekeeper.rdf.writers.OutputWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Iterator;
import java.util.Random;

/**
 * This class helps the integration of the <a href="https://github.com/RMLio/rmlmapper-java">RML Mapper</a> library.
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */
public class RDFizer {

  static Random random = new Random();
  static final File TMP_DIR = FileUtils.getTempDirectory();

  /**
   * @todo
   * */
  public static void storeOnFile(
      File dataset,
      File output,
      OutputFormat format,
      MappingTemplate template
  )
      throws IOException
  {
    OutputWriter writer = new FileWriter(output);
    store(dataset, template, format, writer);
  }

  /**
   * @todo
   * */
  public static void storeOnBlazegraph(
      Iterator<File> datasets,
      String endpoint,
      OutputFormat format,
      MappingTemplate template
  )
      throws IOException
  {
    while(datasets.hasNext()) {
      File dataset = datasets.next();
      storeOnBlazegraph(dataset, endpoint, format, template);
    }
  }

  /**
   * @todo
   * */
  public static void storeOnBlazegraph(
      File dataset,
      String endpoint,
      OutputFormat format,
      MappingTemplate template
  )
      throws IOException
  {
    OutputWriter writer = new BlazegraphWriter(endpoint);
    store(dataset, template, format, writer);
    writer.close();
  }

  /**
   * @todo
   * */
  public static void store(
      File dataset,
      MappingTemplate mappingTemplate,
      OutputFormat format,
      OutputWriter output
  )
      throws IOException
  {
    // Generate mapping file
    String tempMappingFilename = getTempFileName("mapping", "ttl");
    File tempMappingFile = new File(TMP_DIR, tempMappingFilename);
    InputStream mappingStream = generateMappingStream(dataset, mappingTemplate, tempMappingFile);

    // Init temporary output file
    String tempOutputFilename = getTempFileName("output", "dat");
    File tempOutputFile = new File(TMP_DIR, tempOutputFilename);

    // Parse
    QuadStore result = parse(mappingStream);

    // Output the result
    try {
      java.io.FileWriter out = new java.io.FileWriter(tempOutputFile);
      result.write(out, format.toString());
      out.close();

    } catch (Exception e) {
      // @todo handle Exception/Message
      e.printStackTrace();

    } finally {
      // Save parsed data
      output.save(tempOutputFile);

      // Remove temporary files
      FileUtils.delete(tempMappingFile);
    }
  }

  private RDFizer() { }

  /*
   * @param mappingStream
   * @return
   * */
  private static QuadStore parse(InputStream mappingStream) {
    // Set up the basepath for the records factory, i.e., the basepath for the (local file) data sources
    RecordsFactory factory = new RecordsFactory(TMP_DIR.getAbsolutePath());

    // Load the mapping in a QuadStore
    QuadStore rmlStore = null;
    try {
      rmlStore = QuadStoreFactory.read(mappingStream);
    } catch (Exception e) {
      // @todo handle Exception/Message
      e.printStackTrace();
    }

    // Set up the outputstore (needed when you want to output something else than nquads
    QuadStore outputStore = new RDF4JStore();

    // Create the Executor
    Executor executor = null;
    try {
      executor = new Executor(
          rmlStore,
          factory,
          null,
          outputStore,
          Utils.getBaseDirectiveTurtle(mappingStream)
      );
    } catch (Exception e) {
      // @todo handle Exception/Message
      e.printStackTrace();
    }

    // Execute the mapping
    QuadStore result = null;
    try {
      result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));
    } catch (Exception e) {
      // @todo handle Exception/Message
      e.printStackTrace();
    }

    return result;
  }

  /*
   * Generates a new mapping file to a specific dataset
   * @param dataset to map
   * @param mappingTemplate template containing the mapping rules
   * @param mappingFile map
   * @return a new FileInputStream for the specified mapping file
   * @throws IOException
   * */
  private static InputStream generateMappingStream(
      File dataset,
      MappingTemplate mappingTemplate,
      File mappingFile
  )
      throws IOException
  {
    // Read mapping template file
    InputStream mappingTemplateStream = RDFizer.class
        .getClassLoader()
        .getResourceAsStream(mappingTemplate.toString());
    String template = new String(mappingTemplateStream.readAllBytes(), StandardCharsets.UTF_8);

    // Customize mapping file with given data source
    String mappingContent = template.replace("__RML_SRC__", dataset.getAbsolutePath());

    // Save temporary custom mapping file
    FileUtils.writeStringToFile(mappingFile, mappingContent, StandardCharsets.UTF_8);
    InputStream mappingStream = FileUtils.openInputStream(mappingFile);
    return mappingStream;
  }

  /*
   * Generate a random mapping file name.
   * @param prefix
   * @param ext
   * @return
   * */
  private static String getTempFileName(String prefix, String ext) {
    int rand = random.nextInt();
    long timestamp = Instant.now().toEpochMilli();
    return prefix + "-" + timestamp + "-" + rand + "." + ext;
  }

}