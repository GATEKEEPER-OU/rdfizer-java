package org.ou.gatekeeper;

import lib.tests.helpers.RDFoxUtils;
import lib.tests.helpers.TestUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.commons.ResourceUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.SamsungHealthAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.mappings.HelifitMapping;
import org.ou.gatekeeper.rdf.mappings.RMLMapping;
import tech.oxfordsemantic.jrdfox.client.ConnectionFactory;
import tech.oxfordsemantic.jrdfox.client.DataStoreConnection;
import tech.oxfordsemantic.jrdfox.client.ServerConnection;
import tech.oxfordsemantic.jrdfox.exceptions.JRDFoxException;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class KnowledgeGraphTest {

  static final String ONTOLOGY = "ontologies/HeLiFit-OWL-Functional-Syntax.owl";

  static final String RDFOX_HOST = "rdfox:local";
  static final String TEST_ROLE = "aRole";
  static final String TEST_PASSWORD = "aPassword";
  static final String DATASTORE_NAME = "GK-DataStore";
  ServerConnection serverConnection = null;


  @BeforeAll
  void setUpClass() throws JRDFoxException {
    RDFoxUtils.startLocalServer(TEST_ROLE, TEST_PASSWORD);
    serverConnection = ConnectionFactory.newServerConnection(RDFOX_HOST, TEST_ROLE, TEST_PASSWORD);
    RDFoxUtils.createDatastore(serverConnection, DATASTORE_NAME, ONTOLOGY);
  }

  @BeforeEach
  void setUp() {
    // delete clean datastore
  }

  @ParameterizedTest
  @CsvSource({
    "xxx, keep, samsung, FloorClimbed",
//    "xxx, keep, css, GlycosilatedEmoglobin",
  })
  void test_knowledgeGraph(
    String expectedDigest, String policy, String sourceType, String datasetName
  ) throws JRDFoxException {
    String datasetPath = getDatasetPath(sourceType, datasetName);
    File   datasetFile = TestUtils.loadResource(datasetPath);

    OutputFormat tripleFormat = OutputFormat.TURTLE;
    File triplesFile = TestUtils.createOutputFile(datasetName+"-triples", "turtle");

    String queryPath = getQueryPath(sourceType, datasetName);
    File   queryFile = TestUtils.loadResource(queryPath);
    String queryText = ResourceUtils.readFileToString(queryFile);
    File  outputFile = TestUtils.createOutputFile(datasetName+"-output", "turtle");

    FHIRAdapter converter = SamsungHealthAdapter.create();
    RMLMapping    mapping = HelifitMapping.create(tripleFormat);
    RDFizer.trasform(datasetFile, converter, mapping, triplesFile);

    try (DataStoreConnection dataStoreConnection = serverConnection.newDataStoreConnection(DATASTORE_NAME)) {
      RDFoxUtils.importData(dataStoreConnection, triplesFile);
      RDFoxUtils.saveQueryResults(dataStoreConnection, queryText, outputFile);
    }

    try {
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      if (policy.equals("keep")) {
        System.out.println("triplesFile >>> " + triplesFile);
        System.out.println("outputFile >>> " + outputFile);
      } else { // policy: clean
        ResourceUtils.clean(triplesFile);
        ResourceUtils.clean(outputFile);
      }
    }
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  static final String DATASET_PATH_TEMPLATE = "datasets/%s/raw/%s.json";
  static final String QUERY_PATH_TEMPLATE = "queries/%s.txt";

  private static String getDatasetPath(String sourceType, String datasetName) {
    return String.format(DATASET_PATH_TEMPLATE, sourceType, datasetName);
  }

  private static String getQueryPath(String sourceType, String queryFilename) {
    return String.format(QUERY_PATH_TEMPLATE, sourceType, queryFilename);
  }

}