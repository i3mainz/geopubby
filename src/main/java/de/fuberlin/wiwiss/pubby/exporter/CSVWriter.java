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
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

/**
 * Writes a GeoPubby instance as CSV.
 */
public class CSVWriter extends GeoModelWriter {
	
	public CSVWriter(String epsg) {
		super(epsg);
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
				         curfeature.put("geometry",new JSONObject(jsonstring));
				         geom=null;
					}
					if(lon!=null && lat!=null) {
						JSONObject geeo=new JSONObject();
						geeo.put("type","Point");
						geeo.put("coordinates",new JSONArray());
						geeo.getJSONArray("coordinates").put(lat);
						geeo.getJSONArray("coordinates").put(lon);
						curfeature.put("geometry",geeo);
						lat=null;
						lon=null;
					}
				}
			}
		}	
		StringBuilder csvresult=new StringBuilder();
		StringBuilder csvresultheader=new StringBuilder();
		csvresultheader.append("the_geom,");
		GeoJSONReader reader=new GeoJSONReader();
		for(int i=0;i<geojson.getJSONArray("features").length();i++) {
			Geometry geom=reader.read(geojson.getJSONArray("features").getJSONObject(i).getJSONObject("geometry").toString());
			csvresult.append(geom.toText()+",");
			JSONObject props=geojson.getJSONArray("features").getJSONObject(i).getJSONObject("properties");
			for(String key:props.keySet()) {
				if(i==0) {
					csvresultheader.append(key+",");
				}
				if(props.get(key).toString().contains("^^")) {
					csvresult.append(props.get(key).toString().substring(props.get(key).toString().lastIndexOf("^^")+2)+",");
				}else {
					csvresult.append(props.get(key)+",");
				}	
			}
			if((i+1)<geojson.getJSONArray("features").length())
				csvresult.append(System.lineSeparator());
			
		}
		csvresultheader.delete(csvresultheader.length()-1, csvresultheader.length());
		csvresult.delete(csvresult.length()-1, csvresult.length());
		try {
			response.getWriter().write(csvresultheader.toString()+System.lineSeparator()+csvresult.toString());
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
