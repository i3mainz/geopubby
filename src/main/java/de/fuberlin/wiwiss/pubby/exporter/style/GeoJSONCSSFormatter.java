package de.fuberlin.wiwiss.pubby.exporter.style;

import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.apache.jena.query.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

import de.fuberlin.wiwiss.pubby.util.StyleObject;

/**
 * Reads an ontological style description and formats it to GeoJSONCSS.
 *
 */
public class GeoJSONCSSFormatter extends ResultStyleFormatter {

	Map<String,String> attributeMap;
	
	/**
	 * Constructor for this class.
	 */
	public GeoJSONCSSFormatter() {
		this.styleAttribute="style";
		this.attributeMap=new TreeMap<>();
		this.attributeMap.put("fill", "fillColor");
		this.attributeMap.put("stroke", "color");
		this.attributeMap.put("stroke-width", "weight");	
		this.attributeMap.put("stroke-linejoin", "lineJoin");	
	}
	
	@Override
	public String formatter(ResultSet results,String featuretype) throws XMLStreamException {
		return null;
	}
	
	
	
	/**
	 * Converts a CSS literal given in the ontology to a JSON representation.
	 * @param cssString the literal value
	 * @return the JSON object to contain the style information
	 */
	public JSONObject cssLiteralToJSON(String cssString) {
		JSONObject styleproperties=new JSONObject();
		if(cssString==null)
			return styleproperties;
		if(cssString.contains("^^"))
			cssString=cssString.substring(0,cssString.indexOf("^^"));
		if(cssString.contains(";")) {
			for(String statement:cssString.split(";")) {
				String[] split=statement.split(":");
				String key=split[0].replace("\\","").replace("\"","").replace("{","").replace("}","").trim();
				if(this.attributeMap.containsKey(key)) {
					key=this.attributeMap.get(key);
				}
				styleproperties.put(key,
						split[1].replace("\\","").replace("\"","").replace("{","").replace("}","").trim());
			}
		}else if(cssString.contains(",")) {
			for(String statement:cssString.split(",")) {
				String[] split=statement.split(":");
				String key=split[0].replace("\\","").replace("\"","").replace("{","").replace("}","").trim();
				if(this.attributeMap.containsKey(key)) {
					key=this.attributeMap.get(key);
				}
				styleproperties.put(key,
						split[1].replace("\\","").replace("\"","").replace("{","").replace("}","").trim());
			}
		}else {
			String[] split=cssString.split(":");
			String key=split[0].replace("\\","").replace("\"","").replace("{","").replace("}","").trim();
			if(this.attributeMap.containsKey(key)) {
				key=this.attributeMap.get(key);
			}
			styleproperties.put(key,
					split[1].replace("\\","").replace("\"","").replace("{","").replace("}","").trim());
		}
		return styleproperties;
	}
	
	public JSONObject formatForWebView(StyleObject obj) {
		JSONObject result=new JSONObject();
		result.put("name", obj.styleName);
		result.put("id", obj.styleId);
		result.put("Point", new JSONObject(formatGeometry("Point",obj)));
		result.put("LineString", new JSONObject(formatGeometry("LineString",obj)));
		result.put("Polygon", new JSONObject(formatGeometry("Polygon",obj)));
		return result;
	}

	@Override
	public String formatGeometry(String geometrytype,StyleObject styleobj) {
		if(styleobj==null)
			return "{}";
		if(geometrytype.contains("Point")) {
		    JSONObject props=cssLiteralToJSON(styleobj.pointStyle);
		    if(styleobj.pointImage!=null) {
		    	JSONObject iconobj=new JSONObject();
		    	if(styleobj.pointImage.contains("svg")) {
		    		iconobj.put("iconUrl", "url('data:image/svg+xml;utf8,"+styleobj.pointImage+"')");
		    	}else if(styleobj.pointImage.contains("http")) {
		    		iconobj.put("iconUrl", styleobj.pointImage);
		    	}else {
		    		iconobj.put("iconUrl", styleobj.pointImage);
		    	}
		    	JSONArray size=new JSONArray();
		    	size.put(32);
		    	size.put(32);
		    	JSONArray anchor=new JSONArray();
		    	size.put(16);
		    	size.put(16);
		    	iconobj.put("iconSize",size);
		    	iconobj.put("iconAnchor", anchor);
				props.put("icon", iconobj);
		    }
			System.out.println(props.toString());
		    return props.toString();
		}
		if(geometrytype.contains("LineString")) {
			JSONObject props=cssLiteralToJSON(styleobj.lineStringStyle);
			 if(styleobj.lineStringImage!=null) {
			    	JSONObject iconobj=new JSONObject();
			    	if(styleobj.lineStringImage.contains("svg")) {
			    		iconobj.put("iconUrl", "url('data:image/svg+xml;utf8,"+styleobj.lineStringImage+"')");
			    	}else if(styleobj.lineStringImage.contains("http")) {
			    		iconobj.put("iconUrl", styleobj.lineStringImage);
			    	}else {
			    		iconobj.put("iconUrl", styleobj.lineStringImage);
			    	}
			    	JSONArray size=new JSONArray();
			    	size.put(32);
			    	size.put(32);
			    	JSONArray anchor=new JSONArray();
			    	size.put(16);
			    	size.put(16);
			    	iconobj.put("iconSize",size);
			    	iconobj.put("iconAnchor", anchor);
					props.put("icon", iconobj);
			    }
			if(styleobj.hatch!=null) {
				JSONObject hatch=cssLiteralToJSON(styleobj.hatch);
				props.put("hatch",hatch);
			}
			System.out.println(props);
			return props.toString();
		}
		if(geometrytype.contains("Polygon")) {
			JSONObject props=cssLiteralToJSON(styleobj.polygonStyle);
			if(styleobj.polygonImage!=null) {
		    	JSONObject iconobj=new JSONObject();
		    	if(styleobj.polygonImage.contains("svg")) {
		    		iconobj.put("iconUrl", "url('data:image/svg+xml;utf8,"+styleobj.polygonImage+"')");
		    	}else if(styleobj.polygonImage.contains("http")) {
		    		iconobj.put("iconUrl", styleobj.polygonImage);
		    	}else {
		    		iconobj.put("iconUrl", styleobj.polygonImage);
		    	}
		    	JSONArray size=new JSONArray();
		    	size.put(32);
		    	size.put(32);
		    	JSONArray anchor=new JSONArray();
		    	size.put(16);
		    	size.put(16);
		    	iconobj.put("iconSize",size);
		    	iconobj.put("iconAnchor", anchor);
				props.put("icon", iconobj);
		    }
			if(styleobj.hatch!=null) {
				JSONObject hatch=cssLiteralToJSON(styleobj.hatch);
				props.put("hatch",hatch);
			}
			System.out.println(props);
			return props.toString();
		}
		return "{}";
	}

}
