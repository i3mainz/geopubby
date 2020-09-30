package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.json.JSONObject;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class GeoJSONLDWriter extends AbstractGeoJSONWriter {

		public GeoJSONLDWriter(String epsg) {
			super(epsg);
		}
		
		
		@Override
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {	
			JSONObject geojson=this.prepareGeoJSONString(model, response);	
			String res=geojson.toString();
			JSONObject context=new JSONObject();	  
		    context.put("geojson","https://purl.org/geojson/vocab#");
		    context.put("Feature","geojson:Feature");
		    context.put("FeatureCollection","geojson:FeatureCollection");
		    context.put("GeometryCollection","geojson:GeometryCollection");
		    context.put("LineString","geojson:LineString");
		    context.put("MultiLineString", "geojson:MultiLineString");
		    context.put("MultiPoint","geojson:MultiPoint");
		    context.put("MultiPolygon", "geojson:MultiPolygon");
		    context.put("Point", "geojson:Point");
		    context.put("Polygon", "geojson:Polygon");
		    JSONObject bbox=new JSONObject();
		    context.put("bbox", bbox);
		    bbox.put("@container", "@list");
		    bbox.put("@id","geojson:bbox");
		    JSONObject coordinates=new JSONObject();
		    context.put("coordinates", coordinates);
		    coordinates.put("@container", "@list");
		    coordinates.put("@id", "geojson:coordinates");
		    JSONObject featuresschema=new JSONObject();
		    featuresschema.put("@container", "@set");
		    featuresschema.put("@id", "geojson:features");
		    context.put("geometry","geojson:geometry");
		    context.put("id", "@id");
		    context.put("properties", "geojson:properties");
		    context.put("type", "@type");
		    Map<String,Integer> alreadymatched=new TreeMap<String,Integer>();
		    if(this.contextMapper!=null && !this.contextMapper.keySet().isEmpty()) {
			    for(String key:this.contextMapper.keySet()) {
			    	if(!alreadymatched.containsKey(this.contextMapper.get(key))) {
			    		alreadymatched.put(this.contextMapper.get(key),1);
			    		res=res.replace(key,this.contextMapper.get(key));
			    	}else {
			    		res=res.replace(key+alreadymatched.get(this.contextMapper.get(key))+1,this.contextMapper.get(key));
			    		alreadymatched.put(this.contextMapper.get(key),alreadymatched.get(this.contextMapper.get(key))+1);
			    	}
			    	context.put(this.contextMapper.get(key),key);
			    }
		    }
		    JSONObject geojsonresults=new JSONObject(res);
		    geojsonresults.put("@context",context);
			try {
				response.getWriter().write(geojsonresults.toString(2));
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
