package net.geoprism.climate.model;

import net.geoprism.registry.BusinessType;
import net.geoprism.registry.service.business.BusinessTypeBusinessServiceIF;
import net.geoprism.registry.service.business.ServiceFactory;

public class ExpectedBusinessType extends ExpectedMetadata
{

  public static final ExpectedBusinessType PROJECT_SITE      = new ExpectedBusinessType("ProjectSite");

  public static final ExpectedBusinessType PROJECT_SITE_AREA = new ExpectedBusinessType("ProjectSiteArea");

  public static final ExpectedBusinessType PROGRAM           = new ExpectedBusinessType("Program");

  public ExpectedBusinessType(String code, String... attributes)
  {
    super(code);
  }

  public static ExpectedBusinessType[] getAll()
  {
    return new ExpectedBusinessType[] { PROJECT_SITE, PROJECT_SITE_AREA, PROGRAM };
  }

  public BusinessType getServerObject()
  {
    BusinessTypeBusinessServiceIF service = ServiceFactory.getBean(BusinessTypeBusinessServiceIF.class);

    return service.getByCode(this.code);
  }

}
