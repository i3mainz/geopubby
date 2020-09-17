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
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

/**
 * Writes a GeoPubby instance as CSV.
 */
public class CSVWriter extends AbstractGeoJSONWriter {
	
	public CSVWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject geojson=this.prepareGeoJSONString(model, response);
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
