package de.fuberlin.wiwiss.pubby.exporter.rdf;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.wololo.jts2geojson.GeoJSONReader;

import de.fuberlin.wiwiss.pubby.exporter.GeoModelWriter;
import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class LDWriter extends GeoModelWriter {

	String format;

	public LDWriter(String epsg, String format) {
		super(epsg);
		this.format = format;
	}

	@Override
	public ExtendedIterator<Resource> write(Model modell, HttpServletResponse response) throws IOException {
		Model model=modell;
		if (false /*this.epsg != null*/) {
			model = ModelFactory.createModelForGraph(modell.getGraph());
			ExtendedIterator<Resource> it = modell.listSubjects();
			while (it.hasNext()) {
				Resource indd = it.next();
				Resource ind=model.getResource(indd.getURI());
				StmtIterator it2 = indd.listProperties();
				if (ind.hasProperty(GEO.EPSG)) {
					sourceCRS = "EPSG:" + ind.getProperty(GEO.EPSG).getObject().asLiteral().getValue().toString();
				}

				Double lat = null, lon = null;
				while (it2.hasNext()) {
					Statement curst2 = it2.next();	
					Statement curst=ind.getProperty(curst2.getPredicate());
					if (GEO.ASWKT.getURI().equals(curst2.getPredicate().getURI().toString())
							|| GEO.P_GEOMETRY.getURI().equals(curst2.getPredicate().getURI())
							|| GEO.P625.getURI().equals(curst.getPredicate().getURI()) && this.epsg != null) {
						try {
							Geometry geom = reader.read(curst2.getObject().asLiteral().getString());
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
							geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
							curst.changeObject(geom.toText() + "^^<http://www.opengis.net/ont/geosparql#geoJSONLiteral>");
						}
					} else if (GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())) {
						lat = curst.getObject().asLiteral().getDouble();
					} else if (GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())
							&& this.epsg != null) {
						lon = curst.getObject().asLiteral().getDouble();
					} else if (GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())
							&& this.epsg != null) {
						lat = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
						lon = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
						Coordinate coord = ReprojectionUtils.reproject(lon, lat, sourceCRS, epsg);
						curst.changeObject(coord.y + " " + coord.x);
					}
					if (lat != null && lon != null) {
						Coordinate coord = ReprojectionUtils.reproject(lon, lat, sourceCRS, epsg);
						ind.listProperties(GEO.P_LAT).next().changeLiteralObject(coord.y);
						ind.listProperties(GEO.P_LONG).next().changeLiteralObject(coord.x);
					}

				}
			it2.close();
			ind.removeAll(GEO.EPSG);
			ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
			}
		}
		model.getWriter(format).write(model, response.getOutputStream(), null);
		return null;
	}

}
