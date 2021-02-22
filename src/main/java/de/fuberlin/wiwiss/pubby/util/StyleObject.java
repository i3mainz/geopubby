package de.fuberlin.wiwiss.pubby.util;

import java.io.StringWriter;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONObject;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

/**
 * An object containing style definitions to apply on geospatial objects.
 *
 */
public class StyleObject {

	/**
	 * A point style definition in CSS.
	 */
	public String pointStyle;
	
	/**
	 * An SVG definition oder an image path to indicate an icon for a point.
	 */
	public String pointImage;
	
	/**
	 * A hatch definition in CSS.
	 */
	public String lineStringStyle;

	/**
	 * An SVG definition oder an image path to indicate an icon for a LineString.
	 */
	public String lineStringImage;
	
	/**
	 * A Polygon style definition in CSS.
	 */
	public String polygonStyle;
	
	/**
	 * An SVG definition oder an image path to indicate an icon for a Polygon.
	 */
	public String polygonImage;

	/**
	 * A hatch definition in CSS.
	 */
	public String hatch;

	/**
	 * A style definition of an image which is enclosed in a LineString.
	 */
	public String lineStringImageStyle;
	
	/**
	 * A style definition of an image which is applied to a raster.
	 */
	public String rasterStyle;
	
	public Map<String,String> rasterColorMap=new TreeMap<String,String>();
	
	/**
	 * An identifier of a given style.
	 */
	public String styleName;
	
	public String styleId;
	
	/**
	 * A description of a given style.
	 */
	public String styleDescription;
	
	/**
	 * A style definition for the map popup.
	 */
	public String popupStyle;
	
	/**
	 * A style definition for the map popup.
	 */
	public String textStyle;
	
	public List<Condition> conditions;

	private String propertyNamespace="";
	
	public String sldString="";

	public String qmlString="";
	
	public StyleObject() {
		this("");
	}
	
