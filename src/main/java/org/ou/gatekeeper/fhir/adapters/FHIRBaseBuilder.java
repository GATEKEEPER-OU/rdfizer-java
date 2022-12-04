package org.ou.gatekeeper.fhir.adapters;

import com.ibm.fhir.model.resource.*;
import com.ibm.fhir.model.type.*;
import com.ibm.fhir.model.type.code.HTTPVerb;
import com.ibm.fhir.model.type.code.ObservationStatus;
import org.commons.DateTimeUtils;

import java.lang.Integer;
import java.lang.String;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
class FHIRBaseBuilder {

  /**
   * @todo description
   */
  public static Uri buildFullUrl() {
    return buildFullUrl("urn:uuid:" + UUID.randomUUID());
  }

  /**
   * @todo description
   */
  public static Uri buildFullUrl(String uri) {
    return Uri.uri(uri);
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildEntry(Resource resource, String requestUrl) {
    return buildEntry(resource, requestUrl, null, buildFullUrl());
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildEntry(Resource resource, String requestUrl, String requestIdentifier) {
    return buildEntry(resource, requestUrl, requestIdentifier, buildFullUrl());
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildEntry(
    Resource resource,
    String requestUrl,
    String requestIdentifier,
    Uri fullUrl
  ) {
    Bundle.Entry.Request.Builder request = Bundle.Entry.Request.builder()
      .method(HTTPVerb.POST)
      .url(Uri.uri(requestUrl));

    if (requestIdentifier != null) {
      request = request.ifNoneExist(requestIdentifier);
    }

    return Bundle.Entry.builder()
      .fullUrl(fullUrl)
      .request(request.build())
      .resource(resource)
      .build();
  }

  public static Identifier buildIdentifier(String system, String value) {
    return Identifier.builder()
      .system(Uri.uri(system))
      .value(value)
      .build();
  }

  public static Device buildDevice(Identifier identifier) {
    return Device.builder()
      .identifier(identifier)
      .build();
  }

  // @todo refactory buildCodeableConcept split and optimize implementing buildCoding()

  public static Coding buildCoding(String system, String code, String display) {
    return Coding.builder()
      .system(
        Uri.uri(system)
      )
      .code(Code.code(code))
      .display(display)
      .build();
  }

  /**
   * @todo description
   */
  public static CodeableConcept buildCodeableConcept(String text) {
    return buildCodeableConcept(null, text);
  }

  /**
   * @todo description
   */
  public static CodeableConcept buildCodeableConcept(Coding coding) {
    return buildCodeableConcept(coding, null);
  }

  /**
   * @todo description
   */
  public static CodeableConcept buildCodeableConcept(Coding coding, String text) {
    CodeableConcept.Builder cc = CodeableConcept.builder();
    if (coding != null) {
      cc = cc.coding(coding);
    }
    if (text != null) {
      cc = cc.text(text);
    }
    return cc.build();
  }

  /**
   * @todo description
   */
  public static Extension buildPatientAgeExtention(String url, int age) {
    return Extension.builder()
      .url(url)
      .value(Integer.valueOf(age))
      .build();
  }

  /**
   * @todo description
   */
  public static Extension buildResourceReferenceExtention(String url, String resource) {
    return Extension.builder()
      .url(url)
      .value(resource)
      .build();
  }

  /**
   * @todo description
   */
  public static Timestamp toTimestamp(String dateTime) {
    Long l = Long.parseLong(dateTime);
    return new Timestamp(l);
  }

  /**
   * @todo description
   */
  public static DateTime buildDateTime(Timestamp ts, String zoneOffset) {
//    TimeZone asdf = TimeZone.getTimeZone(zoneOffset);
//    System.out.println(">>> " + asdf);


//    Date qwer = new Date(ts.getTime());


    /*
    DateTime.builder()
          .value(
            DateTimeUtils.cast(date) // @note Date needs to be converted to DateTime format in case of EMR dataset
          )
          .build()
    */

    return DateTime.builder()
      .value(DateTime.now().toString()) // TODO FIX
      .build();
  }

  /**
   * @todo description
   */
  public static Period buildPeriod(
    Timestamp start,
    Timestamp end,
    String zoneOffset
  ) {
    // TODO fix this
    return Period.builder()
      .start(DateTime.now())
      .end(DateTime.now())
//      .start(DateTime.of(start)) // <---
//      .end(DateTime.of(end))     // <---
      .build();
  }

  /**
   * @todo description
   */
  public static Quantity buildQuantity(
    Decimal value,
    String unit,
    String system,
    String code
  ) {
    return Quantity.builder()
      .value(value)
      .unit(unit)
      .system(Uri.uri(system))
      .code(Code.code(code))
      .build();
  }

  /**
   * @todo description
   */
  public static Reference buildReference(Identifier identifier) {
    return Reference.builder()
      .identifier(identifier)
      .build();
  }

  /**
   * @todo description
   */
  public static Reference buildReference(Bundle.Entry entry) {
    return Reference.builder()
      .reference(
        entry.getFullUrl().getValue()
      )
      .display(entry.getResource().getId())
      .build();
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservation(
    String date,
    CodeableConcept category,
    CodeableConcept code,
    Bundle.Entry patientEntry,
    Extension... extensions
  ) {
    return buildEntry(
      getBaseObservationBuilder(date, category, code, patientEntry, extensions)
        .build(),
      FHIR_OBSERVATION_TYPE
    );
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservation(
    String date,
    CodeableConcept category,
    CodeableConcept code,
    boolean valueBool,
    Bundle.Entry patientEntry,
    Extension... extensions
  ) {
    return buildEntry(
      getBaseObservationBuilder(date, category, code, patientEntry, extensions)
        .value(valueBool)
        .build(),
      FHIR_OBSERVATION_TYPE
    );
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildObservation(
    String date,
    CodeableConcept category,
    CodeableConcept code,
    Quantity quantity,
    Bundle.Entry patientEntry,
    Extension... extensions
  ) {
    return buildEntry(
      getBaseObservationBuilder(date, category, code, patientEntry, extensions)
        .value(quantity)
        .build(),
      FHIR_OBSERVATION_TYPE
    );
  }

  /**
   * @todo description
   */
  public static Observation.Component buildObservationComponent(
    CodeableConcept code,
    Quantity quantity,
    Extension... extension
  ) {
    return Observation.Component.builder()
      .code(code)
      .value(quantity)
      .extension(extension)
      .build();
  }

  /**
   * @todo description
   */
  public static Observation.Component buildObservationComponent(
    CodeableConcept code,
    String value,
    Extension... extension
  ) {
    return Observation.Component.builder()
      .code(code)
      .value(value)
      .extension(extension)
      .build();
  }

  /**
   * @todo description
   */
  public static Observation.Component buildObservationComponent(
    CodeableConcept code,
    DateTime dateTime,
    Extension... extension
  ) {
    return Observation.Component.builder()
      .code(code)
      .value(dateTime)
      .extension(extension)
      .build();
  }

  /**
   * @todo description
   */
  public static Observation.Component buildObservationComponent(
    CodeableConcept code,
    Period period,
    Extension... extension
  ) {
    return Observation.Component.builder()
      .code(code)
      .value(period)
      .extension(extension)
      .build();
  }

  /**
   * @todo description
   */
  public static Bundle.Entry buildCondition(
    String date,
    CodeableConcept code,
    Bundle.Entry patientEntry
  ) {
    return buildEntry(
      Condition.builder()
        .code(code)
//        .onset(
//          Age.builder().value(age).build()
//        )
        .recordedDate(
          DateTime.of(
            DateTimeUtils.cast(date) // @note Date needs to be converted to DateTime format in case of EMR dataset
          )
        )
        .subject(
          // TODO use buildReference
          Reference.builder()
            .reference(
              patientEntry.getFullUrl().getValue()
            )
            .build()
        )
        .build(),
      FHIR_CONDITION_TYPE
    );
  }


  /**
   * @todo description
   */
  protected static final String FHIR_OBSERVATION_TYPE = "Observation";

  /**
   * @todo description
   */
  protected static final String FHIR_CONDITION_TYPE = "Condition";

  /**
   * @todo description
   */
  protected FHIRBaseBuilder() {
  }

  @Deprecated // @todo use the function toBuilder ?
  protected static Observation.Builder getBaseObservationBuilder(
    String date,
    CodeableConcept category,
    CodeableConcept code,
    Bundle.Entry patientEntry,
    Extension... extensions
  ) {
    return Observation.builder()
      .status(ObservationStatus.FINAL)
      .category(category)
      .code(code)
      .extension(extensions)
      .effective(
        DateTime.builder()
          .value(
            DateTimeUtils.cast(date) // @note Date needs to be converted to DateTime format in case of EMR dataset
          )
          .build()
      )
      .subject(
        // TODO use buildReference
        Reference.builder()
          .reference(
            patientEntry.getFullUrl().getValue()
          )
          .build()
      );
  }

}