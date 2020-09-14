package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.wololo.jts2geojson.GeoJSONReader;

import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class LDWriter extends GeoModelWriter {

	String format;

	public LDWriter(String epsg, String format) {
		super(epsg);
		this.format = format;
	}

	@Override
	public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
		if (this.epsg != null) {
			ExtendedIterator<Resource> it = model.listSubjects();
			while (it.hasNext()) {
				Resource ind = it.next();
				StmtIterator it2 = ind.listProperties();
				if (ind.hasProperty(GEO.EPSG)) {
					sourceCRS = "EPSG:" + ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}
				if (this.epsg != null) {
					ind.removeAll(GEO.EPSG);
				}
				Double lat = null, lon = null;
				while (it2.hasNext()) {
					Statement curst = it2.next();
					if (GEO.ASWKT.getURI().equals(curst.getPredicate().getURI().toString())
							|| GEO.P_GEOMETRY.getURI().equals(curst.getPredicate().getURI())
							|| GEO.P625.getURI().equals(curst.getPredicate().getURI()) && this.epsg != null) {
						try {
							Geometry geom = reader.read(curst.getObject().asLiteral().getString());
							ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
							geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
							curst.changeObject(geom.toText() + "^^<http://www.opengis.net/ont/geosparql#wktLiteral>");
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (GEO.ASGEOJSON.getURI().equals(curst.getPredicate().getURI().toString())
							&& this.epsg != null) {
						if (curst.getObject().asLiteral().getString() != null) {
							GeoJSONReader read = new GeoJSONReader();
							Geometry geom = read.read(curst.getObject().asLiteral().getString());
							ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
							geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
							curst.changeObject(
									geom.toText() + "^^<http://www.opengis.net/ont/geosparql#geoJSONLiteral>");
						}
					} else if (GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())) {
						lat = curst.getObject().asLiteral().getDouble();
					} else if (GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())
							&& this.epsg != null) {
						lon = curst.getObject().asLiteral().getDouble();
					} else if (GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())
							&& this.epsg != null) {
						Coordinate coord = ReprojectionUtils.reproject(lon, lat, sourceCRS, epsg);
						curst.changeObject(coord.y + " " + coord.x);
					}
					if (lat != null && lon != null) {
						Coordinate coord = ReprojectionUtils.reproject(lon, lat, sourceCRS, epsg);
						ind.listProperties(GEO.P_LAT).next().changeLiteralObject(coord.y);
						ind.listProperties(GEO.P_LONG).next().changeLiteralObject(coord.x);
					}

				}
			}
		}
		model.commit();
		model.getWriter(format).write(model, response.getOutputStream(), null);
		return null;
	}

}
