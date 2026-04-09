/**
 * Copyright (c) 2022 TerraFrame, Inc. All rights reserved.
 *
 * This file is part of Geoprism(tm).
 *
 * Geoprism(tm) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Geoprism(tm) is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Geoprism(tm).  If not, see <http://www.gnu.org/licenses/>.
 */
package net.geoprism.climate;

import com.runwaysdk.configuration.CommonsConfigurationResolver;
import com.runwaysdk.configuration.ConfigurationReaderIF;


public class AppProperties
{
  private ConfigurationReaderIF props;

  private static class Singleton 
  {
    private static AppProperties INSTANCE = new AppProperties();
  }

  public AppProperties()
  {
    this.props = CommonsConfigurationResolver.getInMemoryConfigurator();
  }

  public static boolean getCacheData()
  {
    return Singleton.INSTANCE.props.getBoolean("climate.cacheData", false);
  }
}
