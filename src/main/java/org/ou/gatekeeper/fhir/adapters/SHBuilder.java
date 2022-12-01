package org.ou.gatekeeper.fhir.adapters;

import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.ObservationStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.String;
import java.util.Collection;
import java.util.stream.Collectors;

class SHBuilder extends FHIRBaseBuilder {

  public static final String BASE_URL = "https://opensource.samsung.com/projects/helifit";
  public static final String SAMSUNG_LIVE_SYSTEM = "http://samsung/live-data";
  public static final String LOCAL_SYSTEM = "http://local-system";
  public static final String LOINC_SYSTEM = "http://loinc.org";
  public static final String UNITSOFM_SYSTEM = "http://unitsofmeasure.org";

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
    Bundle.Entry patientEntry
  ) {
    String       uuid = dataElement.getString("data_uuid");
    String   deviceId = dataElement.getString("device_id");
    String  startTime = getValue(dataElement, "start_time");
    String    endTime = getValue(dataElement, "end_time");
    String zoneOffset = getValue(dataElement, "time_offset");

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
          // TODO fix just one timestamp (not startTime, endTime)
          buildPeriod(startTime, endTime, zoneOffset)
        )
        .value(getMainValue(dataElement))
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
          buildPeriod(startTime, endTime, zoneOffset)
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
          buildPeriod(startTime, endTime, zoneOffset)
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
          buildPeriod(startTime, endTime, zoneOffset)
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

  private static String getValue(JSONObject dataElement, String key) {
    JSONArray values = dataElement.getJSONArray("values");
    JSONObject element = values.getJSONObject(0);
    return element.getString(key);
  }

  private static Collection<Reference> getReferences(Collection<Bundle.Entry> entries) {
    return entries.stream()
      .map(entry -> buildReference(entry))
      .collect(Collectors.toList());
  }

  private static CodeableConcept getCodes(JSONObject dataElement) {
    String typeId = dataElement.getString("type_id");
    switch (typeId) {
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
      case "stepDailyTrend":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "step_daily_trend", // TODO fix this
              "StepDailyTrend"
            )
          )
          .build();
      case "floorsClimbed":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "floors_climbed", // TODO fix this
              "FloorsClimbed"
            )
          )
          .build();
      case "heartRate":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "heart_rate_sampling",
              "Heart Rate Sampling"
            )
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
        return null; // TODO exception non mapped
    }
  }

  // TODO temporary
  // ask Carlo about value[exercise_type] should be loinc code?
  private static Coding getExerciseCode(JSONObject dataElement) {
    String typeId = getValue(dataElement, "exercise_description");
    switch (typeId) {
      case "Walking":
        return buildCoding(
          LOINC_SYSTEM,
          "82948-1",
          typeId
        );
      case "Running":
        return buildCoding(
          LOINC_SYSTEM,
          "running", // TODO fix this
          typeId
        );
      case "Cycling":
        return buildCoding(
          LOINC_SYSTEM,
          "cycling", // TODO fix this
          typeId
        );
      case "Swimming":
        return buildCoding(
          LOINC_SYSTEM,
          "swimming", // TODO fix this
          typeId
        );
      default:
        return null; // TODO exception non mapped
    }
  }

  private static Quantity getMainValue(JSONObject dataElement) {
    String typeId = dataElement.getString("type_id");
    switch (typeId) {
      case "floorsClimbed":
        return buildQuantity(
          Decimal.of(getValue(dataElement, "floor")),
          "...", // TODO
          UNITSOFM_SYSTEM,
          "..." // TODO
        );
      default:
        return null; // TODO exception non mapped
    }
  }

}