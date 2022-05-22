package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
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
//    "da67f2fd35e4bb3533fa7c95a6fd7bfe9a5d0ac945e0fe77a4e1d9c90c597f84, '000,001,002',         datasets/saxony/phr/dataset-Patient.json",
    // Observations
//    "xxx, '000,001,002,006',     datasets/saxony/phr/01-dataset-complete.json", // ObservationTimeSpan
//    "039715a93d34f1ce55d9f3cc69a04aeda942e6d65075d54585fab1473be85a90, '000,001,002,003,006,013', datasets/saxony/phr/dataset-BodyHeight.json",
    "xxx, '000,001,002,003,006,040', datasets/saxony/phr/dataset-BloodPressure.json",
//    "xxx, '000,001,002,003,006,040', datasets/saxony/phr/dataset-BloodPressure-multiple.json",
//    "xxx, '000,001,002,003,006,060', datasets/saxony/phr/dataset-Cardiovascular.json",
//    "xxx, '000,001,002,003,006,060', datasets/saxony/phr/dataset-Cardiovascular-multiple.json",
//    "xxx, '000,001,002,003,006,070', datasets/saxony/phr/dataset-Respiration.json",
//    "xxx, '000,001,002,003,006,101', datasets/saxony/phr/dataset-Sleep.json",
//    "xxx, '000,001,002,003,006,102', datasets/saxony/phr/dataset-Walking.json"
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
//      ResourceUtils.clean(outputFile); // @todo uncomment this when tests completed
    }
  }

}