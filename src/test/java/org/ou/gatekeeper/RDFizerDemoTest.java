package org.ou.gatekeeper;

import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRPugliaAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.ou.gatekeeper.tlib.helpers.TestUtils.loadResource;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class RDFizerDemoTest {

  @Test
  void transformPugliaFileToFile() {
    String datasetFilename = "datasets/puglia/json/00-dataset-complete.json";
    File datasetFile = loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = FHIRPugliaAdapter.create();
    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasetFile,
      converter,
      mapping,
      outputFile
    );
  }

  @Test
  void transformPugliaFolderToFolder() throws FHIRGeneratorException, IOException {
    File batch01 = TestUtils.loadResource("datasets/puglia/batches/01");
    String[] exts = {"json"};
    Iterator<File> datasets = FileUtils.iterateFiles(batch01, exts, false);
    File outputFolder = FileUtils.getTempDirectory();

    FHIRAdapter converter = FHIRPugliaAdapter.create();
    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasets,
      converter,
      mapping,
      outputFolder
    );
  }

  @Test
  void transformPugliaBlazegraph() {
    String datasetFilename = "datasets/puglia/json/00-dataset-complete.json";
    File datasetFile = loadResource(datasetFilename);
    String endpoint = "localhost:9999";

    FHIRAdapter converter = FHIRPugliaAdapter.create();
    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasetFile,
      converter,
      mapping,
      endpoint
    );
  }

  @Test
  void transformPugliaBlazegraphBatch() throws FHIRGeneratorException, IOException {
    File batch01 = TestUtils.loadResource("datasets/puglia/batches/01");
    String[] exts = {"json"};
    Iterator<File> datasets = FileUtils.iterateFiles(batch01, exts, false);
    String endpoint = "localhost:9999";

    FHIRAdapter converter = FHIRPugliaAdapter.create();
    RMLMapping mapping = HelifitMapping.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasets,
      converter,
      mapping,
      endpoint
    );
  }

}