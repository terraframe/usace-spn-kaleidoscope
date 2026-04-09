package net.geoprism.climate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.runwaysdk.resource.RemoteURLResource;
import com.runwaysdk.resource.ResourceException;

import net.geoprism.configuration.GeoprismProperties;


public class CachedRemoteURLResource extends RemoteURLResource
{
  
  private static final Logger logger = LoggerFactory.getLogger(CachedRemoteURLResource.class);

  public CachedRemoteURLResource(URL url, String filename)
  {
    super(url, filename);
  }
  
  @Override
  public InputStream openNewStream()
  {
    if (!AppProperties.getCacheData())
    {
      return super.openNewStream();
    }
    
    File storage = new File(GeoprismProperties.getGeoprismFileStorage(), "CachedUrlResources");
    
    if (!storage.exists())
    {
      storage.mkdirs();
    }
    
    File cachedFile = new File(storage, this.getName()); // TODO : This doesn't provide a ton of uniqueness
    
    if (cachedFile.exists())
    {
      logger.info("Retrieving resource from cache at [" + cachedFile.getAbsolutePath() + "].");
      
      try
      {
        return new BufferedInputStream(new FileInputStream(cachedFile));
      }
      catch (FileNotFoundException e)
      {
        throw new ResourceException(e);
      }
    }
    else
    {
      try
      {
        try(FileOutputStream fos = new FileOutputStream(cachedFile))
        {
          try (InputStream is = super.openNewStream())
          {
            IOUtils.copy(is, fos);
          }
        }
        
        return new BufferedInputStream(new FileInputStream(cachedFile));
      }
      catch (IOException e)
      {
        throw new ResourceException(e);
      }
    }
  }

}
