package org.ou.gatekeeper.rdf;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import org.ou.gatekeeper.rdf.enums.MappingTemplate;
import org.ou.gatekeeper.rdf.enums.OutputFormat;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * */
class RDFizerTest {

  private ClassLoader classLoader = getClass().getClassLoader();

  @Test
  void storeOnFile() throws IOException {
    // Dataset file containing data to parse
    String datasetFilename = "dataset-1.json";
    String datasetPathname = classLoader.getResource(datasetFilename).getFile();
//    System.out.println("-----> datasetPathname " + datasetPathname); // DEBUG
    File datasetFile = new File(datasetPathname);

    // Output file where save parsed data
    long timestamp = Instant.now().toEpochMilli();
    String outputFilename = "output-"+timestamp+".nt";
//    System.out.println("-----> outputFilename " + RDFizer.TMP_DIR +"/"+ outputFilename); // DEBUG
    File outputFile = new File(RDFizer.TMP_DIR, outputFilename);

    RDFizer.storeOnFile(
        datasetFile,
        outputFile,
        OutputFormat.NTRIPLES,
        MappingTemplate.SAMSUNG
    );

    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
//    System.out.println("----> output file hdigest " + outputDigest); // DEBUG
    assertEquals(outputDigest, "2f8be1fa483710a63b2cf63a0bb8eba9ff2dc1a358b784ff2a189752ace2fcc9");

    // Clean test residues
    outputFile.delete();
  }

  @Test
  @Disabled("Only for live test")
  void storeOnBlazegraph() throws IOException {
    // Dataset file containing data to parse
    String datasetFilename = "dataset-1.json";
    String datasetPathname = classLoader.getResource(datasetFilename).getFile();
//    System.out.println("-----> datasetPathname " + datasetPathname); // DEBUG
    File datasetFile = new File(datasetPathname);

    // Endpoint host where store the output
    String endpointHost = "localhost:9999";

    RDFizer.storeOnBlazegraph(
        datasetFile,
        endpointHost,
        OutputFormat.NTRIPLES,
        MappingTemplate.SAMSUNG
    );
  }

  @Test
  @Disabled("Only for live test")
  void storeOnBlazegraphWithBatch() throws IOException {
    // Retrives resources
    String poolRelativePath = "pool";
    String poolPath = classLoader.getResource(poolRelativePath).getFile();

    // Dataset pool
    File workingDir = new File(poolPath);
    String[] exts = { "json" };
    Iterator<File> datasets = FileUtils.iterateFiles(workingDir, exts, false);

    // Endpoint host where store the output
    String endpointHost = "localhost:9999";

    RDFizer.storeOnBlazegraph(
        datasets,
        endpointHost,
        OutputFormat.NTRIPLES,
        MappingTemplate.SAMSUNG
    );
  }

}