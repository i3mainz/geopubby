package de.fuberlin.wiwiss.pubby.exporter.coverage;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.wololo.jts2geojson.GeoJSONWriter;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;
import de.fuberlin.wiwiss.pubby.util.Tuple;

public class CoverageJSONWriter extends GeoModelWriter {

	public CoverageJSONWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it = super.write(model, response);
		JSONObject covjsonresult=new JSONObject();
		JSONObject properties=new JSONObject();
		while (it.hasNext()) {
			Resource ind = it.next();
			StmtIterator it2 = ind.listProperties();
			while (it2.hasNext()) {
				Statement curst = it2.next();
				Tuple<Boolean,String> handled=this.handleGeometry(curst, ind, model);
				if(!handled.getOne()) {
					if(properties.has(curst.getPredicate().toString())) {
						if(properties.optJSONArray(curst.getPredicate().toString())!=null) {
							if(curst.getObject().toString().contains("^^")) {
								properties.getJSONArray(curst.getPredicate().toString()).put(curst.getObject().toString().substring(0,curst.getObject().toString().lastIndexOf("^^")));
							}else {
								properties.getJSONArray(curst.getPredicate().toString()).put(curst.getObject().toString());						
							}
						}else {
							JSONArray arr=new JSONArray();
							if(curst.getObject().toString().contains("^^")) {
								arr.put(curst.getObject().toString().substring(0,curst.getObject().toString().lastIndexOf("^^")));
							}else {
								arr.put(curst.getObject().toString());					
							}
							properties.put(curst.getPredicate().toString(),arr);
						}
					}else {
						if(curst.getObject().toString().contains("^^")) {
							   properties.put(curst.getPredicate().toString(),curst.getObject().toString().substring(0,curst.getObject().toString().lastIndexOf("^^")));
						}else {
							   properties.put(curst.getPredicate().toString(),curst.getObject().toString());							
						}
					}					
				}
			}
		}
		try {
			if (geom!=null && geom.getGeometryType().equalsIgnoreCase("Point") || (lat != null && lon != null)) {
				covjsonresult.put("type","Coverage");
				JSONObject domain=new JSONObject();
				covjsonresult.put("domain", domain);
				domain.put("type", "Domain");
				domain.put("domainType","Point");
				JSONObject axes=new JSONObject();
				domain.put("axes", axes);
				axes.put("x", new JSONObject());
				axes.put("y", new JSONObject());
				axes.getJSONObject("x").put("values", new JSONArray());
				axes.getJSONObject("y").put("values", new JSONArray());
				if (lat != null || lon != null) {
					axes.getJSONObject("x").getJSONArray("values").put(lon);
					axes.getJSONObject("y").getJSONArray("values").put(lat);
				}else {
					axes.getJSONObject("x").getJSONArray("values").put(geom.getCoordinate().getX());
					axes.getJSONObject("y").getJSONArray("values").put(geom.getCoordinate().getY());
				}
				JSONArray referencing=new JSONArray();
				domain.put("referencing", referencing);
				JSONObject ref=new JSONObject();
				referencing.put(ref);
				ref.put("coordinates", new JSONArray());
				ref.getJSONArray("coordinates").put("x");
				ref.getJSONArray("coordinates").put("y");
				ref.put("system", new JSONObject());
				ref.getJSONObject("system").put("type","GeographicCRS");
				if(sourceCRS.contains(":")) {
					ref.getJSONObject("system").put("id","http://www.opengis.net/def/crs/EPSG/0/"+sourceCRS.substring(sourceCRS.lastIndexOf(':')+1));					
				}else {
					ref.getJSONObject("system").put("id","http://www.opengis.net/def/crs/EPSG/0/"+sourceCRS);
				}
				JSONObject parameters=new JSONObject();
				covjsonresult.put("parameters", parameters);
				for(String key:properties.keySet()) {
					JSONObject param=new JSONObject();
					parameters.put(key, param);
					param.put("type", "Parameter");
					param.put("observedProperty", new JSONObject());
					param.getJSONObject("observedProperty").put("id",key);
					param.getJSONObject("observedProperty").put("label",new JSONObject());					
					param.getJSONObject("observedProperty").getJSONObject("label").put("label", key);
				}
				JSONObject ranges=new JSONObject();
				covjsonresult.put("ranges", ranges);
				for(String key:properties.keySet()) {
					JSONObject range=new JSONObject();
					ranges.put(key, range);
					range.put("type", "NdArray");
					range.put("values", new JSONArray());
					try {
						Number valuenum=properties.getInt(key);
						range.put("dataType", "integer");
						range.getJSONArray("values").put(valuenum);
					}catch(Exception e) {
						try {
							Double valuebool=properties.getDouble(key);
							range.put("dataType", "float");
							range.getJSONArray("values").put(valuebool);
						}catch(Exception ex) {
							range.put("dataType", "string");
							range.getJSONArray("values").put(properties.get(key).toString());
						}
					}
				}
				response.getWriter().write(covjsonresult.toString(2));
				response.getWriter().close();
			}else if (geom != null) {
				if(geom.getGeometryType().equalsIgnoreCase("LineString")) {
					covjsonresult.put("type","Coverage");
					JSONObject domain=new JSONObject();
					covjsonresult.put("domain", domain);
					domain.put("type", "Domain");
					domain.put("domainType","Trajectory");
					JSONObject axes=new JSONObject();
					domain.put("axes", axes);
					JSONObject composite=new JSONObject();
					axes.put("composite", composite);
					composite.put("dataType", "tuple");
					composite.put("coordinates",new JSONArray());
					composite.getJSONArray("coordinates").put("x");
					composite.getJSONArray("coordinates").put("y");
					composite.put("values", new JSONArray());
					for (Coordinate coord : geom.getCoordinates()) {
						JSONArray cor=new JSONArray();
						cor.put(coord.getX());
						cor.put(coord.getY());
						composite.getJSONArray("values").put(cor);
					}
					JSONArray referencing=new JSONArray();
					domain.put("referencing", referencing);
					JSONObject ref=new JSONObject();
					referencing.put(ref);
					ref.put("coordinates", new JSONArray());
					ref.getJSONArray("coordinates").put("x");
					ref.getJSONArray("coordinates").put("y");
					ref.put("system", new JSONObject());
					ref.getJSONObject("system").put("type","GeographicCRS");
					if(sourceCRS.contains(":")) {
						ref.getJSONObject("system").put("id","http://www.opengis.net/def/crs/EPSG/0/"+sourceCRS.substring(sourceCRS.lastIndexOf(':')+1));					
					}else {
						ref.getJSONObject("system").put("id","http://www.opengis.net/def/crs/EPSG/0/"+sourceCRS);
					}
					JSONObject parameters=new JSONObject();
					covjsonresult.put("parameters", parameters);
					for(String key:properties.keySet()) {
						JSONObject param=new JSONObject();
						parameters.put(key, param);
						param.put("type", "Parameter");
						param.put("observedProperty", new JSONObject());
						param.getJSONObject("observedProperty").put("id",key);
						param.getJSONObject("observedProperty").put("label",new JSONObject());					
						param.getJSONObject("observedProperty").getJSONObject("label").put("label", key);
					}
					JSONObject ranges=new JSONObject();
					covjsonresult.put("ranges", ranges);
					for(String key:properties.keySet()) {
						JSONObject range=new JSONObject();
						ranges.put(key, range);
						range.put("type", "NdArray");
						range.put("values", new JSONArray());
						try {
							Number valuenum=properties.getInt(key);
							range.put("dataType", "integer");
							range.getJSONArray("values").put(valuenum);
						}catch(Exception e) {
							try {
								Double valuebool=properties.getDouble(key);
								range.put("dataType", "float");
								range.getJSONArray("values").put(valuebool);
							}catch(Exception ex) {
								range.put("dataType", "string");
								range.getJSONArray("values").put(properties.get(key).toString());
							}
						}
					}
					response.getWriter().write(covjsonresult.toString(2));
				}else if(geom.getGeometryType().equalsIgnoreCase("Polygon")) {
					covjsonresult.put("type","Coverage");
					JSONObject domain=new JSONObject();
					covjsonresult.put("domain", domain);
					domain.put("type", "Domain");
					domain.put("domainType","MultiPolygon");
					JSONObject axes=new JSONObject();
					domain.put("axes", axes);
					JSONObject composite=new JSONObject();
					axes.put("composite", composite);
					composite.put("dataType", "tuple");
					composite.put("coordinates",new JSONArray());
					composite.getJSONArray("coordinates").put("x");
					composite.getJSONArray("coordinates").put("y");
					composite.put("values", new JSONArray());
					GeoJSONWriter writer=new GeoJSONWriter();
					JSONObject obj=new JSONObject(writer.write(geom).toString());
					composite.getJSONArray("values").put(obj.getJSONArray("coordinates"));
					JSONArray referencing=new JSONArray();
					domain.put("referencing", referencing);
					JSONObject ref=new JSONObject();
					referencing.put(ref);
					ref.put("coordinates", new JSONArray());
					ref.getJSONArray("coordinates").put("x");
					ref.getJSONArray("coordinates").put("y");
					ref.put("system", new JSONObject());
					ref.getJSONObject("system").put("type","GeographicCRS");
					if(sourceCRS.contains(":")) {
						ref.getJSONObject("system").put("id","http://www.opengis.net/def/crs/EPSG/0/"+sourceCRS.substring(sourceCRS.lastIndexOf(':')+1));					
					}else {
						ref.getJSONObject("system").put("id","http://www.opengis.net/def/crs/EPSG/0/"+sourceCRS);
					}
					JSONObject parameters=new JSONObject();
					covjsonresult.put("parameters", parameters);
					for(String key:properties.keySet()) {
						JSONObject param=new JSONObject();
						parameters.put(key, param);
						param.put("type", "Parameter");
						param.put("observedProperty", new JSONObject());
						param.getJSONObject("observedProperty").put("id",key);
						param.getJSONObject("observedProperty").put("label",new JSONObject());					
						param.getJSONObject("observedProperty").getJSONObject("label").put("label", key);
					}
					JSONObject ranges=new JSONObject();
					covjsonresult.put("ranges", ranges);
					for(String key:properties.keySet()) {
						JSONObject range=new JSONObject();
						ranges.put(key, range);
						range.put("type", "NdArray");
						range.put("values", new JSONArray());
						try {
							Number valuenum=properties.getInt(key);
							range.put("dataType", "integer");
							range.getJSONArray("values").put(valuenum);
						}catch(Exception e) {
							try {
								Double valuebool=properties.getDouble(key);
								range.put("dataType", "float");
								range.getJSONArray("values").put(valuebool);
							}catch(Exception ex) {
								range.put("dataType", "string");
								range.getJSONArray("values").put(properties.get(key).toString());
							}
						}
					}
					response.getWriter().write(covjsonresult.toString(2));
				}
				response.getWriter().close();
			} else {
				response.getWriter().write("");
				response.getWriter().close();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
