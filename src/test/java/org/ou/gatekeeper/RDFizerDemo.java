package org.ou.gatekeeper;

import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRPugliaAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.ontologies.Ontology;
import org.ou.gatekeeper.rdf.ontologies.HelifitOntology;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.ou.gatekeeper.tlib.helpers.TestUtils.loadResource;

class RDFizerDemo {

  @Test
  void transformPugliaFileToFile() throws FHIRGeneratorException, IOException {
    String datasetFilename = "datasets/puglia/dataset-1.json";
    File datasetFile = loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath()
    );

    RDFizer.trasform(
      datasetFile,
      converter,
      mapping,
      outputFile
    );
  }

  @Test
  void transformPugliaFolderToFolder() throws FHIRGeneratorException, IOException {
    // @todo
  }

  @Test
  void transformPugliaBlazegraph() throws FHIRGeneratorException, IOException {
    String datasetFilename = "datasets/puglia/dataset-1.json";
    File datasetFile = loadResource(datasetFilename);
    String endpoint = "localhost:9999";

    FHIRAdapter converter = new FHIRPugliaAdapter();
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath()
    );

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

    FHIRAdapter converter = new FHIRPugliaAdapter();
    Ontology mapping = HelifitOntology.create(OutputFormat.NTRIPLES);

    RDFizer.trasform(
      datasets,
      converter,
      mapping,
      endpoint
    );
  }

}