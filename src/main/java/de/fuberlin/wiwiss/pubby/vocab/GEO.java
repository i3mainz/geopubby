package de.fuberlin.wiwiss.pubby.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class GEO {

	private static Model model = ModelFactory.createDefaultModel();
	
	public static final String WGS84GEONS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
	public static final String GEOSPARQLNS = "http://www.opengis.net/ont/geosparql#";

	public static final Property P_LONG = model.createProperty( WGS84GEONS + "long" );
	
	public static final Property P_LAT = model.createProperty( WGS84GEONS + "lat" );
	
	public static final Property LOCATION = model.createProperty( WGS84GEONS + "location" );
	
	public static final Property HASGEOMETRY = model.createProperty( GEOSPARQLNS + "hasGeometry" );
	
	public static final Property ASWKT = model.createProperty( GEOSPARQLNS + "asWKT" );
	
}
