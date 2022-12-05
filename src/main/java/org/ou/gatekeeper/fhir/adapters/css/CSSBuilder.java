package org.ou.gatekeeper.fhir.adapters.css;

import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.DateTime;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Identifier;
import com.ibm.fhir.model.type.Uri;
import org.commons.DateTimeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ou.gatekeeper.fhir.adapters.FHIRBaseBuilder;

import java.util.Collection;
import java.util.LinkedList;

import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
class CSSBuilder extends FHIRBaseBuilder {

  public static final String LOINC_SYSTEM = "http://loinc.org";
  public static final String DOID_SYSTEM = "http://purl.obolibrary.org/obo/";
  public static final String UNITSOFM_SYSTEM = "http://unitsofmeasure.org";
  public static final String HL7_SYSTEM = "http://terminology.hl7.org/CodeSystem/observation-category";
  public static final String HL7_PATIENT_AGE = "http://hl7.org/fhir/StructureDefinition/observation-patientAge";

  public static final String LAB_CODE = "laboratory";
  public static final String LAB_DISPLAY = "Laboratory";

  public static final String VS_CODE = "vital-signs";
  public static final String VS_DISPLAY = "Vital Signs";

  public static final String MG_PER_DL_UNIT = "milligram per deciliter";

  public static final String PATIENT_AGE_KEY = "patient_age";

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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "59261-8",
            "Hemoglobin A1c/Hemoglobin.total in Blood by IFCC protocol"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("glycosilated_emoglobin")),
          "millimole per mole",
          UNITSOFM_SYSTEM,
          examination.getString("glycosilated_emoglobin_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "2093-3",
            "Cholesterol [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("total_cholesterol")),
          "millimole per mole",
          UNITSOFM_SYSTEM,
          examination.getString("total_cholesterol_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "2085-9",
            "Cholesterol in HDL [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("HDL")),
          MG_PER_DL_UNIT,
          UNITSOFM_SYSTEM,
          examination.getString("HDL_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "2089-1",
            "Cholesterol in LDL [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("LDL")),
          MG_PER_DL_UNIT,
          LOINC_SYSTEM,
          examination.getString("LDL_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "2571-8",
            "Triglyceride [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("triglycerides")),
          MG_PER_DL_UNIT,
          LOINC_SYSTEM,
          examination.getString("triglycerides_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
        )
      );
    } catch (JSONException e) {
      // @todo just print a warning
      return null;
    }
  }

  // @note TC-HDL missing

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservationSerumCreatinine(JSONObject examination, Bundle.Entry patientEntry) {
    try {
      return buildObservation(
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "2160-0",
            "Creatinine [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("serum_creatinine")),
          MG_PER_DL_UNIT,
          LOINC_SYSTEM,
          examination.getString("serum_creatinine_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "14959-1",
            "Microalbumin/Creatinine [Mass Ratio] in Urine"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("albuminuria_creatininuria_ratio")),
          "milligram per gram",
          LOINC_SYSTEM,
          examination.getString("albuminuria_creatininuria_ratio_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "1742-6",
            "Alanine aminotransferase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("GPT_ALT")),
          "enzyme unit per liter",
          LOINC_SYSTEM,
          examination.getString("GPT_ALT_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "1920-8",
            "Aspartate aminotransferase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("GOT_AST")),
          examination.getString("GOT_AST_unit"),
          LOINC_SYSTEM,
          "enzyme unit per liter"
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "2324-2",
            "Gamma glutamyl transferase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("gammaGT")),
          "international unit per liter",
          LOINC_SYSTEM,
          examination.getString("gammaGT_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "6768-6",
            "Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("alkaline_phosphatase")),
          "international unit per liter",
          LOINC_SYSTEM,
          examination.getString("alkaline_phosphatase_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "3084-1",
            "Urate [Mass/volume] in Serum or Plasma"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("uric_acid")),
          MG_PER_DL_UNIT,
          LOINC_SYSTEM,
          examination.getString("uric_acid_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "48642-3",
            "Glomerular filtration rate/1.73 sq M.predicted among non-blacks [Volume Rate/Area] in Serum, Plasma or Blood by Creatinine-based formula (MDRD)"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("eGFR")),
          examination.getString("eGFR_unit"),
          LOINC_SYSTEM,
          examination.getString("eGFR_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            LAB_CODE,
            LAB_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "5802-4",
            "Nitrite [Presence] in Urine by Test strip"
          )
        ),
        examination.getBoolean("nitrites"),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            VS_CODE,
            VS_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "85354-9",
            "Blood pressure panel with all children optional"
          )
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
          LOINC_SYSTEM,
          "8480-6",
          "Systolic blood pressure"
        )
      ),
      buildQuantity(
        Decimal.of(examination.getInt("systolic_pressure")),
        "millimeter of mercury",
        LOINC_SYSTEM,
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
          LOINC_SYSTEM,
          "8462-4",
          "Diastolic blood pressure"
        )
      ),
      buildQuantity(
        Decimal.of(examination.getInt("diastolic_pressure")),
        "millimeter of mercury",
        LOINC_SYSTEM,
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            VS_CODE,
            VS_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "29463-7",
            "Body weight"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getInt("weight")),
          "kilogram",
          LOINC_SYSTEM,
          examination.getString("weight_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            VS_CODE,
            VS_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            LOINC_SYSTEM,
            "8302-2",
            "Body height"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getBigDecimal("height")),
          "meter",
          LOINC_SYSTEM,
          examination.getString("height_unit")
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            HL7_SYSTEM,
            VS_CODE,
            VS_DISPLAY
          )
        ),
        buildCodeableConcept(
          buildCoding(
            "https://www.phenxtoolkit.org/",
            "PX070801190200",
            "PX070801 Diabetes Mellitus Year"
          )
        ),
        buildQuantity(
          Decimal.of(examination.getInt("years_with_diabetes")),
          "year",
          UNITSOFM_SYSTEM,
          "a_j" // TODO check here https://ucum.org/ucum#para-29
        ),
        patientEntry,
        buildPatientAgeExtention(
          HL7_PATIENT_AGE,
          examination.getInt(PATIENT_AGE_KEY)
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            DOID_SYSTEM,
            "DOID_9452",
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            DOID_SYSTEM,
            "DOID_10825",
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            DOID_SYSTEM,
            "DOID_6000",
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            DOID_SYSTEM,
            "DOID_3083",
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            DOID_SYSTEM,
            "DOID_784",
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
        buildDate(
          examination.getString("date")
        ),
        buildCodeableConcept(
          buildCoding(
            DOID_SYSTEM,
            "DOID_3393",
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

  public static DateTime buildDate(String dateTime) {
    return DateTime.builder()
      .value(
        DateTimeUtils.cast(dateTime)
      )
      .build();
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private CSSBuilder() {
    super();
  }

}