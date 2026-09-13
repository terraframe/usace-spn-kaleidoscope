package net.geoprism.climate.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.runwaysdk.dataaccess.ProgrammingErrorException;
import com.runwaysdk.resource.ApplicationResource;
import com.runwaysdk.system.scheduler.AllJobStatus;
import com.runwaysdk.system.scheduler.ExecutionContext;

import net.geoprism.climate.model.ExpectedHierarchy;
import net.geoprism.climate.model.ExpectedType;
import net.geoprism.data.importer.BasicColumnFunction;
import net.geoprism.data.importer.ShapefileFunction;
import net.geoprism.registry.etl.DataImportJob;
import net.geoprism.registry.etl.FormatSpecificImporterFactory.FormatImporterType;
import net.geoprism.registry.etl.ObjectImporterFactory.JobHistoryType;
import net.geoprism.registry.etl.upload.ImportConfiguration;
import net.geoprism.registry.etl.upload.ImportConfiguration.ImportStrategy;
import net.geoprism.registry.graph.DataSource;
import net.geoprism.registry.io.GeoObjectImportConfiguration;
import net.geoprism.registry.io.Location;
import net.geoprism.registry.io.view.GeoObjectImportConfigurationDTO;
import net.geoprism.registry.io.view.ImportColumnDTO;
import net.geoprism.registry.io.view.ImportTypeDTO;
import net.geoprism.registry.jobs.ImportHistory;
import net.geoprism.registry.model.ServerGeoObjectType;
import net.geoprism.registry.model.ServerHierarchyType;
import net.geoprism.registry.service.business.ETLBusinessService;
import net.geoprism.registry.service.business.ServiceFactory;
import net.geoprism.registry.service.business.ShapefileBusinessService;
import net.geoprism.registry.view.ImportConfigurationView;

public class DataShapefileImporter
{
  protected ApplicationResource            resource;

  protected ExpectedType                   expectedType;

  protected ExpectedHierarchy              expectedHierarchy;

  protected Location[]                     parents;

  protected Map<String, ShapefileFunction> attributeColumnMappings;

  protected Date                           startDate;

  protected Date                           endDate;

  protected boolean                        ignoreFeedbackErrors;

  protected DataSource                     source;

  public DataShapefileImporter(ApplicationResource resource, ExpectedType expectedType, ExpectedHierarchy expectedHierarchy, Map<String, ShapefileFunction> columnMappings, Location[] parents, Date startDate, Date endDate, boolean ignoreFeedbackErrors, DataSource source)
  {
    this.resource = resource;
    this.expectedType = expectedType;
    this.expectedHierarchy = expectedHierarchy;
    this.parents = parents;
    this.attributeColumnMappings = columnMappings;
    this.startDate = startDate;
    this.endDate = endDate;
    this.ignoreFeedbackErrors = ignoreFeedbackErrors;
    this.source = source;
  }

  public boolean hasAttributeColumnMapping(String attribute)
  {
    return this.attributeColumnMappings.containsKey(attribute);
  }

  public ShapefileFunction getAttributeColumnMapping(String attribute)
  {
    return this.attributeColumnMappings.get(attribute);
  }

  public ImportHistory doImport() throws Throwable
  {
    ETLBusinessService etlService = ServiceFactory.getBean(ETLBusinessService.class);
    ShapefileBusinessService service = ServiceFactory.getBean(ShapefileBusinessService.class);

    ServerHierarchyType hierarchyType = expectedHierarchy == null ? null : ServerHierarchyType.get(expectedHierarchy.code);

    GeoObjectImportConfiguration config = getConfiguration(resource, service, expectedType.code, ImportStrategy.NEW_ONLY, startDate, endDate);
    config.setHierarchy(hierarchyType);

    if (this.parents != null)
    {
      for (Location parent : this.parents)
      {
        config.addParent(parent);
      }
    }

    ImportHistory hist = importSynchronously(config);

    hist = ImportHistory.get(hist.getOid());

    if (!hist.getStatus().get(0).equals(AllJobStatus.SUCCESS))
    {
      boolean isFeedBack = hist.getStatus().get(0).equals(AllJobStatus.FEEDBACK);
      String extra = "";
      if (isFeedBack)
      {
        extra = etlService.getImportErrors(hist.getOid(), false, 10, 1).toString();

        String validationProblems = etlService.getValidationProblems(hist.getOid(), false, 10, 1).toString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        extra = gson.toJson(extra) + "\n" + " " + gson.toJson(validationProblems);
      }

      String message = "Shapefile import failed with status [" + hist.getStatus().get(0) + "]:\n" + extra;

      if (!ignoreFeedbackErrors)
      {
        throw new ProgrammingErrorException(message);
      }
      else
      {
        System.err.println(message);
      }
    }

    return hist;
  }

  protected void configureAttributes(List<ImportColumnDTO> attributes)
  {
    for (ImportColumnDTO attribute : attributes)
    {
      configureAttribute(attribute);
    }
  }

  protected void configureAttribute(ImportColumnDTO column)
  {
    String attributeName = column.getCode();

    if (this.attributeColumnMappings.containsKey(attributeName))
    {
      ShapefileFunction function = this.attributeColumnMappings.get(attributeName);

      if (function instanceof BasicColumnFunction)
      {
        column.setTarget( ( (BasicColumnFunction) function ).getAttributeName());
      }
      else
      {
        column.setFunction(ImportConfiguration.toDTO(function));
      }
    }
  }

  protected GeoObjectImportConfiguration getConfiguration(ApplicationResource resource, ShapefileBusinessService service, String gotCode, ImportStrategy strategy, Date startDate, Date endDate)
  {
    try (InputStream stream = resource.openNewStream())
    {
      ImportConfigurationView view = new ImportConfigurationView();
      view.setCopyBlank(false);
      view.setDataSource(source.getCode());
      view.setDescription(null);
      view.setEndDate(endDate);
      view.setStartDate(startDate);
      view.setStrategy(strategy);
      view.setType(gotCode);

      GeoObjectImportConfigurationDTO dto = service.getShapefileConfiguration(resource.getName(), stream, view, true);
      dto.setFormatType(FormatImporterType.SHAPEFILE);
      dto.setObjectType(JobHistoryType.GEO_OBJECT);

      ImportTypeDTO type = dto.getType();
      List<ImportColumnDTO> attributes = type.getAttributes();

      configureAttributes(attributes);

      GeoObjectImportConfiguration config = (GeoObjectImportConfiguration) ImportConfiguration.build(dto, true);

      config.setStartDate(startDate);
      config.setEndDate(endDate);
      config.setImportStrategy(strategy);
      config.setDataSource(source);

      return config;
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  protected ImportHistory importSynchronously(GeoObjectImportConfiguration config) throws Throwable
  {
    if (config.getStartDate() == null)
    {
      config.setStartDate(new Date());
    }

    if (config.getEndDate() == null)
    {
      config.setEndDate(new Date());
    }

    DataImportJob job = new DataImportJob();
    job.apply();
    ImportHistory hist = (ImportHistory) job.createNewHistory();

    config.setHistoryId(hist.getOid());
    config.setJobId(job.getOid());

    ServerGeoObjectType type = config.getType();

    hist.appLock();
    hist.setImportFileId(config.getVaultFileId());
    hist.setConfiguration(config.toDTO());
    hist.setOrganization(type.getOrganization().getOrganization());
    hist.setGeoObjectTypeCode(type.getCode());
    hist.apply();

    ExecutionContext context = job.startSynchronously(hist);

    hist = (ImportHistory) context.getHistory();
    return hist;
  }
}
