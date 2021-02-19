package de.fuberlin.wiwiss.pubby.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.File;
import java.io.FileWriter;
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
	
	Map<String,String> wellKnownNameToSVG=new TreeMap<>();

	private boolean userstyle;

	private String styleId;
	
	public SLDParser() {
		this.model=ModelFactory.createOntologyModel();
		this.wellKnownNameToSVG.put("circle", "");
		this.wellKnownNameToSVG.put("square", "");
		this.wellKnownNameToSVG.put("triangle", "");
		this.wellKnownNameToSVG.put("star", "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"255\" height=\"240\" viewBox=\"0 0 51 48\">\r\n"
				+ "<title>Five Pointed Star</title>\r\n"
				+ "<path fill=\"none\" stroke=\"#000\" d=\"m25,1 6,17h18l-14,11 5,17-15-10-15,10 5-17-14-11h18z\"/>\r\n"
				+ "</svg>");
		this.wellKnownNameToSVG.put("cross", "");
		this.wellKnownNameToSVG.put("x", "");
		this.wellKnownNameToSVG.put("vertline", "");
		this.wellKnownNameToSVG.put("horline", "");
		this.wellKnownNameToSVG.put("slash", "");
		this.wellKnownNameToSVG.put("backslash", "");
		this.wellKnownNameToSVG.put("dot", "");
		this.wellKnownNameToSVG.put("plus", "");
		this.wellKnownNameToSVG.put("times", "");
		this.wellKnownNameToSVG.put("oarrow", "");
		this.wellKnownNameToSVG.put("carrow", "");
		
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
			case "UserStyle":
				this.userstyle=true;
				break;
			case "se:Name":
				this.name=true;
				break;
			case "ogc:PropertyName":
				this.propertyName=true;
				break;
			case "ogc:Point":
				this.literal=true;
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
		if(this.name && this.userstyle && !this.rule) {
			System.out.println(new String(ch,start,length).replace(" ", "_"));
			this.styleId=new String(ch,start,length).replace(" ", "_");
		}
		if(this.name)
			System.out.println("CHaracters: "+new String(ch,start,length)+" "+this.userstyle+" "+this.rule);
		if(this.name && this.userstyle && this.rule) {
			this.ruleName=new String(ch,start,length);
			System.out.println(new String(ch,start,length));
			this.currentStyle.styleName=new String(ch,start,length);
			this.currentStyle.styleId=this.styleId;
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
		case "UserStyle":
			this.userstyle=false;
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
	StringBuilder builder=new StringBuilder();
	for(StyleObject stl:parser.styles) {
		builder.append(stl.toRDF());
	}
	FileWriter writer=new FileWriter(new File("rules.ttl"));
	writer.write(builder.toString());
	writer.close();
}
	
}
