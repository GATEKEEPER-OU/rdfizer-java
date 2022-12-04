package org.ou.gatekeeper.fhir.adapters;

import com.google.common.base.CaseFormat;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.CodeableConcept;
import com.ibm.fhir.model.type.Coding;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Quantity;
import com.ibm.fhir.model.type.code.ObservationStatus;
import org.apache.commons.text.CaseUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

class SHBuilder extends FHIRBaseBuilder {

  public static final String BASE_URL = "https://opensource.samsung.com/projects/helifit";
  public static final String SAMSUNG_LIVE_SYSTEM = "http://samsung/live-data";
  public static final String LOCAL_SYSTEM = "http://local-system";

  /**
   * Base URL of Logical Observation Identifiers Names and Codes
   * <a href="https://loinc.org/search">LOINC_SYSTEM</a>
   * */
  public static final String LOINC_SYSTEM = "http://loinc.org";

  /**
   * Base URL of Unified Code for Units of Measure
   * <a href="https://ucum.org/ucum#section-Base-Units">UNITSOFM_SYSTEM</a>
   * */
  public static final String UNITSOFM_SYSTEM = "http://unitsofmeasure.org";

  //--------------------------------------------------------------------------//
  // Gets
  //--------------------------------------------------------------------------//

  public static boolean hasValue(JSONObject dataElement, String key) {
    JSONArray values = dataElement.getJSONArray("values");
    JSONObject element = values.getJSONObject(0);
    return element.has(key);
  }

  public static String getValue(JSONObject dataElement, String key) {
    JSONArray values = dataElement.getJSONArray("values");
    JSONObject element = values.getJSONObject(0);
    return element.getString(key);
  }

