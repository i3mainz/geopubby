package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

public class GMLWriter implements ModelWriter {

		@Override
		public void write(Model model, HttpServletResponse response) throws IOException {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			StringWriter strwriter=new StringWriter();
			try {
			XMLStreamWriter writer=new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
			writer.writeStartDocument();
			writer.writeStartElement("Document");

				writer.writeDefaultNamespace("http://www.opengis.net/kml/2.2");

			writer.writeStartElement("Placemark");
			writer.writeStartElement("ExtendedData");
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
}
