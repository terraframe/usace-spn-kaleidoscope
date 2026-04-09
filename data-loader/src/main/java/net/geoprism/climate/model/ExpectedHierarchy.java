package net.geoprism.climate.model;

import net.geoprism.registry.model.ServerHierarchyType;
import net.geoprism.registry.service.business.HierarchyTypeBusinessServiceIF;
import net.geoprism.registry.service.business.ServiceFactory;

public class ExpectedHierarchy extends ExpectedMetadata
{

  public static final ExpectedHierarchy ADMINISTRATIVE = new ExpectedHierarchy("ADM_H");

  public ExpectedHierarchy(String code)
  {
    super(code);
  }

  public static ExpectedHierarchy[] getAll()
  {
    return new ExpectedHierarchy[] { ADMINISTRATIVE };
  }

  public ServerHierarchyType getServerObject()
  {
    HierarchyTypeBusinessServiceIF service = ServiceFactory.getBean(HierarchyTypeBusinessServiceIF.class);
    return service.get(code);
  }

}
