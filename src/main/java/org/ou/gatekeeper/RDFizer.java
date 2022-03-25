package org.ou.gatekeeper;

import org.apache.commons.io.FileUtils;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.rdf.RDFMapper;
import org.ou.gatekeeper.rdf.ontologies.Ontology;
import org.ou.gatekeeper.rdf.stores.BlazegraphStore;
import org.ou.gatekeeper.rdf.stores.FileStore;
import org.ou.gatekeeper.rdf.stores.OutputStore;
import org.ou.gatekeeper.utils.ResourceUtils;

import java.io.File;
import java.util.Iterator;

import static org.ou.gatekeeper.utils.ResourceUtils.generateUniqueFilename;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class RDFizer {

  /**
   * Write on file
   * @todo javadoc
   * */
  public static void trasform(
    File dataset,
    FHIRAdapter converter,
    Ontology mapping,
    File output
  ) {
    OutputStore store = FileStore.create(output);
    trasform(dataset, converter, mapping, store);
  }

  /**
   * Write on Blazegraph
   * @todo javadoc
   * */
  public static void trasform(
    File dataset,
    FHIRAdapter converter,
    Ontology mapping,
    String blazeAddr
  ) {
    OutputStore store = BlazegraphStore.create(blazeAddr);
    trasform(dataset, converter, mapping, store);
  }

  /**
   * Write a batch on Blazegraph
   * @todo javadoc
   * */
  public static void trasform(
    Iterator<File> datasets,
    FHIRAdapter converter,
    Ontology mapping,
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
    Ontology mapping,
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