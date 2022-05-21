package org.ou.gatekeeper.rdf;

import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.NamedNode;
import org.apache.commons.io.FileUtils;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import org.ou.gatekeeper.rdf.stores.OutputStore;
import org.commons.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * This class helps the integration of the <a href="https://github.com/RMLio/rmlmapper-java">RML Mapper</a> library.
 *
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
public class RDFMapper {

  /**
   * @todo description
   */
  public static void map(
    RMLMapping mapping,
    OutputStore store
  ) {
    String rmlRules = mapping.getRML();
//    System.out.println(rmlRules); // DEBUG
    try (
      InputStream mappingStream = new ByteArrayInputStream(rmlRules.getBytes());
    ){
      // Map ontology in a quad store
      QuadStore quad = map(mappingStream);
      if (quad != null) {
        // Write the result in a temporary file
        String tempOutputFilename = ResourceUtils.generateUniqueFilename("output", "dat");
        File tempOutputFile = new File(TMP_DIR, tempOutputFilename);
        String format = mapping.getFormat().toString();
        writeOnFile(quad, format, tempOutputFile);

        // Save the output on a OutputStore
        store.save(tempOutputFile);
      }

    } catch (IOException e) {
      LOGGER.error("---> @todo TO THIS WAY 1", e); // @todo
      e.printStackTrace();
    }
  }

  /**
   * @param mappingStream
   * @return
   * @todo description
   */
  public static QuadStore map(InputStream mappingStream) {
    // Set up the basepath for the records factory, i.e., the basepath for the (local file) data sources
    RecordsFactory factory = new RecordsFactory(TMP_DIR.getAbsolutePath());

    // Load the mapping in a QuadStore
    QuadStore rmlStore = null;
    try {
      rmlStore = QuadStoreFactory.read(mappingStream);

    } catch (Exception e) {
      LOGGER.error("---> @todo TO THIS WAY 2", e); // @todo
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
      LOGGER.error("---> @todo TO THIS WAY 3", e); // @todo
      e.printStackTrace();
    }

    // Execute the mapping
    QuadStore result = null;
    try {
      if (executor != null) {
        result = executor.executeV5(null).get(new NamedNode("rmlmapper://default.store"));
      }

    } catch (Exception e) {
      LOGGER.error("---> @todo TO THIS WAY 4", e); // @todo
      e.printStackTrace();
    }

    return result;
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private static final File TMP_DIR = FileUtils.getTempDirectory();
  private static final Logger LOGGER = LoggerFactory.getLogger(RDFMapper.class);

  /**
   * @todo description
   */
  private RDFMapper() {
  }

  //--------------------------------------------------------------------------//
  // Private methods
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private static void writeOnFile(
    QuadStore quadStore,
    String format,
    File output
  ) {
    try (
      Writer file = new FileWriter(output)
    ) {
      quadStore.write(file, format);

    } catch (IOException e) {
      // @todo Message
      e.printStackTrace();
    } catch (Exception e) {
      // @todo Message
      e.printStackTrace();
    }
  }

}