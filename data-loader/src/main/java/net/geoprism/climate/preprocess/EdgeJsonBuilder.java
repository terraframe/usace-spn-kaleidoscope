package net.geoprism.climate.preprocess;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.runwaysdk.build.domain.SpringBootConfiguration;
import com.runwaysdk.dataaccess.ProgrammingErrorException;
import com.runwaysdk.dataaccess.database.Database;
import com.runwaysdk.query.OIterator;
import com.runwaysdk.query.QueryFactory;
import com.runwaysdk.session.Request;

import net.geoprism.climate.model.ExpectedGraphType;
import net.geoprism.climate.model.ExpectedType;
import net.geoprism.registry.ListType;
import net.geoprism.registry.ListTypeQuery;
import net.geoprism.registry.ListTypeVersion;
import net.geoprism.registry.SingleListType;
import net.geoprism.registry.model.ServerGeoObjectType;

@Service
public class EdgeJsonBuilder
{
  private static final Logger logger = LoggerFactory.getLogger(EdgeJsonBuilder.class);

  private static interface QueryBuilder
  {
    public ResultSet generate(ListTypeVersion sourceVersion, ListTypeVersion targetVersion);
  }

  private static class ST_OVERLAPS implements QueryBuilder
  {

    @Override
    public ResultSet generate(ListTypeVersion sourceVersion, ListTypeVersion targetVersion)
    {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT st.code, wa.code");
      sql.append(" FROM " + sourceVersion.getMdBusiness().getTableName() + " AS st,");
      sql.append(" " + targetVersion.getMdBusiness().getTableName() + " AS wa");
      sql.append(" WHERE st.code != wa.code");
      sql.append(" AND ST_OVERLAPS(wa.geom, st.geom) = true");
      // sql.append(" OR ST_DWithin(wa.geom, st.geom, 1000, false) = true ");

      return Database.query(sql.toString());
    }
  }

  private static class ST_CONTAINS implements QueryBuilder
  {

    @Override
    public ResultSet generate(ListTypeVersion sourceVersion, ListTypeVersion targetVersion)
    {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT st.code, wa.code");
      sql.append(" FROM " + sourceVersion.getMdBusiness().getTableName() + " AS st,");
      sql.append(" " + targetVersion.getMdBusiness().getTableName() + " AS wa");
      sql.append(" WHERE st.code != wa.code");
      sql.append(" AND ST_CONTAINS(st.geom, wa.geom) = true");

      return Database.query(sql.toString());
    }
  }

  private static class HAS_SOURCE_CODE implements QueryBuilder
  {
    private String code;

    public HAS_SOURCE_CODE(String code)
    {
      this.code = code;
    }

    @Override
    public ResultSet generate(ListTypeVersion sourceVersion, ListTypeVersion targetVersion)
    {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT st.code, wa.code");
      sql.append(" FROM " + sourceVersion.getMdBusiness().getTableName() + " AS st,");
      sql.append(" " + targetVersion.getMdBusiness().getTableName() + " AS wa");
      sql.append(" WHERE st.code = '" + this.code + "'");

      return Database.query(sql.toString());
    }
  }

  private static class ST_WITHIN implements QueryBuilder
  {

    @Override
    public ResultSet generate(ListTypeVersion sourceVersion, ListTypeVersion targetVersion)
    {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT st.code, wa.code");
      sql.append(" FROM " + sourceVersion.getMdBusiness().getTableName() + " AS st,");
      sql.append(" " + targetVersion.getMdBusiness().getTableName() + " AS wa");
      sql.append(" WHERE st.code != wa.code");
      sql.append(" AND ST_WITHIN(st.geom, wa.geom) = true");

      return Database.query(sql.toString());
    }
  }

  private static class FLOWS_INTO implements QueryBuilder
  {

