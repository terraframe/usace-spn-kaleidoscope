package net.geoprism.climate;

import java.io.IOException;
import java.io.InputStream;

import org.commongeoregistry.adapter.dataaccess.LocalizedValue;
import org.commongeoregistry.adapter.metadata.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.runwaysdk.dataaccess.ProgrammingErrorException;
import com.runwaysdk.dataaccess.transaction.Transaction;
import com.runwaysdk.resource.StreamResource;
import com.runwaysdk.session.Request;

import net.geoprism.climate.model.ExpectedOrganization;
import net.geoprism.climate.model.ExpectedType;
import net.geoprism.registry.cache.ServerMetadataCache;
import net.geoprism.registry.model.DataSourceDTO;
import net.geoprism.registry.model.ServerOrganization;
import net.geoprism.registry.service.business.DataSourceBusinessServiceIF;
import net.geoprism.registry.service.business.OrganizationBusinessServiceIF;
import net.geoprism.registry.service.business.ServiceFactory;
import net.geoprism.registry.xml.XMLImporter;

@Service
public class MetadataBuilderService
{

  private static final Logger           logger = LoggerFactory.getLogger(MetadataBuilderService.class);

  private ServerOrganization            organization;

  @Autowired
  private DataSourceBusinessServiceIF   sourceService;

  @Autowired
  private OrganizationBusinessServiceIF oService;

  @Request
  public void build() throws Throwable
  {
    this.doIt();
  }

  @Transaction
  public void doIt() throws Throwable
  {
    try
    {
      this.defineSource();
      this.defineMetadata();
    }
    catch (Throwable t)
    {
      logger.error("Encountered error while importing data.", t);
      throw t;
    }
  }

  private void defineSource() throws IOException
  {
    DataSourceDTO dto = new DataSourceDTO();
    dto.setCode(DataConstants.SOURCE);

    this.sourceService.apply(dto);
  }

  private void defineMetadata() throws Exception
  {
    this.createOrganization();

    this.importGeoregistryMetadata();
  }

  private void createOrganization()
  {
    logger.info("Creating organization.");

    LocalizedValue displayLabel = new LocalizedValue("USACE");
    LocalizedValue contactInfo = new LocalizedValue("USACE");

    OrganizationDTO dto = new OrganizationDTO(ExpectedOrganization.USACE.code, displayLabel, contactInfo);

    this.organization = oService.create(dto);
  }

  private void importGeoregistryMetadata() throws IOException
  {
    try (InputStream stream = this.getClass().getResourceAsStream("/types/spn-types.xml"))
    {
      logger.info("Importing Georegistry Metadata XML.");

      // Import the metadata xml file
      XMLImporter xmlImporter = new XMLImporter();
      xmlImporter.importXMLDefinitions(new StreamResource(stream, "spn-types.xml"), organization);

      this.assertTypesExist();
    }
  }

  private void assertTypesExist()
  {
    ServerMetadataCache cache = ServiceFactory.getMetadataCache();

    for (ExpectedType et : ExpectedType.getAll())
    {
      if (!cache.getGeoObjectType(et.code).isPresent())
      {
        throw new ProgrammingErrorException("Expected a type with code [" + et.code + "] to exist.");
      }
    }
  }

}
