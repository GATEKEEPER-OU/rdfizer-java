package org.ou.gatekeeper.fhir.adapters;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.generator.FHIRGenerator;
import com.ibm.fhir.model.generator.exception.FHIRGeneratorException;
import com.ibm.fhir.model.resource.Bundle;
import com.ibm.fhir.model.resource.Observation;
import com.ibm.fhir.model.type.Decimal;
import com.ibm.fhir.model.type.code.BundleType;
import com.ibm.fhir.model.visitor.Visitable;
import org.apache.commons.io.FileUtils;
import org.commons.ResourceUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ou.gatekeeper.fhir.adapters.helpers.FHIRNormalizer;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;

import static org.apache.commons.collections.CollectionUtils.addIgnoreNull;
import static org.commons.ResourceUtils.generateUniqueFilename;
import static org.ou.gatekeeper.fhir.adapters.builders.FHIRSamsungHealthBuilder.*;

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
    Collection<Bundle.Entry> members = new LinkedList<>();
    addIgnoreNull(members,
      collectFloor(entries, dataElement, patientEntry));
    //
    addIgnoreNull(members,
      collectCalories(entries, dataElement, patientEntry));
    addIgnoreNull(members,
      collectCount(entries, dataElement, patientEntry));
    addIgnoreNull(members,
      collectDistance(entries, dataElement, patientEntry));
    addIgnoreNull(members,
      collectSpeed(entries, dataElement, patientEntry));
    //
    addIgnoreNull(members,
      collectHeartRate(entries, dataElement, patientEntry));
    addIgnoreNull(members,
      collectHeartRateMin(entries, dataElement, patientEntry));
    addIgnoreNull(members,
      collectHeartRateMax(entries, dataElement, patientEntry));
    addIgnoreNull(members,
      collectHeartBeatCount(entries, dataElement, patientEntry));

    // ...

    addIgnoreNull(members,
      collectCadence(entries, dataElement, patientEntry));
    // ...

    Bundle.Entry obs = buildMainObservation(dataElement, members, patientEntry);
    entries.add(obs);
  }

  private static Bundle.Entry collectFloor(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("floor")) {
      String uuid = dataElement.getString("data_uuid");

      Collection<Bundle.Entry> members = new LinkedList<>();
      Collection<Observation.Component> components = new LinkedList<>();
      //
      // floor
      Observation.Component floorComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "floor",
          "Floor"
        )),
        buildQuantity(
          Decimal.of(values.getString("floor")),
          "...", // TODO missing
          UNITSOFM_SYSTEM,
          "..." // TODO missing
        )
      );
      components.add(floorComponent);

      //
      // aggregated observation
      String aggregatedId = String.format("%s-floor", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "floor",
          "Floor"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectCalories(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("calories")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        // Collect live_data values
        String liveId = String.format("%s-calories-%d", uuid, i);
        String value = liveElement.getString("calories");
        Bundle.Entry liveObs = buildLiveObservation(
          liveId,
          liveElement,
          dataElement,
          buildCodeableConcept(buildCoding(
            SAMSUNG_LIVE_SYSTEM,
            "live_data_calories",
            "Live data calories"
          )),
          buildQuantity(
            Decimal.of(value),
            "kcal/s",
            UNITSOFM_SYSTEM,
            "kcal/s"
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Calories
      Observation.Component caloriesComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "calories",
          "Calories"
        )),
        buildQuantity(
          Decimal.of(values.getString("calories")),
          "kcal/s",
          UNITSOFM_SYSTEM,
          "kcal/s"
        )
      );
      components.add(caloriesComponent);

      // TODO collect others components, if exist
      // max_caloricburn_rate
      // mean_caloricburn_rate

      //
      // aggregated observation
      String aggregatedId = String.format("%s-calories", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "calories",
          "Calories"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectCount(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("count")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
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
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Calories
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

      // TODO collect others components, if exist
      // type???

      //
      // aggregated observation
      String aggregatedId = String.format("%s-count", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "count",
          "Count"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectDistance(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("distance")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
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
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

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

      // TODO collect others components, if exist
      // incline_distance
      // decline_distance

      //
      // aggregated observation
      String aggregatedId = String.format("%s-distance", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "distance",
          "Distance"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectSpeed(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("speed")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        // Collect live_data values
        String liveId = String.format("%s-speed-%d", uuid, i);
        String value = liveElement.getString("calories");
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
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Speed
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

      // TODO collect others components, if exist
      // max_speed
      // mean_speed

      //
      // aggregated observation
      String aggregatedId = String.format("%s-speed", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "speed",
          "Speed"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRate(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_rate")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
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
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Heart Rate
      Observation.Component hrComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate",
          "Heart Rate"
        )),
        buildQuantity(
          Decimal.of(values.getString("heart_rate")),
          "beats/min",
          UNITSOFM_SYSTEM,
          "{Beats}/min"
        )
      );
      components.add(hrComponent);

      // TODO collect others components, if exist

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate",
          "Heart Rate"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRateMin(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_rate_min")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        // Collect live_data values
        String liveId = String.format("%s-heart-rate-min-%d", uuid, i);
        String value = liveElement.getString("heart_rate_min");
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
            Decimal.of(value),
            "beats/min",
            UNITSOFM_SYSTEM,
            "{Beats}/min"
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Min Heart Rate
      Observation.Component hrMinComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_min",
          "Min Heart Rate"
        )),
        buildQuantity(
          Decimal.of(values.getString("heart_rate_min")),
          "beats/min",
          UNITSOFM_SYSTEM,
          "{Beats}/min"
        )
      );
      components.add(hrMinComponent);

      // TODO collect others components, if exist

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate-min", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_min",
          "Min Heart Rate"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectHeartRateMax(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_rate_max")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        // Collect live_data values
        String liveId = String.format("%s-heart-rate-max-%d", uuid, i);
        String value = liveElement.getString("heart_rate_max");
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
            Decimal.of(value),
            "beats/min",
            UNITSOFM_SYSTEM,
            "{Beats}/min"
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Max Heart Rate
      Observation.Component hrMaxComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_max",
          "Max Heart Rate"
        )),
        buildQuantity(
          Decimal.of(values.getString("heart_rate_max")),
          "beats/min",
          UNITSOFM_SYSTEM,
          "{Beats}/min"
        )
      );
      components.add(hrMaxComponent);

      // TODO collect others components, if exist

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-rate-max", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_rate_max",
          "Max Heart Rate"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }

  private static Bundle.Entry collectHeartBeatCount(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if (values.has("heart_beat_count")) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
        // Collect live_data values
        String liveId = String.format("%s-heart-beat-count-%d", uuid, i);
        String value = liveElement.getString("heart_beat_count");
        Bundle.Entry liveObs = buildLiveObservation(
          liveId,
          liveElement,
          dataElement,
          buildCodeableConcept(buildCoding(
            SAMSUNG_LIVE_SYSTEM,
            "live_data_heart_beat_count",
            "Live data heart beat count"
          )),
          buildQuantity(
            Decimal.of(value),
            "beats/min",
            UNITSOFM_SYSTEM,
            "{Beats}/min"
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

      Collection<Observation.Component> components = new LinkedList<>();
      //
      // Heart Beat Count
      Observation.Component beatCountComponent = buildObservationComponent(
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_beat_count",
          "Heart Beat Count"
        )),
        buildQuantity(
          Decimal.of(values.getString("heart_beat_count")),
          "beats/min",
          UNITSOFM_SYSTEM,
          "beats/min"
        )
      );
      components.add(beatCountComponent);

      // TODO collect others components, if exist

      //
      // aggregated observation
      String aggregatedId = String.format("%s-heart-beat-count", uuid);
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "heart_beat_count",
          "Heart Beat Count"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
  }






  private static Bundle.Entry collectCadence(
    Collection<Bundle.Entry> entries,
    JSONObject dataElement,
    Bundle.Entry patientEntry
  ) {
    JSONObject values = dataElement.getJSONArray("values").getJSONObject(0);
    if ( values.has("mean_cadence")
      && values.has("max_cadence")
    ) {
      String uuid = dataElement.getString("data_uuid");

      //
      // collect live data
      Collection<Bundle.Entry> members = new LinkedList<>();
      JSONArray liveData = getLiveData(dataElement);
      for (int i = 0; i < liveData.length(); ++i) {
        JSONObject liveElement = liveData.getJSONObject(i);
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
          )
        );
        members.add(liveObs);
        entries.add(liveObs);
      }

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
      Bundle.Entry obs = buildAggregatedObservation(
        aggregatedId,
        dataElement,
        buildCodeableConcept(buildCoding(
          LOCAL_SYSTEM,
          "cadence",
          "Cadence"
        )),
        components,
        members,
        patientEntry
      );
      entries.add(obs);
      return obs;
    }
    return null;
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