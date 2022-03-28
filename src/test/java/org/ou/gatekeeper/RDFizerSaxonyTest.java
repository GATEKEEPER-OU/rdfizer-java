package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRSaxonyAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.ontologies.HelifitOntology;
import org.ou.gatekeeper.rdf.ontologies.Ontology;
import org.ou.gatekeeper.tlib.helpers.TestUtils;
import org.ou.gatekeeper.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class RDFizerSaxonyTest {

  @ParameterizedTest
  @CsvSource({
    "a6925b5772779ae25a5d151998c83569980370cd8f16a6dd9aa1e0a2cf8a57f5, '000,001',         datasets/saxony/fhir/dataset-Patient.json",
    "33d46e48e91c439863de3085e11190a02971d6821db4cb260de62c610273c22b, '000,001,004',     datasets/saxony/fhir/01-dataset-complete.json",
    "c6a47e838736ef4479bc854b43d2215a0c206f7b5b5f31f4726061155dec97ca, '000,001,004,013', datasets/saxony/fhir/dataset-BodyHeight.json",
    "4859a4534df8ce6ea07c2fb7c7119d3a8bb4adc5378e92b71dca33cd2ec3ba2b, '000,001,004,040', datasets/saxony/fhir/dataset-BloodPressure.json",
    "e3b6bd1f01c44a203faa17887b07f7722820d6fdaab3fe3617fa48f0100a3629, '000,001,004,040', datasets/saxony/fhir/dataset-BloodPressure-multiple.json",
    "3957970f90bfb94db8db133860adfedcd870e62253a8dc796fb75d26b74e80d1, '000,001,004,060', datasets/saxony/fhir/dataset-Cardiovascular.json",
    "71685f66381dc42ca96475b6e07cb1c9fff86edd6138fd9bec6469f8f4a8cafe, '000,001,004,060', datasets/saxony/fhir/dataset-Cardiovascular-multiple.json",
    "e1dfea4d870be89663585cbe38f4cd1473ec825e70e62972c7a63f6ba8c8df6a, '000,001,004,070', datasets/saxony/fhir/dataset-Respiration.json",
    "ab154f87d37a6acac6ecc251c3cefdb019a03a9174672178cbb9db1e233027c2, '000,001,004,101', datasets/saxony/fhir/dataset-Sleep.json",
    "415f6bf235865579131a27116d439b9bb3aeeb5a7eb120df9d2cf627a96b2b9b, '000,001,004,102', datasets/saxony/fhir/dataset-Walking.json"
  })
  void test_transform_FHIRtoRDF(String expectedDigest, String modules, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRSaxonyAdapter();
    String[] partsToInclude = modules.split(",");
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath(),
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