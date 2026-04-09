package net.geoprism.climate.dataset;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.runwaysdk.dataaccess.graph.GraphDBService;
import com.runwaysdk.dataaccess.graph.GraphRequest;
import com.runwaysdk.resource.ApplicationResource;

import net.geoprism.climate.DataConstants;
import net.geoprism.climate.model.ExpectedHierarchy;
import net.geoprism.climate.model.ExpectedType;
import net.geoprism.data.importer.ShapefileFunction;
import net.geoprism.registry.graph.DataSource;
import net.geoprism.registry.io.Location;
import net.geoprism.registry.model.ServerGeoObjectType;

public class GeoObjectDataset extends AbstractDataset
{

  public static enum DatasetType {
    SHAPEFILE, EXCEL
  }

  private static final Logger              logger = LoggerFactory.getLogger(GeoObjectDataset.class);

  protected ApplicationResource            resource;

  protected ExpectedType                   expectedType;

  protected ExpectedHierarchy              expectedHierarchy;

  protected Location[]                     parents;

  protected Map<String, ShapefileFunction> attributeColumnMappings;

  protected Date                           startDate;

  protected Date                           endDate;

  protected boolean                        ignoreFeedbackErrors;

  protected DataSource                     source;

  protected DatasetType                    type;

  public GeoObjectDataset(DatasetType type, ApplicationResource resource, ExpectedType expectedType, Map<String, ShapefileFunction> columnMappings, boolean ignoreFeedbackErrors, DataSource source)
  {
    super();

    this.type = type;
    this.resource = resource;
    this.expectedType = expectedType;
    this.attributeColumnMappings = columnMappings;
    this.expectedHierarchy = null;
    this.parents = null;
    this.startDate = DataConstants.START_DATE;
    this.endDate = DataConstants.END_DATE;
    this.ignoreFeedbackErrors = ignoreFeedbackErrors;
    this.source = source;
  }

  public GeoObjectDataset(DatasetType type, ApplicationResource resource, ExpectedType expectedType, ExpectedHierarchy expectedHierarchy, Map<String, ShapefileFunction> columnMappings, Location[] parents, Date startDate, Date endDate, boolean ignoreFeedbackErrors)
  {
    super();

    this.type = type;
    this.resource = resource;
    this.expectedType = expectedType;
    this.expectedHierarchy = expectedHierarchy;
    this.parents = parents;
    this.attributeColumnMappings = columnMappings;
    this.startDate = startDate;
    this.endDate = endDate;
    this.ignoreFeedbackErrors = ignoreFeedbackErrors;
  }

  public ExpectedType getExpectedType()
  {
    return this.expectedType;
  }

  public DataShapefileImporter getShapefileConfiguration()
  {
    return new DataShapefileImporter(resource, expectedType, expectedHierarchy, attributeColumnMappings, parents, startDate, endDate, this.ignoreFeedbackErrors, source);
  }

  public DataExcelImporter getExcelConfiguration()
  {
    return new DataExcelImporter(resource, expectedType, expectedHierarchy, attributeColumnMappings, parents, startDate, endDate, this.ignoreFeedbackErrors, source);
  }

  @Override
  public void synchronize() throws Throwable
  {
    // if (currentVersion > 0)
    // {
    // // this.destroyAll(currentVersion);
    //
    // throw new UnsupportedOperationException("Unable to automatically patch
    // this dataset.");
    // }

    this.buildLatest();
  }

  public void destroyAll(Integer currentVersion)
  {
    // TODO : And what about Edges that reference this data???

    ServerGeoObjectType serverGOTT = this.getExpectedType().getServerObject();

    logger.info("About to delete all data on GeoObjectType [" + this.expectedType.code + "].");

    String statement = "DELETE FROM " + serverGOTT.getMdVertex().getDBClassName() + " UNSAFE";

    GraphDBService service = GraphDBService.getInstance();
    GraphRequest request = service.getGraphDBRequest();
    Map<String, Object> parameters = new HashMap<String, Object>();

    service.command(request, statement, parameters);
  }

  // @Transaction
  public void buildLatest() throws Throwable
  {
    if (this.type.equals(DatasetType.SHAPEFILE))
    {
      DataShapefileImporter config = this.getShapefileConfiguration();

      config.doImport();
    }
    else
    {
      DataExcelImporter config = this.getExcelConfiguration();

      config.doImport();

    }
  }

}