  public static CodeableConcept getCodes(JSONObject dataElement) {
    String typeId = dataElement.getString("type_id");
    switch (typeId) {
      case "bloodPressure":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "85354-9",
              "Blood pressure"
            )
          )
          .build();
      case "heartRate":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOCAL_SYSTEM,
              "heart_rate_sampling",
              "Heart Rate Sampling"
            )
          )
          .build();
      case "exercise":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "LA11834-1",
              "Exercise"
            ),
            getExerciseCode(dataElement)
          )
          .build();
      case "sleep":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "93832-4",
              "Sleep"
            )
          )
          .build();
      default:
        String display = CaseUtils.toCamelCase(typeId, true);
        String code = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, display);
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOCAL_SYSTEM,
              code,
              display
            )
          )
          .build();
    }
  }

  public static Coding getExerciseCode(JSONObject dataElement) {
    String typeId = getValue(dataElement, "exercise_description");
    switch (typeId) {
      case "Walking":
        return buildCoding(
          LOINC_SYSTEM,
          "82948-1",
          typeId
        );
      default:
        String display = CaseUtils.toCamelCase(typeId, true);
        String code = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, display);
        return buildCoding(
          LOCAL_SYSTEM,
          code,
          display
        );
    }
  }

  public static Quantity getMainValue(JSONObject dataElement) {
    String typeId = dataElement.getString("type_id");
    switch (typeId) {
      case "height":
        return buildQuantity(
          Decimal.of(
            SHBuilder.getValue(dataElement, "height")
          ),
          "centimeter",
          UNITSOFM_SYSTEM,
          "cm"
        );
      case "weight":
        return buildQuantity(
          Decimal.of(
            SHBuilder.getValue(dataElement, "weight")
          ),
          "kilogram",
          UNITSOFM_SYSTEM,
          "Kg"
        );
      case "waterIntake":
      case "caffeineIntake":
        return buildQuantity(
          Decimal.of(
            SHBuilder.getValue(dataElement, "amount")
          ),
          "milliliters",
          UNITSOFM_SYSTEM,
          "mL"
        );
      case "floorsClimbed":
        return buildQuantity(
          Decimal.of(
            SHBuilder.getValue(dataElement, "floor")
          ),
          "...", // TODO
          UNITSOFM_SYSTEM,
          "..." // TODO
        );
      default:
        return null;
    }
  }

  //--------------------------------------------------------------------------//
  // Builders
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  public static Bundle.Entry buildPatient(JSONObject patient) {
    String patientId = patient.getString("user_uuid");
    String patientEmail = patient.getString("user_id");
    String fullUrl = BASE_URL + "/patient/" + patientEmail;
    return buildEntry(
      Patient.builder()
        .id(patientId)
        .identifier(
          buildIdentifier(
            BASE_URL + "/identifier",
            patientEmail
          ),
          buildIdentifier(
            BASE_URL + "/identifier",
            patientId
          )
        )
        .build(),
      "Patient",
      null,
      buildFullUrl(fullUrl)
    );
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildMainObservation(
    JSONObject dataElement,
    Collection<Observation.Component> components,
    Quantity value,
    Bundle.Entry patientEntry
  ) {
    String       uuid = dataElement.getString("data_uuid");
    String   deviceId = dataElement.getString("device_id");
    String zoneOffset = getValue(dataElement, "time_offset");
    String  startTime = getValue(dataElement, "start_time");
    String endTime = hasValue(dataElement, "end_time")
      ? getValue(dataElement, "end_time")
      : null;

    return buildEntry(
      Observation.builder()
        .id(uuid)
        .status(ObservationStatus.FINAL)
        .identifier(
          buildIdentifier(
            BASE_URL + "/identifier",
            "Observation/" + uuid
          )
        )
        .code(
          getCodes(dataElement)
        )
        .effective(
          endTime != null
            ? buildPeriod(toTimestamp(startTime), toTimestamp(endTime), zoneOffset)
            : buildDateTime(toTimestamp(startTime), zoneOffset)
        )
        .component(components)
        .value(value)
        .device(
          buildReference(buildIdentifier(
            BASE_URL + "/device", deviceId
          ))
        )
        .subject(
          buildReference(patientEntry)
        )
        .build(),
      "Observation",
      null,
      buildFullUrl(BASE_URL + "/observation/" + uuid)
    );
  }

  public static Bundle.Entry buildAggregatedObservation(
    String id,
    JSONObject dataElement,
    CodeableConcept codes,
    Collection<Observation.Component> components,
    Quantity quantity,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    String   deviceId = dataElement.getString("device_id");
    String  startTime = getValue(dataElement, "start_time");
    String    endTime = getValue(dataElement, "end_time");
    String zoneOffset = getValue(dataElement, "time_offset");
    return buildEntry(
      Observation.builder()
        .id(id)
        .status(ObservationStatus.FINAL)
        .identifier(buildIdentifier(
          BASE_URL + "/identifier", id
        ))
        .code(codes)
        .component(components)
        .value(quantity)
        .effective(
          buildPeriod(
            toTimestamp(startTime),
            toTimestamp(endTime),
            zoneOffset
          )
        )
        .device(
          buildReference(buildIdentifier(
            BASE_URL + "/device", deviceId
          ))
        )
        .derivedFrom(
          buildReference(parentEntry)
        )
        .subject(
          buildReference(patientEntry)
        )
        .build(),
      "Observation",
      null,
      buildFullUrl(BASE_URL + "/observation/" + id)
    );
  }

  public static Bundle.Entry buildLiveObservation(
    String id,
    JSONObject liveElement,
    JSONObject dataElement,
    CodeableConcept codes,
    Quantity quantity,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    String   deviceId = dataElement.getString("device_id");
    String  startTime = liveElement.getString("start_time");
    String    endTime = liveElement.getString("end_time");
    String zoneOffset = getValue(dataElement, "time_offset");
    return buildEntry(
      Observation.builder()
        .id(id)
        .status(ObservationStatus.FINAL)
        .identifier(buildIdentifier(
          BASE_URL + "/identifier", id
        ))
        .code(codes)
        .value(quantity)
        .effective(
          buildPeriod(
            toTimestamp(startTime),
            toTimestamp(endTime),
            zoneOffset
          )
        )
        .device(
          buildReference(buildIdentifier(
            BASE_URL + "/device", deviceId
          ))
        )
        .derivedFrom(
          buildReference(parentEntry)
        )
        .subject(
          buildReference(patientEntry)
        )
        .build(),
      "Observation",
      null,
      buildFullUrl(BASE_URL + "/observation/" + id)
    );
  }

  public static Bundle.Entry buildLocation(
    String id,
    JSONObject locationElement,
    JSONObject dataElement,
    Collection<Observation.Component> components,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    String  startTime = locationElement.getString("start_time");
    String    endTime = locationElement.getString("end_time");
    String zoneOffset = getValue(dataElement, "time_offset");
    return buildEntry(
      Observation.builder()
        .id(id)
        .status(ObservationStatus.FINAL)
        .identifier(buildIdentifier(
          BASE_URL + "/identifier", id
        ))
        .code(buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "location",
          "Location"
        )))
        .component(components)
        .effective(
          buildPeriod(
            toTimestamp(startTime),
            toTimestamp(endTime),
            zoneOffset
          )
        )
        .derivedFrom(
          buildReference(parentEntry)
        )
        .subject(
          buildReference(patientEntry)
        )
        .build(),
      "Observation",
      null,
      buildFullUrl(BASE_URL + "/observation/" + id)
    );
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private SHBuilder() {
    super();
  }

}