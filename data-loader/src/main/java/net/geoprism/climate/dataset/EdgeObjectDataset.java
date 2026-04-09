package net.geoprism.climate.dataset;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.runwaysdk.dataaccess.ProgrammingErrorException;
import com.runwaysdk.resource.ApplicationResource;
import com.runwaysdk.system.scheduler.AllJobStatus;
import com.runwaysdk.system.scheduler.ExecutionContext;

import net.geoprism.climate.DataConstants;
import net.geoprism.climate.model.ExpectedGraphType;
import net.geoprism.registry.etl.DataImportJob;
import net.geoprism.registry.etl.FormatSpecificImporterFactory.FormatImporterType;
import net.geoprism.registry.etl.ImportStage;
import net.geoprism.registry.etl.ObjectImporterFactory.ObjectImportType;
import net.geoprism.registry.etl.upload.EdgeObjectImportConfiguration;
import net.geoprism.registry.etl.upload.EdgeObjectImporter.ReferenceStrategy;
import net.geoprism.registry.etl.upload.ImportConfiguration;
import net.geoprism.registry.etl.upload.ImportConfiguration.ImportStrategy;
import net.geoprism.registry.graph.DataSource;
import net.geoprism.registry.jobs.ImportHistory;
import net.geoprism.registry.service.business.ETLBusinessService;
import net.geoprism.registry.service.business.GraphBusinessService;
import net.geoprism.registry.service.business.ServiceFactory;

public class EdgeObjectDataset extends AbstractDataset
{

  private static final Logger   logger = LoggerFactory.getLogger(EdgeObjectDataset.class);

  protected ApplicationResource resource;

  protected ExpectedGraphType   expectedGraphType;

  protected DataSource          source;

  protected Date                startDate;

  protected Date                endDate;

  protected boolean             ignoreFeedbackErrors;

  public EdgeObjectDataset(ApplicationResource resource, ExpectedGraphType expectedGraphType, DataSource source, boolean ignoreFeedbackErrors)
  {
    this(resource, expectedGraphType, DataConstants.START_DATE, DataConstants.END_DATE, source, ignoreFeedbackErrors);
  }

  public EdgeObjectDataset(ApplicationResource resource, ExpectedGraphType expectedGraphType, Date startDate, Date endDate, DataSource source, boolean ignoreFeedbackErrors)
  {
    super();

    this.expectedGraphType = expectedGraphType;
    this.resource = resource;
    this.source = source;
    this.startDate = startDate;
    this.endDate = endDate;
    this.ignoreFeedbackErrors = ignoreFeedbackErrors;
  }

  @Override
  public void synchronize() throws Throwable
  {
    this.buildLatest();
  }

  // @Transaction
  public void buildLatest() throws Throwable
  {
    ETLBusinessService etlService = ServiceFactory.getBean(ETLBusinessService.class);

    EdgeObjectImportConfiguration config = this.getTestConfiguration(ImportStrategy.NEW_ONLY);

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

      String message = "Edge object import failed with status [" + hist.getStatus().get(0) + "]:\n" + extra;

      if (!ignoreFeedbackErrors)
      {
        throw new ProgrammingErrorException(message);
      }
      else
      {
        System.err.println(message);
      }
    }
  }

  public EdgeObjectImportConfiguration getTestConfiguration(ImportStrategy strategy)
  {
    GraphBusinessService service = ServiceFactory.getBean(GraphBusinessService.class);

    try (InputStream istream = this.resource.openNewStream())
    {
      ObjectNode result = service.getJsonImportConfiguration(this.expectedGraphType.getGraphTypeClass(), this.expectedGraphType.getCode(), this.startDate, this.endDate, this.source.getCode(), this.resource.getName(), istream, strategy);

      result.put(ImportConfiguration.FORMAT_TYPE, FormatImporterType.JSON.name());
      result.put(ImportConfiguration.OBJECT_TYPE, ObjectImportType.EDGE_OBJECT.name());

      result.put(EdgeObjectImportConfiguration.EDGE_SOURCE, "source");
      result.put(EdgeObjectImportConfiguration.EDGE_SOURCE_STRATEGY, ReferenceStrategy.CODE.name());
      result.put(EdgeObjectImportConfiguration.EDGE_SOURCE_TYPE, "sourceType");
      result.put(EdgeObjectImportConfiguration.EDGE_SOURCE_TYPE_STRATEGY, ReferenceStrategy.CODE.name());
      result.put(EdgeObjectImportConfiguration.EDGE_TARGET, "target");
      result.put(EdgeObjectImportConfiguration.EDGE_TARGET_STRATEGY, ReferenceStrategy.CODE.name());
      result.put(EdgeObjectImportConfiguration.EDGE_TARGET_TYPE, "targetType");
      result.put(EdgeObjectImportConfiguration.EDGE_TARGET_TYPE_STRATEGY, ReferenceStrategy.CODE.name());

      EdgeObjectImportConfiguration configuration = (EdgeObjectImportConfiguration) ImportConfiguration.build(result.toString(), true);

      return configuration;
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  protected ImportHistory importSynchronously(EdgeObjectImportConfiguration config) throws Throwable
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

    hist.appLock();
    // Skip the validation stage
    hist.clearStage();
    hist.addStage(ImportStage.IMPORT);
    hist.setImportFileId(config.getVaultFileId());
    hist.setConfigJson(config.toJSON().toString());
    hist.apply();

    ExecutionContext context = job.startSynchronously(hist);

    hist = (ImportHistory) context.getHistory();
    return hist;
  }

}
