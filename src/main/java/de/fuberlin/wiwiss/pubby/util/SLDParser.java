package de.fuberlin.wiwiss.pubby.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class SLDParser extends DefaultHandler2 {

	OntModel model;
	
	Boolean featureTypeStyle=false;
	
	Boolean filter=false,rule=false,polygon=false,linestring=false,point=false,name=false,and=false,or=false;
	
	Boolean svgParameter=false;
	
	Map<String,String> svgInstance=new HashMap<String,String>();
	
	List<StyleObject> styles=new LinkedList<StyleObject>();
	
	String svgParamName,ruleName;
	
	StyleObject currentStyle;
	
	public SLDParser() {
		this.model=ModelFactory.createOntologyModel();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		System.out.println(qName);
		switch(qName) {
			case "se:PolygonSymbolizer":
				polygon=true;
				break;
			case "se:LineStringSymbolizer":
				linestring=true;
				break;
			case "se:PointSymbolizer":
				point=true;
				break;
			case "se:SvgParameter":
				this.svgParameter=true;
				this.svgParamName=attributes.getValue("name");
				break;
			case "se:CssParameter":
				this.svgParameter=true;
				this.svgParamName=attributes.getValue("name");
				break;
			case "se:Rule":
				this.rule=true;
				this.currentStyle=new StyleObject();
				break;
			case "se:Name":
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
		case "se:SvgParameter":
			this.svgParameter=false;
			break;
		case "se:CssParameter":
			this.svgParameter=false;
			break;
		case "se:PolygonSymbolizer":
			polygon=false;
			this.currentStyle.polygonStyle=this.currentStyle.mapToCSS(this.svgInstance);
			break;
		case "se:LineStringSymbolizer":
			linestring=false;
			this.currentStyle.lineStringStyle=this.currentStyle.mapToCSS(this.svgInstance);
			break;
		case "se:PointSymbolizer":
			point=false;
			this.currentStyle.pointStyle=this.currentStyle.mapToCSS(this.svgInstance);
			break;
		case "se:Rule":
			this.rule=false;
			this.styles.add(currentStyle);
			break;
		case "se:Name":
			this.name=false;
			break;
		}
	}


public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
	SLDParser parser=new SLDParser();
	SAXParserFactory.newInstance().newSAXParser().parse("test.sld", parser);
	System.out.println(parser.styles);
	System.out.println(parser.styles.get(0).toJSON());
}
	
}
