package org.ou.gatekeeper;

import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.CSSAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.SHAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import lib.tests.helpers.TestUtils;

import java.io.File;

import static lib.tests.helpers.TestUtils.loadResource;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class RDFizerDemoTest {

  @Test
  void transformPHRFileToFile() {
    String datasetFilename = "datasets/saxony/phr/01-dataset-complete.json";
    File datasetFile = loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = SHAdapter.create();
    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasetFile,
      converter,
      mapping,
      outputFile
    );
  }

  @Test
  void transformEMRFileToFile() {
    String datasetFilename = "datasets/puglia/emr/00-dataset-complete.json";
    File datasetFile = loadResource(datasetFilename);
//    File outputFile = TestUtils.createOutputFile("output", "nt");
    File outputFile = TestUtils.createOutputFile("output", "turtle");

    FHIRAdapter converter = CSSAdapter.create();
//    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);
    RMLMapping mapping = HelifitMapping.create(OutputFormat.TURTLE);

    RDFizer.trasform(
      datasetFile,
      converter,
      mapping,
      outputFile
    );
  }

}