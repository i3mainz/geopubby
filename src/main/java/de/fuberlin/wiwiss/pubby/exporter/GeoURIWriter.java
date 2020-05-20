package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class GeoURIWriter implements ModelWriter {

	WKTReader reader=new WKTReader();
	
	@Override
	public void write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=model.
				listResourcesWithProperty(GEO.HASGEOMETRY);
				if(!it.hasNext()) {
					it.close();
					it=model.listResourcesWithProperty(GEO.P_LAT);
				}
				if(!it.hasNext()) {
					it.close();
					it=model.listResourcesWithProperty(GEO.HASGEOMETRY);
				}
				if(!it.hasNext()) {
					it.close();
					it=model.listResourcesWithProperty(GEO.GEORSSPOINT);
				}
				if(!it.hasNext()) {
					it.close();
					it=model.listResourcesWithProperty(GEO.P625);
				}
				Double lat=null,lon=null;
				while(it.hasNext()) {
						Resource ind=it.next();
						StmtIterator it2 = ind.listProperties();
						while(it2.hasNext()) {
							Statement curst=it2.next();
							if(GEO.HASGEOMETRY.getURI().equals(curst.getPredicate().getURI().toString()) || 
									GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
									|| 
									GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
								try {
									Geometry geom=reader.read(curst.getObject().asLiteral().getValue().toString());
									lat=geom.getCentroid().getCoordinate().getY();
									lon=geom.getCentroid().getCoordinate().getX();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else if(GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())){
								lat=curst.getObject().asLiteral().getDouble();
							}else if(GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())){
								lon=curst.getObject().asLiteral().getDouble();
							}else if(GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())){
								lat=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
								lon=Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
							}
						}
					}	
				try {
					if(lat==null || lon==null) {
						response.getWriter().write("");
						response.getWriter().close();
					}else {
						response.getWriter().write("geo:"+lat+","+lon);
						response.getWriter().close();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	}

}
