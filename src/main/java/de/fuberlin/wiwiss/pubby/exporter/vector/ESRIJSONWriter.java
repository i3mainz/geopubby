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
import org.locationtech.jts.geom.Geometry;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;
import de.fuberlin.wiwiss.pubby.util.Tuple;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class ESRIJSONWriter extends GeoModelWriter {
	
	public ESRIJSONWriter(String epsg) {
		super(epsg);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject geojson=new JSONObject();
		ExtendedIterator<Resource> it=super.write(model, response);
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
				curfeature.put("attributes",properties);
				Double lat=null,lon=null;
				if(ind.hasProperty(GEO.EPSG)) {
					sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}
				while(it2.hasNext()) {
					Statement curst=it2.next();
					Tuple<Boolean,String> handled=this.handleGeometry(curst, ind, model);
					if(!handled.getOne()) {
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
				        curfeature.put("geometry",handleGeometry(geom,this.epsg));
						geom=null;
					}
					if(lon!=null && lat!=null) {
						JSONObject geeo=new JSONObject();
						geeo.put("x",lon);
						geeo.put("y",lat);
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
		return null;
	}
	
	public JSONObject handleGeometry(Geometry geom,String epsgcode) {
		JSONObject result=new JSONObject();
		if(geom.getGeometryType().equalsIgnoreCase("Point")) {
			result.put("x", geom.getCoordinate().getX());
			result.put("y", geom.getCoordinate().getY());
			result.put("wkid", epsgcode);
		}
		return result;
	}
	
}
