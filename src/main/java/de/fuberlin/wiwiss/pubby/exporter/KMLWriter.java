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
import org.json.JSONException;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class KMLWriter implements ModelWriter {

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
		writer.writeStartElement("Document");
		writer.writeDefaultNamespace("http://www.opengis.net/kml/2.2");
		writer.writeStartElement("Placemark");
		writer.writeStartElement("ExtendedData");
		while(it.hasNext()) {
			Resource ind=it.next();
			StmtIterator it2 = ind.listProperties();
			while(it2.hasNext()) {
				Statement curst=it2.next();
				writer.writeStartElement("Data");
				writer.writeAttribute("name",curst.getPredicate().getURI().toString());
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
