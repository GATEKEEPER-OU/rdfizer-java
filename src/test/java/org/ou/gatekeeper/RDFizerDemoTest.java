package org.ou.gatekeeper;

import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.EMRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.PHRAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;

import static org.ou.gatekeeper.tlib.helpers.TestUtils.loadResource;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class RDFizerDemoTest {

  @Test
  void transformPHRFileToFile() {
    String datasetFilename = "datasets/saxony/phr/01-dataset-complete.json";
    File datasetFile = loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = PHRAdapter.create();
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
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = EMRAdapter.create();
    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasetFile,
      converter,
      mapping,
      outputFile
    );
  }

}