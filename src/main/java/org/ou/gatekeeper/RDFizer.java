package org.ou.gatekeeper;

import org.apache.commons.io.FileUtils;
import org.commons.FilenameUtils;
import org.commons.ResourceUtils;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.rdf.RDFMapper;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;

import java.io.File;
import java.util.Iterator;

import static org.commons.ResourceUtils.generateUniqueFilename;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class RDFizer {

  /**
   * Reads datasets from a folder and writes the result in there.
   * @param datasets list of file to convert
   * @param converter the adapter that understands the input dataset
   * @param mapping RML mapping file which contains mapping rules
   * @param outputFolder folder where save the RDF output files
   * @param newExtension extension of output files {@link org.ou.gatekeeper.rdf.enums.OutputFormat}
   * */
  @Deprecated
  public static void trasform(
    Iterator<File> datasets,
    FHIRAdapter converter,
    RMLMapping mapping,
    File outputFolder,
    String newExtension // @todo workround
  ) {
    while (datasets.hasNext()) {
      File dataset = datasets.next();
      String trimmedDatasetName = FilenameUtils.trim2LvlExtension(dataset.getName());
      String outputFilename = "output-" + FilenameUtils
        .changeExtension(trimmedDatasetName, newExtension);
      File output = new File(outputFolder, outputFilename);
      trasform(dataset, converter, mapping, output);
    }
  }

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
    String fhirFilename = generateUniqueFilename("output", "fhir.json");
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