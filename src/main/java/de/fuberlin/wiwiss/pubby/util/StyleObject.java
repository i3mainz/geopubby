package de.fuberlin.wiwiss.pubby.util;

import java.io.StringWriter;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
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
	 * An identifier of a given style.
	 */
	public String styleName;
	
	/**
	 * A description of a given style.
	 */
	public String styleDescription;
	
	/**
	 * A style definition for the map popup.
	 */
	public String popupStyle;
	
	public List<List<Condition>> conditions;

	@Override
	public String toString() {
		return "StyleObject [pointStyle=" + pointStyle + ", pointImage=" + pointImage + ", lineStringStyle="
				+ lineStringStyle + ", lineStringImage=" + lineStringImage + ", polygonStyle=" + polygonStyle
				+", popupStyle=" + popupStyle +", polygonImage=" + polygonImage + ", hatch=" + hatch + "]";
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
	
	public String toRDF() {
		Set<String> ttl=new HashSet<String>();
		ttl.add("geo:"+this.styleName+" rdf:type geo:Style . ");
		if(pointStyle!=null) {
			ttl.add("geo:pointStyle geo:pointStyle \""+this.pointStyle+"\"^^geo:cssLiteral . ");
		}
		if(pointImage!=null) {
			ttl.add("geo:"+this.styleName+" geo:pointImage \""+this.pointImage+"\"^^geo:svgLiteral . ");
		}
		if(lineStringStyle!=null) {
			ttl.add("geo:"+this.styleName+" geo:lineStringStyle \""+this.lineStringStyle+"\"^^geo:svgLiteral . ");
		}
		if(lineStringImage!=null) {
			ttl.add("geo:"+this.styleName+" geo:lineStringImage \""+this.lineStringImage+"\"^^geo:svgLiteral . ");
		}
		if(lineStringImageStyle!=null) {
			ttl.add("geo:"+this.styleName+" geo:lineStringImageStyle \""+this.lineStringImageStyle+"\"^^geo:cssLiteral . ");
		}
		if(polygonStyle!=null) {
			ttl.add("geo:"+this.styleName+" geo:polygonStyle \""+this.polygonStyle+"\"^^geo:cssLiteral . ");
		}
		if(polygonImage!=null) {
			ttl.add("geo:"+this.styleName+" geo:polygonImage \""+this.polygonImage+"\"^^geo:svgLiteral . ");
		}
		if(hatch!=null) {
			ttl.add("geo:"+this.styleName+" geo:hatch \""+this.hatch+"\"^^geo:svgLiteral . ");
		}
		if(popupStyle!=null) {
			ttl.add("geo:"+this.popupStyle+" geo:hatch \""+this.popupStyle+"\"^^geo:cssLiteral . ");
		}
		return ttl.toString();
	}
	
	public String conditionsToSHACL() {
		return "";
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
