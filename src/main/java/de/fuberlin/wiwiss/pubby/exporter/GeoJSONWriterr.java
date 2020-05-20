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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class GeoJSONWriterr implements ModelWriter {


	WKTReader reader=new WKTReader();
	
	@Override
	public void write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=model.
		listResourcesWithProperty(GEO.HASGEOMETRY);
		JSONObject geojson=new JSONObject();
		if(!it.hasNext()) {
			it.close();
			it=model.listResourcesWithProperty(GEO.P_LAT);
		}
		if(!it.hasNext()) {
			it.close();
			it=model.listResourcesWithProperty(GEO.HASGEOMETRY);
		}
		if(!it.hasNext()) {
			it=model.listResourcesWithProperty(model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
			while(it.hasNext()) {
				Resource ind=it.next();
				StmtIterator it2 = ind.listProperties();
				while(it2.hasNext()) {
					Statement curst=it2.next();
					geojson.put(curst.getPredicate().toString(),curst.getObject().toString());
				}
			}
		}else {
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
				JSONObject geometry=new JSONObject();
				curfeature.put("geometry",geometry);
				curfeature.put("properties",properties);
				while(it2.hasNext()) {
					Statement curst=it2.next();
					if(GEO.HASGEOMETRY.getURI().equals(curst.getPredicate().getURI().toString()) || 
							GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())) {
						try {
							Geometry geom=reader.read(curst.getObject().asLiteral().getValue().toString());
							 GeoJSONWriter writer = new GeoJSONWriter();
					            GeoJSON json = writer.write(geom);
					            String jsonstring = json.toString();
					            geometry.put("geometry",new JSONObject(jsonstring));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else {
						properties.put(curst.getPredicate().toString(),curst.getObject().toString());
					}
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
