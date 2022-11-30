package org.ou.gatekeeper.fhir.adapters;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.Quantity;
import com.ibm.fhir.model.type.code.BundleType;
import com.ibm.fhir.model.visitor.Visitable;
import org.apache.commons.io.FileUtils;
import org.commons.ResourceUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ou.gatekeeper.fhir.helpers.FHIRNormalizer;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;

import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;
import static org.commons.ResourceUtils.generateUniqueFilename;
import static org.ou.gatekeeper.fhir.adapters.SamsungHealthBuilder.*;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * TODO description
 */
public class SamsungHealthAdapter implements FHIRAdapter {

  /**
   * TODO description
   */
  public static SamsungHealthAdapter create() {
    return new SamsungHealthAdapter();
  }

  @Override
  public void transform(File dataset, File output) {
    transform(dataset, output, false);
  }

  @Override
  public void transform(File dataset, File output, boolean normalize) {
    try (
      InputStream datasetInputStream = new FileInputStream(dataset)
    ) {
      // Parse JSON dataset
      JSONTokener tokenizer = new JSONTokener(datasetInputStream);
      JSONObject json = new JSONObject(tokenizer);

      // Collect all entries
      Collection<Bundle.Entry> entries = new LinkedList<>();
      siftData(json, entries);

      // Build FHIR bundle
      Visitable bundle = Bundle.builder()
        .entry(entries)
        .type(BundleType.TRANSACTION)
        .build();

      // Save FHIR bundle in a JSON file
      String tempFilename = generateUniqueFilename("output", "tmp.fhir.json");
      File tempOutputFile = new File(TMP_DIR, tempFilename);
      save(bundle, tempOutputFile);

      // Normalize FHIR bundles, if requested
      if (normalize) {
        FHIRNormalizer.normalize(tempOutputFile, output);
        ResourceUtils.clean(tempOutputFile);
      } else {
        Files.move(tempOutputFile.toPath(), output.toPath());
      }

    } catch (FileNotFoundException e) {
      // TODO Message
      e.printStackTrace();

    } catch (IOException e) {
      // TODO Message
      e.printStackTrace();
    }
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  private static final File TMP_DIR = FileUtils.getTempDirectory();

  /**
   * TODO description
   */
  protected SamsungHealthAdapter() {}

  /**
   * TODO description
   */
  private static void siftData(
    JSONObject json,
    Collection<Bundle.Entry> entries
  ) {
    Bundle.Entry patientEntry = collectPatient(entries, json);
    JSONArray data = json.getJSONArray("data");
    for (int i = 0; i < data.length(); ++i) {
      JSONObject element = data.getJSONObject(i);
      collectObservations(entries, element, patientEntry);
    }
  }

  /**
   * TODO description
   */
  private static void save(Visitable bundle, File output) {
    try (
      Writer file = new FileWriter(output)
    ) {
      FHIRGenerator
        .generator(Format.JSON, true)
        .generate(bundle, file);

    } catch (IOException e) {
      // TODO Message
      e.printStackTrace();

    } catch (FHIRGeneratorException e) {
      // TODO Message
      e.printStackTrace();
    }
  }

  /**
   * TODO description
   */
  private static Bundle.Entry collectPatient(
    Collection<Bundle.Entry> entries,
    JSONObject json
  ) {
    Bundle.Entry patientEntry = buildPatient(json);
    addIgnoreNull(entries, patientEntry);
    return patientEntry;
  }

  /**
   * @todo description
   */
  private static void collectObservations(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    Bundle.Entry mainObservation = buildMainObservation(dataElement, patientEntry);
    entries.add(mainObservation);

    // NOTE 'floor' collected inside buildMainObservation

    collectCalorie(entries, dataElement, mainObservation, patientEntry);
    collectCount(entries, dataElement, mainObservation, patientEntry);
    collectDistance(entries, dataElement, mainObservation, patientEntry);
    collectSpeed(entries, dataElement, mainObservation, patientEntry);

    collectHeartRateInstantaneous(entries, dataElement, mainObservation, patientEntry);
    collectHeartRateMin(entries, dataElement, mainObservation, patientEntry);
    collectHeartRateMax(entries, dataElement, mainObservation, patientEntry);
    collectHeartBeatCount(entries, dataElement, mainObservation, patientEntry);

    collectCadence(entries, dataElement, mainObservation, patientEntry);
//    collectCount(entries, dataElement, mainObservation, patientEntry);     // NOTE already collected above
//    collectDistance(entries, dataElement, mainObservation, patientEntry);  // NOTE already collected above
//    collectHeartRate(entries, dataElement, mainObservation, patientEntry); // NOTE already collected above
    collectPower(entries, dataElement, mainObservation, patientEntry);
//    collectSpeed(entries, dataElement, mainObservation, patientEntry);     // NOTE already collected above
    collectAltitude(entries, dataElement, mainObservation, patientEntry);
    collectDuration(entries, dataElement, mainObservation, patientEntry);
    collectHeartRate(entries, dataElement, mainObservation, patientEntry);
    collectRpm(entries, dataElement, mainObservation, patientEntry);
    collectVo2Max(entries, dataElement, mainObservation, patientEntry);
    collectLocation(entries, dataElement, mainObservation, patientEntry);
  }

  private static Bundle.Entry collectCalorie(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("calorie")
      || values.has("mean_caloricburn_rate")
      || values.has("max_caloricburn_rate")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Calorie
      Observation.Component calorieComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "calorie",
          "Calorie"
        )),
        buildQuantity(
          Decimal.of(values.getString("calorie")),
          "kcal/s",
          UNITSOFM_SYSTEM,
          "kcal/s"
        )
      );
      components.add(calorieComponent);
      //
      // mean_caloricburn_rate
      if (values.has("mean_caloricburn_rate")) {
        Observation.Component meanComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "mean_caloricburn_rate",
            "Mean caloric burn rate"
          )),
          buildQuantity(
            Decimal.of(values.getString("mean_caloricburn_rate")),
            "kcal/s",
            UNITSOFM_SYSTEM,
            "kcal/s"
          )
        );
        components.add(meanComponent);
      }
      //
      // maxc_caloricburn_rate
      if (values.has("max_caloricburn_rate")) {
        Observation.Component maxComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "max_caloricburn_rate",
            "Max caloric burn rate"
          )),
          buildQuantity(
            Decimal.of(values.getString("mean_caloricburn_rate")),
            "kcal/s",
            UNITSOFM_SYSTEM,
            "kcal/s"
          )
        );
        components.add(maxComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-calorie", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "calorie",
          "Calorie"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("calorie")) {
          // Collect live_data values
          String liveId = String.format("%s-calorie-%d", uuid, i);
          String value = liveElement.getString("calorie");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_calorie",
              "Live data calorie"
            )),
            buildQuantity(
              Decimal.of(value),
              "kcal/s",
              UNITSOFM_SYSTEM,
              "kcal/s"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectCount(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("count")) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // count
      Observation.Component countComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "count",
          "Count"
        )),
        buildQuantity(
          Decimal.of(values.getString("count")),
          "...", // TODO
          UNITSOFM_SYSTEM,
          "..." // TODO
        )
      );
      components.add(countComponent);
      //
      // count_type
      if (values.has("count_type")) {
        String value = values.getString("count_type");
        Observation.Component meanComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "count_type",
            "Count type"
          )),
          Quantity.builder()
            .value(Decimal.of(value))
            .build()
        );
        components.add(meanComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-count", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "count",
          "Count"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("count")) {
          // Collect live_data values
          String liveId = String.format("%s-count-%d", uuid, i);
          String value = liveElement.getString("count");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_count",
              "Live data count"
            )),
            buildQuantity(
              Decimal.of(value),
              "...",
              UNITSOFM_SYSTEM,
              "..."
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectDistance(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("distance")
      || values.has("incline_distance")
      || values.has("decline_distance")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Distance
      Observation.Component distanceComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "distance",
          "Distance"
        )),
        buildQuantity(
          Decimal.of(values.getString("distance")),
          "m",
          UNITSOFM_SYSTEM,
          "m"
        )
      );
      components.add(distanceComponent);
      //
      // incline_distance
      if (values.has("incline_distance")) {
        Observation.Component inclineComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "incline_distance",
            "Incline distance"
          )),
          buildQuantity(
            Decimal.of(values.getString("incline_distance")),
            "m",
            UNITSOFM_SYSTEM,
            "m"
          )
        );
        components.add(inclineComponent);
      }
      //
      // decline_distance
      if (values.has("decline_distance")) {
        Observation.Component inclineComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "decline_distance",
            "Decline distance"
          )),
          buildQuantity(
            Decimal.of(values.getString("decline_distance")),
            "m",
            UNITSOFM_SYSTEM,
            "m"
          )
        );
        components.add(inclineComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-distance", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "distance",
          "Distance"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("distance")) {
          // Collect live_data values
          String liveId = String.format("%s-distance-%d", uuid, i);
          String value = liveElement.getString("distance");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_distance",
              "Live data distance"
            )),
            buildQuantity(
              Decimal.of(value),
              "m",
              UNITSOFM_SYSTEM,
              "m"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectSpeed(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("speed")
      || values.has("mean_speed")
      || values.has("max_speed")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // Speed
      if (values.has("speed")) {
        Observation.Component speedComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "speed",
            "Speed"
          )),
          buildQuantity(
            Decimal.of(values.getString("speed")),
            "m/s",
            UNITSOFM_SYSTEM,
            "m/s"
          )
        );
        components.add(speedComponent);
      }

      //
      // mean_speed
      if (values.has("mean_speed")) {
        Observation.Component meanComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "mean_speed",
            "Mean speed"
          )),
          buildQuantity(
            Decimal.of(values.getString("mean_speed")),
            "m/s",
            UNITSOFM_SYSTEM,
            "m/s"
          )
        );
        components.add(meanComponent);
      }

      //
      // max_speed
      if (values.has("max_speed")) {
        Observation.Component maxComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "max_speed",
            "Max speed"
          )),
          buildQuantity(
            Decimal.of(values.getString("max_speed")),
            "m/s",
            UNITSOFM_SYSTEM,
            "m/s"
          )
        );
        components.add(maxComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-speed", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "speed",
          "Speed"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("speed")) {
          // Collect live_data values
          String liveId = String.format("%s-speed-%d", uuid, i);
          String value = liveElement.getString("speed");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_speed",
              "Live data speed"
            )),
            buildQuantity(
              Decimal.of(value),
              "m/s",
              UNITSOFM_SYSTEM,
              "m/s"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRateInstantaneous(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_rate")) {
      String uuid = dataElement.getString("data_uuid");
      String aggrValue = values.getString("heart_rate");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_instantaneous",
          "Heart Rate Instantaneous"
        )),
        components,
        buildQuantity(
          Decimal.of(aggrValue),
          "beats/min",
          UNITSOFM_SYSTEM,
          "{Beats}/min"
        ),
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("heart_rate")) {
          // Collect live_data values
          String liveId = String.format("%s-heart-rate-%d", uuid, i);
          String value = liveElement.getString("heart_rate");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_heart_rate_instantaneous",
              "Live data heart rate instantaneous"
            )),
            buildQuantity(
              Decimal.of(value),
              "beats/min",
              UNITSOFM_SYSTEM,
              "{Beats}/min"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRateMin(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_rate_min")) {
      String uuid = dataElement.getString("data_uuid");
      String aggrValue = values.getString("heart_rate_min");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate-min", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_min",
          "Min Heart Rate"
        )),
        components,
        buildQuantity(
          Decimal.of(aggrValue),
          "beats/min",
          UNITSOFM_SYSTEM,
          "{Beats}/min"
        ),
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("heart_rate_min")) {
          // Collect live_data values
          String liveId = String.format("%s-heart-rate-min-%d", uuid, i);
          String liveValue = liveElement.getString("heart_rate_min");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_heart_rate_min",
              "Live data min heart rate"
            )),
            buildQuantity(
              Decimal.of(liveValue),
              "beats/min",
              UNITSOFM_SYSTEM,
              "{Beats}/min"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRateMax(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_rate_max")) {
      String uuid = dataElement.getString("data_uuid");
      String aggrValue = values.getString("heart_rate_max");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate-max", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_max",
          "Max Heart Rate"
        )),
        components,
        buildQuantity(
          Decimal.of(aggrValue),
          "beats/min",
          UNITSOFM_SYSTEM,
          "{Beats}/min"
        ),
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("heart_rate_max")) {
          // Collect live_data values
          String liveId = String.format("%s-heart-rate-max-%d", uuid, i);
          String liveValue = liveElement.getString("heart_rate_max");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_heart_rate_max",
              "Live data max heart rate"
            )),
            buildQuantity(
              Decimal.of(liveValue),
              "beats/min",
              UNITSOFM_SYSTEM,
              "{Beats}/min"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectHeartBeatCount(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_beat_count")) {
      String uuid = dataElement.getString("data_uuid");
      String aggrValue = values.getString("heart_beat_count");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-beat-count", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_beat_count",
          "Heart Beat Count"
        )),
        components,
        buildQuantity(
          Decimal.of(aggrValue),
          "beats/min",
          UNITSOFM_SYSTEM,
          "beats/min"
        ),
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);
      return aggregatedObservation;
    }
    return null;
  }






  private static Bundle.Entry collectCadence(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("mean_cadence")
      && values.has("max_cadence")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // mean_cadence
      Observation.Component meanComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "mean_cadence",
          "Mean Cadence"
        )),
        buildQuantity(
          Decimal.of(values.getString("mean_cadence")),
          "m/s",
          UNITSOFM_SYSTEM,
          "m/s"
        )
      );
      components.add(meanComponent);

      //
      // max_cadence
      Observation.Component maxComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "max_cadence",
          "Max Cadence"
        )),
        buildQuantity(
          Decimal.of(values.getString("max_cadence")),
          "m/s",
          UNITSOFM_SYSTEM,
          "m/s"
        )
      );
      components.add(maxComponent);

      //
      // aggregated observation
      String aggregatedId = String.format("%s-cadence", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "cadence",
          "Cadence"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("cadence")) {
          // Collect live_data values
          String liveId = String.format("%s-cadence-%d", uuid, i);
          String value = liveElement.getString("cadence");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_cadence",
              "Live data cadence"
            )),
            buildQuantity(
              Decimal.of(value),
              "m/s",
              UNITSOFM_SYSTEM,
              "m/s"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectPower(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("mean_power")
      && values.has("max_power")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // mean_power
      Observation.Component meanComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "mean_power",
          "Mean Power"
        )),
        buildQuantity(
          Decimal.of(values.getString("mean_power")),
          "watt",
          UNITSOFM_SYSTEM,
          "W"
        )
      );
      components.add(meanComponent);

      //
      // max_power
      Observation.Component maxComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "max_power",
          "Max Power"
        )),
        buildQuantity(
          Decimal.of(values.getString("max_power")),
          "watt",
          UNITSOFM_SYSTEM,
          "W"
        )
      );
      components.add(maxComponent);

      //
      // aggregated observation
      String aggregatedId = String.format("%s-power", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "power",
          "Power"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("power")) {
          // Collect live_data values
          String liveId = String.format("%s-power-%d", uuid, i);
          String value = liveElement.getString("power");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_power",
              "Live data power"
            )),
            buildQuantity(
              Decimal.of(value),
              "watt",
              UNITSOFM_SYSTEM,
              "W"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectAltitude(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("altitude_gain")
      && values.has("altitude_loss")
      && values.has("min_altitude")
      && values.has("max_altitude")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // altitude_gain
      if (values.has("altitude_gain")) {
        Observation.Component gainComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "altitude_gain",
            "Altitude gain"
          )),
          buildQuantity(
            Decimal.of(values.getString("altitude_gain")),
            "m",
            UNITSOFM_SYSTEM,
            "m"
          )
        );
        components.add(gainComponent);
      }

      //
      // altitude_loss
      if (values.has("altitude_loss")) {
        Observation.Component lossComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "altitude_loss",
            "Altitude loss"
          )),
          buildQuantity(
            Decimal.of(values.getString("altitude_loss")),
            "m",
            UNITSOFM_SYSTEM,
            "m"
          )
        );
        components.add(lossComponent);
      }

      //
      // min_altitude
      if (values.has("min_altitude")) {
        Observation.Component minComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "min_altitude",
            "Min altitude"
          )),
          buildQuantity(
            Decimal.of(values.getString("min_altitude")),
            "m",
            UNITSOFM_SYSTEM,
            "m"
          )
        );
        components.add(minComponent);
      }

      //
      // max_altitude
      if (values.has("max_altitude")) {
        Observation.Component maxComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "max_altitude",
            "Max Altitude"
          )),
          buildQuantity(
            Decimal.of(values.getString("max_altitude")),
            "m",
            UNITSOFM_SYSTEM,
            "m"
          )
        );
        components.add(maxComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-altitude", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "power",
          "Power"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectDuration(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("duration")) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // aggregated observation
      String aggregatedId = String.format("%s-duration", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "duration",
          "Duration"
        )),
        components,
        buildQuantity(
          Decimal.of(values.getString("duration")),
          "s",
          UNITSOFM_SYSTEM,
          "s"
        ),
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRate(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("min_heart_rate")
      || values.has("mean_heart_rate")
      || values.has("max_heart_rate")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // Heart Rate Min
      if (values.has("min_heart_rate")) {
        Observation.Component hrMinComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "min_heart_rate",
            "Heart Rate Min"
          )),
          buildQuantity(
            Decimal.of(values.getString("min_heart_rate")),
            "beats/min",
            UNITSOFM_SYSTEM,
            "{Beats}/min"
          )
        );
        components.add(hrMinComponent);
      }

      //
      // Heart Rate Mean
      if (values.has("mean_heart_rate")) {
        Observation.Component hrMeanComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "mean_heart_rate",
            "Heart Rate Mean"
          )),
          buildQuantity(
            Decimal.of(values.getString("mean_heart_rate")),
            "beats/min",
            UNITSOFM_SYSTEM,
            "{Beats}/min"
          )
        );
        components.add(hrMeanComponent);
      }

      //
      // Heart Rate Max
      if (values.has("max_heart_rate")) {
        Observation.Component hrMaxComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "max_heart_rate",
            "Heart Rate Max"
          )),
          buildQuantity(
            Decimal.of(values.getString("max_heart_rate")),
            "beats/min",
            UNITSOFM_SYSTEM,
            "{Beats}/min"
          )
        );
        components.add(hrMaxComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate",
          "Heart Rate"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      //
      // collect live data
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        if (liveElement.has("heart_rate")) {
          // Collect live_data values
          String liveId = String.format("%s-heart-rate-%d", uuid, i);
          String value = liveElement.getString("heart_rate");
          Bundle.Entry liveObs = buildLiveObservation(
            liveId,
            liveElement,
            dataElement,
            buildCodeableConcept(buildCoding(
              SAMSUNG_LIVE_SYSTEM,
              "live_data_heart_rate",
              "Live data heart rate"
            )),
            buildQuantity(
              Decimal.of(value),
              "beats/min",
              UNITSOFM_SYSTEM,
              "{Beats}/min"
            ),
            aggregatedObservation,
            patientEntry
          );
          entries.add(liveObs);
        }
      }

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectRpm(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("mean_rpm")
      && values.has("max_rpm")
    ) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // mean_rpm
      if (values.has("mean_rpm")) {
        Observation.Component meanComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "mean_rpm",
            "Mean rpm"
          )),
          buildQuantity(
            Decimal.of(values.getString("mean_rpm")),
            "Hz",
            UNITSOFM_SYSTEM,
            "hZ"
          )
        );
        components.add(meanComponent);
      }

      //
      // max_rpm
      if (values.has("max_rpm")) {
        Observation.Component maxComponent = buildObservationComponent(
          buildCodeableConcept(buildCoding(
            LOCAL_SYSTEM,
            "max_rpm",
            "Max rpm"
          )),
          buildQuantity(
            Decimal.of(values.getString("max_rpm")),
            "Hz",
            UNITSOFM_SYSTEM,
            "hZ"
          )
        );
        components.add(maxComponent);
      }

      //
      // aggregated observation
      String aggregatedId = String.format("%s-altitude", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "power",
          "Power"
        )),
        components,
        null,
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      return aggregatedObservation;
    }
    return null;
  }

  private static Bundle.Entry collectVo2Max(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("vo2_max")) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Observation.Component> components = new LinkedList<>();

      //
      // aggregated observation
      String aggregatedId = String.format("%s-vo2-max", uuid);
      Bundle.Entry aggregatedObservation = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "vo2_max",
          "Vo2_max"
        )),
        components,
        buildQuantity(
          Decimal.of(values.getString("vo2_max")),
          "mL/kg/min",
          UNITSOFM_SYSTEM,
          "mL/kg/min"
        ),
        parentEntry,
        patientEntry
      );
      entries.add(aggregatedObservation);

      return aggregatedObservation;
    }
    return null;
  }

  private static void collectLocation(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry parentEntry,
    Bundle.Entry patientEntry
  ) {
    if (dataElement.has("location_data")) {
      String uuid = dataElement.getString("data_uuid");

      JSONArray locationData = dataElement.getJSONArray("location_data");
      for (int i = 0; i < locationData.length(); ++i) {
        JSONObject locationElement = locationData.getJSONObject(i);
        String locationId = String.format("%s-location-%d", uuid, i);

        Collection<Observation.Component> components = new LinkedList<>();

        //
        // Latitude
        if (locationElement.has("latitude")) {
          Observation.Component latitudeComponent = buildObservationComponent(
            buildCodeableConcept(buildCoding(
              LOCAL_SYSTEM,
              "latitude",
              "Latitude"
            )),
            buildQuantity(
              Decimal.of(locationElement.getString("latitude")),
              "...", // TODO
              UNITSOFM_SYSTEM,
              "..." // TODO
            )
          );
          components.add(latitudeComponent);
        }

        //
        // Longitude
        if (locationElement.has("longitude")) {
          Observation.Component longitudeComponent = buildObservationComponent(
            buildCodeableConcept(buildCoding(
              LOCAL_SYSTEM,
              "longitude",
              "Longitude"
            )),
            buildQuantity(
              Decimal.of(locationElement.getString("longitude")),
              "...", // TODO
              UNITSOFM_SYSTEM,
              "..." // TODO
            )
          );
          components.add(longitudeComponent);
        }

        //
        // Altitude
        if (locationElement.has("altitude")) {
          Observation.Component altitudeComponent = buildObservationComponent(
            buildCodeableConcept(buildCoding(
              LOCAL_SYSTEM,
              "altitude",
              "Altitude"
            )),
            buildQuantity(
              Decimal.of(locationElement.getString("altitude")),
              "m",
              UNITSOFM_SYSTEM,
              "..." // TODO
            )
          );
          components.add(altitudeComponent);
        }

        //
        // Accuracy
        if (locationElement.has("accuracy")) {
          Observation.Component accuracyComponent = buildObservationComponent(
            buildCodeableConcept(buildCoding(
              LOCAL_SYSTEM,
              "accuracy",
              "Accuracy"
            )),
            buildQuantity(
              Decimal.of(locationElement.getString("accuracy")),
              "...", // TODO
              UNITSOFM_SYSTEM,
              "..." // TODO
            )
          );
          components.add(accuracyComponent);
        }

        Bundle.Entry location = buildLocation(
          locationId,
          locationElement,
          dataElement,
          components,
          parentEntry,
          patientEntry
        );
        entries.add(location);
      }
    }
  }



  private static JSONArray getLiveData(JSONObject dataElement) {
    if (dataElement.has("live_data")) {
      return dataElement.getJSONArray("live_data");
    }
    if (dataElement.has("binning_data")) {
      return dataElement.getJSONArray("binning_data");
    }
    return new JSONArray();
  }

}