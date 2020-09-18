package de.fuberlin.wiwiss.pubby.exporter.vector;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.wololo.jts2geojson.GeoJSONReader;

import de.fuberlin.wiwiss.pubby.exporter.AbstractGeoJSONWriter;

public class GRASSVectorASCIIWriter extends AbstractGeoJSONWriter {

	public GRASSVectorASCIIWriter(String epsg) {
		super(epsg);
	}

	GeoJSONReader reader=new GeoJSONReader();
	
	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		JSONObject obj=this.prepareGeoJSONString(model, response);
		response.getWriter().write("VERTI:"+System.lineSeparator());
		for(int i=0;i<obj.getJSONArray("features").length();i++) {
			JSONObject feat=obj.getJSONArray("features").getJSONObject(i);
			Geometry geom=reader.read(feat.getJSONObject("geometry").toString());
			switch(geom.getGeometryType()) {
			case "Point": 
				response.getWriter().write("P\t");
				break;
			case "LineString":
				response.getWriter().write("L\t");
			default:
				response.getWriter().write("B\t");
			}
			response.getWriter().write(geom.getCoordinates().length+System.lineSeparator());
			for(Coordinate coord:geom.getCoordinates()) {
				response.getWriter().write(coord.getX()+" "+coord.getY());
				if(!Double.isNaN(coord.getZ())) {
					response.getWriter().write(coord.getZ()+"");
				}
				response.getWriter().write(System.lineSeparator());
			}
		}
		response.getWriter().close();
		return null;
	}
	
}
