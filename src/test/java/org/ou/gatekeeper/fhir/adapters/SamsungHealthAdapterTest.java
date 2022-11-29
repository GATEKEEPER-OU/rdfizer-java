package org.ou.gatekeeper.fhir.adapters;

import org.apache.commons.codec.digest.DigestUtils;
import org.commons.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class SamsungHealthAdapterTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
//    "xxx, keep, datasets/samhealth/raw/Patient.json",

    // Observations
    "xxx, keep, datasets/samhealth/raw/FloorClimbed.json",
//    "xxx, keep, datasets/samhealth/raw/StepDailyTrend.json",
//    "xxx, keep, datasets/samhealth/raw/HeartRate.json",
//    "xxx, keep, datasets/samhealth/raw/Walking.json",
//    "xxx, keep, datasets/samhealth/raw/Swimming.json",
//    "xxx, keep, datasets/samhealth/raw/Sleep.json",

    // Complete datasets
//    "xxx, datasets/phr/raw/00-dataset-complete.json"
  })
  void test_transform_RawToFHIR(String expectedDigest, String policy, String dataset) {
    File    datasetFile = TestUtils.loadResource(dataset);
    File     outputFile = TestUtils.createOutputFile("output", "fhir.json");

    FHIRAdapter converter = SamsungHealthAdapter.create();
    converter.transform(datasetFile, outputFile);

    File outputNormFile = TestUtils.createOutputFile("output", "norm.json");
    converter.transform(datasetFile, outputNormFile, true);

    try {
      // Before evalutate, it must be eliminated outo-generated values (uuids, etc...)
      // because they will change each run
      // TODO id
//      TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
//      TestUtils.removeAllLinesFromFile(outputFile, "reference");
//      TestUtils.removeAllLinesFromFile(outputFile, "valueString");

      // Evaluation
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      if (policy.equals("keep")) {
        System.out.println("outputFile >>> " + outputFile);
      } else { // policy: clean
        ResourceUtils.clean(outputFile);
      }
    }
  }

}