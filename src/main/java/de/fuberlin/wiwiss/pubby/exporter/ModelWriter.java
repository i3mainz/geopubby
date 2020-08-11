package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.locationtech.jts.io.WKTReader;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

/**
 * A model class for exporting RDF in several downlift formats.
 */
public class ModelWriter {
	
protected Property usedProperty=null;

protected WKTReader reader=new WKTReader();
	
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=model.
				listResourcesWithProperty(GEO.HASGEOMETRY);			
		usedProperty=GEO.HASGEOMETRY;
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.P_LAT);
			usedProperty=GEO.P_LAT;
		}
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.HASGEOMETRY);
			usedProperty=GEO.HASGEOMETRY;
		}
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.GEORSSPOINT);
			usedProperty=GEO.GEORSSPOINT;
		}
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.P625);
			usedProperty=GEO.P625;
		}
		return it;
	}
}