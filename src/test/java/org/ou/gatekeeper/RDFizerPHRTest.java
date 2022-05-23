package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
import org.commons.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.PHRAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import org.ou.gatekeeper.tlib.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class RDFizerPHRTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
    "da67f2fd35e4bb3533fa7c95a6fd7bfe9a5d0ac945e0fe77a4e1d9c90c597f84, '000,001,002',         datasets/saxony/phr/dataset-Patient.json",
    // Observations
//    "xxx, '000,001,002,006',     datasets/saxony/phr/01-dataset-complete.json", // ObservationTimeSpan
    "039715a93d34f1ce55d9f3cc69a04aeda942e6d65075d54585fab1473be85a90, '000,001,002,003,006,013', datasets/saxony/phr/dataset-BodyHeight.json",
    "45ef4b2a7f36109d1aaac43bdc00f3c845bba64327a1dd895d36b33686367cb5, '000,001,002,003,006,040', datasets/saxony/phr/dataset-BloodPressure.json",
    "b1945ca173e328938bc6f35d3416214bb9a4d56be63216cb52a232cbe2f08f86, '000,001,002,003,006,040', datasets/saxony/phr/dataset-BloodPressure-multiple.json",
    "ea997ea8e397232630e5d6ea4641f310e779661a267a356caf2862e867195d5c, '000,001,002,003,006,060', datasets/saxony/phr/dataset-Cardiovascular.json",
    "56b6d15a2ae0194cee3bbb34677ec6480fa4c36fe343fc21f4f1dee2d8925a3c, '000,001,002,003,006,060', datasets/saxony/phr/dataset-Cardiovascular-multiple.json",
    "33bd4b2db107bb8f58c84aa966be152fd724f4ea012ed319de08332765014e69, '000,001,002,003,006,070', datasets/saxony/phr/dataset-Respiration.json",
    "59bc9411703d40e591ad243e4bb1bb45e64e02978d8cb5164df996dd1a292796, '000,001,002,003,006,101', datasets/saxony/phr/dataset-Sleep.json",
    "37913fba703414073d192c23218dd8eca6b686b86d6e4858216f2db8e2874633, '000,001,002,003,006,102', datasets/saxony/phr/dataset-Walking.json"
  })
  void test_transform_FHIRtoRDF(String expectedDigest, String modules, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = PHRAdapter.create();
    String[] partsToInclude = modules.split(",");
    RMLMapping mapping = HelifitMapping.create(
      OutputFormat.NTRIPLES,
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
      ResourceUtils.clean(outputFile);
    }
  }

}