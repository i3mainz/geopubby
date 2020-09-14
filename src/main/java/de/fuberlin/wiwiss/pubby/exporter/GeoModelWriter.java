package de.fuberlin.wiwiss.pubby.exporter;

public class GeoModelWriter extends ModelWriter {

	String epsg=null;
	
	String sourceCRS="EPSG:4326";
	
	public GeoModelWriter(String epsg) {
		this.epsg=epsg;
	}
	
	public GeoModelWriter() {
		super();
	}
	
	
}
