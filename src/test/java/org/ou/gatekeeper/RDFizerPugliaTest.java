package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRDefaultAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.ontologies.HelifitOntology;
import org.ou.gatekeeper.rdf.ontologies.Ontology;
import org.ou.gatekeeper.tlib.helpers.TestUtils;
import org.ou.gatekeeper.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RDFizerPugliaTest {


  // ...

  @Test
  @Disabled("It needs to be fixed (TimeSpan: resource.effectiveDateTime)")
  void test_transform_BodyWeight_FHIRtoRDF() {
    String datasetFilename = "datasets/puglia/fhir/dataset-BodyWeight.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRDefaultAdapter();
    String[] partsToInclude = { "000", "001", "004", "014" };
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath(),
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
      String expectedDigest = "";
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
      ResourceUtils.clean(outputFile);
    }
  }



  // ...




}