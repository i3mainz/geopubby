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

public class GXLWriter extends GeoModelWriter {
	
	public GXLWriter(String epsg) {
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
        Set<String> uriToNodeId = new HashSet<String>();
		Integer literalcounter = 0, edgecounter = 0;
		StringWriter strwriter = new StringWriter();
		StringBuilder builder=new StringBuilder();
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter writer = new IndentingXMLStreamWriter(factory.createXMLStreamWriter(strwriter));
			writer.writeStartDocument();
			writer.writeStartElement("gxl");
			writer.writeAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
			writer.writeStartElement("graph");
			writer.writeAttribute("id", "G");
			writer.writeAttribute("edgeids", "true");
			writer.writeAttribute("edgemode", "directed");
			writer.writeAttribute("hypergraph", "false");
			for (Resource res : resources) {
				if (!res.isURIResource())
					continue;
				String subprefix = null;
				if (!uriToNodeId.contains(res.getURI())) {
					writer.writeStartElement("node");
					writer.writeAttribute("id", res.getURI());
					writer.writeAttribute("uri", res.getURI());
					subprefix = res.getModel().getNsURIPrefix(res.getNameSpace());
					if (subprefix != null) {
						writer.writeAttribute("label",subprefix + ":" + res.getLocalName());
					} else {
						writer.writeAttribute("label",res.getLocalName());
					}
					writer.writeEndElement();
					uriToNodeId.add(res.getURI());
				}
				StmtIterator propiter = res.listProperties();
				while (propiter.hasNext()) {
					Statement curst = propiter.next();
					if (curst.getObject().isURIResource()) {
						if (!uriToNodeId.contains(curst.getObject().asResource().getURI())) {
							writer.writeStartElement("node");
							writer.writeAttribute("id", curst.getObject().asResource().getURI());
							subprefix = res.getModel().getNsURIPrefix(curst.getObject().asResource().getNameSpace());
							if (subprefix != null) {
								writer.writeAttribute("label",subprefix + ":" + curst.getObject().asResource().getLocalName());
							} else {
								writer.writeAttribute("label",curst.getObject().asResource().getLocalName());
							}
							writer.writeAttribute("uri", curst.getObject().asResource().getURI());
							writer.writeEndElement();
							uriToNodeId.add(curst.getObject().asResource().getURI());
						}
						builder.append("<edge from=\""+curst.getSubject().getURI()+"\" to=\""+curst.getObject().asResource().getURI()+"\" label=\""+curst.getPredicate().getURI()+"\" id=\""+"e" + edgecounter+++"\"/>"+System.lineSeparator());
					} else if (curst.getObject().isLiteral()) {
						writer.writeStartElement("node");
						writer.writeAttribute("id", "literal" + literalcounter);
						writer.writeAttribute("label", curst.getObject().asLiteral().getLexicalForm()+" ("+curst.getObject().asLiteral().getDatatypeURI()+")");
						writer.writeEndElement();
						builder.append("<edge from=\""+curst.getSubject().getURI()+"\" to=\"literal" + literalcounter+"\" label=\""+curst.getPredicate().getURI()+"\" id=\""+"e" + edgecounter+++"\"/>"+System.lineSeparator());
						literalcounter++;
					}
				}
			}
			writer.flush();
			strwriter.write(builder.toString()+System.lineSeparator());
			strwriter.flush();
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			strwriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.getWriter().write(strwriter.toString());
		response.getWriter().close();
		return null;
	}

}
