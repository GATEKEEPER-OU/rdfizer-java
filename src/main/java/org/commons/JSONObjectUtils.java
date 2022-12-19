package org.commons;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class JSONObjectUtils {

  public static final Logger LOGGER = LoggerFactory.getLogger(JSONObjectUtils.class);

  /**
   * @todo description
   * */
  public static String getId(JSONObject resource, String key) {
    String uuid = resource.getString(key);
    if (StringUtils.isBlank(uuid)) {
      uuid = UUID.randomUUID().toString();
      String message = String.format("Property '%s' is missing, generated a random one: %s", key, uuid);
      LOGGER.warn(message);
      return uuid;
    }
    return uuid;
  }

  /**
   * @todo description
   * */
  public static String getElementValue(JSONObject element, String key) {
    if (element.has(key)) {
      String value = element.getString(key);
      if (org.apache.commons.lang.StringUtils.isBlank(value)) {
        String message = String.format("Property '%s' is blank", key);
        LOGGER.warn(message);
      }
      return value;
    }
    return ""; // TODO request to remove empty values
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private JSONObjectUtils() {
  }

}