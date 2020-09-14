package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class XYZASCIIWriter extends GeoModelWriter {
	
	public XYZASCIIWriter(String epsg) {
		super(epsg);
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		ExtendedIterator<Resource> it = super.write(model, response);
		Double lat = null, lon = null;
		Geometry geom = null;
		while (it.hasNext()) {
			Resource ind = it.next();
			if(ind.hasProperty(GEO.EPSG)) {
				sourceCRS="EPSG:"+ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
			}
			StmtIterator it2 = ind.listProperties();
			while (it2.hasNext()) {
				Statement curst = it2.next();
				if (GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString())
						|| GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
						|| GEO.P625.getURI().equals(curst.getPredicate().getURI())) {
					try {
						geom = reader.read(curst.getObject().asLiteral().getString());
						if(this.epsg!=null) {
							geom=ReprojectionUtils.reproject(geom, sourceCRS, epsg);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())) {
					lat = curst.getObject().asLiteral().getDouble();
				} else if (GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())) {
					lon = curst.getObject().asLiteral().getDouble();
				} else if (GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())) {
					lat = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
					lon = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
				}
			}
		}
		try {
			if (geom != null) {
				for (Coordinate coord : geom.getCoordinates()) {
					response.getWriter().write(coord.getX() + " " + coord.getY());
					if (!Double.isNaN(coord.getZ())) {
						response.getWriter().write(" " + coord.getZ());
					}
				}
				response.getWriter().close();
			} else if (lat != null || lon != null) {
				try {
					geom = reader.read("Point("+lon+" "+lat+")");
					if(this.epsg!=null) {
						geom=ReprojectionUtils.reproject(geom, sourceCRS, epsg);
					}
					Coordinate coord=geom.getCoordinate();
					response.getWriter().write(coord.getX() + " " + coord.getY());
					if (!Double.isNaN(coord.getZ())) {
						response.getWriter().write(" " + coord.getZ());
					}
					response.getWriter().close();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
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
