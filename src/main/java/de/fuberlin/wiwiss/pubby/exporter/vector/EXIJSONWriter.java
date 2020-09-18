package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.ct.exi.json.jackson.EXI4JSONFactory;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class EXIJSONWriter extends AbstractGeoJSONWriter {

	public EXIJSONWriter(String epsg) {
		super(epsg);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject geojson=this.prepareGeoJSONString(model, response);
		EXI4JSONFactory fEXI = new EXI4JSONFactory();
		ObjectMapper mapperEXI = new ObjectMapper(fEXI);
		ObjectMapper mapperJSON = new ObjectMapper();
		JsonNode car = mapperJSON.readTree(geojson.toString()); 
		// generate
		OutputStream e4jOS = response.getOutputStream(); // output stream
		mapperEXI.writeTree(fEXI.createGenerator(e4jOS), car);
		response.getWriter().close();
		return null;
	}

}