    @Override
    public ResultSet generate(ListTypeVersion sourceVersion, ListTypeVersion targetVersion)
    {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT st.code, wa.code");
      sql.append(" FROM " + sourceVersion.getMdBusiness().getTableName() + " AS st,");
      sql.append(" " + targetVersion.getMdBusiness().getTableName() + " AS wa");
      sql.append(" WHERE st.code != wa.code");
      sql.append(" AND ST_INTERSECTS(wa.geom, st.geom) = true");
      sql.append(" AND degrees(ST_Azimuth(ST_Centroid(wa.geom), ST_Centroid(st.geom))) > 90");
      sql.append(" AND degrees(ST_Azimuth(ST_Centroid(wa.geom), ST_Centroid(st.geom))) < 180");

      return Database.query(sql.toString());
    }
  }

  @Request
  public void generate(CommandLine cmd) throws IOException
  {
    if (Boolean.valueOf(cmd.getOptionValue("list", "true")))
    {
      this.createLists();
    }

    if (Boolean.valueOf(cmd.getOptionValue("publish", "true")))
    {
      this.publishLists();
    }

    File directory = new File(cmd.getOptionValue("out_path", "files/usace-spn/edges/"));

//    this.createIndexes();
//
//    generateHasMitigtaion(directory);
//    generateFloodRisk(directory);
//    generateFlowsInto(directory);
//    generateLocatedIn(directory);
  }

  private void publishLists()
  {
    ExpectedType[] expectedTypes = ExpectedType.getAll();

    for (ExpectedType expectedType : expectedTypes)
    {
      ServerGeoObjectType type = expectedType.getServerObject();
      ListTypeVersion version = getVersion(type);

      if (version != null)
      {
        version.publishNoAuth();
      }
    }
  }

  private void createLists()
  {
    ExpectedType[] expectedTypes = ExpectedType.getAll();

    for (ExpectedType expectedType : expectedTypes)
    {
      ServerGeoObjectType type = expectedType.getServerObject();
      ListTypeVersion version = getVersion(type);

      if (version == null)
      {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.clear();
        calendar.set(2023, Calendar.MARCH, 1);

        SingleListType list = new SingleListType();
        list.setValidOn(calendar.getTime());
        list.setGeoObjectType(type.getType());
        list.getDisplayLabel().setValue(type.getLabel().getValue());
        list.setCode(type.getCode());
        list.setOrganization(type.getOrganization());
        list.getDescription().setValue(type.getLabel().getValue());
        list.setHierarchies(new JsonArray().toString());

        ListType.apply(list.toJSON());

        getVersion(type).publishNoAuth();
      }
    }
  }

  private void createIndexes()
  {
    ExpectedType[] expectedTypes = ExpectedType.getAll();

    for (ExpectedType expectedType : expectedTypes)
    {
      ServerGeoObjectType type = expectedType.getServerObject();
      ListTypeVersion version = getVersion(type);

      String tableName = version.getMdBusiness().getTableName();
      String indexName = tableName.substring(0, Math.min(tableName.length(), 20)) + "_idx";

      System.out.println("Creating index for: " + type.getCode());

      StringBuilder statement = new StringBuilder();
      statement.append("CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName + " USING GIST (geom);");

      Database.executeBatch(Arrays.asList(statement.toString()));

    }

    System.out.println("Finished creating indexes");
  }

  private void generateHasMitigtaion(File directory) throws IOException
  {
    System.out.println("Generating At Flood Risk.");

    final JsonArray edges = new JsonArray();

    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.FLOOD_SCENARIO }, new ExpectedType[] { ExpectedType.CP_BRIDGE, ExpectedType.CP_CHANNEL_FTPRNT, ExpectedType.CP_FLOODWALL, ExpectedType.CP_PROP_GRAVEL, ExpectedType.CP_PROP_RIPRAP, ExpectedType.CP_SLOPE_REPAIR }, new HAS_SOURCE_CODE("2"));

