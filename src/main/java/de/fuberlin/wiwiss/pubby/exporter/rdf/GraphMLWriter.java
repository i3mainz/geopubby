package de.fuberlin.wiwiss.pubby.exporter.rdf;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class GraphMLWriter extends GeoModelWriter {
	
	public GraphMLWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
        Set<Resource> resources = new HashSet<>();
        model.listStatements().toList()
                .forEach( statement -> {
                    resources.add(statement.getSubject());
                    RDFNode object = statement.getObject();
                    if (object.isResource()) {
                        resources.add(object.asResource());
                    }
        });
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(response.getWriter()));
			writer.writeStartDocument();
	        writer.writeStartElement("graphml");
	        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
	        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	        writer.writeAttribute("xmlns:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
	        writer.writeStartElement("graph");
	        writer.writeAttribute("id", "G");
	        writer.writeAttribute("edgedefault", "undirected");    
			for(Resource res:resources) {
				writer.writeStartElement("node");
				writer.writeAttribute("id",res.getURI());
				writer.writeAttribute("name",res.getLocalName());
				writer.writeAttribute("uri", res.getURI());
				writer.writeEndElement();
	        	StmtIterator propiter = res.listProperties();
	        	while(propiter.hasNext()) {
	        		Statement curst=propiter.next();
	        		if(curst.getObject().isURIResource()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id",curst.getObject().asResource().getURI());
						writer.writeAttribute("name",res.getLocalName());
						writer.writeAttribute("uri", res.getURI());
						writer.writeEndElement();
						writer.writeStartElement("edge");
						writer.writeAttribute("id",curst.getPredicate().getURI());
						writer.writeAttribute("uri",curst.getPredicate().getURI());
						writer.writeAttribute("name",curst.getPredicate().getLocalName());
						writer.writeEndElement();
	        		}else if(curst.getObject().isLiteral()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id",curst.getObject().asLiteral().getValue().toString());
						writer.writeAttribute("name",res.getLocalName());
						writer.writeAttribute("uri", res.getURI());
						writer.writeEndElement();
						writer.writeStartElement("edge");
						writer.writeAttribute("id",curst.getPredicate().getURI());
						writer.writeAttribute("uri",curst.getPredicate().getURI());
						writer.writeAttribute("name",curst.getPredicate().getLocalName());
						writer.writeEndElement();
	        		}
	        	}
	        }
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
		}catch(Exception e) {
			
		}
		response.getWriter().close();
		return null;
	}

}
