import requests
import os
import json
from dotenv import load_dotenv

load_dotenv()

# Updated SPARQL query using new FROM clauses
def execute(name: str) -> list:
    MAX_ERROR_LENGTH = 1000
    
    statement = f"""
    PREFIX lpg: <https://spn.geoprism.net#> 
    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
    PREFIX text: <http://jena.apache.org/text#>

    SELECT ?code ?type ?s
    FROM <https://spn.geoprism.net/spn>
    WHERE {{
      BIND('{name}' AS ?search)

      {{
        (?s ?score) text:query (rdfs:label ?search) .
      }}
      UNION
      {{
        ?s lpg:GeoObject-code ?search .
        BIND(1000000 AS ?score)
      }}

      OPTIONAL {{ ?s lpg:GeoObject-code ?code . }}

      ?s a ?type .
    }}
    ORDER BY DESC(?score)
    LIMIT 100
    """
    
    try:
        response = requests.post(
            os.getenv('JENA_URL'), 
            data={'query': statement},
            timeout=240
        )
    
        response.raise_for_status()

        try:
            responseJson = response.json()
        except ValueError:
            return f"[ERROR] Received non-JSON response from Jena:\n{response.text[:MAX_ERROR_LENGTH]}"

    
        results = []
        for r in responseJson.get('results', {}).get('bindings', []):
            if 'code' in r:
                results.append({
                    'code': r['code']['value'],
                    'type': r['type']['value'],
                    'uri': r['s']['value']
                })

        return results
    except requests.exceptions.HTTPError as e:
        error_detail = response.text[:MAX_ERROR_LENGTH] if response is not None else "No response body"
        return f"[ERROR] HTTP error: {str(e)}\nDetails: {error_detail}"

    except requests.exceptions.RequestException as e:
        # Handle network-level issues like timeouts, DNS failures, etc.
        return f"[ERROR] Network error while contacting Jena: {str(e)}"

    except Exception as e:
        return f"[ERROR] An unexpected exception occurred: {str(e)}"

def lambda_handler(event, context):
    agent = event['agent']
    actionGroup = event['actionGroup']
    function = event['function']
    parameters = event.get('parameters', [])

    print("EVENT:", json.dumps(event))
    if not parameters or "value" not in parameters[0]:
        return {
            "error": "Missing required 'value' parameter.",
            "event": event
        }

    result = execute(parameters[0]["value"])

    responseBody = {
        "TEXT": {
            "body": json.dumps(result)
        }
    }

    action_response = {
        'actionGroup': actionGroup,
        'function': function,
        'functionResponse': {
            'responseBody': responseBody
        }
    }

    return {'response': action_response, 'messageVersion': event['messageVersion']}
