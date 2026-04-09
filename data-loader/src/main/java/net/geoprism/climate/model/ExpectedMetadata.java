package net.geoprism.climate.model;

/**
 * The list of types that are expected to be imported from the model.xml file.
 * 
 * @author rrowlands
 */
public class ExpectedMetadata
{
  public String code;
  
  public ExpectedMetadata(String code)
  {
    this.code = code;
  }
  
  public String getCode()
  {
    return this.code;
  }
}
