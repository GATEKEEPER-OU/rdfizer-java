package org.ou.gatekeeper.fhir.adapters.sh;

import com.google.common.base.CaseFormat;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.resource.Patient;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.ObservationStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.text.CaseUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ou.gatekeeper.fhir.adapters.FHIRBaseBuilder;
import java.lang.String;
import java.util.Collection;

class SHBuilder extends FHIRBaseBuilder {

  public static final String BASE_URL = "https://opensource.samsung.com/projects/helifit";
  public static final String SAMSUNG_LIVE_SYSTEM = "http://samsung/live-data";

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
    if (element.has(key)) {
      String value = element.getString(key);
      if (StringUtils.isBlank(value)) {
        String message = String.format("Property '%s' is blank", key);
        LOGGER.warn(message);
      }
      return value;
    }
    return ""; // TODO request to remove empty values
  }

  public static String getCountType(String value) {
    switch (value) {
      case "30001": return "stride";
      case "30002": return "stroke";
      case "30003": return "swing";
      case "30004": return "repetition";
      default:
        return "undefined";
    }
  }

  public static CodeableConcept getCodes(JSONObject dataElement) {
    String typeId = dataElement.getString("type_id");
    switch (typeId) {
      case "height":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "8302-2",
              "Body height"
            )
          )
          .build();
      case "weight":
        return CodeableConcept.builder()
          .coding(
            buildCoding(
              LOINC_SYSTEM,
              "29463-7",
              "Body weight"
            )
          )
          .build();
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
          "floor",
          LOCAL_SYSTEM,
          "floor"
        );
      default:
        return null;
    }
  }

  private static String getTimeOffset(JSONObject dataElement) {
    String key = "time_offset";
    JSONArray values = dataElement.getJSONArray("values");
    JSONObject element = values.getJSONObject(0);
    return element.has(key) ? element.getString(key) : "UTC+0000";
  }

  //--------------------------------------------------------------------------//
  // Builders
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  public static Bundle.Entry buildPatient(JSONObject patient) {
//    String patientId = JSONObjectUtils.getId(patient, "user_uuid");
    String patientEmail = patient.getString("user_id");
    String patientId = patientEmail.replace("@", "-");
    String fullUrl = BASE_URL + "/patient/" + patientId;
//    String fullUrl = BASE_URL + "/patient/" + patientEmail;
    return buildEntry(
      Patient.builder()
        .id(patientId)
        .identifier(
//          buildIdentifier(
//            BASE_URL + "/identifier",
//            patientId
//          ),
          buildIdentifier(
            BASE_URL + "/identifier",
            patientEmail
          )
        )
        .build(),
      "Patient",
      "identifier=" + BASE_URL + "/patient|" + patientId,
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
    String zoneOffset = getTimeOffset(dataElement); // TODO re-think again in the future
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
            ? buildPeriod(startTime, endTime, zoneOffset)
            : buildDateTime(startTime, zoneOffset)
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
    String endTime = hasValue(dataElement, "end_time")
            ? getValue(dataElement, "end_time")
            : null;
    String zoneOffset = getTimeOffset(dataElement); // TODO re-think again in the future
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
                endTime != null
                        ? buildPeriod(startTime, endTime, zoneOffset)
                        : buildDateTime(startTime, zoneOffset)
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
    String zoneOffset = getTimeOffset(dataElement); // TODO re-think again in the future
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

  public static Observation.Component buildObservationComponent(
    CodeableConcept code,
    Element value
  ) {
    return Observation.Component.builder()
      .code(code)
      .value(value)
      .build();
  }

  public static Bundle.Entry buildLocation(
    String id,
    JSONObject locationElement,
    JSONObject dataElement,
    Collection<Observation.Component> components,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    String   deviceId = dataElement.getString("device_id");
    String  startTime = locationElement.getString("start_time");
    String    endTime = locationElement.getString("end_time");
    String zoneOffset = getTimeOffset(dataElement); // TODO re-think again in the future
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

  /**
   * @todo description
   */
  public static DateTime buildDateTime(String dateTime, String zoneOffset) {
    String stdDateTime = dateTimeTranslator(dateTime, zoneOffset);
    return DateTime.builder()
      .value(stdDateTime)
      .build();
  }

  /**
   * @todo description
   */
  public static Period buildPeriod(
    String start,
    String end,
    String zoneOffset
  ) {
    return Period.builder()
      .start(DateTime.of(dateTimeTranslator(start, zoneOffset)))
      .end(DateTime.of(dateTimeTranslator(end, zoneOffset)))
      .build();
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

  private static String dateTimeTranslator(String timestamp, String zoneOffset){
    // @see https://developer.samsung.com/health/android/data/api-reference/com/samsung/android/sdk/healthdata/HealthConstants.SessionMeasurement.html
    Long lTimestamp = Long.parseLong(timestamp);
    String pattern = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern();
    String isoDateTime = DateFormatUtils.format(lTimestamp, pattern);
//    String isoDateTime = DateFormatUtils.format(lTimestamp, pattern, timeZone);
//    System.out.println("zoneOffset>>> " + zoneOffset); // DEBUG
//    System.out.println("isoDateTime>>> " + isoDateTime); // DEBUG
    return isoDateTime;
  }

}