package org.ou.gatekeeper.fhir.adapters.builders;

import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Identifier;
import com.ibm.fhir.model.type.Uri;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.LinkedList;

import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FHIRPuglia extends FHIRBase {

  /**
   * @todo description
   */
  public static Bundle.Entry buildPatient(JSONObject patient) {
    String baseUrl = "https://www.gatekeeper-project.eu/sid/puglia";
    String patientId = patient.getString("patient_id");
    return buildEntry(
      Patient.builder()
        .identifier(
          Identifier.builder()
            .system(Uri.uri(baseUrl + "/patient"))
            .value(patientId)
            .build()
        )
        .build(),
      "Patient",
      "identifier=" + baseUrl + "/patient|" + patientId
    );
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationGlycosilatedEmoglobin(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "59261-8",
            "Hemoglobin A1c/Hemoglobin.total in Blood by IFCC protocol"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("glycosilated_emoglobin")),
          "millimole per mole", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("glycosilated_emoglobin_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationTotalCholesterol(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "2093-3",
            "Cholesterol [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("total_cholesterol")),
          "millimole per mole", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("total_cholesterol_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationHDL(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "2085-9",
            "Cholesterol in HDL [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("HDL")),
          "milligram per deciliter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("HDL_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationLDL(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "2089-1",
            "Cholesterol in LDL [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("LDL")),
          "milligram per deciliter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("LDL_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationTriglycerides(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "2571-8",
            "Triglyceride [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("triglycerides")),
          "milligram per deciliter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("triglycerides_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  // @todo TC_HDL ask

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationSerumCreatinine(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "2160-0",
            "Creatinine [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("serum_creatinine")),
          "milligram per deciliter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("serum_creatinine_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationAlbuminuriaCreatininuriaRatio(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "14959-1",
            "Microalbumin/Creatinine [Mass Ratio] in Urine"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("albuminuria_creatininuria_ratio")),
          "milligram per gram", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("albuminuria_creatininuria_ratio_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationALT(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "1742-6",
            "Alanine aminotransferase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("GPT_ALT")),
          "enzyme unit per liter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("GPT_ALT_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationAST(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "1920-8",
            "Aspartate aminotransferase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("GOT_AST")),
          "enzyme unit per liter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("GOT_AST_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationGammaGT(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "2324-2",
            "Gamma glutamyl transferase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("gammaGT")),
          "international unit per liter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("gammaGT_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationAlkalinePhosphatase(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "6768-6",
            "Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("alkaline_phosphatase")),
          "international unit per liter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("alkaline_phosphatase_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationUricAcid(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "3084-1",
            "Urate [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("uric_acid")),
          "milligram per deciliter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("uric_acid_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationGFR(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "48642-3",
            "Glomerular filtration rate/1.73 sq M.predicted among non-blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (MDRD)"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("eGFR")),
          examination.getString("eGFR_unit"),
          "http://unitsofmeasure.org",
          examination.getString("eGFR_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationNitrites(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "laboratory",
            "Laboratory"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "5802-4",
            "Nitrite [Presence] in Urine by Test strip"
          )
        ),
        examination.getBoolean("nitrites"),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationBloodPressure(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      Bundle.Entry observationEntry = buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "vital-signs",
            "Vital Signs"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "85354-9",
            "Blood pressure panel with all children optional"
          )
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );

      Collection<Observation.Component> components = new LinkedList<>();
      addIgnoreNull(components,
        buildComponentSystolicPressure(examination));
      addIgnoreNull(components,
        buildComponentDiastolicPressure(examination));

      return observationEntry.toBuilder()
        .resource(
          observationEntry.getResource().as(Observation.class).toBuilder()
            .component(components)
            .build()
        )
        .build();

    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Observation.Component buildComponentSystolicPressure(JSONObject examination) {
    return buildObservationComponent(
      buildCodeableConcept(
        buildCoding(
          "http://loinc.org/",
          "8480-6",
          "Systolic blood pressure"
        )
      ),
      buildQuantity(
        Decimal.of(examination.getInt("systolic_pressure")),
        "millimeter of mercury",
        "http://unitsofmeasure.org",
        examination.getString("systolic_pressure_unit")
      )
    );
  }

  /**
   * @todo description
   */
  public static Observation.Component buildComponentDiastolicPressure(JSONObject examination) {
    return buildObservationComponent(
      buildCodeableConcept(
        buildCoding(
          "http://loinc.org/",
          "8462-4",
          "Diastolic blood pressure"
        )
      ),
      buildQuantity(
        Decimal.of(examination.getInt("diastolic_pressure")),
        "millimeter of mercury",
        "http://unitsofmeasure.org",
        examination.getString("diastolic_pressure_unit")
      )
    );
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationBodyWeight(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "vital-signs",
            "Vital Signs"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "29463-7",
            "Body weight"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getInt("weight")),
          "kilogram", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("weight_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationBodyHeight(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "vital-signs",
            "Vital Signs"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "8302-2",
            "Body height"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("height")),
          "meter", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          examination.getString("height_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationYearsWithDiabetes(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://terminology.hl7.org/CodeSystem/observation-category",
            "vital-signs",
            "Vital Signs"
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "http://loinc.org/",
            "PX070801190200",
            "PX070801 Diabetes Mellitus Year"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getInt("years_with_diabetes")),
          "year", // @note missing, so hard coded
          "http://unitsofmeasure.org",
          "a" // @todo ask if error or just to hardcode
        ),
        patientEntry,
        buildPatientAgeExtention(
          "http://hl7.org/fhir/StructureDefinition/observation-patientAge",
          examination.getInt("patient_age")
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildConditionHepaticSteatosis(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildCondition(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://snomed.info/sct",
            "197321007",
            "Steatosis of liver (disorder)"
          ),
          String.valueOf(examination.getBoolean("hepatic_steatosis"))
        ),
        patientEntry
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildConditionHypertension(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildCondition(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://snomed.info/sct",
            "38341003",
            "Hypertensive disorder, systemic arterial (disorder)"
          ),
          String.valueOf(examination.getBoolean("hypertension"))
        ),
        patientEntry
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildConditionHeartFailure(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildCondition(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://snomed.info/sct",
            "84114007",
            "Heart failure (disorder)"
          ),
          String.valueOf(examination.getBoolean("heart_failure"))
        ),
        patientEntry
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildConditionBPCO(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildCondition(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://snomed.info/sct",
            "13645005",
            "Chronic obstructive lung disease (disorder)"
          ),
          String.valueOf(examination.getBoolean("bpco"))
        ),
        patientEntry
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildConditionChronicKidneyDisease(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildCondition(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://snomed.info/sct",
            "709044004",
            "Chronic kidney disease (disorder)"
          ),
          String.valueOf(examination.getBoolean("chronic_kidney_disease"))
        ),
        patientEntry
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildConditionIschemicHeartDisease(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildCondition(
        examination.getString("date"),
        buildCodeableConcept(
          buildCoding(
            "http://snomed.info/sct",
            "414545008",
            "Ischemic heart disease (disorder)"
          ),
          String.valueOf(examination.getBoolean("ischemic_heart_disease"))
        ),
        patientEntry
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private FHIRPuglia() {
    super();
  }

}