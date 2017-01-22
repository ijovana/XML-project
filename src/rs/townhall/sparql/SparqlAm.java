package rs.townhall.sparql;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import util.Util;
import util.Util.ConnectionProperties;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

public class SparqlAm {
	
	private static final String PROPERTY = "<http://www.parlament.gov.rs/predicate/";

		
	public void executeQuery(ConnectionProperties props, String datum_donosenja, String datum_kreiranja, String broj_glasova_za, String broj_glasova_protiv, String mesto) {

		DatabaseClient client = DatabaseClientFactory.newClient(props.host,
				props.port, props.database, props.user, props.password,
				props.authType);
		
		HashMap<String,String> elementiZaPretragu = new HashMap<String,String>();
		if (datum_donosenja!=""){
			elementiZaPretragu.put("datum_donosenja", "\"" + datum_donosenja + "\"^^xs:date");
		}
		if (datum_kreiranja!=""){
			elementiZaPretragu.put("datum_kreiranja", "\"" + datum_kreiranja + "\"^^xs:date");
		}
		if(mesto!=""){
			elementiZaPretragu.put("mesto", "\"" + mesto + "\"^^xs:string");
		}
		if(broj_glasova_protiv!=""){
			elementiZaPretragu.put("broj_glasova_protiv", "\"" + broj_glasova_protiv + "\"^^xs:int");
		}
		if(broj_glasova_za!=""){
			elementiZaPretragu.put("broj_glasova_za", "\"" + broj_glasova_za + "\"^^xs:int");
		}
		
		StringBuilder query = new StringBuilder();
		query.append("PREFIX xs: <http://www.w3.org/2001/XMLSchema#>\n");
		query.append("SELECT ?amandman FROM <amendment/metadata>\n");
		query.append("WHERE{\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"datum_donosenja> ?datum_donosenja .\n}\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"datum_kreiranja> ?datum_kreiranja .\n}\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"broj_glasova_za> ?broj_glasova_za .\n}\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"broj_glasova_protiv> ?broj_glasova_protiv .\n}");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"mesto> ?mesto .\n}");
		
		
		StringBuilder filter = new StringBuilder();
		filter.append("FILTER (\n");
		Iterator<HashMap.Entry<String, String>> elementiIter = elementiZaPretragu.entrySet().iterator();
		while (elementiIter.hasNext()) {
		    HashMap.Entry<String, String> element = elementiIter.next();
		      	filter.append(" ?"+element.getKey() + " = " + element.getValue());
		      	if (elementiIter.hasNext()){
		      		filter.append(" , ");
		      	}else{
		      		filter.append(")\n}");    
		      	}
		}
		
		if (elementiZaPretragu.isEmpty()){
			query.append("}");
		}else{
			query.append(filter);
		}
	
		
		// Create a SPARQL query manager to query RDF datasets
		SPARQLQueryManager sparqlQueryManager = client.newSPARQLQueryManager();
		
		JacksonHandle resultsHandle = new JacksonHandle();
		resultsHandle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);
		
		SPARQLQueryDefinition queryDef = sparqlQueryManager.newQueryDefinition(query.toString());
		
		resultsHandle = sparqlQueryManager.executeSelect(queryDef, resultsHandle);	
		
		List<JsonNode> tuples = resultsHandle.get().findValues("value");
		for ( JsonNode am : tuples ) {
			String[] a=am.asText().split("/");
			System.out.println(a[a.length-1]);
		}
		
		//System.out.println(resultsHandle.get().findValues("value")); 
	}
	
	public static void main(String[] args){
		SparqlAm sa = new SparqlAm();
		ConnectionProperties prop = null;
		try {
			prop = Util.loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  sa.executeQuery(prop,"", "2017-05-30","","1116412966", "XeYE.dHO.oRQZ8oKuctaTk");
		
	}
}
