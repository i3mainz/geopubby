package de.fuberlin.wiwiss.pubby.exporter.coverage;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;
import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class X3DWriter extends AbstractGeoJSONWriter {

	public X3DWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=super.write(model, response);
		while (it.hasNext()) {
			Resource ind = it.next();
			if(ind.hasProperty(GEO.EPSG)) {
				sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
			}
			StmtIterator it2 = ind.listProperties();
			while (it2.hasNext()) {
				Statement curst = it2.next();
				this.handleGeometry(curst, ind, model);
			}
		}
		try {
			if(geom!=null) {
				lat=geom.getCentroid().getCoordinate().getY();
				lon=geom.getCentroid().getCoordinate().getX();
			}
			if (lat != null && lon != null) {
				try {
					geom = reader.read("Point("+lon+" "+lat+")");
					if(this.epsg!=null) {
						geom=ReprojectionUtils.reproject(geom, sourceCRS, epsg);
					}
					XMLOutputFactory factory = XMLOutputFactory.newInstance();
					StringWriter strwriter = new StringWriter();
						XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
						writer.writeStartDocument();
						writer.writeStartElement("X3D");
						writer.writeAttribute("profile", "Immersive");
						writer.writeAttribute("vesrion", "3.3");
						writer.writeStartElement("head");
						writer.writeStartElement("component");
						writer.writeAttribute("level", "1");
						writer.writeAttribute("name", "Geospatial");
						writer.writeEndElement();
						writer.writeStartElement("meta");
						writer.writeAttribute("name", "generator");
						writer.writeAttribute("content", "GeoPubby");
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeStartElement("Scene");
						writer.writeStartElement("WorldInfo");
						writer.writeAttribute("title", "");
						writer.writeEndElement();
						writer.writeStartElement("Group");
						writer.writeAttribute("bboxCenter", "0 0 0");
						writer.writeAttribute("bboxSize", "-1 -1 -1");
						writer.writeEndElement();
						writer.writeStartElement("Background");
						writer.writeAttribute("bboxCenter", "0 0 0");
						writer.writeAttribute("skyColor", "1 1 1");
						writer.writeAttribute("transparency", "0");
						writer.writeEndElement();
						writer.writeStartElement("GeoViewpoint");
						writer.writeAttribute("description", "Initial Geoviewpoint");
						writer.writeAttribute("orientation", "1 0 0 -1.57");
						writer.writeAttribute("position", "51.5122 -40.0 10000000");
						writer.writeAttribute("containerField", "children");
						writer.writeEndElement();
						writer.writeStartElement("Shape");
						writer.writeAttribute("bboxCenter", "0 0 0");
						writer.writeAttribute("bboxSize", "-1 -1 -1");
						writer.writeStartElement("Appearance");
						writer.writeStartElement("Material");
						writer.writeAttribute("diffuseColor", "0.8 1.0 0.3");
						writer.writeEndElement();
						writer.writeStartElement("GeoElevationGrid");
						writer.writeAttribute("creaseAngle", "1.05");
						writer.writeAttribute("geoGridOrigin", "-90 -180 0");
						writer.writeAttribute("xDimension", "11");
						writer.writeAttribute("xSpacing", "36");
						writer.writeAttribute("zDimension", "11");
						writer.writeAttribute("zSpacing", "18");
						writer.writeAttribute("height", "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeStartElement("GeoLocation");
						writer.writeAttribute("geoCoords", lon+" "+lat+" 200000");
						writer.writeAttribute("bboxCenter", "0 0 0");
						writer.writeAttribute("bboxSize", "-1 -1 -1");
						writer.writeAttribute("containerField", "children");
						writer.writeStartElement("Transform");
						writer.writeAttribute("rotation","1 0 0 3.1415926");
						writer.writeAttribute("bboxCenter", "0 0 0");
						writer.writeAttribute("bboxSize", "-1 -1 -1");
						writer.writeStartElement("Shape");
						writer.writeAttribute("bboxCenter", "0 0 0");
						writer.writeAttribute("bboxSize", "-1 -1 -1");
						writer.writeStartElement("Appearance");
						writer.writeStartElement("Material");
						writer.writeAttribute("diffuseColor", "1 1 0");
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeStartElement("Cone");
						writer.writeAttribute("bottomRadius","100000");
						writer.writeAttribute("height", "500000");
						writer.writeAttribute("side", "true");
						writer.writeAttribute("bottom", "true");
						writer.writeAttribute("solid", "true");
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						response.getWriter().write(strwriter.toString());
					}catch(Exception e) {
						e.printStackTrace();
						response.getWriter().write("");

					}
				
				response.getWriter().close();
			}else{
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
