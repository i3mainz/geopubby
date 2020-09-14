package de.fuberlin.wiwiss.pubby.exporter;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.wololo.jts2geojson.GeoJSONReader;

import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class GeoModelWriter extends ModelWriter {

	String epsg=null;
	
	Double lat,lon;
	
	String sourceCRS="EPSG:4326";
	
	Geometry geom;
	
	public GeoModelWriter(String epsg) {
		this.epsg=epsg;
	}
	
	public GeoModelWriter() {
		super();
	}
	
	
	public boolean handleGeometry(Statement curst,Resource ind,Model model) {
		boolean handled=false;
		if (GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString())
				|| GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
				|| GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
			try {
				geom = reader.read(curst.getObject().asLiteral().getString());
				if(this.epsg!=null) {
					geom=ReprojectionUtils.reproject(geom, sourceCRS, epsg);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handled=true;
		} else if (GEO.ASGEOJSON.getURI().equals(curst.getPredicate().getURI().toString())
				&& this.epsg != null) {
			if (curst.getObject().asLiteral().getString() != null) {
				GeoJSONReader read = new GeoJSONReader();
				geom = read.read(curst.getObject().asLiteral().getString());
				if(this.epsg!=null) {
					ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
					geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
				}
			}
			handled=true;
		} else if (GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())) {
			lat = curst.getObject().asLiteral().getDouble();
			handled=true;
		} else if (GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())) {
			lon = curst.getObject().asLiteral().getDouble();
			handled=true;
		} else if (GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())) {
			lat = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
			lon = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
			handled=true;
		}
		return handled;
	}
	
	
}
