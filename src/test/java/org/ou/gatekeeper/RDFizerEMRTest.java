package org.ou.gatekeeper;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.adapters.FHIRSamsungHealthAdapter;
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
class RDFizerEMRTest {

  @ParameterizedTest
  @CsvSource({
    // Patient
    "b81eff1f7ce54b94535dfde2307faea55e33752a29c220b61d35d4a65db04db0, '000,001',         datasets/puglia/fhir/dataset-Patient.json",
    // Observations
    "e57cd58c9550fd991ccd3cd7bf69e4499b1709230c33c84fed061b250411c1b6, '000,004',         datasets/puglia/fhir/00-dataset-complete.json", // ObservationTimeSpan
    "298c60e77fd6dfdd640cb76e1af707092d778a15fe774451be8d3ceb409adb2e, '000,001,004,013', datasets/puglia/fhir/dataset-BodyHeight.json",
    "e4b3b531c8ed4cabe68d2deafbbdbbdd956a38bee17b7a322a1da202c208ef7e, '000,001,004,014', datasets/puglia/fhir/dataset-BodyWeight.json",
    "afceff2351cb1ba9cf3b6363fba1d35a24934c4d5e5aa1af0f79074dec51450c, '000,001,004,040', datasets/puglia/fhir/dataset-BloodPressure.json",
    "bd29fe90eb5f69a8086043506a2d2d98a90fea46358e3383c43ba9f6bbbc2819, '000,001,004,041', datasets/puglia/fhir/dataset-GlycosilatedEmoglobin.json",
    "94a1953a8bda750f44b9fb32f983235c2f105478af0dee996febe78442efbe21, '000,001,004,042', datasets/puglia/fhir/dataset-TotalCholesterol.json",
    "7f2cdb5332472b96f21083d03206c78adc19bb48a8db461d7e7f67049c97673c, '000,001,004,043', datasets/puglia/fhir/dataset-HighDensityLipoprotein.json",
    "d32f61ad99fc54ced8d7bc52b8daf24a1f92867437ee93d35b68e0d25f7fbde4, '000,001,004,044', datasets/puglia/fhir/dataset-LowDensityLipoprotein.json",
    "78ef3afd3a5863003a4a4cb71fb055bac89d7f5002a374fc0e7b0db13b0635a5, '000,001,004,045', datasets/puglia/fhir/dataset-Triglycerides.json",
    "0810c7daf0e4dac75d2edb4328821d7f8210d9daf6544baa3fd9c7326ebf4e06, '000,001,004,046', datasets/puglia/fhir/dataset-SerumCreatinine.json",
    "baf267bea8b2dc13e7ce0586ca0f7536c075df71e168ea0dea61b73a7c85e8c8, '000,001,004,047', datasets/puglia/fhir/dataset-AlbuminuriaCreatininuriaRatio.json",
    "8d4b5e0e36840242a1b745105fb5fcc79c156aa9a3d4f49a7b539f6658284499, '000,001,004,051', datasets/puglia/fhir/dataset-AlkalinePhosphatase.json",
    "27ced78b50799352c17ca042bc190b52d37fd784fd1ee78953fbefecd9a2001e, '000,001,004,052', datasets/puglia/fhir/dataset-UricAcid.json",
    "616531d0740e0ebcbad7c95f5aba9cfe63d9b334f3b952450dbf93a13d2cf67a, '000,001,004,053', datasets/puglia/fhir/dataset-EstimatedGlomerularFiltrationRate.json",
    "60b5852add024595c535122b27d12e1c923bdddc3cd9e312ac6cc372a7d82f34, '000,001,004,054', datasets/puglia/fhir/dataset-Nitrites.json",
    // Conditions
    "f0b9327fff5fc7fe206942d39bb651afa28831d05852b428548ed51962ec7725, '000,005',     datasets/puglia/fhir/00-dataset-complete.json", // ConditionTimeSpan
    "4d363d7f6aa1c37bf1d072d1237e355e962d0f06ce2c0d665490fa89da71f15f, '000,001,005,201', datasets/puglia/fhir/dataset-HepaticSteatosis.json",
    "c8f904b3b9667b8afeb0b528b5ee635b56ed0dc5c024bec803edeeb79b8085bf, '000,001,005,202', datasets/puglia/fhir/dataset-Hypertension.json",
    "cb0bfbc48040b1b874840aa8d878dde3e8e4230350cb1390d49653df11d55963, '000,001,005,203', datasets/puglia/fhir/dataset-HeartFailure.json",
    "4d6e7b12d87f02c72f101a1cdf49fbadd01775f76916498f121868932a8ba166, '000,001,005,204', datasets/puglia/fhir/dataset-BPCO.json",
    "ed15f4e4fd1f6ae58f0fcdda45fbf1114f66655302da467ca9a9b217f2cd97a3, '000,001,005,205', datasets/puglia/fhir/dataset-ChronicKidneyDisease.json",
    "0e4739214d5e8037156250b0753ffd078ff22e61ae147c06a9f6293dfb2b496f, '000,001,005,206', datasets/puglia/fhir/dataset-IschemicHeartDisease.json",
  })
  void test_transform_FHIRtoRDF(String expectedDigest, String modules, String dataset) {
    File datasetFile = TestUtils.loadResource(dataset);
    File outputFile = TestUtils.createOutputFile("output", "nt");

    FHIRAdapter converter = FHIRSamsungHealthAdapter.create();
    String[] partsToInclude = modules.split(",");
    RMLMapping mapping = HelifitMapping.create(
      OutputFormat.NTRIPLES,
      partsToInclude,
      true
    );

    RDFizer.trasform(datasetFile, converter, mapping, outputFile);
System.out.println("---> " + outputFile);
    try {
      String outputDigest = new DigestUtils(SHA3_256).digestAsHex(outputFile);
      assertEquals(expectedDigest, outputDigest);
    } catch (IOException e) {
    } finally {
//      ResourceUtils.clean(outputFile);
    }
  }

}