<<<<<<< Updated upstream
package rdf;
=======
package org.ou.gatekeeper.rdf;
>>>>>>> Stashed changes

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.*;

<<<<<<< Updated upstream
import rdf.enums.MappingTemplate;
import rdf.enums.OutputFormat;
=======
import org.ou.gatekeeper.rdf.enums.MappingTemplate;
import org.ou.gatekeeper.rdf.enums.OutputFormat;
>>>>>>> Stashed changes

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
    String outputFilename = "output-"+timestamp+".n3";
//    System.out.println("-----> outputFilename " + RDFizer.TMP_DIR +"/"+ outputFilename); // DEBUG
    File outputFile = new File(RDFizer.TMP_DIR, outputFilename);

    RDFizer.storeOnFile(
        datasetFile,
        outputFile,
        OutputFormat.NTRIPLES,
        MappingTemplate.SAMSUNG
    );

    String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
<<<<<<< Updated upstream
    assertEquals(outputDigest, "e6060a0cff4a5a765380d812b1bce73fa940e2a4abb1218c2bf72312");
    System.out.println("----> output file hdigest " + outputDigest); // DEBUG
=======
//    System.out.println("----> output file hdigest " + outputDigest); // DEBUG
    assertEquals(outputDigest, "2f8be1fa483710a63b2cf63a0bb8eba9ff2dc1a358b784ff2a189752ace2fcc9");
>>>>>>> Stashed changes

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
  void storeOnBlazegraphBatch() throws IOException {
    // Retrives resources
    String poolRelativePath = "pool";
    String poolPath = classLoader.getResource(poolRelativePath).getFile();

    // Dataset pool
    File workingDir = new File(poolPath);
    String[] exts = { "json" };
    Iterator<File> datasets = FileUtils.iterateFiles(workingDir, exts, false);

    String endpointHost = "localhost:9999";

    RDFizer.storeOnBlazegraph(
        datasets,
        endpointHost,
        OutputFormat.NTRIPLES,
        MappingTemplate.SAMSUNG
    );
  }

}