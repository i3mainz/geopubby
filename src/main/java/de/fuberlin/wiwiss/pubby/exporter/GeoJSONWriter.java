package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class GeoJSONWriter implements ModelWriter {


	@Override
	public void write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=model.
		listResourcesWithProperty(model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		JSONObject geojson=new JSONObject();
		geojson.put("type","FeatureCollection");
		JSONArray features=new JSONArray();
		geojson.put("features",features);
		while(it.hasNext()) {
			Resource ind=it.next();
			StmtIterator it2 = ind.listProperties();
			JSONObject curfeature=new JSONObject();
			features.put(curfeature);
			curfeature.put("id",ind.getURI());
			JSONObject properties=new JSONObject();
			curfeature.put("properties",properties);
			while(it2.hasNext()) {
				Statement curst=it2.next();
				if(GEO.HASGEOMETRY.equals(curst.getPredicate().getURI().toString())) {
					properties.put(curst.getPredicate().toString(),curst.getObject().toString());
				}else {
					properties.put(curst.getPredicate().toString(),curst.getObject().toString());
				}
			}
		}
		try {
			response.getWriter().write(geojson.toString(2));
			response.getWriter().close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
