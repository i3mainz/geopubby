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
        Integer literalcounter=0,edgecounter=0;
        Integer typecounter=0,langcounter=0,valcounter=0;
        StringWriter strwriter=new StringWriter();
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
			writer.writeStartDocument();
	        writer.writeStartElement("graphml");
	        writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
	        writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	        writer.writeAttribute("xmlns:y","http://www.yworks.com/xml/graphml");
	        writer.writeAttribute("xmlns:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
	        writer.writeStartElement("key");
	        writer.writeAttribute("for", "node");
	        writer.writeAttribute("id", "nodekey");
	        writer.writeAttribute("yfiles.type", "nodegraphics");
	        writer.writeEndElement();
	        writer.writeStartElement("key");
	        writer.writeAttribute("for", "edge");
	        writer.writeAttribute("id", "edgekey");
	        writer.writeAttribute("yfiles.type", "edgegraphics");
	        writer.writeEndElement();
	        writer.writeStartElement("graph");
	        writer.writeAttribute("id", "G");
	        writer.writeAttribute("edgedefault", "undirected");    
			for(Resource res:resources) {
				if(!res.isURIResource())
					continue;
				writer.writeStartElement("node");	
				writer.writeAttribute("id",res.getURI());
				writer.writeAttribute("uri", res.getURI());
				writer.writeStartElement("data");
				writer.writeAttribute("key", "nodekey");
				literalcounter++;
				writer.writeStartElement("y:ShapeNode");
				writer.writeStartElement("y:Shape");
				writer.writeAttribute("shape", "ellipse");
				writer.writeEndElement();
				writer.writeStartElement("y:Fill");
				writer.writeAttribute("color", "#800080");
				writer.writeAttribute("transparent", "false");
				writer.writeEndElement();
				writer.writeStartElement("y:NodeLabel");
				writer.writeAttribute("alignment", "center");
				writer.writeAttribute("autoSizePolicy", "content");
				writer.writeAttribute("fontSize", "12");
				writer.writeAttribute("fontStyle", "plain");
				writer.writeAttribute("hasText", "true");
				writer.writeAttribute("visible", "true");
				writer.writeAttribute("width", "4.0");
				String subprefix=res.getModel().getNsURIPrefix(res.getNameSpace());
				if(subprefix!=null) {
					writer.writeCharacters(subprefix+":"+res.getLocalName());							
				}else {
					writer.writeCharacters(res.getLocalName());
				}
				writer.writeCharacters(res.getLocalName());
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
				writer.writeEndElement();
	        	StmtIterator propiter = res.listProperties();
	        	while(propiter.hasNext()) {
	        		Statement curst=propiter.next();
	        		if(curst.getObject().isURIResource()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id",curst.getObject().asResource().getURI());
						writer.writeAttribute("value",curst.getObject().asResource().getLocalName());
						writer.writeAttribute("uri", curst.getObject().asResource().getURI());
						writer.writeStartElement("data");
						writer.writeAttribute("key", "nodekey");
						literalcounter++;
						writer.writeStartElement("y:ShapeNode");
						writer.writeStartElement("y:Shape");
						writer.writeAttribute("shape", "ellipse");
						writer.writeEndElement();
						writer.writeStartElement("y:Fill");
						if(curst.getPredicate().getURI()!=null 
								&& 
								(curst.getPredicate().getURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
								|| curst.getPredicate().getURI().equals("http://www.w3.org/2000/01/rdf-schema#subClassOf"))) {
							writer.writeAttribute("color", "#ffa500");	
						}else if(curst.getPredicate().getURI()!=null && curst.getPredicate().getURI().startsWith("http://www.w3.org/2000/01/rdf-schema#")){
							writer.writeAttribute("color", "#F08080");	
						}else {
							writer.writeAttribute("color", "#800080");								
						}
						writer.writeAttribute("transparent", "false");
						writer.writeEndElement();
						writer.writeStartElement("y:NodeLabel");
						writer.writeAttribute("alignment", "center");
						writer.writeAttribute("autoSizePolicy", "content");
						writer.writeAttribute("fontSize", "12");
						writer.writeAttribute("fontStyle", "plain");
						writer.writeAttribute("hasText", "true");
						writer.writeAttribute("visible", "true");
						writer.writeAttribute("width", "4.0");
						subprefix=curst.getObject().asResource().getModel().getNsURIPrefix(curst.getObject().asResource().getNameSpace());
						if(subprefix!=null) {
							writer.writeCharacters(subprefix+":"+curst.getObject().asResource().getLocalName());							
						}else {
							writer.writeCharacters(curst.getObject().asResource().getLocalName());
						}
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeStartElement("edge");
						writer.writeAttribute("id","e"+edgecounter++);
						writer.writeAttribute("uri",curst.getPredicate().getURI());
						writer.writeAttribute("source", curst.getSubject().getURI());
						writer.writeAttribute("target", curst.getObject().asResource().getURI());
						writer.writeEndElement();
	        		}else if(curst.getObject().isLiteral()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id","literal"+literalcounter);
						if(!curst.getObject().asLiteral().getValue().toString().isEmpty()) {
							writer.writeStartElement("data");
							writer.writeAttribute("key", "nodekey");
							writer.writeStartElement("y:ShapeNode");
							writer.writeStartElement("y:Shape");
							writer.writeAttribute("shape", "ellipse");
							writer.writeEndElement();
							writer.writeStartElement("y:Fill");
							writer.writeAttribute("color", "#008000");								
							writer.writeAttribute("transparent", "false");
							writer.writeEndElement();
							writer.writeStartElement("y:NodeLabel");
							writer.writeAttribute("alignment", "center");
							writer.writeAttribute("autoSizePolicy", "content");
							writer.writeAttribute("fontSize", "12");
							writer.writeAttribute("fontStyle", "plain");
							writer.writeAttribute("hasText", "true");
							writer.writeAttribute("visible", "true");
							writer.writeAttribute("width", "4.0");
							writer.writeCharacters(curst.getObject().asLiteral().getValue().toString()+" (xsd:"+curst.getObject().asLiteral().getDatatype().getURI().substring(curst.getObject().asLiteral().getDatatype().getURI().lastIndexOf('#')+1)+")");
							writer.writeEndElement();
							writer.writeEndElement();
							writer.writeEndElement();
							valcounter++;
						}
						writer.writeStartElement("data");
						writer.writeAttribute("key", "type"+typecounter);
						typecounter++;
						writer.writeCharacters(curst.getObject().asLiteral().getDatatypeURI());
						writer.writeEndElement();
						if(curst.getObject().asLiteral().getLanguage()!=null) {
							writer.writeStartElement("data");
							writer.writeAttribute("key", "lang"+langcounter);
							langcounter++;
							writer.writeCharacters(curst.getObject().asLiteral().getLanguage());
							writer.writeEndElement();						
						}
						writer.writeEndElement();
						writer.writeStartElement("edge");
						writer.writeAttribute("id","e"+edgecounter++);
						writer.writeAttribute("uri",curst.getPredicate().getURI());
						writer.writeAttribute("source", curst.getSubject().getURI());
						writer.writeAttribute("target", "literal"+literalcounter);
						writer.writeStartElement("data");
						writer.writeAttribute("key", "edgekey");
						literalcounter++;
						writer.writeStartElement("y:PolyLineEdge");
						writer.writeStartElement("y:EdgeLabel");
						writer.writeAttribute("alignment", "center");
						writer.writeAttribute("configuration", "AutoFlippingLabel");
						writer.writeAttribute("fontSize", "12");
						writer.writeAttribute("fontStyle", "plain");
						writer.writeAttribute("hasText", "true");
						writer.writeAttribute("visible", "true");
						writer.writeAttribute("width", "4.0");
						subprefix=curst.getPredicate().asResource().getModel().getNsURIPrefix(curst.getPredicate().asResource().getNameSpace());
						if(subprefix!=null) {
							writer.writeCharacters(subprefix+":"+curst.getPredicate().getLocalName());							
						}else {
							writer.writeCharacters(curst.getPredicate().getLocalName());
						}
						writer.writeEndElement();
						writer.writeEndElement();
						writer.writeEndElement();
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
