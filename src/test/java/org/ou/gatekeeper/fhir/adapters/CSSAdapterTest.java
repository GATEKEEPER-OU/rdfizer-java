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
//    "xxx, keep, Patient",
//    "xxx, keep, BodyHeight",
//    "xxx, keep, BodyWeight",

    // Observations
    "xxx, keep, GlycosilatedEmoglobin",
//    "xxx, keep, TotalCholesterol",
//    "xxx, keep, HighDensityLipoprotein",
//    "xxx, keep, LowDensityLipoprotein",
//    "xxx, keep, Triglycerides",
//    "xxx, keep, SerumCreatinine",
//    "xxx, keep, AlbuminuriaCreatininuriaRatio",
//    "xxx, keep, AlkalinePhosphatase",
//    "xxx, keep, UricAcid",
//    "xxx, keep, EstimatedGlomerularFiltrationRate",
//    "xxx, keep, Nitrites",
//    "xxx, keep, BloodPressure",

    // Conditions
//    "xxx, keep, HepaticSteatosis",
//    "xxx, keep, Hypertension",
//    "xxx, keep, HeartFailure",
//    "xxx, keep, BPCO",
//    "xxx, keep, ChronicKidneyDisease",
//    "xxx, keep, IschemicHeartDisease",
  })
  void test_transform_RawToFHIR(String expectedDigest, String policy, String datasetName) {
    String sourceType = "css";
    String datasetPath = TestUtils.getDatasetPath(sourceType, datasetName);
    File datasetFile = TestUtils.loadResource(datasetPath);
    File outputFile = TestUtils.createOutputFile("output-"+datasetName, "fhir.json");

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