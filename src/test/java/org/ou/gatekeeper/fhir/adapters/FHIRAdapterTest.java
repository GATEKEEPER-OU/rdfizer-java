package org.ou.gatekeeper.fhir.adapters;

import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

class FHIRAdapterTest {

  private static ClassLoader classLoader = TestUtils.class.getClassLoader();

  @Test
  void transformPugliaToFHIR() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/dataset-1.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

//    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
//    System.out.println("----> testParsingCode().hdigest: " + outputDigest); // DEBUG
//    assertEquals(outputDigest, "032b2fdb9ddab43600fb47908926eec15897d295099ece7bcbf1a0d76cd4efa5");

    // Clean test residues
//    outputFile.delete();
  }

}