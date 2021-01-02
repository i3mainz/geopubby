package de.fuberlin.wiwiss.pubby.exporter.rdf;

import java.io.IOException;
import java.io.StringWriter;
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
        Integer literalcounter=0;
        StringWriter strwriter=new StringWriter();
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
			writer.writeStartDocument();
	        writer.writeStartElement("graphml");
	        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
	        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	        writer.writeAttribute("xmlns:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
	        writer.writeStartElement("graph");
	        writer.writeAttribute("id", "G");
	        writer.writeAttribute("edgedefault", "undirected");    
			for(Resource res:resources) {
				if(!res.isURIResource())
					continue;
				writer.writeStartElement("node");	
				writer.writeAttribute("id",res.getURI());
				writer.writeAttribute("value",res.getLocalName());
				writer.writeAttribute("uri", res.getURI());
				writer.writeEndElement();
	        	StmtIterator propiter = res.listProperties();
	        	while(propiter.hasNext()) {
	        		Statement curst=propiter.next();
	        		if(curst.getObject().isURIResource()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id",curst.getObject().asResource().getURI());
						writer.writeAttribute("value",curst.getObject().asResource().getLocalName());
						writer.writeAttribute("uri", curst.getObject().asResource().getURI());
						writer.writeEndElement();
						writer.writeStartElement("edge");
						writer.writeAttribute("id",curst.getPredicate().getURI());
						writer.writeAttribute("uri",curst.getPredicate().getURI());
						writer.writeAttribute("value",curst.getPredicate().getLocalName());
						writer.writeAttribute("source", curst.getSubject().getURI());
						writer.writeAttribute("target", curst.getObject().asResource().getURI());
						writer.writeEndElement();
	        		}else if(curst.getObject().isLiteral()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id","literal"+literalcounter);
						writer.writeAttribute("value",curst.getObject().asLiteral().getValue().toString());
						writer.writeAttribute("type", curst.getObject().asLiteral().getDatatypeURI());
						if(curst.getObject().asLiteral().getLanguage()!=null) {
							writer.writeAttribute("lang", curst.getObject().asLiteral().getLanguage());							
						}
						writer.writeEndElement();
						writer.writeStartElement("edge");
						writer.writeAttribute("id",curst.getPredicate().getURI());
						writer.writeAttribute("uri",curst.getPredicate().getURI());
						writer.writeAttribute("value",curst.getPredicate().getLocalName());
						writer.writeAttribute("source", curst.getSubject().getURI());
						writer.writeAttribute("target", "literal"+literalcounter);
						writer.writeEndElement();
						literalcounter++;
	        		}
	        	}
	        }
			writer.writeEndElement();
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			strwriter.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
		response.getWriter().write(strwriter.toString());
		response.getWriter().close();
		return null;
	}

}
