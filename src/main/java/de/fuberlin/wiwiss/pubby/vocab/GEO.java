package de.fuberlin.wiwiss.pubby.vocab;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class GEO {

	private static Model model = ModelFactory.createDefaultModel();
	
	public static final String WGS84GEONS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
	public static final String GEOSPARQLNS = "http://www.opengis.net/ont/geosparql#";
	
	public static final String GEOSPARQLSTYLENS = "http://www.opengis.net/ont/geosparql/style#";
	
	public static final String WIKIDATANS = "http://www.wikidata.org/prop/direct/";

	public static final Property P_LONG = model.createProperty( WGS84GEONS + "long" );
	
	public static final Property P_LAT = model.createProperty( WGS84GEONS + "lat" );
	
	public static final Property P_GEOMETRY = model.createProperty( WGS84GEONS + "geometry" );
	
	public static final Property LOCATION = model.createProperty( WGS84GEONS + "location" );
	
	public static final Property HASGEOMETRY = model.createProperty( GEOSPARQLNS + "hasGeometry" );
		
	public static final Property ASWKT = model.createProperty( GEOSPARQLNS + "asWKT" );

	public static final Property ASHEXWKB = model.createProperty( GEOSPARQLNS + "asHexWKB" );
	
	public static final Property ASWKB = model.createProperty( GEOSPARQLNS + "asWKB" );
	
	public static final Property ASGEOJSON = model.createProperty( GEOSPARQLNS + "asGeoJSON" );
	
	public static final Property ASGML = model.createProperty( GEOSPARQLNS + "asGML" );
	
	public static final Property EPSG = model.createProperty( GEOSPARQLNS + "epsg" );
	
	public static final Property STYLE = model.createProperty( GEOSPARQLNS + "style" );
	
	public static final Property LINESTRINGSTYLE=model.createProperty(GEOSPARQLNS+"lineStringStyle");
	
	public static final Property LINESTRINGIMAGE=model.createProperty(GEOSPARQLNS+"lineStringImage");
	
	public static final Property POINTSTYLE=model.createProperty(GEOSPARQLNS+"pointStyle");
	
	public static final Property POINTIMAGE=model.createProperty(GEOSPARQLNS+"pointImage");
	
	public static final Property POLYGONSTYLE=model.createProperty(GEOSPARQLNS+"polygonStyle");	
	
	public static final Property POLYGONIMAGE=model.createProperty(GEOSPARQLNS+"polygonImage");
	
	public static final Property HATCH=model.createProperty(GEOSPARQLNS+"hatch");
	
	public static final Property LINESTRINGIMAGESTYLE=model.createProperty(GEOSPARQLNS+"lineStringImageStyle");

	public static final Property POPUPSTYLE = model.createProperty( GEOSPARQLNS + "popupStyle" );
	
	public static final Property TEXTSTYLE = model.createProperty( GEOSPARQLNS + "textStyle" );

	public static final Property LINESTRINGSTYLE2=model.createProperty(GEOSPARQLSTYLENS+"lineStringStyle");
	
	public static final Property LINESTRINGIMAGE2=model.createProperty(GEOSPARQLSTYLENS+"lineStringImage");
	
	public static final Property POINTSTYLE2=model.createProperty(GEOSPARQLSTYLENS+"pointStyle");
	
	public static final Property POINTIMAGE2=model.createProperty(GEOSPARQLSTYLENS+"pointImage");
	
	public static final Property POLYGONSTYLE2=model.createProperty(GEOSPARQLSTYLENS+"polygonStyle");	
	
	public static final Property POLYGONIMAGE2=model.createProperty(GEOSPARQLSTYLENS+"polygonImage");
	
	public static final Property HATCH2=model.createProperty(GEOSPARQLSTYLENS+"hatch");
	
	public static final Property LINESTRINGIMAGESTYLE2=model.createProperty(GEOSPARQLSTYLENS+"lineStringImageStyle");

	public static final Property POPUPSTYLE2 = model.createProperty( GEOSPARQLSTYLENS + "popupStyle" );
	
	public static final Property TEXTSTYLE2 = model.createProperty( GEOSPARQLSTYLENS + "textStyle" );
	
	public static final Property P625 = model.createProperty( WIKIDATANS + "P625" );
	
	public static final Property GEORSSPOINT= model.createProperty("http://www.georss.org/georss/point");

	public static final Property ASKML = model.createProperty( GEOSPARQLNS + "asKML" );

	public static List<Property> geomprops=Arrays.asList(new Property[] {P_LONG,P_LAT,P_GEOMETRY,LOCATION,HASGEOMETRY,ASWKT,ASKML,ASGML,ASWKB,ASHEXWKB,ASGEOJSON,P625,GEORSSPOINT});

	
	
}
