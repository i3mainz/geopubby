package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.sis.coverage.grid.GridCoverage;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.gml2.GMLReader;
import org.wololo.jts2geojson.GeoJSONReader;
import org.xml.sax.SAXException;

import de.fuberlin.wiwiss.pubby.exporter.style.ResultStyleFormatter;
import de.fuberlin.wiwiss.pubby.util.ReprojectionUtils;
import de.fuberlin.wiwiss.pubby.util.StyleObject;
import de.fuberlin.wiwiss.pubby.util.Tuple;
import de.fuberlin.wiwiss.pubby.vocab.GEO;

public class GeoModelWriter extends ModelWriter {

	protected String epsg=null;
	
	protected Double lat,lon;
	
	protected String sourceCRS="EPSG:4326";
	
	protected Geometry geom;
	
	protected GridCoverage cov;
	
	protected StyleObject styleObject;
	
	protected ResultStyleFormatter styleformatter;
	
	public GeoModelWriter(String epsg) {
		this.epsg=epsg;
	}
	
	public GeoModelWriter() {
		super();
	}
	
	protected StyleObject handleStyle(Resource res) {
		StyleObject result=new StyleObject();
		if(res.hasProperty(GEO.POINTSTYLE)){
			result.pointStyle=res.getProperty(GEO.POINTSTYLE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POINTIMAGE)){
			result.pointImage=res.getProperty(GEO.POINTIMAGE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGSTYLE)){
			result.lineStringStyle=res.getProperty(GEO.LINESTRINGSTYLE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGIMAGE)){
			result.lineStringImage=res.getProperty(GEO.LINESTRINGIMAGE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGIMAGESTYLE)){
			result.lineStringImageStyle=res.getProperty(GEO.LINESTRINGIMAGESTYLE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POLYGONSTYLE)){
			result.polygonStyle=res.getProperty(GEO.POLYGONSTYLE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POLYGONIMAGE)){
			result.polygonImage=res.getProperty(GEO.POLYGONIMAGE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.HATCH)){
			result.polygonImage=res.getProperty(GEO.HATCH).getLiteral().getString();
		}
		return result;
	}
	
	
	public Tuple<Boolean,String> handleGeometry(Statement curst,Resource ind,Model model) {
		boolean handled=false;
		String type="vector";
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
			handled=true;
		} else if (GEO.ASGEOJSON.getURI().equals(curst.getPredicate().getURI().toString())
				&& this.epsg != null) {
			if (curst.getObject().asLiteral().getString() != null) {
				GeoJSONReader read = new GeoJSONReader();
				geom = read.read(curst.getObject().asLiteral().getString());
				if(this.epsg!=null) {
					ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
					geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
				}
			}
			handled=true;
		} else if (GEO.ASWKB.getURI().equals(curst.getPredicate().getURI().toString())
				&& this.epsg != null) {
			if (curst.getObject().asLiteral().getString() != null) {
				WKBReader wkbread=new WKBReader();
				try {
					geom = wkbread.read(curst.getObject().asLiteral().getString().getBytes());
					if(this.epsg!=null) {
						ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
						geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			handled=true;
		} else if (GEO.ASHEXWKB.getURI().equals(curst.getPredicate().getURI().toString())
				&& this.epsg != null) {
			if (curst.getObject().asLiteral().getString() != null) {
				WKBReader wkbread=new WKBReader();
				try {
					geom = wkbread.read(WKBReader.hexToBytes(curst.getObject().asLiteral().getString()));
					if(this.epsg!=null) {
						ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
						geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			handled=true;
		}else if (GEO.ASGML.getURI().equals(curst.getPredicate().getURI().toString())
				&& this.epsg != null) {
			if (curst.getObject().asLiteral().getString() != null) {
				GMLReader reader=new GMLReader();
				try {
					geom=reader.read(curst.getObject().asLiteral().getString(), new GeometryFactory());
					if(this.epsg!=null) {
						ind.addProperty(GEO.EPSG, model.createTypedLiteral(this.epsg));
						geom = ReprojectionUtils.reproject(geom, sourceCRS, epsg);
					}
				} catch (SAXException | IOException | ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			handled=true;
		}else if (GEO.P_LAT.getURI().equals(curst.getPredicate().getURI().toString())) {
			lat = curst.getObject().asLiteral().getDouble();
			handled=true;
		} else if (GEO.P_LONG.getURI().equals(curst.getPredicate().getURI().toString())) {
			lon = curst.getObject().asLiteral().getDouble();
			handled=true;
		} else if (GEO.GEORSSPOINT.getURI().equals(curst.getPredicate().getURI().toString())) {
			lat = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[0]);
			lon = Double.valueOf(curst.getObject().asLiteral().getString().split(" ")[1]);
			handled=true;
		}
		return new Tuple<Boolean,String>(handled,type);
	}
	
	
}
