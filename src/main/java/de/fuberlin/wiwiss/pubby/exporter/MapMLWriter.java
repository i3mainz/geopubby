package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class MapMLWriter extends GeoModelWriter {

	GeometryFactory fac = new GeometryFactory();
	
	public MapMLWriter(String epsg) {
		super(epsg);
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it = super.write(model, response);
		if (!it.hasNext()) {
			response.getWriter().write("");
			response.getWriter().close();
		} else {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			StringWriter strwriter = new StringWriter();
			try {
				XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
				writer.writeStartDocument();
				writer.writeStartElement("mapml");
				writer.writeStartElement("head");
				writer.writeStartElement("title");
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeStartElement("body");
				it.close();
				it = model.listResourcesWithProperty(usedProperty);				
				Integer countgeoms = 0;
				Resource ind = it.next();
				if(ind.hasProperty(GEO.EPSG)) {
					sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}
				writer.writeStartElement("feature");
				writer.writeAttribute("id", ind.getURI());
				writer.writeAttribute("itemscope", "itemscope");
				writer.writeStartElement("properties");
				writer.writeStartElement("div");
				writer.writeAttribute("class", "table-container");
				writer.writeStartElement("table");
				writer.writeStartElement("caption");
				writer.writeCharacters("Feature Properties");
				writer.writeStartElement("tbody");
				StmtIterator it2 = ind.listProperties();
				Double lat = null, lon = null;
				List<Geometry> geoms = new LinkedList<Geometry>();
				while (it2.hasNext()) {
					Statement curst = it2.next();
					if (GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString())
							|| GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
							|| GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
						try {
							Geometry geom = reader.read(curst.getObject().asLiteral().getString());
							if(this.epsg!=null) {
								geom=ReprojectionUtils.reproject(geom, sourceCRS, epsg);
							}
							geoms.add(geom);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())) {
						lat = curst.getObject().asLiteral().getDouble();
					} else if (GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())) {
						lon = curst.getObject().asLiteral().getDouble();
					} else if (GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())) {
						lat = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
						lon = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
					} else {
						String last = curst.getPredicate().toString()
								.substring(curst.getPredicate().toString().lastIndexOf('/') + 1);
						writer.writeStartElement("th");
						writer.writeAttribute("scope", "row");
						writer.writeCharacters(last);
						writer.writeEndElement();
						if (curst.getObject().toString().contains("^^")) {
							writer.writeStartElement("td");
							writer.writeAttribute("itemprop", last);
							writer.writeCharacters(curst.getObject().toString().substring(0,
									curst.getObject().toString().lastIndexOf("^^")));
							writer.writeEndElement();
						} else {
							writer.writeStartElement("td");
							writer.writeAttribute("itemprop", last);
							writer.writeCharacters(curst.getObject().toString());
							writer.writeEndElement();
						}
					}
					if (lon != null && lat != null) {
						Geometry point = fac.createPoint(
								new Coordinate(Double.valueOf(lon.toString()), Double.valueOf(lat.toString())));
						if(this.epsg!=null) {
							point=ReprojectionUtils.reproject(point, sourceCRS, epsg);
						}
						geoms.add(point);
						lat = null;
						lon = null;
						countgeoms++;
					}
				}
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeStartElement("geometry");
				for (Geometry geom : geoms) {
					writer.writeStartElement(geom.getGeometryType());
					writer.writeStartElement("coordinates");
					for (Coordinate coord : geom.getCoordinates()) {
						writer.writeCharacters(coord.getX() + " " + coord.getY() + " ");
					}
					writer.writeEndElement();
					writer.writeEndElement();
				}
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndDocument();
				writer.flush();
			} catch (XMLStreamException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			response.getWriter().write(strwriter.toString());
			response.getWriter().close();
		}

		return null;
	}

}
