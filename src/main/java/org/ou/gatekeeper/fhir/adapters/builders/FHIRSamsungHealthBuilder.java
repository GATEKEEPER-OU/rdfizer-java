package org.ou.gatekeeper.fhir.adapters.builders;

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

public class FHIRSamsungHealthBuilder extends FHIRBase {

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
    Collection<Bundle.Entry> members,
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
        .hasMember(getReferences(members))
        .effective(
          // TODO fix just one timestamp (not startTime, endTime)
          buildPeriod(startTime, endTime, zoneOffset)
        )
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
    Collection<Bundle.Entry> members,
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
        .hasMember(getReferences(members))
        .effective(
          buildPeriod(startTime, endTime, zoneOffset)
        )
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
      buildFullUrl(BASE_URL + "/observation/" + id)
    );
  }

  public static Bundle.Entry buildLiveObservation(
    String id,
    JSONObject liveElement,
    JSONObject dataElement,
    CodeableConcept codes,
    Quantity quantity
//    , Bundle.Entry patientEntry
  ) {
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
        .build(),
      "Observation",
      null,
      buildFullUrl(BASE_URL + "/observation/" + id)
    );
  }


  /**
   * @todo description
   */
//  public static Bundle.Entry buildLiveObservation(
//    String id,
//    CodeableConcept code,
//    Period period,
//    Quantity quantity,
//    Bundle.Entry patientEntry
//  ) {
//    // TODO use build entry
//    return Bundle.Entry.builder()
//      .fullUrl(buildFullUrl(BASE_URL + "/observation/" + id))
////      .request(request.build()) // TODO
//      .resource(
//        Observation.builder()
//          .id(id)
//          .status(ObservationStatus.FINAL)
//          .code(code)
//          .identifier(
//            buildIdentifier(
//              BASE_URL + "/identifier",
//              "Observation/" + id
//            )
//          )
//          .effective(period)
//          .value(quantity)
//          .subject(
//            buildReference(patientEntry)
//          )
//          .build()
//      )
//      .build();
//  }

//  /**
//   * @todo description
//   */
//  public static List<Bundle.Entry> buildCadence(JSONObject dataElement, Bundle.Entry patientEntry) {
//    return null;
//  }
//  public static List<Bundle.Entry> buildCadence(JSONArray liveData, JSONObject dataElement, Bundle.Entry patientEntry) {
//    String uuid = dataElement.getString("data_uuid");
//    String zoneOffset = getValue(dataElement, "time_offset");
//    List<Bundle.Entry> observations = new LinkedList();
//    // For each live_data
//    for (int i = 0; i < liveData.length(); ++i) {
//      JSONObject liveElement = liveData.getJSONObject(i);
//      // Collect live_data values
//      String  streamId = String.format("%s-cadence-%d", uuid, i);
//      String startTime = liveElement.getString("start_time");
//      String   endTime = liveElement.getString("end_time");
//      String   cadence = liveElement.getString("cadence");
//      // Build live observation
//      Bundle.Entry liveObservation = buildLiveObservation(
//        streamId,
//        buildCodeableConcept(
//          buildCoding(
//            SAMSUNG_LIVE_SYSTEM,
//            "live_data_cadence",
//            "Live data cadence"
//          )
//        ),
//        buildPeriod(startTime, endTime, zoneOffset),
//        buildQuantity(
//          Decimal.of(cadence),
//          "m/s",
//          UNITSOFM_SYSTEM,
//          "m/s"
//        ),
//        patientEntry
//      );
//      // Append live data
//      observations.add(liveObservation);
//    }
//
//    // Build aggregated observation
//    // connect live observation to aggregated one
//    // prepend aggregated observation
//
//    return observations;
//  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private FHIRSamsungHealthBuilder() {
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
              "heart_rate", // TODO fix this
              "HeartRate"
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

}