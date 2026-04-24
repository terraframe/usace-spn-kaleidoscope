import { ChatMessage, LocationPage } from "./models/chat.model";
import { Configuration } from "./models/configuration.model";
import { GeoObject } from "./models/geoobject.model";
import { Style } from "./models/style.model";
import { ExplorerInit } from "./service/explorer.service";

export class MockUtil {


  public static message: ChatMessage =
    {
      id: '1',
      sender: 'user',
      text: "The following school districts would be impacted if LEV_A_96 floods:\n\n<location><label>Concordia Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2200480</uri></location>\n<location><label>Richland Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201350</uri></location>\n<location><label>East Carroll Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2200570</uri></location>\n<location><label>West Carroll Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201950</uri></location>\n<location><label>Monroe City School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201080</uri></location>\n<location><label>McGehee School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-0509630</uri></location>\n<location><label>Ouachita Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201200</uri></location>\n<location><label>Dumas School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-0505500</uri></location>\n<location><label>Avoyelles Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2200150</uri></location>\n<location><label>Tensas Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201710</uri></location>\n<location><label>Franklin Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2200660</uri></location>\n<location><label>Madison Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201050</uri></location>\n<location><label>Catahoula Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2200420</uri></location>\n<location><label>Dermott School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-0505170</uri></location>\n<location><label>Hamburg School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-0500042</uri></location>\n<location><label>Lakeside School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-0508640</uri></location>\n<location><label>Pine Bluff School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-0500026</uri></location>\n<location><label>Morehouse Parish School District</label><uri>https://localhost:4200/lpg/graph_801104/0#SchoolZone-2201110</uri></location>\n\n",
      mappable: false,
      purpose: 'standard'
    }

  public static messages: ChatMessage[] = [
    // { id: '1', sender: "user", mappable: false, purpose: 'standard', text: "what is the total population at flood risk for the no mitigation flood scenario versus the combo plan mitigation scenario" },
    // { id: '2', "sender": "system", mappable: false, purpose: 'standard', text: "Here is the total daytime population at flood risk for each scenario:\n\n| Scenario | Population Under 65 | Population Over 65 | Total |\n|---|---|---|---|\n| No Mitigation Scenario | 29,465 | 5,341 | 34,806 |\n| Combo Plan Scenario | 24,281 | 4,216 | 28,497 |\n\nThe Combo Plan Scenario reduces the total population at flood risk by 6,309 people (~18% reduction) compared to the No Mitigation Scenario." },
    // { id: '3', "sender": "user", mappable: false, purpose: 'standard', text: "what is the total population at flood risk for the no mitigation flood scenario?" },
    // { id: '4', "sender": "system", mappable: true, purpose: 'standard', text: "Here are 5 land parcels:\n\n1. <location><label>26137020</label><uri>https://spn.geoprism.net#LandParcel-26137020</uri></location>\n   - Address: 1600 AMPHITHEATRE PKWY | Land Use: VACANT URBAN | Lot Acres: 0.17\n\n2. <location><label>25947067</label><uri>https://spn.geoprism.net#LandParcel-25947067</uri></location>\n   - Address: 1161 MCBAIN AVE | Land Use: RETAIL USES AUTO SERVICE/GARAGES | Lot Acres: 0.17\n\n3. <location><label>25947054</label><uri>https://spn.geoprism.net#LandParcel-25947054</uri></location>\n   - Address: 1161 MCBAIN AVE | Land Use: SINGLE FAMILY | Lot Acres: 0.11\n\n4. <location><label>25947041</label><uri>https://spn.geoprism.net#LandParcel-25947041</uri></location>\n   - Address: 1161 MCBAIN AVE | Land Use: VACANT URBAN | Lot Acres: 0.11\n\n5. <location><label>25946092</label><uri>https://spn.geoprism.net#LandParcel-25946092</uri></location>\n   - Address: 960 N SAN ANTONIO RD STE 114 | Land Use: GENERAL INDUSTRIAL NONMANUFACTURING OR MANUFACTURING/NONMANUFACTURING | Lot Acres: 0.24\n\n" }
  ];

  public static explorerInit: ExplorerInit = {

  }

