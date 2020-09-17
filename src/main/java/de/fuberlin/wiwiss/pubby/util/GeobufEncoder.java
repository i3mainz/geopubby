package de.fuberlin.wiwiss.pubby.util;

import java.util.Map;
import java.util.TreeMap;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import com.google.protobuf.Message;

public class GeobufEncoder {

	//https://github.com/digoal/geobuf/blob/master/encode.js

	static Map<String,Integer> geomMap;
	
	static {
		geomMap.put("Point", 0);
		geomMap.put("MultiPoint", 1);
		geomMap.put("LineString", 2);
		geomMap.put("MultiLineString", 3);
		geomMap.put("Polygon", 4);
		geomMap.put("MultiPolygon", 5);
		geomMap.put("GeometryColection", 6);
		
	}
	
	Integer e=1;
	
	Double maxPrecision=1e6;
	/*
	public static String encode(Geometry geom, Message pbf) {
		Map<String,String> key=new TreeMap<>();
		Integer keysNum=0;
		Integer dimensions=0;
			
	}*/
	

	/*public void convertGeometry(Feature feat) {
		if(geom.getGeometryType().equalsIgnoreCase("FeatureCollect"))
	}
	
	public void convertGeometry(Geometry geom) {
		if(geom.getGeometryType().equalsIgnoreCase("Point")) {
			
		}
	}
	
	public saveKey()*/
	
	
	public Coordinate convertCoordinate(Coordinate coord) {
		Integer length=0;
		Double[] point= {coord.getX(),coord.getY(),coord.getZ(),coord.getM()}; 
		if(Double.isNaN(coord.getX()))
			length++;
		if(Double.isNaN(coord.getY()))
			length++;
		if(Double.isNaN(coord.getZ()))
			length++;
		if(Double.isNaN(coord.getM()))
			length++;
		Double dim = Math.max(2., length);
		Coordinate result=new Coordinate();
	    // find max precision
	    for (int i = 0; i < length; i++) {
	    	if(!Double.isNaN(point[i])) {
	    		while (Math.round(point[i] * e) / e != point[i] && e < maxPrecision) e *= 10;
	    	} 
	    }
	    if(Double.isNaN(coord.getX()))
			result.x=coord.getX();
		if(Double.isNaN(coord.getY()))
			result.y=coord.getY();
		if(Double.isNaN(coord.getZ()))
			result.z=coord.getZ();
		if(Double.isNaN(coord.getM()))
			result.setM(coord.getM());
		return result;
	}
	
	/*
	public String writeGeometry(Geometry geom, Message pbf) {
		geom.
	}
	*/
	
}