    writeEdgesToFile(directory, ExpectedGraphType.HAS_MITIGATION, "has_mitigation.json", edges);
  }

  private void generateFloodRisk(File directory) throws IOException
  {
    System.out.println("Generating At Flood Risk.");

    final JsonArray edges = new JsonArray();

//    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.FLOOD_SCENARIO }, new ExpectedType[] { ExpectedType.CENSUS_BLOCK }, new ST_OVERLAPS());
    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.FLOOD_SCENARIO }, new ExpectedType[] { ExpectedType.ROAD }, new ST_CONTAINS());

    writeEdgesToFile(directory, ExpectedGraphType.HAS_FLOOD_RISK, "flood-risk.json", edges);
  }

  private void generateFlowsInto(File directory) throws IOException
  {
    System.out.println("Generating Flows Into JSON.");

    final QueryBuilder queryBuilder = new FLOWS_INTO();
    final JsonArray edges = new JsonArray();

    ExpectedType[] expectedTypes = new ExpectedType[] { ExpectedType.CREEK, ExpectedType.LAKE, ExpectedType.PROJECT_REACH };

    generateJsonForTypes(edges, expectedTypes, expectedTypes, queryBuilder);

    writeEdgesToFile(directory, ExpectedGraphType.FLOWS_INTO, "flows-into.json", edges);
  }

  private void generateLocatedIn(File directory) throws IOException
  {
    System.out.println("Generating Flows Into JSON.");

    final JsonArray edges = new JsonArray();

    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.PROJECT_REACH }, new ExpectedType[] { ExpectedType.PROJECT_AREA }, new ST_WITHIN());
    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.CP_BRIDGE, ExpectedType.CP_CHANNEL_FTPRNT, ExpectedType.CP_FLOODWALL, ExpectedType.CP_PROP_GRAVEL, ExpectedType.CP_PROP_RIPRAP, ExpectedType.CP_SLOPE_REPAIR }, new ExpectedType[] { ExpectedType.PROJECT_AREA }, new ST_WITHIN());
    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.STRUCTURE }, new ExpectedType[] { ExpectedType.PROJECT_AREA, ExpectedType.LAND_PARCEL }, new ST_WITHIN());
    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.ROAD, ExpectedType.LAKE }, new ExpectedType[] { ExpectedType.PROJECT_AREA }, new ST_WITHIN());
