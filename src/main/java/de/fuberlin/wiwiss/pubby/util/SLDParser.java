package de.fuberlin.wiwiss.pubby.util;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class SLDParser extends DefaultHandler2 {
public boolean rule,and,svgParameter,filter,name,or,polygon,linestring,point;

List<StyleObject> styles;

StyleObject currentStyle;

@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch(qName) {
		case "Rule":
			currentStyle=new StyleObject();
			break;
		case "SVGParameter":
			this.svgParameter=true;
			break;
		case "CSSParameter":
			this.svgParameter=true;
			break;
		case "Filter":
			this.filter=true;
			break;
		case "PolygonSymbolizser":
			this.polygon=true;
			break;
		case "And":
			this.and=true;
			break;
		case "Or":
			this.or=true;
			break;
		case "Name":
			this.name=true;
			break;
		}
	}

@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(name) {
			
		}
		if(svgParameter) {
			
		}
	}

@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	switch(qName) {
	case "Rule":
		this.styles.add(currentStyle);
		break;
	case "SVGParameter":
		this.svgParameter=true;
		
		break;
	case "CSSParameter":
		this.svgParameter=true;
		break;
	case "Filter":
		this.filter=true;
		break;
	case "PolygonSymbolizser":
		this.polygon=true;
		break;
	case "And":
		this.and=true;
		break;
	case "Or":
		this.or=true;
		break;
	case "Name":
		this.name=true;
		break;
	}
	}


public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
	SLDParser parser=new SLDParser();
	SAXParserFactory.newInstance().newSAXParser().parse("test.sld", parser);
	System.out.println(parser.styles);
}

/*
public static void main(String[] args) {
	String query="PREFIX my: <http://example.org/ApplicationSchema#>\n"
			+ "PREFIX geo: <http://www.opengis.net/ont/geosparql#>\n"
			+ "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n"
			+ "SELECT ?union\n"
			+ "WHERE {\n"
			+ "  my:A geo:hasDefaultGeometry ?aGeom .\n"
			+ "  ?aGeom geo:asGML ?aGML .\n"
			+ "  my:B geo:hasDefaultGeometry ?bGeom .\n"
			+ "  ?bGeom geo:asGML ?bGML .\n"
			+ "  BIND (geof:union(?aGML, ?bGML) as ?union)\n"
			+ "}\n"
			+ "";
	Query q = QueryFactory.create(query);

	QueryEngineHTTP qexec =new QueryEngineHTTP("http://localhost:3030/ds", q);
	qexec.addParam("format", "json");
	        try {
	            ResultSet results = qexec.execSelect();
	            while(results.hasNext()) {
	            	QuerySolution solu=results.next();
	            	System.out.println(solu.toString());
	            }

	        } catch (Exception ex) {
	            System.out.println(ex.getMessage());
	        }
	        qexec.close();
}*/
	
}
