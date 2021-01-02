package de.fuberlin.wiwiss.pubby.exporter.rdf;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class TGFWriter extends GeoModelWriter {
	
	public TGFWriter(String epsg) {
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
        Integer nodecounter=0,edgecounter=0;
        Integer resnodeid=0;
        StringBuilder edgebuilder=new StringBuilder();
        for(Resource res:resources) {
        	if(!res.isURIResource())
				continue;
        	resnodeid=nodecounter++;
        	response.getWriter().write(resnodeid+" "+res.getURI());
        	StmtIterator propiter = res.listProperties();
        	while(propiter.hasNext()) {
        		Statement curst=propiter.next();
        		if(curst.getObject().isURIResource()) {
        			response.getWriter().write(nodecounter+" "+curst.getObject().asResource().getURI()+System.lineSeparator());
        			edgebuilder.append(resnodeid+" "+nodecounter+++" "+curst.getPredicate().getURI()+System.lineSeparator());
        		}else if(curst.getObject().isLiteral()) {
        			response.getWriter().write(nodecounter+" "+curst.getObject().asLiteral().getValue()+" ("+curst.getObject().asLiteral().getDatatypeURI()+")"+System.lineSeparator());
        			edgebuilder.append(resnodeid+" "+nodecounter+++" "+curst.getPredicate().getURI()+System.lineSeparator());
        		}
        	}
        }
        response.getWriter().write("#"+System.lineSeparator());
        response.getWriter().write(edgebuilder.toString());
        response.getWriter().close();
        return null;
	}

}
