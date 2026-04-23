package net.geoprism.climate.model;

import net.geoprism.registry.model.ServerGeoObjectType;

public class ExpectedType extends ExpectedMetadata
{
  public static final ExpectedType CREEK             = new ExpectedType("Creek");

  public static final ExpectedType LAND_PARCEL       = new ExpectedType("LandParcel");

  public static final ExpectedType FLOOD_SCENARIO    = new ExpectedType("FloodScenario");

  public static final ExpectedType LAKE              = new ExpectedType("Lake");

  public static final ExpectedType ROAD              = new ExpectedType("Road");

  public static final ExpectedType CP_BRIDGE         = new ExpectedType("ComboPlanBridge");

  public static final ExpectedType CP_FLOODWALL      = new ExpectedType("ComboPlanFloodwall");

  public static final ExpectedType CP_SLOPE_REPAIR   = new ExpectedType("ComboPlanSlopeRepair");

  public static final ExpectedType CP_CHANNEL_FTPRNT = new ExpectedType("ComboPlanChannelFtprnt");

  public static final ExpectedType CP_PROP_GRAVEL    = new ExpectedType("ComboPlanPropGravel");

  public static final ExpectedType CP_PROP_RIPRAP    = new ExpectedType("ComboPlanPropRiprap");

  public static final ExpectedType PROJECT_REACH     = new ExpectedType("ProjectReach");

  public static final ExpectedType PROJECT_AREA      = new ExpectedType("ProjectArea");

  public static final ExpectedType STRUCTURE         = new ExpectedType("Structure");

//  public static final ExpectedType CENSUS_BLOCK      = new ExpectedType("CensusBlock");

  public ExpectedType(String code, String... attributes)
  {
    super(code);
  }

  public static ExpectedType[] getAll()
  {
    return new ExpectedType[] { STRUCTURE, PROJECT_AREA, PROJECT_REACH, CREEK, LAND_PARCEL, FLOOD_SCENARIO, LAKE, ROAD, CP_BRIDGE, CP_FLOODWALL, CP_SLOPE_REPAIR, CP_CHANNEL_FTPRNT, CP_PROP_GRAVEL, CP_PROP_RIPRAP };
  }

  public ServerGeoObjectType getServerObject()
  {
    return ServerGeoObjectType.get(code);
  }

}