	public StyleObject(String propertyNamespace) {
		this.propertyNamespace=propertyNamespace;
	}

	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder();
		builder.append("StyleObject [styleId=" +styleId+","+System.lineSeparator()+" styleName=\""+styleName+"\","+System.lineSeparator());
		if(pointStyle!=null) {
			builder.append(" pointStyle=" + pointStyle+System.lineSeparator());
		}
		if(pointImage!=null) {
			builder.append(" pointImage=" + pointImage+System.lineSeparator());
		}
		if(lineStringStyle!=null) {
			builder.append(" lineStringStyle=" + lineStringStyle+System.lineSeparator());
		}
		if(lineStringImage!=null) {
			builder.append(" lineStringImage=" + lineStringImage+System.lineSeparator());
		}
		if(polygonStyle!=null) {
			builder.append(" polygonStyle=" + polygonStyle+System.lineSeparator());
		}
		if(polygonImage!=null) {
			builder.append(" polygonImage=" + polygonImage+System.lineSeparator());
		}
		if(hatch!=null) {
			builder.append(" hatch=" + hatch+System.lineSeparator());
		}
		if(popupStyle!=null) {
			builder.append(" popupStyle=" + popupStyle+System.lineSeparator());
		}
		if(rasterColorMap!=null) {
			builder.append(" rasterColorMap=" + rasterColorMap+System.lineSeparator());
		}
		builder.append(" conditions: "+this.conditions+"]"+System.lineSeparator());
		return builder.toString();
	}
	
	/**
	 * Exports the StyleObject to JSON. 
	 * @return The JSONObject containing the style information
	 */
	public String toJSON() {
		JSONObject result=new JSONObject();
		result.put("pointStyle",(pointStyle!=null?pointStyle.replace("\"","").replace("\\",""):null));
		result.put("pointImage",(pointImage!=null?pointImage.replace("\"","").replace("\\",""):null));
		result.put("lineStringStyle",(lineStringStyle!=null?lineStringStyle.replace("\"","").replace("\\",""):null));
		result.put("lineStringImage",(lineStringImage!=null?lineStringImage.replace("\"","").replace("\\",""):null));
		result.put("lineStringImageStyle",(lineStringImageStyle!=null?lineStringImageStyle.replace("\"","").replace("\\",""):null));
		result.put("polygonStyle",(polygonStyle!=null?polygonStyle.replace("\"","").replace("\\",""):null));
		result.put("polygonImage",(polygonImage!=null?polygonImage.replace("\"","").replace("\\",""):null));
		result.put("popupStyle",(popupStyle!=null?popupStyle.replace("\"","").replace("\\",""):null));
		result.put("hatch",(hatch!=null?hatch.replace("\"","").replace("\\",""):null));
		result.put("styleName",styleName);
		return result.toString(2);
	}
	
	public String getCommonPrefixes() {
		StringBuilder builder=new StringBuilder();
		builder.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."+System.lineSeparator());
		builder.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."+System.lineSeparator());
		builder.append("@prefix geo: <http://www.opengis.net/ont/geosparql#> ."+System.lineSeparator());
		builder.append("@prefix geost: <http://www.opengis.net/ont/geosparql/style#> ."+System.lineSeparator());
		builder.append("@prefix sh: <http://www.w3.org/ns/shacl#> ."+System.lineSeparator());				
		return builder.toString();
	}

	public String toRDF() {
		return toRDF(null);
	}
	
	
	public String toRDF(String target) {
		StringBuilder ttl=new StringBuilder();
		if(this.styleId==null || this.styleName==null)
			return "";
		String curstyleid=this.styleId+"_"+this.styleName.replace(" ", "_");
		String curstyleind="geo:"+curstyleid;
		if(target==null && propertyNamespace!=null && !propertyNamespace.isEmpty()) {
			target="<"+propertyNamespace+curstyleid+">";
		}else if(target==null){
			target="geo:"+curstyleid;
		}else if(target.startsWith("http")) {
			target="<"+target+">";
		}
		if(propertyNamespace!=null && !propertyNamespace.isEmpty()) {
			curstyleind="<"+propertyNamespace+curstyleid+">";
		}
		ttl.append(target+" rdf:type rdfs:Class, sh:NodeShape . "+System.lineSeparator());
		ttl.append(curstyleind+" rdf:type geo:Style . "+System.lineSeparator());
		if(!sldString.isEmpty()) {
			ttl.append(curstyleind+" geost:asSLD \""+this.sldString.replace("\"", "'")+"\"^^geost:sldLiteral . "+System.lineSeparator());
		}
		if(!qmlString.isEmpty()) {
			ttl.append(curstyleind+" geost:asQML \""+this.sldString.replace("\"", "'")+"\"^^geost:qmlLiteral . "+System.lineSeparator());
		}
		ttl.append(curstyleind+" rdfs:label \""+this.styleName+"\"@en . "+System.lineSeparator());
		ttl.append(curstyleind+" rdfs:comment \""+this.styleName+"\"@en . "+System.lineSeparator());
		if(pointStyle!=null) {
			ttl.append(curstyleind+" geost:pointStyle \""+this.pointStyle+"\"^^geost:cssLiteral . "+System.lineSeparator());
		}
		if(pointImage!=null) {
			ttl.append(curstyleind+" geost:pointImage \""+this.pointImage+"\"^^geost:svgLiteral . "+System.lineSeparator());
		}
		if(lineStringStyle!=null) {
			ttl.append(curstyleind+" geost:lineStringStyle \""+this.lineStringStyle+"\"^^geost:cssLiteral . "+System.lineSeparator());
		}
		if(lineStringImage!=null) {
			ttl.append(curstyleind+" geost:lineStringImage \""+this.lineStringImage+"\"^^geost:svgLiteral . "+System.lineSeparator());
		}
		if(lineStringImageStyle!=null) {
			ttl.append(curstyleind+" geost:lineStringImageStyle \""+this.lineStringImageStyle+"\"^^geost:cssLiteral . "+System.lineSeparator());
		}
		if(polygonStyle!=null) {
			ttl.append(curstyleind+" geost:polygonStyle \""+this.polygonStyle+"\"^^geost:cssLiteral . "+System.lineSeparator());
		}
		if(polygonImage!=null) {
			ttl.append(curstyleind+" geost:polygonImage \""+this.polygonImage+"\"^^geost:svgLiteral . "+System.lineSeparator());
		}
		if(hatch!=null) {
			ttl.append(curstyleind+" geost:hatch \""+this.hatch+"\"^^geost:cssLiteral . "+System.lineSeparator());
		}
		if(popupStyle!=null) {
			ttl.append(curstyleind+" geost:popupStyle \""+this.popupStyle+"\"^^geost:cssLiteral . "+System.lineSeparator());
		}
		if(propertyNamespace!=null && !propertyNamespace.isEmpty()) {
			ttl.append(this.conditionsToSHACL(target,"<"+propertyNamespace+curstyleid+">"));			
		}else {
			ttl.append(this.conditionsToSHACL(target,"geo:"+curstyleid));			
		}
		return ttl.toString().replace("\"\"", "\"");
	}
	
	public String conditionsToSHACL(String curind,String targetStyle) {
		if(conditions.isEmpty())
			return "";
		StringBuilder builder=new StringBuilder();
		builder.append(curind+" sh:rule ["+System.lineSeparator());
		builder.append("a sh:TripleRule ;"+System.lineSeparator());
		builder.append("sh:subject sh:this ;"+System.lineSeparator());
		builder.append("sh:predicate geo:style ;"+System.lineSeparator());
		builder.append("sh:object "+targetStyle+" ; "+System.lineSeparator());
		for(Condition cond:conditions) {
			builder.append(cond.toSHACL());
		}
		builder.append("]."+System.lineSeparator());
		return builder.toString();
	}
	
	public String mapToCSS(Map<String,String> map){
		StringBuilder result=new StringBuilder();
		result.append("\"");
		for(String key:map.keySet()) {
			result.append(key+":"+map.get(key)+";");
		}
		result.append("\"");
		return result.toString();
	}
	
	/**
	 * Exports the StyleObject to HTML.
	 * @return A HTML table containing the StyleObject information
	 */
	public String toHTML() {
		StringBuilder result=new StringBuilder();
		result.append("<table border=1><tr><th>Styleaspect</th><th>CSS</th></tr>");
		result.append("<tr><td>pointStyle</td><td>"+(pointStyle!=null?pointStyle.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>pointImage</td><td>"+(pointImage!=null?pointImage.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>lineStringStyle</td><td>"+(lineStringStyle!=null?lineStringStyle.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>lineStringImage</td><td>"+(lineStringImage!=null?lineStringImage.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>lineStringImageStyle</td><td>"+(lineStringImageStyle!=null?lineStringImageStyle.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>polygonStyle</td><td>"+(polygonStyle!=null?polygonStyle.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>polygonImage</td><td>"+(polygonImage!=null?polygonImage.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>popupStyle</td><td>"+(popupStyle!=null?popupStyle.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>hatch</td><td>"+(hatch!=null?hatch.replace("\"","").replace("\\",""):"")+"</td></tr>");
		result.append("<tr><td>styleDescription</td><td>"+styleDescription+"</td></tr>");
		result.append("<tr><td>styleName</td><td>"+styleName+"</td></tr></table>");
		return result.toString();
	}
	
	/**
	 * Exports the StyleObject to XML.
	 * @return A String representation of the XML serialization
	 */
	public String toXML() {
		StringWriter strwriter = new StringWriter();
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		XMLStreamWriter writer;
			try {
				writer = new IndentingXMLStreamWriter(output.createXMLStreamWriter(strwriter));
				writer.writeStartDocument();
				writer.writeStartElement("style");
				writer.writeStartElement(styleName);
				writer.writeStartElement("pointStyle");
				writer.writeCharacters(pointStyle);
				writer.writeEndElement();
				writer.writeStartElement("pointImage");
				writer.writeCharacters(pointImage);
				writer.writeEndElement();
				writer.writeStartElement("lineStringStyle");
				writer.writeCharacters(lineStringStyle);
				writer.writeEndElement();
				writer.writeStartElement("lineStringImage");
				writer.writeCharacters(lineStringImage);
				writer.writeEndElement();
				writer.writeStartElement("lineStringImageStyle");
				writer.writeCharacters(lineStringImageStyle);
				writer.writeEndElement();
				writer.writeStartElement("polygonStyle");
				writer.writeCharacters(polygonStyle);
				writer.writeEndElement();
				writer.writeStartElement("popupStyle");
				writer.writeCharacters(popupStyle);
				writer.writeEndElement();
				writer.writeStartElement("polygonImage");
				writer.writeCharacters(polygonImage);
				writer.writeEndElement();
				writer.writeStartElement("hatch");
				writer.writeCharacters(hatch);
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndDocument();
				writer.flush();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return strwriter.toString();
	}
	
	
	
	
}
