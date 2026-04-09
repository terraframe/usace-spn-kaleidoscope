package net.geoprism.climate;

import java.util.Calendar;
import java.util.Date;

import com.runwaysdk.dataaccess.graph.attributes.ValueOverTime;

import net.geoprism.registry.GeoRegistryUtil;

public class DataConstants
{
  public static final Date                    START_DATE;

  static
  {
    Calendar cal = Calendar.getInstance(GeoRegistryUtil.SYSTEM_TIMEZONE);
    cal.clear();
    cal.set(1950, Calendar.JANUARY, 1);

    START_DATE = cal.getTime();
  }

  public static final Date   END_DATE = ValueOverTime.INFINITY_END_DATE;

  public static final String SOURCE   = "USACE SPN";
}
