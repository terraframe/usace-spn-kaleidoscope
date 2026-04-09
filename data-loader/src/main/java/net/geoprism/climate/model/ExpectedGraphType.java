package net.geoprism.climate.model;

import org.commongeoregistry.adapter.metadata.GraphTypeDTO;

import net.geoprism.registry.model.GraphType;
import net.geoprism.registry.service.business.DirectedAcyclicGraphTypeBusinessServiceIF;
import net.geoprism.registry.service.business.ServiceFactory;
import net.geoprism.registry.service.business.UndirectedGraphTypeBusinessServiceIF;

public class ExpectedGraphType extends ExpectedMetadata
{
  public static final ExpectedGraphType HAS_FLOOD_RISK = new ExpectedGraphType("HasFloodRisk", true);

  public static final ExpectedGraphType FLOWS_INTO     = new ExpectedGraphType("FlowsInto", true);

  public static final ExpectedGraphType LOCATED_IN     = new ExpectedGraphType("LocatedIn", true);

  private boolean                       isDirected;

  public ExpectedGraphType(String code, boolean isDirected)
  {
    super(code);

    this.isDirected = isDirected;
  }

  public String getGraphTypeClass()
  {
    if (this.isDirected)
    {
      return GraphTypeDTO.DIRECTED_ACYCLIC_GRAPH_TYPE;
    }
    else
    {
      return GraphTypeDTO.UNDIRECTED_GRAPH_TYPE;
    }
  }

  public GraphType getServerObject()
  {
    if (this.isDirected)
    {
      DirectedAcyclicGraphTypeBusinessServiceIF service = ServiceFactory.getBean(DirectedAcyclicGraphTypeBusinessServiceIF.class);

      return service.getByCode(this.code).orElseThrow();
    }
    else
    {
      UndirectedGraphTypeBusinessServiceIF service = ServiceFactory.getBean(UndirectedGraphTypeBusinessServiceIF.class);
      return service.getByCode(this.code).orElseThrow();
    }
  }

}
