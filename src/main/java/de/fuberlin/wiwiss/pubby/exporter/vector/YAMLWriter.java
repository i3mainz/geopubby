package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class YAMLWriter extends AbstractGeoJSONWriter {

	public YAMLWriter(String epsg) {
		super(epsg);
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject geojson = this.prepareGeoJSONString(model, response);
		JsonNode jsonNodeTree;
		try {
			jsonNodeTree = new ObjectMapper().readTree(geojson.toString());
			String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
			response.getWriter().write(jsonAsYaml);
			response.getWriter().close();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

}
