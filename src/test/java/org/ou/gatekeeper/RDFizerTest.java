package org.ou.gatekeeper;

import lib.tests.helpers.TestUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.commons.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;

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
    //
    // CSS
    // -------------------------------------------------------------------------

    // Patient
//    "e2562d1d74cae9e2e4477cc42518f5fc76829357e7424d9de6699f522211e64d, keep, CSS, Patient, '0000,0001,0002'",

    // Observations
//    "xxx, keep, CSS, .., all",
//    "xxx, keep, CSS, GlycosilatedEmoglobin, '0000,0001,0002,0020,0021,0040,0041,...'",

    //
    // Samsung Health
    // -------------------------------------------------------------------------

    // Patient
//    "e2562d1d74cae9e2e4477cc42518f5fc76829357e7424d9de6699f522211e64d, keep, SH, Patient, '0000,0001,0002'",

    // Observations
    "xxx, keep, SH, FloorsClimbed, '0000,0001,0002,0020,0021,0040,0041,2101'",
//    "xxx, keep, SH, StepDailyTrend, '0000,0001,0002,0020,0021,0040,0041,2102,0010,2001,2002,2003,2004'",
//    "xxx, keep, SH, HeartRate, '0000,0001,0002,0020,0021,...'",
//    "xxx, keep, SH, Walking,   '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002,3003,3004,3005,3006,3007'",
//    "xxx, keep, SH, Running,   '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002'",
//    "xxx, keep, SH, Cycling,   '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002'",
//    "xxx, keep, SH, Swimming,  '0000,0001,0002,0020,0021,0040,0041,3101,0010,3001,3002'",
//    "xxx, keep, SH, Sleep,     '0000,0001,0002,0020,0021,...'",

  })
  /*
   * NOTES
   *   @param modules can be a list of prefixes separated by commas, or the label 'all'
   *   RECOMMENDATION:
   *   - use 'all' label is discoraged into selective tests, because it will produce
   *     many false-positive warnings, make it hard to debug
   * */
  void test_transform_RawtoRDF(String expectedDigest, String policy, String sourceType, String datasetName, String modules) {
    String datasetPath = TestUtils.getDatasetPath(sourceType, datasetName);
    File datasetFile = TestUtils.loadResource(datasetPath);
//    OutputFormat outputFormat = OutputFormat.NTRIPLES;
//    File outputFile = TestUtils.createOutputFile("output", "nt");
    OutputFormat outputFormat = OutputFormat.TURTLE;
    File outputFile = TestUtils.createOutputFile("output", "turtle");

    FHIRAdapter converter = TestUtils.getFHIRAdapter(sourceType);
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
      } else if (policy.equals("clean")) {
        ResourceUtils.clean(outputFile);
      } else {
        throw new IllegalArgumentException("Only 'keep' or 'clean' policies allowed");
      }
    }
  }

}