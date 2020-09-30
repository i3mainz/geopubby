package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class GeobufWriter extends AbstractGeoJSONWriter {

	
	public GeobufWriter(String epsg) {
		super(epsg);
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
	
		// TODO Auto-generated method stub
		return super.write(model, response);
	}

}
