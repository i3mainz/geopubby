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
import org.json.JSONException;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class CypherWriter extends AbstractGeoJSONWriter {

	public CypherWriter(String epsg) {
		super(epsg);
		this.namespaceToPrefix=new HashMap<String,String>();
		// TODO Auto-generated constructor stub
	}
	
	Map<String,String> namespaceToPrefix;
	
	Integer namespacecounter=0;
	
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
        	if(!res.isURIResource() || res.getLocalName()==null || res.getLocalName().isEmpty())
        		continue;
			String resprefix=res.getModel().getNsURIPrefix(res.getNameSpace());
			if(resprefix==null) {
				if(namespaceToPrefix.containsKey(res.getNameSpace())) {
					resprefix=namespaceToPrefix.get(res.getNameSpace());
				}else {
					resprefix="ns"+namespacecounter++;
					namespaceToPrefix.put(res.getNameSpace(),resprefix);
				}
			}else {
				resprefix=resprefix.replace("-", "_");
			}
        	literalresult.append("CREATE (").append(resprefix+"_"+res.getLocalName()).append(" { ");
        	if(res.getURI()!=null && !res.getURI().isEmpty()) {
        		literalresult.append("_id:'"+resprefix+"_"+res.getLocalName().replace(".","_")+"', ");
        		literalresult.append("_uri:'"+res.getURI()+"', ");
        	}
        	StmtIterator propiter = res.listProperties();
        	while(propiter.hasNext()) {
        		Statement curst=propiter.next();
    			String subprefix=curst.getSubject().asResource().getModel().getNsURIPrefix(curst.getSubject().asResource().getNameSpace());
    			if(subprefix==null) {
    				if(namespaceToPrefix.containsKey(curst.getSubject().getNameSpace())) {
    					subprefix=namespaceToPrefix.get(curst.getSubject().getNameSpace());
    				}else {
    					subprefix="ns"+namespacecounter++;
    					namespaceToPrefix.put(curst.getSubject().getNameSpace(),subprefix);
    				}
    			}else {
    				subprefix=subprefix.replace("-", "_");
    			}
    			String predprefix=curst.getPredicate().asResource().getModel().getNsURIPrefix(curst.getPredicate().asResource().getNameSpace());
    			if(predprefix==null) {
    				if(namespaceToPrefix.containsKey(curst.getPredicate().getNameSpace())) {
    					predprefix=namespaceToPrefix.get(curst.getPredicate().getNameSpace());
    				}else {
    					predprefix="ns"+namespacecounter++;
    					namespaceToPrefix.put(curst.getPredicate().getNameSpace(),predprefix);
    				}
    			}else {
    				predprefix=predprefix.replace("-", "_");
    			}
    			if(curst.getObject().isURIResource() && curst.getObject().asResource().getLocalName()!=null && !curst.getObject().asResource().getLocalName().isEmpty()) {
        			String objprefix=curst.getObject().asResource().getModel().getNsURIPrefix(curst.getObject().asResource().getNameSpace());
        			if(objprefix==null) {
        				if(namespaceToPrefix.containsKey(curst.getObject().asResource().getNameSpace())) {
        					objprefix=namespaceToPrefix.get(curst.getObject().asResource().getNameSpace());
        				}else {
        					objprefix="ns"+namespacecounter++;
        					namespaceToPrefix.put(curst.getObject().asResource().getNameSpace(),objprefix);
        				}
        			}else {
        				objprefix=objprefix.replace("-", "_");
        			}
                	resourceresult.append("CREATE ("+subprefix+"_"+curst.getSubject().getLocalName().replace(".","_")+")-[:"+predprefix+"_"+curst.getPredicate().getLocalName().replace(".","_")+"]->("+objprefix+"_"+curst.getObject().asResource().getLocalName().replace(".","_")+"),\n");	
        		}else if(curst.getObject().isLiteral()){
                	literalresult.append(predprefix+"_"+curst.getPredicate().getLocalName().replace(".","_")+":'"+curst.getObject().asLiteral().getValue().toString()+"', ");        			
        		}
        	}
        	if(!literalresult.toString().endsWith("{"))
        		literalresult.delete(literalresult.length()-2, literalresult.length());
        	if(!resourceresult.toString().isEmpty())
        		resourceresult.delete(resourceresult.length()-1, resourceresult.length());
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
