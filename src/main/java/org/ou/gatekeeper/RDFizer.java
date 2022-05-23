package org.ou.gatekeeper;

import org.apache.commons.io.FileUtils;
import org.commons.FilenameUtils;
import org.commons.ResourceUtils;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.rdf.RDFMapper;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import org.ou.gatekeeper.rdf.stores.BlazegraphStore;
import org.ou.gatekeeper.rdf.stores.FileStore;
import org.ou.gatekeeper.rdf.stores.OutputStore;

import java.io.File;
import java.util.Iterator;

import static org.commons.ResourceUtils.generateUniqueFilename;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class RDFizer {

  /**
   * Reads dataset from file and writes the result on a file.
   * @todo javadoc
   * */
  public static void trasform(
    File dataset,
    FHIRAdapter converter,
    RMLMapping mapping,
    File output
  ) {
    OutputStore store = FileStore.create(output);
    trasform(dataset, converter, mapping, store);
  }

  /**
   * Reads datasets from a folder and writes the result in there.
   * @todo javadoc
   * */
  public static void trasform(
    Iterator<File> datasets,
    FHIRAdapter converter,
    RMLMapping mapping,
    File outputFolder,
    String newExtension // @todo workround
  ) {
    while (datasets.hasNext()) {
      File dataset = datasets.next();
//      String outputFilename = "output-" + dataset.getName();
      String outputFilename = "output-" + FilenameUtils
        .changeExtention(dataset.getName(), newExtension);
      File output = new File(outputFolder, outputFilename);
      OutputStore store = FileStore.create(output);
      trasform(dataset, converter, mapping, store);
    }
  }

  /**
   * Reads dataset from file and writes on Blazegraph endpoint.
   * @todo javadoc
   * */
  public static void trasform(
    File dataset,
    FHIRAdapter converter,
    RMLMapping mapping,
    String blazeAddr
  ) {
    OutputStore store = BlazegraphStore.create(blazeAddr);
    trasform(dataset, converter, mapping, store);
  }

  /**
   * Reads datasets from a folder and writes on Blazegraph endpoint.
   * @todo javadoc
   * */
  public static void trasform(
    Iterator<File> datasets,
    FHIRAdapter converter,
    RMLMapping mapping,
    String blazeAddr
  ) {
    while (datasets.hasNext()) {
      File dataset = datasets.next();
      OutputStore store = BlazegraphStore.create(blazeAddr);
      trasform(dataset, converter, mapping, store);
    }
  }

  /**
   * @todo description
   */
  public static void trasform(
    File dataset,
    FHIRAdapter converter,
    RMLMapping mapping,
    OutputStore store
  ) {
    String fhirFilename = generateUniqueFilename("output", "fhir.json");
    File tempFhirFile = new File(TMP_DIR, fhirFilename);
    converter.transform(dataset, tempFhirFile, true);
    mapping.setLocalSource(tempFhirFile.getAbsolutePath());
    RDFMapper.map(mapping, store);
    ResourceUtils.clean(tempFhirFile);
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private static final File TMP_DIR = FileUtils.getTempDirectory();
//  private static final Logger LOGGER = LoggerFactory.getLogger(RDFizer.class); // @todo

  /**
   * @todo description
   */
  private RDFizer() {
  }

}