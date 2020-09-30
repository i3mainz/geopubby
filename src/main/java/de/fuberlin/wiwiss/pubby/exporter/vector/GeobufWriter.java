package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONObject;
import org.wololo.jts2geojson.GeoJSONReader;

import com.conveyal.data.geobuf.GeobufEncoder;
import com.conveyal.data.geobuf.GeobufFeature;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;
import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class GeobufWriter extends AbstractGeoJSONWriter {

	GeoJSONReader geojsonreader = new GeoJSONReader();
	
	public GeobufWriter(String epsg) {
		super(epsg);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject geojson=this.prepareGeoJSONString(model, response);
		List<GeobufFeature> feats=new LinkedList<>();
		for(int i=0;i<geojson.getJSONArray("features").length();i++) {
			JSONObject ft=geojson.getJSONArray("features").getJSONObject(i);
			GeobufFeature feat=new GeobufFeature();
			feat.geometry=geojsonreader.read(ft.getJSONObject("geometry").toString());			
		}

		GeobufEncoder enc=new GeobufEncoder(null, 0);
		enc.
	}

}
