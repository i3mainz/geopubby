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
import org.json.JSONException;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class CypherWriter extends AbstractGeoJSONWriter {

	public CypherWriter(String epsg) {
		super(epsg);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
        // index all resources
        Set<Resource> resources = new HashSet<>();
        model.listStatements().toList()
                .forEach( statement -> {
                    resources.add(statement.getSubject());
                    RDFNode object = statement.getObject();
                    if (object.isResource()) {
                        resources.add(object.asResource());
                    }
                });
        
        StringBuilder literalresult=new StringBuilder();
        StringBuilder resourceresult=new StringBuilder();
        for(Resource res:resources) {
        	if(!res.isURIResource() && res.getLocalName()!=null)
        		continue;
        	literalresult.append("CREATE (").append(res.getURI()).append(" {");
        	StmtIterator propiter = res.listProperties();
        	while(propiter.hasNext()) {
        		Statement curst=propiter.next();
        		if(curst.getObject().isURIResource()) {
                	resourceresult.append("CREATE\n("+curst.getSubject().getURI()+")-[:"+curst.getPredicate().getLocalName()+"]->("+curst.getObject().asResource().getURI()+"),\n");	
        		}else {
                	literalresult.append(curst.getPredicate().getURI()+":'"+curst.getObject().asLiteral().getValue().toString()+"', ");        			
        		}
        	}
        	if(!literalresult.toString().endsWith("{"))
        		literalresult.delete(literalresult.length()-2, literalresult.length());
        	literalresult.append(" })\n");
        }
        try {
			response.getWriter().write(literalresult.toString()+System.lineSeparator()+resourceresult.toString());
			response.getWriter().close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
