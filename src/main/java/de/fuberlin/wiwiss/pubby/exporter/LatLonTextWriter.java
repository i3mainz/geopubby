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

import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class LatLonTextWriter extends GeoModelWriter {

	public LatLonTextWriter(String epsg) {
		super(epsg);
	}
	
	public String convertDecimalToLatLonText(Double D, Boolean lng){
	    String dir;
		if(D<0) {
			if(lng) {
				dir="W";
			}else {
				dir="S";
			}
		}else {
			if(lng) {
				dir="E";
			}else {
				dir="N";
			}
		}
		Double deg=D<0?-D:D;
		Double min=D%1*60;
		Double sec=(D*60%1*6000)/100;
		return deg+"°"+min+"'"+sec+"\""+dir;
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
			if (lat == null || lon == null) {
				response.getWriter().write("");
				response.getWriter().close();
			} else {
				response.getWriter().write(convertDecimalToLatLonText(lat,false)+" "+convertDecimalToLatLonText(lon,true));	
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
