package org.ou.gatekeeper.fhir.adapters;

import org.apache.commons.codec.digest.DigestUtils;
import org.commons.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import lib.tests.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class CSSAdapterTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
//    "xxx, datasets/css/raw/Patient.json",
//    "xxx, datasets/css/raw/BodyHeight.json",
//    "xxx, datasets/css/raw/BodyWeight.json",

    // Observations
    "xxx, keep, datasets/css/raw/GlycosilatedEmoglobin.json",
//    "xxx, datasets/css/raw/TotalCholesterol.json",
//    "xxx, datasets/css/raw/HighDensityLipoprotein.json",
//    "xxx, datasets/css/raw/LowDensityLipoprotein.json",
//    "xxx, datasets/css/raw/Triglycerides.json",
//    "xxx, datasets/css/raw/SerumCreatinine.json",
//    "xxx, datasets/css/raw/AlbuminuriaCreatininuriaRatio.json",
//    "xxx, datasets/css/raw/AlkalinePhosphatase.json",
//    "xxx, datasets/css/raw/UricAcid.json",
//    "xxx, datasets/css/raw/EstimatedGlomerularFiltrationRate.json",
//    "xxx, datasets/css/raw/Nitrites.json",
//    "xxx, datasets/css/raw/BloodPressure.json",

    // Conditions
//    "xxx, datasets/css/raw/HepaticSteatosis.json",
//    "xxx, datasets/css/raw/Hypertension.json",
//    "xxx, datasets/css/raw/HeartFailure.json",
//    "xxx, datasets/css/raw/BPCO.json",
//    "xxx, datasets/css/raw/ChronicKidneyDisease.json",
//    "xxx, datasets/css/raw/IschemicHeartDisease.json",

    // Complete dataset
//    "xxx, datasets/emr/raw/00-complete.json"
  })
  void test_transform_RawToFHIR(String expectedDigest, String policy, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
    File outputFile = TestUtils.createOutputFile("output", "fhir.json");

    FHIRAdapter converter = new CSSAdapter();
    converter.transform(datasetFile, outputFile);

    try {
      TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
      TestUtils.removeAllLinesFromFile(outputFile, "reference");
      TestUtils.removeAllLinesFromFile(outputFile, "valueString");
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