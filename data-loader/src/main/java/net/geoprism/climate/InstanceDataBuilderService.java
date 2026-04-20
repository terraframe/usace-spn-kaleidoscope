package net.geoprism.climate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.commongeoregistry.adapter.dataaccess.GeoObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runwaysdk.Pair;
import com.runwaysdk.resource.ApplicationResource;
import com.runwaysdk.resource.FileResource;
import com.runwaysdk.session.Request;

import net.geoprism.climate.dataset.AbstractDataset;
import net.geoprism.climate.dataset.EdgeObjectDataset;
import net.geoprism.climate.dataset.GeoObjectDataset;
import net.geoprism.climate.dataset.GeoObjectDataset.DatasetType;
import net.geoprism.climate.model.ExpectedGraphType;
import net.geoprism.climate.model.ExpectedType;
import net.geoprism.data.importer.BasicColumnFunction;
import net.geoprism.data.importer.ShapefileFunction;
import net.geoprism.registry.graph.DataSource;
import net.geoprism.registry.io.ConstantShapefileFunction;
import net.geoprism.registry.service.business.DataSourceBusinessServiceIF;

@Service
public class InstanceDataBuilderService
{

  public static final String USACE_SPN = "usace-spn";

  public static class Params
  {
    public boolean    local;

    public String     path;

    public boolean    ignoreFeedback;

    public boolean    types;

    public boolean    edges;

    public DataSource source;

    public static Params parse(CommandLine cmd)
    {
      Params params = new Params();
      params.local = Boolean.valueOf(cmd.getOptionValue("local", "true"));
      params.path = cmd.getOptionValue("source", "files/");
      params.ignoreFeedback = Boolean.valueOf(cmd.getOptionValue("ignoreFeedback", "true"));
      params.types = Boolean.valueOf(cmd.getOptionValue("types", "true"));
      params.edges = Boolean.valueOf(cmd.getOptionValue("edges", "true"));

      return params;
    }
  }

  private static final Logger         logger = LoggerFactory.getLogger(InstanceDataBuilderService.class);

  @Autowired
  private DataSourceBusinessServiceIF sourceService;

  @Request
  public void build(Params params) throws Throwable
  {
    params.source = this.sourceService.getByCode(DataConstants.SOURCE).orElseThrow();

    this.doIt(params);
  }

  public void doIt(Params params) throws Throwable
  {
    try
    {
      this.importInstanceData(params);
    }
    catch (Throwable t)
    {
      logger.error("Encountered error while importing data.", t);
      throw t;
    }
  }

  public void importInstanceData(Params params) throws Throwable
  {
    for (AbstractDataset dataset : this.getAllDatasets(params))
    {
      try
      {

        dataset.synchronizeDatabase();
      }
      catch (Exception e)
      {
        throw new RuntimeException("Unable to import dataset:" + e.getMessage(), e);
      }
    }
  }

  public List<AbstractDataset> getAllDatasets(Params params) throws Throwable
  {
    List<AbstractDataset> datasets = new ArrayList<AbstractDataset>();

    if (params.types)
    {
      datasets.addAll(this.getGeoObjectDatasets(params));
    }

    if (params.edges)
    {
      datasets.addAll(this.getEdgeDatasets(params));
    }

    return datasets;
  }

