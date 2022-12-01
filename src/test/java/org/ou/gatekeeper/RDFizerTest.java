package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
import org.commons.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.fhir.adapters.CSSAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.SamsungHealthAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import lib.tests.helpers.TestUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class RDFizerTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
//    "e2562d1d74cae9e2e4477cc42518f5fc76829357e7424d9de6699f522211e64d, keep, '0000,0001,0002', datasets/phr/fhir/Patient.json",

    // Observations
//    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,2101', datasets/samsung/raw/FloorClimbed.json",
    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,2102,0010,2001,2002,2003,2004', datasets/samsung/raw/StepDailyTrend.json",
//    "xxx, keep, '0000,0001,0002,0020,0021,...', datasets/samsung/raw/HeartRate.json",
//    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002,3003,3004,3005,3006,3007', datasets/samsung/raw/Walking.json",
//    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002', datasets/samsung/raw/Running.json",
//    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002', datasets/samsung/raw/Cycling.json",
//    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002', datasets/samsung/raw/Swimming.json",
//    "xxx, keep, '0000,0001,0002,0020,0021,...', datasets/samsung/raw/Sleep.json",

  })
  /*
   * NOTES
   *   @param modules can be a list of prefixes separated by commas, or the label 'all'
   *   RECOMMENDATION:
   *   - use 'all' label is discoraged into selective tests, because it will produce
   *     many false-positive warnings, make it hard to debug
   * */
  void test_transform_SAMtoRDF(String expectedDigest, String policy, String modules, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
//    OutputFormat outputFormat = OutputFormat.NTRIPLES;
//    File outputFile = TestUtils.createOutputFile("output", "nt");
    OutputFormat outputFormat = OutputFormat.TURTLE;
    File outputFile = TestUtils.createOutputFile("output", "turtle");

    FHIRAdapter converter = SamsungHealthAdapter.create();
    String[] partsToInclude = !modules.equals("all") ? modules.split(",") : null;
    RMLMapping mapping = HelifitMapping.create(outputFormat, partsToInclude, true);
    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
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

  @ParameterizedTest
  @CsvSource({
    // Patient
//    "e2562d1d74cae9e2e4477cc42518f5fc76829357e7424d9de6699f522211e64d, keep, '0000,0001,0002', datasets/phr/fhir/Patient.json",

//    "xxx, keep, all, datasets/css/raw/.....json",
//    "xxx, keep, '0000,0001,0002,0020,0021,0040,0041,...', datasets/css/raw/....json",
  })
    /*
     * NOTES
     *   @param modules can be a list of prefixes separated by commas, or the label 'all'
     *   RECOMMENDATION:
     *   - use 'all' label is discoraged into selective tests, because it will produce
     *     many false-positive warnings, make it hard to debug
     * */
  void test_transform_CSStoRDF(String expectedDigest, String policy, String modules, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
//    OutputFormat outputFormat = OutputFormat.NTRIPLES;
//    File outputFile = TestUtils.createOutputFile("output", "nt");
    OutputFormat outputFormat = OutputFormat.TURTLE;
    File outputFile = TestUtils.createOutputFile("output", "turtle");

    FHIRAdapter converter = CSSAdapter.create();
    String[] partsToInclude = !modules.equals("all") ? modules.split(",") : null;
    RMLMapping mapping = HelifitMapping.create(outputFormat, partsToInclude, true);
    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
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