  public static locations: LocationPage = {
    statement: "A list of available locations",
    locations: [
      {
        "properties": {
          "code": "220072002479",
          "label": "BELLE PLACE ELEMENTARY SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220072002479",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220072002479",
        "geometry": {
          "coordinates": [
            [
              -91.7549,
              30.007
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220159001222",
          "label": "CECILIA PRIMARY SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220159001222",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220159001222",
        "geometry": {
          "coordinates": [
            [
              -91.852,
              30.3396
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "228010002497",
          "label": "CENTRAL LOUISIANA SUPPORTS AND SERVICES CENTER",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-228010002497",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-228010002497",
        "geometry": {
          "coordinates": [
            [
              -92.4971,
              31.2965
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220072000519",
          "label": "JEANERETTE SENIOR HIGH SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220072000519",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220072000519",
        "geometry": {
          "coordinates": [
            [
              -91.7026,
              29.9453
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220129001054",
          "label": "D.F. HUDDLE ELEMENTARY",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220129001054",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220129001054",
        "geometry": {
          "coordinates": [
            [
              -92.4754,
              31.2976
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220156001209",
          "label": "PORT BARRE ELEMENTARY SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220156001209",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220156001209",
        "geometry": {
          "coordinates": [
            [
              -91.9504,
              30.569
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220156001989",
          "label": "NORTH CENTRAL HIGH SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220156001989",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220156001989",
        "geometry": {
          "coordinates": [
            [
              -91.9845,
              30.7278
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220129001053",
          "label": "HORSESHOE DRIVE ELEMENTARY NEW VISION ACADEMY",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220129001053",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220129001053",
        "geometry": {
          "coordinates": [
            [
              -92.4616,
              31.2614
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220129001072",
          "label": "POLAND JUNIOR HIGH SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220129001072",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220129001072",
        "geometry": {
          "coordinates": [
            [
              -92.2757,
              31.1673
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220159001220",
          "label": "CATAHOULA ELEMENTARY SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220159001220",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220159001220",
        "geometry": {
          "coordinates": [
            [
              -91.7098,
              30.2173
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "228010001627",
          "label": "RAYMOND LABORDE CORRECTIONAL CENTER",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-228010001627",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-228010001627",
        "geometry": {
          "coordinates": [
            [
              -92.0257,
              30.9728
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220159001219",
          "label": "BREAUX BRIDGE HIGH SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220159001219",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220159001219",
        "geometry": {
          "coordinates": [
            [
              -91.8578,
              30.255
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220015000073",
          "label": "MARKSVILLE HIGH SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220015000073",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220015000073",
        "geometry": {
          "coordinates": [
            [
              -92.0716,
              31.1271
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220129001038",
          "label": "ACADIAN ELEMENTARY",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220129001038",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220129001038",
        "geometry": {
          "coordinates": [
            [
              -92.4119,
              31.2872
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220159001218",
          "label": "BREAUX BRIDGE PRIMARY SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220159001218",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220159001218",
        "geometry": {
          "coordinates": [
            [
              -91.8912,
              30.2786
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220129001080",
          "label": "W.O. HALL 6TH GRADE ACADEMY",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220129001080",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220129001080",
        "geometry": {
          "coordinates": [
            [
              -92.432,
              31.2918
            ]
          ],
          "type": "MultiPoint"
        }
      },
      {
        "properties": {
          "code": "220004702462",
          "label": "CENTRAL SOUTHWEST ALTERNATIVE HIGH SCHOOL",
          "type": "https://localhost:4200/lpg/graph_801104/0/rdfs#School",
          "uri": "https://localhost:4200/lpg/graph_801104/0#School-220004702462",
          edges: {}
        },
        "type": "Feature",
        "id": "https://localhost:4200/lpg/graph_801104/0#School-220004702462",
        "geometry": {
          "coordinates": [
            [
              -92.1665,
              30.9368
            ]
          ],
          "type": "MultiPoint"
        }
      }
    ],
    limit: 17,
    offset: 0,
    count: 17
  };

  public static styles: Configuration = {
    layers: [],
    token: "",
    styles: {
      "https://localhost:4200/lpg/graph_801104/0#RealProperty": {
        "color": "#79F294",
        "order": 0
      },
      "https://localhost:4200/lpg/graph_801104/0#ChannelReach": {
        "color": "#79DAF2",
        "order": 4
      },
      "https://localhost:4200/lpg/graph_801104/0#Reservoir": {
        "color": "#CAEEFB",
        "order": 5
      },
      "https://localhost:4200/lpg/graph_801104/0#RecreationArea": {
        "color": "#F2E779",
        "order": 3
      },
      "https://localhost:4200/lpg/graph_801104/0#LeveedArea": {
        "color": "#C379F2",
        "order": 4
      },
      "https://localhost:4200/lpg/graph_801104/0#SchoolZone": {
        "color": "#FBE3D6",
        "order": 6
      },
      "https://localhost:4200/lpg/graph_801104/0#Project": {
        "color": "#C0F279",
        "order": 6
      },
      "https://localhost:4200/lpg/graph_801104/0#LeveeArea": {
        "color": "#D1D1D1",
        "order": 4
      },
      "https://localhost:4200/lpg/graph_801104/0#School": {
        "color": "#F2A579",
        "order": 0
      },
      "https://localhost:4200/lpg/graph_801104/0#Levee": {
        "color": "#F279E0",
        "order": 0
      },
      "https://localhost:4200/lpg/graph_801104/0#Hospital": {
        "color": "#F2799D",
        "order": 0
      },
      "https://localhost:4200/lpg/graph_801104/0#Dam": {
        "color": "#D5F279",
        "order": 0
      },
      "https://localhost:4200/lpg/graph_801104/0#River": {
        "color": "#7999F2",
        "order": 2
      },
      "https://localhost:4200/lpg/graph_801104/0#Watershed": {
        "color": "#79F2C9",
        "order": 4
      },
      "https://localhost:4200/lpg/graph_801104/0#ChannelArea": {
        "color": "#156082",
        "order": 4
      },
      "https://localhost:4200/lpg/graph_801104/0#ChannelLine": {
        "color": "#79F2A0",
        "order": 1
      },
      "https://localhost:4200/lpg/graph_801104/0#UsaceRecreationArea": {
        "color": "#F2BE79",
        "order": 3
      },
      "http://dime.usace.mil/ontologies/cwbi-concept#Program": {
        "color": "#FF5733",
        "order": 0
      },
      "https://localhost:4200/lpg/graph_801104/0#WaterLock": {
        "color": "#79F2E2",
        "order": 0
      }
    }
  }




}
