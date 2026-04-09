package net.geoprism.climate.model;

import net.geoprism.registry.Organization;
import net.geoprism.registry.model.ServerOrganization;

public class ExpectedOrganization extends ExpectedMetadata
{
  public static final ExpectedOrganization USACE = new ExpectedOrganization("USACE");
  
  public ExpectedOrganization(String code)
  {
    super(code);
  }
  
  public ServerOrganization getServerObject()
  {
    return ServerOrganization.get(Organization.getByCode(code));
  }
}
