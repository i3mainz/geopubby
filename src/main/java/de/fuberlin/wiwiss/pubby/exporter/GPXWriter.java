package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.expr.NodeValue;

public class GPXWriter implements ModelWriter  {

	@Override
	public void write(Model model, HttpServletResponse response) throws IOException {
		StringBuilder gpxout=new StringBuilder();
		StringBuilder attbuilder=new StringBuilder();
		gpxout.append("<?xml version='1.0' encoding='UTF-8' standalone='no' ?><gpx version='1.0'><name>"+featuretype+"</name>");
	    while(results.hasNext()) {
	    	QuerySolution solu=results.next();
	    	Iterator<String> varnames = solu.varNames();
	    	gpxout.append(attbuilder.toString());
	    	attbuilder.delete(0,attbuilder.length());
	    	if(lastQueriedElemCount>0) {
	    		attbuilder.append("</wpt>");
	    	}
	    	while(varnames.hasNext()) {
	    		String name=varnames.next();
	    		if(name.equalsIgnoreCase("lat")){
	    			if(solu.get("lon")!=null) {
	    				gpxout.append("<wpt lat=\""+solu.get("lat").toString().substring(0,solu.get("lat").toString().indexOf("^^"))+"\" lon=\""+solu.get("lon").toString().substring(0,solu.get("lon").toString().indexOf("^^"))+"\">");
	    			}
	    		}else if(!name.endsWith("_geom")) {
	    			attbuilder.append("<"+name+">");
	    			attbuilder.append(solu.get(name));
	    			attbuilder.append("</"+name+">");
	    		}else {
	    			AsGPX gpx=new AsGPX();
	    			NodeValue val=gpx.exec(NodeValue.makeNode(solu.getLiteral(name).getString(),solu.getLiteral(name).getDatatype()));
	    			String res=val.asString();
	    			gpxout.append(res);
	    		}
	    	}
	    	gpxout.append("</trk>");
	    }
	    gpxout.append("</gpx>");
		return gpxout.toString();
		
	}

}
