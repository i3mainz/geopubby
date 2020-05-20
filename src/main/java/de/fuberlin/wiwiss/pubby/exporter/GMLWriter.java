package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.io.StringWriter;

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
import org.locationtech.jts.io.WKTReader;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class GMLWriter implements ModelWriter {

	WKTReader reader=new WKTReader();
	
		@Override
		public void write(Model model, HttpServletResponse response) throws IOException {
			ExtendedIterator<Resource> it=model.
					listResourcesWithProperty(GEO.HASGEOMETRY);
					if(!it.hasNext()) {
						it.close();
						it=model.listResourcesWithProperty(GEO.P_LAT);
					}
					if(!it.hasNext()) {
						it.close();
						it=model.listResourcesWithProperty(GEO.HASGEOMETRY);
					}
					if(!it.hasNext()) {
						it.close();
						it=model.listResourcesWithProperty(GEO.GEORSSPOINT);
					}
					if(!it.hasNext()) {
						it.close();
						it=model.listResourcesWithProperty(GEO.P625);
					}
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			StringWriter strwriter=new StringWriter();
			try {
			XMLStreamWriter writer=new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
			writer.writeStartDocument();
			writer.writeStartElement("Feature");
				while(it.hasNext()) {
					Resource ind=it.next();
					StmtIterator it2 = ind.listProperties();
					Double lat=null,lon=null;
					while(it2.hasNext()) {
						Statement curst=it2.next();
						if(GEO.HASGEOMETRY.getURI().equals(curst.getPredicate().getURI().toString()) || 
								GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
								|| 
								GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
							try {
								Geometry geom=reader.read(curst.getObject().asLiteral().getValue().toString());
								writer.writeStartElement(geom.getGeometryType());
								writer.writeStartElement("coordinates");
								writer.writeCharacters(lat+" "+lon);
								writer.writeEndElement();
								writer.writeEndElement();
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
						}else {
							writer.writeStartElement(curst.getPredicate().toString());
							writer.writeCharacters(curst.getObject().toString());
							writer.writeEndElement();			
						}
						if(lon!=null && lat!=null) {
							writer.writeStartElement("Point");
							writer.writeStartElement("coordinates");
							writer.writeCharacters(lat+" "+lon);
							writer.writeEndElement();
							writer.writeEndElement();
							lat=null;
							lon=null;
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
}
