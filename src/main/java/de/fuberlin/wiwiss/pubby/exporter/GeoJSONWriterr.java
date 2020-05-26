package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
			it.close();
			it=model.listResourcesWithProperty(GEO.GEORSSPOINT);
		}
		if(!it.hasNext()) {
			it.close();
			it=model.listResourcesWithProperty(GEO.P625);
		}
		if(!it.hasNext()) {
			it=model.listResourcesWithProperty(model.createProperty("http://www.w3.org/2000/01/rdf-schema#label"));
			while(it.hasNext()) {
				Resource ind=it.next();
				StmtIterator it2 = ind.listProperties();
				while(it2.hasNext()) {
					Statement curst=it2.next();
					if(geojson.has(curst.getPredicate().toString())) {
						if(geojson.optJSONArray(curst.getPredicate().toString())!=null) {
							geojson.getJSONArray(curst.getPredicate().toString()).put(curst.getObject().toString());
						}else {
							JSONArray arr=new JSONArray();
							arr.put(curst.getObject().toString());
							geojson.put(curst.getPredicate().toString(),arr);
						}
					}else {
						geojson.put(curst.getPredicate().toString(),curst.getObject().toString());
					}	
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
				curfeature.put("type","Feature");
				JSONObject properties=new JSONObject();
				curfeature.put("properties",properties);
				Double lat=null,lon=null;
				while(it2.hasNext()) {
					Statement curst=it2.next();
					if(GEO.HASGEOMETRY.getURI().equals(curst.getPredicate().getURI().toString()) ||
							GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
							|| 
							GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
						try {
							Geometry geom=null;
							if(GEO.HASGEOMETRY.getURI().equals(curst.getPredicate().getURI().toString())) {
								geom=reader.read(curst.getObject().asResource().getProperty(GEO.ASWKT).getObject().asLiteral().getString());
							}else {
								geom=reader.read(curst.getObject().asLiteral().getString());
							}		
							 GeoJSONWriter writer = new GeoJSONWriter();
					            GeoJSON json = writer.write(geom);
					            String jsonstring = json.toString();
					            curfeature.put("geometry",new JSONObject(jsonstring));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())){
						lat=curst.getObject().asLiteral().getDouble();
					}else if(GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())){
						lon=curst.getObject().asLiteral().getDouble();
					}else if(GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())){
						lat=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
						lon=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
					}else {
						if(properties.has(curst.getPredicate().toString())) {
							if(properties.optJSONArray(curst.getPredicate().toString())!=null) {
								properties.getJSONArray(curst.getPredicate().toString()).put(curst.getObject().toString());
							}else {
								JSONArray arr=new JSONArray();
								arr.put(curst.getObject().toString());
								properties.put(curst.getPredicate().toString(),arr);
							}
						}else {
						   properties.put(curst.getPredicate().toString(),curst.getObject().toString());
						}					
					}
					if(lon!=null && lat!=null) {
						JSONObject geeo=new JSONObject();
						geeo.put("type","Point");
						geeo.put("coordinates",new JSONArray());
						geeo.getJSONArray("coordinates").put(lat);
						geeo.getJSONArray("coordinates").put(lon);
						curfeature.put("geometry",geeo);
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
