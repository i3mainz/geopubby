package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;
import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

/**
 * Extracts an OSM link from the given GeoPubby instance.
 */
public class OSMLinkWriter extends GeoModelWriter {

	public OSMLinkWriter(String epsg) {
		super(epsg);
	}
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it=super.write(model, response);
		while (it.hasNext()) {
			Resource ind = it.next();
			if(ind.hasProperty(GEO.EPSG)) {
				sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
			}
			StmtIterator it2 = ind.listProperties();
			while (it2.hasNext()) {
				Statement curst = it2.next();
				this.handleGeometry(curst, ind, model);
			}
		}
		try {
			if(geom!=null) {
				Point p=geom.getCentroid();
				response.getWriter().write("http://www.openstreetmap.org/index.html?lat="+p.getX()+"&lon="+p.getY());
				response.getWriter().close();
			}else if (lat != null || lon != null) {
				try {
					geom = reader.read("Point("+lon+" "+lat+")");
					if(this.epsg!=null) {
						geom=ReprojectionUtils.reproject(geom, sourceCRS, epsg);
					}
					response.getWriter().write("http://www.openstreetmap.org/index.html?lat="+geom.getCoordinate().getX()+"&lon="+geom.getCoordinate().getY());
					response.getWriter().write(geom.toText());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				response.getWriter().close();
			}else{
				response.getWriter().write("");
				response.getWriter().close();
			}
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
