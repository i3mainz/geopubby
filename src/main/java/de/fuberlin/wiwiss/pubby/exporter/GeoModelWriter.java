package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.thrift.wire.RDF_StreamRow;
import org.apache.jena.vocabulary.RDFS;
import org.apache.sis.coverage.grid.GridCoverage;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.gml2.GMLReader;
import org.locationtech.jts.io.kml.KMLReader;
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
	
	public static StyleObject handleStyle(Resource res) {
		StyleObject result=new StyleObject();
		if(res.hasProperty(GEO.STYLE)){
			result.styleId=res.getProperty(GEO.STYLE).getObject().asResource().getURI().toString();
		}
		if(res.hasProperty(GEO.STYLE) && res.getProperty(GEO.STYLE).getObject().asResource().hasProperty(RDFS.label)){
			result.styleName= res.getProperty(GEO.STYLE).getObject().asResource().getProperty(RDFS.label).getLiteral().getString();
		}
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
			result.hatch=res.getProperty(GEO.HATCH).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POPUPSTYLE)){
			result.hatch=res.getProperty(GEO.POPUPSTYLE).getLiteral().getString();
		}
		if(res.hasProperty(GEO.TEXTSTYLE2)){
			result.hatch=res.getProperty(GEO.TEXTSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POINTSTYLE2)){
			result.pointStyle=res.getProperty(GEO.POINTSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POINTIMAGE2)){
			result.pointImage=res.getProperty(GEO.POINTIMAGE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGSTYLE2)){
			result.lineStringStyle=res.getProperty(GEO.LINESTRINGSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGIMAGE2)){
			result.lineStringImage=res.getProperty(GEO.LINESTRINGIMAGE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGIMAGESTYLE2)){
			result.lineStringImageStyle=res.getProperty(GEO.LINESTRINGIMAGESTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POLYGONSTYLE2)){
			result.polygonStyle=res.getProperty(GEO.POLYGONSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POLYGONIMAGE2)){
			result.polygonImage=res.getProperty(GEO.POLYGONIMAGE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.HATCH2)){
			result.hatch=res.getProperty(GEO.HATCH2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POPUPSTYLE2)){
			result.hatch=res.getProperty(GEO.POPUPSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.TEXTSTYLE2)){
			result.hatch=res.getProperty(GEO.TEXTSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POINTSTYLE2)){
			result.pointStyle=res.getProperty(GEO.POINTSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POINTIMAGE2)){
			result.pointImage=res.getProperty(GEO.POINTIMAGE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGSTYLE2)){
			result.lineStringStyle=res.getProperty(GEO.LINESTRINGSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGIMAGE2)){
			result.lineStringImage=res.getProperty(GEO.LINESTRINGIMAGE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.LINESTRINGIMAGESTYLE2)){
			result.lineStringImageStyle=res.getProperty(GEO.LINESTRINGIMAGESTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POLYGONSTYLE2)){
			result.polygonStyle=res.getProperty(GEO.POLYGONSTYLE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.POLYGONIMAGE2)){
			result.polygonImage=res.getProperty(GEO.POLYGONIMAGE2).getLiteral().getString();
		}
		if(res.hasProperty(GEO.HATCH2)){
			result.polygonImage=res.getProperty(GEO.HATCH2).getLiteral().getString();
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
		} else if (GEO.ASKML.getURI().equals(curst.getPredicate().getURI().toString())
				&& this.epsg != null) {
			if (curst.getObject().asLiteral().getString() != null) {
				KMLReader read = new KMLReader();
				try {
					geom = read.read(curst.getObject().asLiteral().getString());
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
