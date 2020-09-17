package de.fuberlin.wiwiss.pubby.exporter.vector;

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
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONWriter;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class TopoJSONWriter extends GeoModelWriter {

	public TopoJSONWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject topojson=new JSONObject();
		ExtendedIterator<Resource> it=super.write(model, response);
		if(!it.hasNext()) {
			it=model.listResourcesWithProperty(model.createProperty("http://www.w3.org/2000/01/rdf-schema#label"));
			while(it.hasNext()) {
				Resource ind=it.next();
				StmtIterator it2 = ind.listProperties();
				while(it2.hasNext()) {
					Statement curst=it2.next();
					if(topojson.has(curst.getPredicate().toString())) {
						if(topojson.optJSONArray(curst.getPredicate().toString())!=null) {
							topojson.getJSONArray(curst.getPredicate().toString()).put(curst.getObject().toString());
						}else {
							JSONArray arr=new JSONArray();
							arr.put(curst.getObject().toString());
							topojson.put(curst.getPredicate().toString(),arr);
						}
					}else {
						topojson.put(curst.getPredicate().toString(),curst.getObject().toString());
					}	
				}
			}
		}else {
			topojson.put("type","Topology");
			JSONObject features=new JSONObject();
			topojson.put("objects",features);
			topojson.put("arcs",new JSONArray());
			while(it.hasNext()) {
				Resource ind=it.next();
				if(ind.hasProperty(GEO.EPSG)) {
					sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}
				StmtIterator it2 = ind.listProperties();
				JSONObject curfeature=new JSONObject();
				curfeature.put("id",ind.getURI());
				JSONObject properties=new JSONObject();
				curfeature.put("properties",properties);
				Double lat=null,lon=null;
				while(it2.hasNext()) {
					Statement curst=it2.next();
					boolean handled=this.handleGeometry(curst, ind, model);
					if(!handled) {
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
					if(geom!=null) {
						GeoJSONWriter writer = new GeoJSONWriter();
				        GeoJSON json = writer.write(geom);
				        String jsonstring = json.toString();
					    curfeature.put("coordinates",new JSONObject(jsonstring).getJSONArray("coordinates"));
						curfeature.put("type",new JSONObject(jsonstring).getString("type"));
						features.put(new JSONObject(jsonstring).getString("type").toLowerCase(),curfeature);
						geom=null;
					}
					if(lon!=null && lat!=null) {
						curfeature.put("type","Point");
						curfeature.put("coordinates",new JSONArray());
						curfeature.getJSONArray("coordinates").put(lon);
						curfeature.getJSONArray("coordinates").put(lat);
						features.put("point",curfeature);
					}
				}
			}
		}	
		try {
			response.getWriter().write(topojson.toString(2));
			response.getWriter().close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}