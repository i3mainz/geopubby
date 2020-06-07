package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class KMLWriter implements ModelWriter {

	WKTReader reader=new WKTReader();
	
	@Override
	public void write(Model model, HttpServletResponse response) throws IOException {
		Property usedProperty;
		ExtendedIterator<Resource> it=model.
				listResourcesWithProperty(GEO.HASGEOMETRY);			
		usedProperty=GEO.HASGEOMETRY;
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.P_LAT);
			usedProperty=GEO.P_LAT;
		}
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.HASGEOMETRY);
			usedProperty=GEO.HASGEOMETRY;
		}
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.GEORSSPOINT);
			usedProperty=GEO.GEORSSPOINT;
		}
		if(!it.hasNext()) {
			usedProperty=null;
			it.close();
			it=model.listResourcesWithProperty(GEO.P625);
			usedProperty=GEO.P625;
		}
		if(!it.hasNext()) {
			response.getWriter().write("");
			response.getWriter().close();	
		}else {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		StringWriter strwriter=new StringWriter();
		try {
		XMLStreamWriter writer=new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
		writer.writeStartDocument();
		writer.writeStartElement("Document");
		writer.writeDefaultNamespace("http://www.opengis.net/kml/2.2");
		writer.writeStartElement("Placemark");
		Double lat=null,lon=null;
		while(it.hasNext()) {
			Resource ind=it.next();
			StmtIterator it2 = ind.listProperties();
			while(it2.hasNext()) {
				Statement curst=it2.next();
				if(GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString()) ||
						GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
						|| 
						GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
					try {
						Geometry geom=reader.read(curst.getObject().asLiteral().getString());								lat=geom.getCentroid().getCoordinate().getY();
						lon=geom.getCentroid().getCoordinate().getX();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if(GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())){
					lat=curst.getObject().asLiteral().getDouble();
				}else if(GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())){
					lon=curst.getObject().asLiteral().getDouble();
				}else if(GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())){
					lat=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
					lon=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
				}
			}
		}
		it.close();
		if(lat!=null && lon!=null) {
			writer.writeStartElement("Point");
			writer.writeStartElement("coordinates");
			writer.writeCharacters(lat+" "+lon);
			writer.writeEndElement();
			writer.writeEndElement();
		}
		it=model.listResourcesWithProperty(usedProperty);
		writer.writeStartElement("ExtendedData");
		while(it.hasNext()) {
			Resource ind=it.next();
			StmtIterator it2 = ind.listProperties();
			while(it2.hasNext()) {
				Statement curst=it2.next();
				writer.writeStartElement("Data");
				writer.writeAttribute("name",curst.getPredicate().getURI().toString());
				writer.writeStartElement("displayName");
				writer.writeCharacters(curst.getPredicate().getURI().toString().substring(curst.getPredicate().getURI().toString().lastIndexOf('/')+1));
				writer.writeEndElement();
				writer.writeStartElement("value");
				if(curst.getObject().toString().contains("^^")) {
					writer.writeCharacters(curst.getObject().toString().substring(0,curst.getObject().toString().lastIndexOf("^^")));
				}else {
					writer.writeCharacters(curst.getObject().toString());
				}
				writer.writeEndElement();
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		} catch (XMLStreamException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			response.getWriter().write(strwriter.toString());
			response.getWriter().close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

	
	
}
