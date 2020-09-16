package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONObject;

public class JSONPWriter extends AbstractGeoJSONWriter {

	public JSONPWriter(String epsg) {
		super(epsg);
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject geojson = this.prepareGeoJSONString(model, response);
		response.getWriter().write("parseResponse("+geojson.toString()+")");
		response.getWriter().close();
		return null;
	}
	
}