//    generateJsonForTypes(edges, new ExpectedType[] { ExpectedType.CENSUS_BLOCK }, new ExpectedType[] { ExpectedType.PROJECT_AREA }, new ST_WITHIN());

    writeEdgesToFile(directory, ExpectedGraphType.LOCATED_IN, "located-in.json", edges);
  }

  private void writeEdgesToFile(File directory, ExpectedGraphType expectedGraphType, String filename, JsonArray edges)
  {
    System.out.println("Writing [" + edges.size() + "] edges for type [" + expectedGraphType.getCode() + "] to file [" + filename + "].");

    // JsonObject joGraphType = new JsonObject();
    // joGraphType.addProperty("graphTypeClass",
    // expectedGraphType.getGraphTypeClass());
    // joGraphType.addProperty("code", expectedGraphType.getCode());
    // joGraphType.add("edges", edges);
    //
    // JsonArray graphTypes = new JsonArray();
    // graphTypes.add(joGraphType);
    //
    // JsonObject main = new JsonObject();
    // main.add("graphTypes", graphTypes);

    // Print items
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    try
    {
      File file = new File(directory, filename);

      System.out.println("Writing file: " + file.getAbsolutePath());

      FileUtils.write(file, gson.toJson(edges), "UTF-8");
    }
    catch (IOException e)
    {
      throw new ProgrammingErrorException(e);
    }
  }

  private void generateJsonForTypes(final JsonArray edges, final ExpectedType[] expectedSourceTypes, final ExpectedType[] expectedTargetTypes, final QueryBuilder queryBuilder)
  {
    for (ExpectedType expectedSourceType : expectedSourceTypes)
    {
      ServerGeoObjectType sourceType = expectedSourceType.getServerObject();
      ListTypeVersion sourceVersion = getVersion(sourceType);

      if (sourceVersion != null)
      {
        for (ExpectedType expectedTargetType : expectedTargetTypes)
        {
          ServerGeoObjectType targetType = expectedTargetType.getServerObject();
          ListTypeVersion targetVersion = getVersion(targetType);

          if (targetVersion != null)
          {
            generateJsonForType(edges, sourceType, sourceVersion, targetType, targetVersion, queryBuilder);
          }
          else
          {
            throw new UnsupportedOperationException("Expected to find list version for type [" + expectedTargetType.code + "].");
          }
        }
      }
      else
      {
        throw new UnsupportedOperationException("Expected to find list version for type [" + expectedSourceType.code + "].");
      }
    }
  }

  private void generateJsonForType(JsonArray edges, ServerGeoObjectType sourceType, ListTypeVersion sourceVersion, ServerGeoObjectType targetType, ListTypeVersion targetVersion, QueryBuilder builder)
  {
    Set<String> keys = new TreeSet<String>();

    long startTime = System.currentTimeMillis();

    System.out.println("Generating SQL between [" + sourceType.getCode() + "] and [" + targetType.getCode() + "] using QueryBuilder [" + builder.getClass().getName() + "].");

    int i = 0;

    try (ResultSet results = builder.generate(sourceVersion, targetVersion))
    {
      while (results.next())
      {
        String sourceCode = results.getString(1);
        String targetCode = results.getString(2);

        String key = sourceCode.compareTo(targetCode) > 0 ? sourceCode + "-" + targetCode : targetCode + "-" + sourceCode;

        if (!keys.contains(key))
        {
          JsonObject object = new JsonObject();
          object.addProperty("source", sourceCode);
          object.addProperty("sourceType", sourceType.getCode());
          object.addProperty("target", targetCode);
          object.addProperty("targetType", targetType.getCode());

          edges.add(object);

          keys.add(key);
        }

        i = i + 1;

        if (i % 1000 == 0)
        {
          System.out.println("Edges array is of size [" + edges.size() + "].");
        }
      }

    }
    catch (SQLException e)
    {
      throw new ProgrammingErrorException(e);
    }

    System.out.println("Finished - " + ( System.currentTimeMillis() - startTime ) + " ms");

  }

  private ListTypeVersion getVersion(ServerGeoObjectType sourceType)
  {
    ListTypeQuery listTypeQuery = new ListTypeQuery(new QueryFactory());
    listTypeQuery.WHERE(listTypeQuery.getGeoObjectType().EQ(sourceType.getOid()));

    try (OIterator<? extends ListType> it = listTypeQuery.getIterator())
    {
      if (it.hasNext())
      {
        ListType listType = it.next();

        List<ListTypeVersion> versions = listType.getVersions();

        if (versions.size() > 0)
        {
          return versions.get(0);
        }
      }
    }

    return null;
  }

  public static void main(String[] args) throws IOException, ParseException
  {
    // create Options object
    Options options = new Options();
    options.addOption(Option.builder("l").longOpt("list").desc("build list if one doest exist").hasArg().build());
    options.addOption(Option.builder("p").longOpt("publish").desc("publish all of the lists").hasArg().build());
    options.addOption(Option.builder("o").longOpt("out_path").desc("path of folder in which to output the json files").hasArg().build());

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    SpringApplication application = new SpringApplication(SpringBootConfiguration.class);
    // application.setWebApplicationType(WebApplicationType.NONE);

    ConfigurableApplicationContext ctx = application.run(args);

    try
    {
      EdgeJsonBuilder obj = ctx.getBean(EdgeJsonBuilder.class);
      obj.generate(cmd);
    }
    finally
    {
      ctx.close();
    }
  }

}
