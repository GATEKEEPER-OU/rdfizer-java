package org.ou.gatekeeper.fhir.adapters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class SamsungHealthAdapterTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
//    "xxx, datasets/samhealth/raw/Patient.json",

    // Observations
//    "xxx, datasets/samhealth/raw/FloorClimbed.json",
//    "xxx, datasets/samhealth/raw/StepDailyTrend.json",
//    "xxx, datasets/samhealth/raw/HeartRate.json",
//    "xxx, datasets/samhealth/raw/Walking.json",
//    "xxx, datasets/samhealth/raw/Running.json",
//    "xxx, datasets/samhealth/raw/Swimming.json",
//    "xxx, datasets/samhealth/raw/Sleep.json",

    // Complete datasets
//    "xxx, datasets/samhealth/raw/00-dataset-complete.json"
  })
  void test_transform_RawtoFHIR(String expectedDigest, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
    File outputFile = TestUtils.createOutputFile("output", "fhir.json");
    File outputNormFile = TestUtils.createOutputFile("output", "normalized.fhir.json");

    FHIRAdapter converter = SamsungHealthAdapter.create();
    converter.transform(datasetFile, outputFile);
    converter.transform(datasetFile, outputNormFile, true);

    try {
      // Before evalutate, it must be eliminated outo-generated values (uuids, etc...)
      // because they will change each run
//      TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
//      TestUtils.removeAllLinesFromFile(outputFile, "reference");
//      TestUtils.removeAllLinesFromFile(outputFile, "valueString");

      // Evaluation
//      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
//      assertEquals(expectedDigest, outputDigest);

//    } catch (IOException e) {
    } finally {
        System.out.println("outputFile >>> " + outputFile);
//      ResourceUtils.clean(outputFile);
    }
  }

}