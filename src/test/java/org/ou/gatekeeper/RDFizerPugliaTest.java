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
class RDFizerPugliaTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
    "e2562d1d74cae9e2e4477cc42518f5fc76829357e7424d9de6699f522211e64d, '000,001,002',         datasets/puglia/phr/dataset-Patient.json",
    // Observations
//    "xxx, '000,004',         datasets/puglia/phr/00-dataset-complete.json", // ObservationTimeSpan
    "02b570df01108fa4dd3c5ad4ba508cd044e99a18d64d9eba3cb8cd8f3b874c68, '000,001,002,003,006,013', datasets/puglia/phr/dataset-BodyHeight.json",
    "732bb1092a859dc46f1ae0a088b3b9f8705ee746301cc72b4cbfc9469de923ee, '000,001,002,003,006,014', datasets/puglia/phr/dataset-BodyWeight.json",
    "f457d739a5e84fe5fed01908e0e05253000f90fc9163530ae103d34337335ac1, '000,001,002,003,004,006,040', datasets/puglia/phr/dataset-BloodPressure.json",
    "8a00cadfdc171c179b1a89e0f188c8a0ce7ad368cbb1d73e4fb8b4a25d17b7cc, '000,001,002,003,006,041', datasets/puglia/phr/dataset-GlycosilatedEmoglobin.json",
    "e8e1ba3e6b75a7947a376a61e7fb663645130827dd86c887213c87402fcac736, '000,001,002,003,006,042', datasets/puglia/phr/dataset-TotalCholesterol.json",
    "03522517bc66f4bb2bd989915d7c317ce96015c4c63973a1001fefb8d354808f, '000,001,002,003,006,043', datasets/puglia/phr/dataset-HighDensityLipoprotein.json",
    "9ba54a8a83545fd403e02825c68c5c4a5d6093c600e239c2c8f7997b7eb9f04a, '000,001,002,003,006,044', datasets/puglia/phr/dataset-LowDensityLipoprotein.json",
    "4ebac8038691fa735a1fb2762256461bdfa070f8bfb2c6b254461360949d92ed, '000,001,002,003,006,045', datasets/puglia/phr/dataset-Triglycerides.json",
    "91e7207e7fe879f22b57378e8aa13da9a763e2ec3ad5f6321c2da6032042ee98, '000,001,002,003,006,046', datasets/puglia/phr/dataset-SerumCreatinine.json",
    "01bfd8f8311092464cf6b33b859c9567895dbb1ee3b18345d3ae9ffa48505dd1, '000,001,002,003,006,047', datasets/puglia/phr/dataset-AlbuminuriaCreatininuriaRatio.json",
    "572aaeab656acdf20aa51f0ee123125f1d3a198ad59daf67e5f866103d219e29, '000,001,002,003,006,051', datasets/puglia/phr/dataset-AlkalinePhosphatase.json",
    "9d11877440b8e7c3c1c7b9e3fa654694242a9e3575b91cee4fe7ca261f533ba0, '000,001,002,003,006,052', datasets/puglia/phr/dataset-UricAcid.json",
    "5b55d64293d710c793fe4660a884f54e80fa35240c6bf1504db6255e611f2eda, '000,001,002,003,006,053', datasets/puglia/phr/dataset-EstimatedGlomerularFiltrationRate.json",
    "076752f9c21884c746386fd84745499a56ee8aed757b7549174597c43db89149, '000,001,002,003,006,054', datasets/puglia/phr/dataset-Nitrites.json",
    // Conditions
//    "xxx, '000,005',     datasets/puglia/phr/00-dataset-complete.json", // ConditionTimeSpan
    "d7252d3b7c2bdc134b646375c2bb3e6df778df1fee0dec857eff28cf2b9e2375, '000,001,002,003,007,206', datasets/puglia/phr/dataset-IschemicHeartDisease.json",
  })
  void test_transform_PHRtoRDF(String expectedDigest, String modules, String dataset) {
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