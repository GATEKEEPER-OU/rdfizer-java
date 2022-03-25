package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRDefaultAdapter;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
import org.ou.gatekeeper.rdf.ontologies.HelifitOntology;
import org.ou.gatekeeper.rdf.ontologies.Ontology;
import org.ou.gatekeeper.tlib.helpers.TestUtils;
import org.ou.gatekeeper.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RDFizerDefaultTest {

  @Test
  void test_transform_Patient_FHIRtoRDF() {
    String datasetFilename = "datasets/fhir/parts/dataset-Patient.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRDefaultAdapter();
    String[] partsToInclude = { "000", "001" };
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath(),
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
      String expectedDigest = "a6925b5772779ae25a5d151998c83569980370cd8f16a6dd9aa1e0a2cf8a57f5";
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
      ResourceUtils.clean(outputFile);
    }
  }

  @Test
  void test_transform_TimeSpan_FHIRtoRDF() {
    String datasetFilename = "datasets/fhir/parts/00-dataset-complete.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRDefaultAdapter();
    String[] partsToInclude = { "000", "001", "004" };
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath(),
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
      String expectedDigest = "33d46e48e91c439863de3085e11190a02971d6821db4cb260de62c610273c22b";
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
      ResourceUtils.clean(outputFile);
    }
  }

  @Test
  void test_transform_BodyHeight_FHIRtoRDF() {
    String datasetFilename = "datasets/fhir/parts/dataset-BodyHeight.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRDefaultAdapter();
    String[] partsToInclude = { "000", "001", "004", "013" };
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath(),
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
      String expectedDigest = "";
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
//      ResourceUtils.clean(outputFile);
    }
  }

  @Test
  @Disabled
  void test_transform_BodyWeight_FHIRtoRDF() {
    String datasetFilename = "datasets/fhir/parts/dataset-BodyWeight.json";
    File datasetFile = TestUtils.loadResource(datasetFilename);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = new FHIRDefaultAdapter();
    String[] partsToInclude = { "000", "001", "004", "014" };
    Ontology mapping = HelifitOntology.create(
      OutputFormat.NTRIPLES,
      datasetFile.getAbsolutePath(),
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);

    try {
      String expectedDigest = "";
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
      ResourceUtils.clean(outputFile);
    }
  }

}