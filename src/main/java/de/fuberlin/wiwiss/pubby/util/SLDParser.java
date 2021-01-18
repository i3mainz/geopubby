package de.fuberlin.wiwiss.pubby.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class SLDParser extends DefaultHandler2 {

	OntModel model;
	
	Boolean featureTypeStyle=false;
	
	Boolean filter=false,rule=false,polygon=false,linestring=false,point=false,name=false;
	
	Boolean svgParameter=false;
	
	Map<String,String> svgInstance=new HashMap<String,String>();
	
	List<StyleObject> styles=new LinkedList<StyleObject>();
	
	String svgParamName,ruleName;
	
	
	
	public SLDParser() {
		this.model=ModelFactory.createOntologyModel();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		switch(qName) {
			case "PolygonSymbolizer":
				polygon=true;
				break;
			case "LineStringSymbolizer":
				linestring=true;
				break;
			case "PointSymbolizer":
				point=true;
				break;
			case "SvgParameter":
				this.svgParameter=true;
				this.svgParamName=attributes.getValue("name");
				break;
			case "Rule":
				this.rule=true;
				break;
			case "Name":
				this.name=true;
				break;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(svgParameter) {
			this.svgInstance.put(this.svgParamName, new String(ch,start,length));
		}
		if(this.name) {
			this.ruleName=new String(ch,start,length);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch(qName) {
		case "SvgParameter":
			this.svgParameter=false;
			break;
		case "PolygonSymbolizer":
			polygon=false;
			break;
		case "LineStringSymbolizer":
			linestring=false;
			break;
		case "PointSymbolizer":
			point=false;
			break;
		case "Rule":
			this.rule=false;
			break;
		case "Name":
			this.name=false;
			break;
		}
	}
	
	
	
}
