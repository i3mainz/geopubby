package de.fuberlin.wiwiss.pubby.exporter.rdf;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;

public class TLPWriter extends GeoModelWriter {
	
	public TLPWriter(String epsg) {
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
        response.getWriter().write("(tlp \"2.0\" (nodes ");
        Map<String,Integer> uriToNodeId=new HashMap<String,Integer>();
        Integer nodecounter=0;
        Integer resnodeid=0;
        StringBuilder edgebuilder=new StringBuilder();
        for(Resource res:resources) {
        	if(!res.isURIResource())
				continue;
        	if(uriToNodeId.containsKey(res.getURI())) {
        		resnodeid=uriToNodeId.get(res.getURI());
        	}else {
            	resnodeid=nodecounter++;
            	uriToNodeId.put(res.getURI(),resnodeid);
            	response.getWriter().write(resnodeid+" ");
        	}
        	StmtIterator propiter = res.listProperties();
        	while(propiter.hasNext()) {
        		Statement curst=propiter.next();
        		if(curst.getObject().isURIResource()) {
        			Integer objnodeid=0;
        			if(uriToNodeId.containsKey(curst.getObject().asResource().getURI())) {
        				objnodeid=uriToNodeId.get(curst.getObject().asResource().getURI());
        			}else {
        				objnodeid=nodecounter;
        				uriToNodeId.put(curst.getObject().asResource().getURI(),objnodeid);
            			response.getWriter().write(objnodeid+" ");
        			}
        			edgebuilder.append("(edge "+curst.getPredicate().getURI()+" "+resnodeid+" "+objnodeid+") "+System.lineSeparator());
        		}else if(curst.getObject().isLiteral()) {
        			response.getWriter().write(nodecounter+" ");
        			edgebuilder.append("(edge "+curst.getPredicate().getURI()+" "+resnodeid+" "+nodecounter+++") "+System.lineSeparator());
        		}
        	}
        }
        response.getWriter().write(")"+System.lineSeparator()+edgebuilder.toString()+System.lineSeparator()+")");
        response.getWriter().close();
        return null;
	}

}
