package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

/**
 * Writes a GeoPubby instance as GPX.
 */
public class GPXWriter extends ModelWriter {

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
				writer.setPrefix("gml","http://www.opengis.net/gml");
				writer.setPrefix("gpx","http://www.topografix.com/GPX/1/1");
				writer.writeStartElement("http://www.topografix.com/GPX/1/1", "gpx");
				writer.writeDefaultNamespace("http://www.topografix.com/GPX/1/1");
				writer.writeNamespace("gml", "http://www.opengis.net/gml");
				Map<String, String> ns = new TreeMap<String, String>();
				while (it.hasNext()) {
					Resource ind = it.next();
					StmtIterator it2 = ind.listProperties();
					int nscounter = 1;
					while (it2.hasNext()) {
						Statement curst = it2.next();
						String nss = curst.getPredicate().getURI().toString().substring(0,
								curst.getPredicate().getURI().toString().lastIndexOf('/'));
						if (!ns.containsKey(nss)) {
							writer.setPrefix("ns" + nscounter, nss);
							writer.writeNamespace("ns" + nscounter, nss);
							ns.put(nss, "ns" + nscounter++);
						}
					}
				}
				it.close();
				it = model.listResourcesWithProperty(usedProperty);

				while (it.hasNext()) {
					Resource ind = it.next();
					StmtIterator it2 = ind.listProperties();
					Double lat = null, lon = null;
					while (it2.hasNext()) {
						Statement curst = it2.next();
						if (GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString())
								|| GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
								|| GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
							try {
								Geometry geom = reader.read(curst.getObject().asLiteral().getString());
								writer.writeStartElement("http://www.opengis.net/gml", geom.getGeometryType());
								writer.writeStartElement("http://www.opengis.net/gml", "posList");
								writer.writeCharacters(lat + " " + lon);
								writer.writeEndElement();
								writer.writeEndElement();
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
							String namespace = curst.getPredicate().toString().substring(0,
									curst.getPredicate().toString().lastIndexOf('/'));
							String last = curst.getPredicate().toString()
									.substring(curst.getPredicate().toString().lastIndexOf('/') + 1);
							if (ns.containsKey(namespace)) {
								writer.writeStartElement(namespace, last);
								if (curst.getObject().toString().contains("^^")) {
									writer.writeCharacters(curst.getObject().toString().substring(0,
											curst.getObject().toString().lastIndexOf("^^")));
								} else {
									writer.writeCharacters(curst.getObject().toString());
								}
								writer.writeEndElement();
							} else {
								writer.writeStartElement(curst.getPredicate().toString());
								if (curst.getObject().toString().contains("^^")) {
									writer.writeCharacters(curst.getObject().toString().substring(0,
											curst.getObject().toString().lastIndexOf("^^")));
								} else {
									writer.writeCharacters(curst.getObject().toString());
								}
								writer.writeEndElement();
							}
						}
						if (lon != null && lat != null) {
							writer.writeStartElement("wpt");
							writer.writeAttribute("lat", lat + "");
							writer.writeAttribute("lon", lon + "");
							// collectColumns(writer,
							// geojson.getJSONArray("features").getJSONObject(i).getJSONObject("properties"),"");
							writer.writeEndElement();
							lat = null;
							lon = null;
						}
					}
				}
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
