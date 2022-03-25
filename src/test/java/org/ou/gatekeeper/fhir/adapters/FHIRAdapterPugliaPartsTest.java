package org.ou.gatekeeper.fhir.adapters;

import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FHIRAdapterPugliaPartsTest {

  private static ClassLoader classLoader = TestUtils.class.getClassLoader();

  // -------------------------------------------------------------------------//
  // Patient
  // -------------------------------------------------------------------------//

  @Test
  void testTransformPatient() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/parts/01-dataset-patient.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

    TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
    System.out.println(" -> testTransformPatient().hdigest: " + outputDigest); // DEBUG
    assertEquals(outputDigest, "ec7e2ebf7b2b3a27521e410e215e8623e8736c109388d92a2a2d7ae04658d582");

    // Clean test residues
    outputFile.delete();
  }

  // -------------------------------------------------------------------------//
  // Observations
  // -------------------------------------------------------------------------//

  @Test
  void testTransformGlycosilatedEmoglobin() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/parts/02-dataset-GlycosilatedEmoglobin.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

    TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
    TestUtils.removeAllLinesFromFile(outputFile, "reference");
    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
    System.out.println(" -> testTransformGlycosilatedEmoglobin().hdigest: " + outputDigest); // DEBUG
    assertEquals(outputDigest, "9ba90282ea2fb8eaf011499267d5719599058df7dc7f0a7613c39c53803fab3a");

    // Clean test residues
    outputFile.delete();
  }

  @Test
  void testTransformTotalCholesterol() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/parts/03-dataset-TotalCholesterol.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

    TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
    TestUtils.removeAllLinesFromFile(outputFile, "reference");
    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
    System.out.println(" -> testTransformTotalCholesterol().hdigest: " + outputDigest); // DEBUG
    assertEquals(outputDigest, "23e5f03321134105d7065a681c4f5190a20a7719c7109efbbc86c33e0b978822");

    // Clean test residues
    outputFile.delete();
  }

  // @todo other Observations


  @Test
  void testTransformBloodPressure() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/parts/0x-dataset-BloodPressure.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

    TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
    TestUtils.removeAllLinesFromFile(outputFile, "reference");
    TestUtils.removeAllLinesFromFile(outputFile, "valueString");
    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
    System.out.println(" -> testTransformBloodPressure().hdigest: " + outputDigest); // DEBUG
    assertEquals(outputDigest, "308bb6721ebd98e06c8000bd4dee84fc1fc927b0188a5c51a68c777fd70232bd");

    // Clean test residues
    outputFile.delete();
  }

  // @todo other Observations

  // -------------------------------------------------------------------------//
  // Conditions
  // -------------------------------------------------------------------------//

  @Test
  void testTransformHepaticSteatosis() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/parts/0x-dataset-HepaticSteatosis.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

    TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
    TestUtils.removeAllLinesFromFile(outputFile, "reference");
    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
    System.out.println(" -> testTransformHepaticSteatosis().hdigest: " + outputDigest); // DEBUG
    assertEquals(outputDigest, "a49f1b8b4734f1039edd62b0b80b57a8379d8e42aff8a99df1df1dc4dafd2902");

    // Clean test residues
    outputFile.delete();
  }

  @Test
  void testTransformHypertension() throws IOException, FHIRGeneratorException {
    String datasetFilename = "datasets/puglia/parts/0x-dataset-Hypertension.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "json");

    FHIRAdapter converter = new FHIRPugliaAdapter();
    converter.transform(datasetFile, outputFile);

    TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
    TestUtils.removeAllLinesFromFile(outputFile, "reference");
    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
    System.out.println(" -> testTransformHypertension().hdigest: " + outputDigest); // DEBUG
    assertEquals(outputDigest, "b6e2bdc6e238be810eb86329408f64e835be782b28b2d4ae7aaf8986cc06f67a");

    // Clean test residues
    outputFile.delete();
  }

  // @todo other Conditions

}