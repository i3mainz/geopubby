package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONObject;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;
import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class SHPWriter extends AbstractGeoJSONWriter {

	public SHPWriter(String epsg) {
		super(epsg);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject json=this.prepareGeoJSONString(model, response);
		return null;
	}
	
}
