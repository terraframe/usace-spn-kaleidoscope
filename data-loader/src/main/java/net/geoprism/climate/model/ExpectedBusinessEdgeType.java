package net.geoprism.climate.model;

import net.geoprism.registry.BusinessEdgeType;
import net.geoprism.registry.service.business.BusinessEdgeTypeBusinessServiceIF;
import net.geoprism.registry.service.business.ServiceFactory;

public class ExpectedBusinessEdgeType extends ExpectedMetadata
{

  public static final ExpectedBusinessEdgeType PART_OF         = new ExpectedBusinessEdgeType("PartOf");

  public static final ExpectedBusinessEdgeType AREA_WITHIN     = new ExpectedBusinessEdgeType("AreaWithin");

  public static final ExpectedBusinessEdgeType PROGRAM_PROJECT = new ExpectedBusinessEdgeType("ProgramProject");

  public static final ExpectedBusinessEdgeType SUB_PROGRAM     = new ExpectedBusinessEdgeType("SubProgram");

  public ExpectedBusinessEdgeType(String code)
  {
    super(code);
  }

  public static ExpectedBusinessEdgeType[] getAll()
  {
    return new ExpectedBusinessEdgeType[] { PART_OF, AREA_WITHIN, PROGRAM_PROJECT, SUB_PROGRAM };
  }

  public BusinessEdgeType getServerObject()
  {
    BusinessEdgeTypeBusinessServiceIF service = ServiceFactory.getBean(BusinessEdgeTypeBusinessServiceIF.class);

    return service.getByCode(this.code).orElseThrow();
  }

}
