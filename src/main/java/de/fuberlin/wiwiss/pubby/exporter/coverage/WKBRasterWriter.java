package de.fuberlin.wiwiss.pubby.exporter.coverage;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class WKBRasterWriter extends GeoModelWriter {

	public WKBRasterWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		return super.write(model, response);
	}
	
}
