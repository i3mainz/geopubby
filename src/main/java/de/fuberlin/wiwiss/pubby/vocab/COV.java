package de.fuberlin.wiwiss.pubby.vocab;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class COV {

	private static Model model = ModelFactory.createDefaultModel();
	
	public static final String GEOSPARQLNS = "http://www.opengis.net/ont/geosparql#";
	
	public static final Property HASCOVERAGE = model.createProperty( GEOSPARQLNS + "hasCoverage" );

	public static final Property ASHEXRASTWKB = model.createProperty( GEOSPARQLNS + "asHexRastWKB" );
	
	public static final Property ASRASTWKB = model.createProperty( GEOSPARQLNS + "asRastWKB" );
	
	public static final Property ASCOVERAGEJSON = model.createProperty( GEOSPARQLNS + "asCoverageJSON" );
	
	public static final Property ASGMLCOV = model.createProperty( GEOSPARQLNS + "asGMLCOV" );
	
	public static final Property EPSG = model.createProperty( GEOSPARQLNS + "epsg" );
	
	public static List<Property> covprops=Arrays.asList(new Property[] {HASCOVERAGE,ASCOVERAGEJSON,ASGMLCOV,ASRASTWKB,ASHEXRASTWKB});
	
}