  public List<AbstractDataset> getGeoObjectDatasets(Params params) throws Throwable
  {
    List<AbstractDataset> datasets = new ArrayList<AbstractDataset>();

    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "Creeks"), ExpectedType.CREEK, buildAttributeMapping("NAME", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "Land_Parcels"), ExpectedType.LAND_PARCEL, buildAttributeMapping("APN", "APN", //
        new Pair<String, String>("landUse", "LANDUSE"), //
        new Pair<String, String>("lotAcres", "LOTACRES"), //
        new Pair<String, String>("estimatedValue", "Value"), //
        new Pair<String, String>("mailAddress", "MAILADDR") //
    ), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "basic_scenario"), ExpectedType.FLOOD_SCENARIO, buildAttributeMapping("Label", "Code", //
        new Pair<String, String>("depth", "Depth"), //
        new Pair<String, String>("velocity", "Velocity") //
    ), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "combo_scenario"), ExpectedType.FLOOD_SCENARIO, buildAttributeMapping("Label", "Code", //
        new Pair<String, String>("depth", "Depth"), //
        new Pair<String, String>("velocity", "Velocity") //
        ), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "UG_Lakes"), ExpectedType.LAKE, buildAttributeMapping("NAME", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "UG_MajRoads"), ExpectedType.ROAD, buildAttributeMapping("HWYNAME", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "UpperGuad_ProjectReaches"), ExpectedType.PROJECT_REACH, buildAttributeMapping("Reach_1", "Reach_1"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "UpperGuad_ProjectArea_Buffer1000ft"), ExpectedType.PROJECT_AREA, buildAttributeMapping("Label", "OBJECTID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "UG_StrucsOnly_Ind_0824"), ExpectedType.STRUCTURE, buildAttributeMapping("Struc_Name", "Struc_Name"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "Demographics_by_Census_Block_CLIPPED"), ExpectedType.CENSUS_BLOCK, buildAttributeMapping("FACILITYID", "FACILITYID", new Pair<String, String>("totalPop", "POPTOTAL"), //
        new Pair<String, String>("raceWhite", "RACEWHITE"), //
        new Pair<String, String>("raceBlack", "RACEBLACK"), //
        new Pair<String, String>("raceAsian", "RACEASIAN"), //
        new Pair<String, String>("raceOther", "RACEOTHER"), //
        new Pair<String, String>("raceHispanic", "RACEHISPAN") //
    ), params.ignoreFeedback, params.source));

    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "ComboPlan_Bridges"), ExpectedType.CP_BRIDGE, buildAttributeMapping("ID", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "ComboPlan_Floodwalls"), ExpectedType.CP_FLOODWALL, buildAttributeMapping("ID", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "ComboPlan_SlopeRepair"), ExpectedType.CP_SLOPE_REPAIR, buildAttributeMapping("ID", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "ComboPlan_ChannelWideningFtprnt"), ExpectedType.CP_CHANNEL_FTPRNT, buildAttributeMapping("ID", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "ComboPlan_ProposedGravelAugmentation"), ExpectedType.CP_PROP_GRAVEL, buildAttributeMapping("ID", "ID"), params.ignoreFeedback, params.source));
    datasets.add(new GeoObjectDataset(DatasetType.SHAPEFILE, getRawShapefileResource(params, "ComboPlan_ProposedRiprap"), ExpectedType.CP_PROP_RIPRAP, buildAttributeMapping("ID", "ID"), params.ignoreFeedback, params.source));

    return datasets;
  }

  public List<AbstractDataset> getEdgeDatasets(Params params) throws Throwable
  {
    List<AbstractDataset> datasets = new ArrayList<AbstractDataset>();

    datasets.add(new EdgeObjectDataset(getEdgeResource(params, "parcel-flood-risk"), ExpectedGraphType.HAS_FLOOD_RISK, params.source, true));
    datasets.add(new EdgeObjectDataset(getEdgeResource(params, "flood-risk"), ExpectedGraphType.HAS_FLOOD_RISK, params.source, true));
    datasets.add(new EdgeObjectDataset(getEdgeResource(params, "flows-into"), ExpectedGraphType.FLOWS_INTO, params.source, true));
    datasets.add(new EdgeObjectDataset(getEdgeResource(params, "located-in"), ExpectedGraphType.LOCATED_IN, params.source, true));
    datasets.add(new EdgeObjectDataset(getEdgeResource(params, "has_mitigation"), ExpectedGraphType.HAS_MITIGATION, params.source, true));

    return datasets;
  }

  private Map<String, ShapefileFunction> buildAttributeMapping(String label, String code)
  {
    Map<String, ShapefileFunction> attributeColumnMapping = new HashMap<String, ShapefileFunction>();

    attributeColumnMapping.put(GeoObject.CODE, new BasicColumnFunction(code));
    attributeColumnMapping.put(GeoObject.DISPLAY_LABEL, new BasicColumnFunction(label));

    return attributeColumnMapping;
  }

  private Map<String, ShapefileFunction> buildAttributeMapping(ShapefileFunction label, String code)
  {
    Map<String, ShapefileFunction> attributeColumnMapping = new HashMap<String, ShapefileFunction>();
    
    attributeColumnMapping.put(GeoObject.CODE, new BasicColumnFunction(code));
    attributeColumnMapping.put(GeoObject.DISPLAY_LABEL, label);
    
    return attributeColumnMapping;
  }
  
  @SafeVarargs
  private Map<String, ShapefileFunction> buildAttributeMapping(String label, String code, Pair<String, String>... mappings)
  {
    Map<String, ShapefileFunction> attributeColumnMapping = new HashMap<String, ShapefileFunction>();

    attributeColumnMapping.put(GeoObject.CODE, new BasicColumnFunction(code));
    attributeColumnMapping.put(GeoObject.DISPLAY_LABEL, new BasicColumnFunction(label));

    for (Pair<String, String> mapping : mappings)
    {
      attributeColumnMapping.put(mapping.getFirst(), new BasicColumnFunction(mapping.getSecond()));
    }

    return attributeColumnMapping;
  }

  @SafeVarargs
  private Map<String, ShapefileFunction> buildAttributeMapping(ShapefileFunction label, String code, Pair<String, String>... mappings)
  {
    Map<String, ShapefileFunction> attributeColumnMapping = new HashMap<String, ShapefileFunction>();
    
    attributeColumnMapping.put(GeoObject.CODE, new BasicColumnFunction(code));
    attributeColumnMapping.put(GeoObject.DISPLAY_LABEL, label);
    
    for (Pair<String, String> mapping : mappings)
    {
      attributeColumnMapping.put(mapping.getFirst(), new BasicColumnFunction(mapping.getSecond()));
    }
    
    return attributeColumnMapping;
  }
  
  protected ApplicationResource getRawShapefileResource(Params params, String fileName) throws MalformedURLException
  {
    return this.getRawShapefileResource(params, USACE_SPN, "files", fileName);
  }

  protected ApplicationResource getRawExcelResource(Params params, String fileName) throws MalformedURLException
  {
    return this.getDataResource(params, USACE_SPN, "files", fileName, "xlsx");
  }

  protected ApplicationResource getRawShapefileResource(Params params, String project, String version, String fileName) throws MalformedURLException
  {
    return this.getDataResource(params, project, version, fileName, "zip");
  }

  protected ApplicationResource getEdgeResource(Params params, String fileName) throws MalformedURLException
  {
    return this.getEdgeResource(params, USACE_SPN, "files", fileName, "json");
  }

  protected ApplicationResource getEdgeResource(Params params, String project, String version, String fileName, String extension) throws MalformedURLException
  {
    if (!params.local)
    {
      return new CachedRemoteURLResource(new URL("https://dl.cloudsmith.io/public/terraframe/" + project + "/raw/" + version + "/" + fileName + "." + extension), fileName + "." + extension);
    }

    File root = new File(params.path);
    File directory = new File(new File(root, project), "edges");

    return new FileResource(new File(directory, fileName + ".json"));
  }

  protected ApplicationResource getDataResource(Params params, String project, String version, String fileName, String extension) throws MalformedURLException
  {
    if (!params.local)
    {
      return new CachedRemoteURLResource(new URL("https://dl.cloudsmith.io/public/terraframe/" + project + "/raw/" + version + "/" + fileName + "." + extension), fileName + "." + extension);
    }

    File root = new File(params.path);
    File directory = new File(new File(root, project), "data");

    return new FileResource(new File(directory, fileName + "." + extension));
  }

}
