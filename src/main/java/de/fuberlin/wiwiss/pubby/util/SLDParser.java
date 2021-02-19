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
	
	Boolean filter=false,rule=false,polygon=false,linestring=false,point=false,name=false,and=false,or=false,condition=false;
	
	Boolean svgParameter=false;
	
	Map<String,String> svgInstance=new HashMap<String,String>();
	
	List<StyleObject> styles=new LinkedList<StyleObject>();
	
	String svgParamName,ruleName,conditionName;
	
	StyleObject currentStyle;
	
	Condition currentCondition;
	
	List<Condition> curcondlist;

	private boolean propertyName,literal,gmlgeometry;
	
	public SLDParser() {
		this.model=ModelFactory.createOntologyModel();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch(qName) {
			case "se:PolygonSymbolizer":
				polygon=true;
				break;
			case "se:LineSymbolizer":
				linestring=true;
				break;
			case "ogc:Filter":
				filter=true;
				this.currentCondition=new Condition();
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
				this.curcondlist=new LinkedList<>();
				break;
			case "se:Name":
				this.name=true;
				break;
			case "ogc:PropertyName":
				this.propertyName=true;
				break;
			case "ogc:Literal":
				this.literal=true;
				break;
			case "ogc:PropertyIsEqualTo":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsEqualTo";
				break;
			case "ogc:PropertyIsNotEqualTo":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsNotEqualTo";
				break;
			case "ogc:PropertyIsLessThan":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsLessThan";
				break;
			case "ogc:PropertyIsLessThanOrEqualTo":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsLessThanOrEqualTo";
				break;
			case "ogc:PropertyIsGreaterThan":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsGreaterThan";
				break;
			case "ogc:PropertyIsGreaterThanOrEqualTo":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsGreaterThanOrEqualTo";
				break;
			case "ogc:PropertyIsLike":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsLike";
				break;
			case "ogc:PropertyIsNull":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsNull";
				break;
			case "ogc:PropertyIsBetween":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="PropertyIsBetween";
				break;
			case "ogc:Intersects":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Intersects";
				break;
			case "ogc:Equals":
				this.gmlgeometry=true;
				this.condition=true;
				this.currentCondition.operator="Equals";
				break;
			case "ogc:Disjoint":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Disjoint";
				break;
			case "ogc:Touches":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Touches";
				break;
			case "ogc:Within":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Within";
				break;
			case "ogc:Overlaps":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Overlaps";
				break;
			case "ogc:Crosses":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Crosses";
				break;
			case "ogc:Contains":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Contains";
				break;
			case "ogc:DWithin":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="DWithin";
				break;
			case "ogc:Beyond":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="Beyond";
				break;
			case "ogc:BBOX":
				this.condition=true;
				this.gmlgeometry=true;
				this.currentCondition.operator="BBOX";
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
		if(this.propertyName) {
			this.currentCondition.property=new String(ch,start,length);
		}
		if(this.literal) {
			this.currentCondition.value=new String(ch,start,length);
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
		case "ogc:Filter":
			filter=false;
			this.curcondlist.add(currentCondition);
			System.out.println(currentCondition.toSHACL());
			break;
		case "se:PolygonSymbolizer":
			polygon=false;
			this.currentStyle.polygonStyle=this.currentStyle.mapToCSS(this.svgInstance);
			break;
		case "se:LineSymbolizer":
			linestring=false;
			this.currentStyle.lineStringStyle=this.currentStyle.mapToCSS(this.svgInstance);
			break;
		case "se:PointSymbolizer":
			point=false;
			this.currentStyle.pointStyle=this.currentStyle.mapToCSS(this.svgInstance);
			break;
		case "se:Rule":
			this.rule=false;
			this.currentStyle.conditions=this.curcondlist;
			this.styles.add(currentStyle);
			break;
		case "se:Name":
			this.name=false;
			break;
		case "ogc:PropertyName":
			this.propertyName=false;
			break;
		case "ogc:Literal":
			this.literal=false;
			break;
		case "ogc:PropertyIsEqualTo":
			this.condition=false;
			break;
		case "ogc:PropertyIsNotEqualTo":
			this.condition=false;
			break;
		case "ogc:PropertyIsLessThan":
			this.condition=false;
			break;
		case "ogc:PropertyIsLessThanOrEqualTo":
			this.condition=false;
			break;
		case "ogc:PropertyIsGreaterThan":
			this.condition=false;
			break;
		case "ogc:PropertyIsGreaterThanOrEqualTo":
			this.condition=false;
			break;
		case "ogc:PropertyIsLike":
			this.condition=false;
			break;
		case "ogc:PropertyIsNull":
			this.condition=false;
			break;
		case "ogc:PropertyIsBetween":
			this.condition=false;
			break;
		case "ogc:Intersects":
			this.condition=false;
			break;
		case "ogc:Equals":
			this.condition=false;
			break;
		case "ogc:Disjoint":
			this.condition=false;
			break;
		case "ogc:Touches":
			this.condition=false;
			break;
		case "ogc:Within":
			this.condition=false;
			break;
		case "ogc:Overlaps":
			this.condition=false;
			break;
		case "ogc:Crosses":
			this.condition=false;
			break;
		case "ogc:Contains":
			this.condition=false;
			break;
		case "ogc:DWithin":
			this.condition=false;
			break;
		case "ogc:Beyond":
			this.condition=false;
			break;
		case "ogc:BBOX":
			this.condition=false;
			break;
		}
	}


public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
	SLDParser parser=new SLDParser();
	SAXParserFactory.newInstance().newSAXParser().parse("test.sld", parser);
	System.out.println(parser.styles);
	System.out.println(parser.styles.size());
	System.out.println(parser.styles.get(0).toJSON());
}
	
}
