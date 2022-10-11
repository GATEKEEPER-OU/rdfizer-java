package org.ou.gatekeeper;

import org.apache.commons.io.FileUtils;
import org.commons.ResourceUtils;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.rdf.RDFMapper;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;

import java.io.File;

import static org.commons.ResourceUtils.generateUniqueFilename;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class RDFizer {

  /**
   * Reads dataset from file and writes the result on a file.
   * @param input file to convert
   * @param converter the adapter that understands the input dataset
   * @param mapping RML mapping file which contains mapping rules
   * @param output RDF output file
   * */
  public static void trasform(
    File input,
    FHIRAdapter converter,
    RMLMapping mapping,
    File output
  ) {
    String fhirFilename = generateUniqueFilename("output", "json");
//    String fhirFilename = generateUniqueFilename("output", "fhir.json");
    File tempFhirFile = new File(TMP_DIR, fhirFilename);
    converter.transform(input, tempFhirFile, true);
    mapping.setLocalSource(tempFhirFile.getAbsolutePath());
    RDFMapper.map(mapping, output);
    ResourceUtils.clean(tempFhirFile);
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private static final File TMP_DIR = FileUtils.getTempDirectory();
//  private static final Logger LOGGER = LoggerFactory.getLogger(RDFizer.class); // @todo

  /**
   * This class is not instantiable
   */
  private RDFizer() {
  }

}