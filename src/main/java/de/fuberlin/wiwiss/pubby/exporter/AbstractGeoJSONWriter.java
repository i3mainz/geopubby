package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONWriter;

import de.fuberlin.wiwiss.pubby.util.Tuple;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class AbstractGeoJSONWriter extends GeoModelWriter {

	protected Map<String,String> contextMapper;
	
	public AbstractGeoJSONWriter(String epsg) {
		super(epsg);
		this.contextMapper=new TreeMap<String,String>();
	}
	
	public JSONObject prepareGeoJSONString(Model model, HttpServletResponse response) throws IOException {
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
				if(ind.hasProperty(GEO.EPSG)) {
					sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}
				while(it2.hasNext()) {
					Statement curst=it2.next();
					Tuple<Boolean,String> handled=this.handleGeometry(curst, ind, model);
					if(!handled.getOne()) {
						if(properties.has(curst.getPredicate().toString())) {
							if (curst.getPredicate().toString().contains("#")) {
								contextMapper.put(curst.getPredicate().toString(),curst.getPredicate().toString().substring(curst.getPredicate().toString().lastIndexOf('#') + 1));
							} else {
								contextMapper.put(curst.getPredicate().toString(),curst.getPredicate().toString().substring(curst.getPredicate().toString().lastIndexOf('/') + 1));
							}
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
				        curfeature.put("geometry",new JSONObject(jsonstring));
						geom=null;
					}
					if(lon!=null && lat!=null) {
						JSONObject geeo=new JSONObject();
						geeo.put("type","Point");
						geeo.put("coordinates",new JSONArray());
						geeo.getJSONArray("coordinates").put(lon);
						geeo.getJSONArray("coordinates").put(lat);
						curfeature.put("geometry",geeo);
					}
				}
			}
		}
		return geojson;
	}
	
}
