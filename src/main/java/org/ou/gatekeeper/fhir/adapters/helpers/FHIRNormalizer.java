package org.ou.gatekeeper.fhir.adapters.helpers;

import org.commons.UrlUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class FHIRNormalizer {

  /**
   * @todo description
   * explain with a json example, the result with the resourceId
   * @param dataset dataset to normalize
   */
  public static void normalize(File dataset, File output) {
    try (
      InputStream datasetInputStream = new FileInputStream(dataset)
    ) {
      JSONTokener tokener = new JSONTokener(datasetInputStream);
      JSONObject json = new JSONObject(tokener);
      JSONArray entries = json.getJSONArray("entry");
      siftEntries(entries);
      save(json, output);

    } catch (FileNotFoundException e) {
      // @todo Message ?
      e.printStackTrace();
    } catch (IOException e) {
      // @todo Message ?
      e.printStackTrace();
    }
  }

  //--------------------------------------------------------------------------//
  // Private methods
  //--------------------------------------------------------------------------//

  /**
   * Loop a
   * ...@todo put the reference to FHIR entries array definition
   * array looking for resources that contain component.
   * @param entries to sift
   */
  private static void siftEntries(JSONArray entries) {
    for (int i = 0; i < entries.length(); ++i) {
      JSONObject entry = entries.getJSONObject(i);
      JSONObject resource = entry.getJSONObject("resource");

      // add systemDomainName to identifier.system or code.system
      if (resource.getString("resourceType").equals("Patient")) {
        JSONArray identifiers = resource.getJSONArray("identifier");
        appendDomainName(identifiers);
      }
      if (resource.has("code")) {
        JSONArray codes = resource
          .getJSONObject("code")
          .getJSONArray("coding");
        appendDomainName(codes);
      }
      // ---

      // connect resource to components
      if (resource.getString("resourceType").equals("Observation")) {
        String resourceId = entry.getString("fullUrl");
        if (resource.has("component")) {
          JSONArray components = resource.getJSONArray("component");
          connectResourceToComponents(components, resourceId);
        }
      }
      // ---
    }
  }

  /**
   * @todo description
   * @param collection of identifiers or codes
   */
  private static void appendDomainName(JSONArray collection) {
    for (int i = 0; i < collection.length(); ++i) {
      JSONObject item = collection.getJSONObject(i);
      String system = item.getString("system");
      // --- workround
      if (system.startsWith("identifier=")) {
        system = system.replace("identifier=", "");
      }
      // ---
      String domainName = UrlUtils
        .getHost(system)
        .replace(".", "_");
      item.put("systemDomainName", domainName);
    }
  }

  /**
   * @todo description
   */
  private static void connectResourceToComponents(JSONArray components, String resourceId) {
    for (int i = 0; i < components.length(); ++i) {
      JSONObject component = components.getJSONObject(i);
      component.put("resourceId", resourceId);
    }
  }

  /**
   * Save a JSON Object on file.
   * @param json object to save
   * @param output the file where save the object
   */
  private static void save(JSONObject json, File output) {
    try (
      FileWriter file = new FileWriter(output)
    ) {
      file.write(json.toString());

    } catch (IOException e) {
      // @todo Message ?
      e.printStackTrace();
    }
  }

  /**
   * @todo description
   */
  private FHIRNormalizer() {
  }

}