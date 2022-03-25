package org.ou.gatekeeper.fhir.helpers;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.helpers.FHIRNormalizer;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FHIRNormalizerTest {

  @Test
  void testNormalizeFHIR() {
    String datasetFilename = "datasets/fhir/dataset-1.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nomalized.fhir.json");

    FHIRNormalizer.normalize(datasetFile, outputFile);

    try {
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      System.out.println(" -> testNormalizeFHIR().hdigest: " + outputDigest); // DEBUG
      assertEquals("6591c5580c6a14ba8654836952be10784c5d796e50f0b199ed6854409c72548f", outputDigest);
    } catch (IOException e) {
    } finally {
      outputFile.delete();
    }
  }

}