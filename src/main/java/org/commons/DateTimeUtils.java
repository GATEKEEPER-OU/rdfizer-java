package org.commons;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Riccardo Pala (riccardo.pala@open.ac.uk)
 * @todo description
 */
public class DateTimeUtils {

  /**
   * @todo description
   */
  public static String cast(String date) {
    if (date.length() == 10) {
      try {
        String pattern = "yyyy-MM-dd";
        Date parsedDate = DateUtils.parseDate(date, pattern);
        // @todo change ISO_DATETIME_TIME_ZONE_FORMAT -> ISO_8601_EXTENDED_DATETIME_FORMAT
        String isoDateTime = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(parsedDate);
//        System.out.println(" date >>>> " + date); // DEBUG
//        System.out.println(" parsedDate >>>> " + parsedDate); // DEBUG
//        System.out.println(" isoDateTime >>>> " + isoDateTime); // DEBUG
        return isoDateTime;

      } catch (ParseException e) {
        // @todo Message
        e.printStackTrace(); // DEBUG
      }
    }
    return date;
  }

  //--------------------------------------------------------------------------//
  // Class definition
  //--------------------------------------------------------------------------//

  /**
   * @todo description
   */
  private DateTimeUtils() {
  }

}