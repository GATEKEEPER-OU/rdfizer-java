package org.ou.gatekeeper.fhir.adapters.css;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.type.code.BundleType;
import com.ibm.fhir.model.visitor.Visitable;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ou.gatekeeper.fhir.adapters.FHIRAdapter;
import org.ou.gatekeeper.fhir.helpers.FHIRNormalizer;
import org.commons.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;

import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;
import static org.ou.gatekeeper.fhir.adapters.css.CSSBuilder.*;
import static org.commons.ResourceUtils.generateUniqueFilename;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class CSSAdapter implements FHIRAdapter {

  /**
   * @todo description
   */
  public static FHIRAdapter create() {
    return new CSSAdapter();
  }

  /**
   * @todo description
   */
  public void transform(
    File dataset,
    File output
  ) {
    transform(dataset, output, false);
  }

  /**
   * @todo description
   */
  public void transform(
    File dataset,
    File output,
    boolean normalize
  ) {
    try (InputStream datasetInputStream = new FileInputStream(dataset)) {
      // Parse JSON dataset
      JSONTokener tokenizer = new JSONTokener(datasetInputStream);
      JSONObject json = new JSONObject(tokenizer);

      // Collect all entries
      Collection<Bundle.Entry> entries = new LinkedList<>();
      siftExaminations(json, entries);

      // Build FHIR bundle
      Visitable bundle = Bundle.builder()
        .entry(entries)
        .type(BundleType.TRANSACTION)
        .build();

      // Save FHIR bundle in a JSON file
      String tempFilename = generateUniqueFilename("output", "tmp.fhir.json");
      File tempOutputFile = new File(TMP_DIR, tempFilename);
      save(bundle, tempOutputFile);

      // Normalize dataset, if requested
      if (normalize) {
        FHIRNormalizer.normalize(tempOutputFile, output);
        ResourceUtils.clean(tempOutputFile);
      } else {
        Files.move(tempOutputFile.toPath(), output.toPath());
      }

    } catch (FileNotFoundException e) {
      // @todo Message
      e.printStackTrace();
    } catch (IOException e) {
      // @todo Message
      e.printStackTrace();
    }
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private static final File TMP_DIR = FileUtils.getTempDirectory();

  /**
   * @todo description
   */
  protected CSSAdapter() {}

  /**
   * @todo description
   */
  private static void siftExaminations(
    JSONObject json,
    Collection<Bundle.Entry> entries
  ) {
    JSONArray examinations = json.getJSONArray("clinical_examinations");
    for (int i = 0; i < examinations.length(); ++i) {
      JSONObject examination = examinations.getJSONObject(i);

      // Build and collect entries from input JSON
      Bundle.Entry patientEntry = collectPatient(entries, examination);
      Bundle.Entry patientAgeEntry = collectPatientAge(entries, examination, patientEntry);
      collectObservations(entries, examination, patientEntry, patientAgeEntry);
      collectConditions(entries, examination, patientEntry);
    }
  }

  /**
   * @todo description
   */
  private static void save(Visitable bundle, File output) {
    try (
      Writer file = new FileWriter(output)
    ) {
      FHIRGenerator
        .generator(Format.JSON, true)
        .generate(bundle, file);

    } catch (IOException e) {
      // @todo Message
      e.printStackTrace();
    } catch (FHIRGeneratorException e) {
      // @todo Message
      e.printStackTrace();
    }
  }

  /**
   * @todo description
   */
  private static Bundle.Entry collectPatient(
    Collection<Bundle.Entry> entries,
    JSONObject examination
  ) {
    JSONObject    patientJson = examination.getJSONObject("patient");
    Bundle.Entry patientEntry = buildPatient(patientJson);
    addIgnoreNull(entries, patientEntry);
    return patientEntry;
  }

  private static Bundle.Entry collectPatientAge(
    Collection<Bundle.Entry> entries,
    JSONObject examination,
    Bundle.Entry patientEntry
  ) {
    Bundle.Entry ageEntry = buildObservationAge(examination, patientEntry);
    addIgnoreNull(entries, ageEntry);
    return ageEntry;
  }

  private static void collectObservations(
    Collection<Bundle.Entry> entries,
    JSONObject examination,
    Bundle.Entry patientEntry,
    Bundle.Entry patientAgeEntry
  ) {
    addIgnoreNull(entries,
      buildObservationBodyHeight(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationBodyWeight(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationGlycosilatedEmoglobin(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationTotalCholesterol(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationHDL(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationLDL(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationTriglycerides(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationTotalCholesterolHDL(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationSerumCreatinine(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationAlbuminuriaCreatininuriaRatio(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationALT(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationAST(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationGammaGT(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationAlkalinePhosphatase(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationUricAcid(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationGFR(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationNitrites(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationBloodPressure(examination, patientEntry, patientAgeEntry));
    addIgnoreNull(entries,
      buildObservationYearsWithDiabetes(examination, patientEntry, patientAgeEntry));
  }

  private static void collectConditions(
    Collection<Bundle.Entry> entries,
    JSONObject examination,
    Bundle.Entry patientEntry
  ) {
    addIgnoreNull(entries,
      buildConditionHepaticSteatosis(examination, patientEntry));
    addIgnoreNull(entries,
      buildConditionHypertension(examination, patientEntry));
    addIgnoreNull(entries,
      buildConditionHeartFailure(examination, patientEntry));
    addIgnoreNull(entries,
      buildConditionBPCO(examination, patientEntry));
    addIgnoreNull(entries,
      buildConditionChronicKidneyDisease(examination, patientEntry));
    addIgnoreNull(entries,
      buildConditionIschemicHeartDisease(examination, patientEntry));
  }

}