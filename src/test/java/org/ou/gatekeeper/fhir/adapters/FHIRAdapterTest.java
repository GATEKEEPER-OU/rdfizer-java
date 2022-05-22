package org.ou.gatekeeper.fhir.adapters;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.tlib.helpers.TestUtils;
import org.commons.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 */
class FHIRAdapterTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
//    "ec7e2ebf7b2b3a27521e410e215e8623e8736c109388d92a2a2d7ae04658d582, datasets/puglia/json/dataset-Patient.json",
//    "xxx, datasets/puglia/json/dataset-BodyHeight.json",
//    "xxx, datasets/puglia/json/dataset-BodyWeight.json",

    // Observations
//    "9ba90282ea2fb8eaf011499267d5719599058df7dc7f0a7613c39c53803fab3a, datasets/puglia/json/dataset-GlycosilatedEmoglobin.json",
//    "23e5f03321134105d7065a681c4f5190a20a7719c7109efbbc86c33e0b978822, datasets/puglia/json/dataset-TotalCholesterol.json",
//    "xxx, datasets/puglia/json/dataset-HighDensityLipoprotein.json",
//    "xxx, datasets/puglia/json/dataset-LowDensityLipoprotein.json",
//    "xxx, datasets/puglia/json/dataset-Triglycerides.json",
//    "xxx, datasets/puglia/json/dataset-SerumCreatinine.json",
//    "xxx, datasets/puglia/json/dataset-AlbuminuriaCreatininuriaRatio.json",
//    "xxx, datasets/puglia/json/dataset-AlkalinePhosphatase.json",
//    "xxx, datasets/puglia/json/dataset-UricAcid.json",
//    "xxx, datasets/puglia/json/dataset-EstimatedGlomerularFiltrationRate.json",
//    "xxx, datasets/puglia/json/dataset-Nitrites.json",
//    "7de1e7a0994a5c5eb9ff3dd39a96ca6859dd686d085af68b6eef45ff2b33af1d, datasets/puglia/json/dataset-BloodPressure.json",

    // Conditions
//    "a49f1b8b4734f1039edd62b0b80b57a8379d8e42aff8a99df1df1dc4dafd2902, datasets/puglia/json/dataset-HepaticSteatosis.json",
//    "xxx, datasets/puglia/json/dataset-Hypertension.json",
//    "xxx, datasets/puglia/json/dataset-HeartFailure.json",
//    "xxx, datasets/puglia/json/dataset-BPCO.json",
//    "xxx, datasets/puglia/json/dataset-ChronicKidneyDisease.json",
//    "xxx, datasets/puglia/json/dataset-IschemicHeartDisease.json",

    // Complete dataset
//    "31d1dac57cc6e24d8adea296f7d5ee1ee1b31b0ab884b31388e74f80522a9a72, datasets/puglia/json/00-dataset-complete.json"
  })
  void test_transform_JSONtoFHIR(String expectedDigest, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
    File outputFile = TestUtils.createOutputFile("output", "fhir.json");

    FHIRAdapter converter = new EMRAdapter();
    converter.transform(datasetFile, outputFile);

    try {
      TestUtils.removeAllLinesFromFile(outputFile, "fullUrl");
      TestUtils.removeAllLinesFromFile(outputFile, "reference");
      TestUtils.removeAllLinesFromFile(outputFile, "valueString");
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
        ResourceUtils.clean(outputFile);
    }
  }

}