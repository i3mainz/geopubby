package de.fuberlin.wiwiss.pubby.negotiation;

import java.util.regex.Pattern;

public class PubbyNegotiator {
	private final static ContentTypeNegotiator pubbyNegotiator;
	private final static ContentTypeNegotiator dataNegotiator;
	
	static {
		pubbyNegotiator = new ContentTypeNegotiator();
		pubbyNegotiator.setDefaultAccept("text/html");
		
		// MSIE (7.0) sends either */*, or */* with a list of other random types,
		// but always without q values, so it doesn't provide any basis for
		// actual negotiation. We will simply send HTML to MSIE, no matter what.
		pubbyNegotiator.addUserAgentOverride(Pattern.compile("MSIE"), null, "text/html");
		
		// Send Turtle to clients that indicate they accept everything.
		// This is specifically so that cURL sees Turtle.
		//
		// NOTE: Rumor has it that some browsers send the Accept header
		//       "*/*" too, but I believe that's only when images or scripts
		//       are requested, not for normal web page requests.   --RC 
		pubbyNegotiator.addUserAgentOverride(null, "*/*", "text/turtle");

		pubbyNegotiator.addVariant("text/html;q=0.81")
				.addAliasMediaType("application/xhtml+xml;q=0.81");
		pubbyNegotiator.addVariant("application/rdf+xml")
				.addAliasMediaType("application/xml;q=0.45")
				.addAliasMediaType("text/xml;q=0.4");
		pubbyNegotiator.addVariant("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;q=0.95");
		pubbyNegotiator.addVariant("application/msexcel;q=0.95");
		pubbyNegotiator.addVariant("application/rdf+json;q=0.99");
		pubbyNegotiator.addVariant("text/rdf+n3;charset=utf-8;q=0.95")
				.addAliasMediaType("text/n3;q=0.5")
				.addAliasMediaType("application/n3;q=0.5");
		pubbyNegotiator.addVariant("application/json;q=0.77")
				.addAliasMediaType("text/json;q=0.77")
				.addAliasMediaType("application/json-ld;q=0.77")
				.addAliasMediaType("text/json-ld;q=0.77");
		pubbyNegotiator.addVariant("application/x-trix;q=0.95")
				.addAliasMediaType("application/trix;q=0.8")
				.addAliasMediaType("text/trix;q=0.5");
		pubbyNegotiator.addVariant("text/cipher;q=0.95");
		pubbyNegotiator.addVariant("text/tgf;q=0.95");
		pubbyNegotiator.addVariant("text/gexf;q=0.95");
		pubbyNegotiator.addVariant("text/gxl;q=0.95");
		pubbyNegotiator.addVariant("text/graphml;q=0.95");
		pubbyNegotiator.addVariant("text/esrijson;q=0.95");
		pubbyNegotiator.addVariant("text/geohash;q=0.95");
		pubbyNegotiator.addVariant("text/grass;q=0.95");
		pubbyNegotiator.addVariant("application/rdf+xml+exi;q=0.95");
		pubbyNegotiator.addVariant("application/json+exi;q=0.95");
		pubbyNegotiator.addVariant("text/xyz;q=0.95");
		pubbyNegotiator.addVariant("application/geojson-ld;q=0.95");
		pubbyNegotiator.addVariant("application/x-trig;q=0.95")
				.addAliasMediaType("application/trig;q=0.8")
				.addAliasMediaType("text/trig;q=0.5");
		pubbyNegotiator.addVariant("application/x-turtle;q=0.95")
				.addAliasMediaType("application/turtle;q=0.8")
				.addAliasMediaType("text/turtle;q=0.5");
		pubbyNegotiator.addVariant("application/geojson;q=0.95")
				.addAliasMediaType("text/geojson;q=0.8");
		pubbyNegotiator.addVariant("text/olc;q=0.95")
		.addAliasMediaType("application/olc;q=0.8");
		pubbyNegotiator.addVariant("application/prs.coverage+json;q=0.95");
		pubbyNegotiator.addVariant("application/topojson;q=0.95")
		.addAliasMediaType("text/topojson;q=0.8");
		pubbyNegotiator.addVariant("application/kml;q=0.95")
				.addAliasMediaType("text/kml;q=0.8");
		pubbyNegotiator.addVariant("application/gml;q=0.95")
				.addAliasMediaType("text/gml;q=0.8");
		pubbyNegotiator.addVariant("application/gml2;q=0.95")
		.addAliasMediaType("text/gml2;q=0.8");
		pubbyNegotiator.addVariant("application/osm+xml;q=0.95");
		pubbyNegotiator.addVariant("text/googlemapslink;q=0.95");
		pubbyNegotiator.addVariant("text/osmlink;q=0.95");
		pubbyNegotiator.addVariant("text/latlon;q=0.95");
		pubbyNegotiator.addVariant("text/ewkt;q=0.95");
		pubbyNegotiator.addVariant("text/wkt;q=0.95");
		pubbyNegotiator.addVariant("text/twkb;q=0.95");
		pubbyNegotiator.addVariant("text/vnd.yaml;q=0.95");
		pubbyNegotiator.addVariant("text/wkb;q=0.95");
		pubbyNegotiator.addVariant("text/gdf;q=0.95");
		pubbyNegotiator.addVariant("text/tlp;q=0.95");
		pubbyNegotiator.addVariant("text/gpx;q=0.95")
			.addAliasMediaType("application/gpx;q=0.8");
		pubbyNegotiator.addVariant("application/javascript;q=0.95");
		pubbyNegotiator.addVariant("model/x3d+xml;q=0.95");
		pubbyNegotiator.addVariant("image/svg+xml;q=0.95");
		pubbyNegotiator.addVariant("application/rt;q=0.95");
		pubbyNegotiator.addVariant("application/hex+x-ndjson;q=0.95");
		pubbyNegotiator.addVariant("text/mapml;q=0.95");
		pubbyNegotiator.addVariant("application/geouri;q=0.95")
				.addAliasMediaType("text/geouri;q=0.8");
		pubbyNegotiator.addVariant("text/plain;q=0.2");

		dataNegotiator = new ContentTypeNegotiator();
		dataNegotiator.addVariant("application/rdf+xml;q=0.99")
				.addAliasMediaType("application/xml;q=0.45")
				.addAliasMediaType("text/xml;q=0.4");
		dataNegotiator.addVariant("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;q=0.95");
		dataNegotiator.addVariant("application/msexcel;q=0.95");
		dataNegotiator.addVariant("application/rdf+json;q=0.99");
		dataNegotiator.addVariant("text/xyz;q=0.95");
		dataNegotiator.addVariant("text/grass;q=0.95");
		dataNegotiator.addVariant("application/rdf+xml+exi;q=0.95");
		dataNegotiator.addVariant("application/json+exi;q=0.95");
		dataNegotiator.addVariant("model/x3d+xml;q=0.95");
		dataNegotiator.addVariant("text/esrijson;q=0.95");
		dataNegotiator.addVariant("text/geohash;q=0.95");
		dataNegotiator.addVariant("text/gdf;q=0.95");
		dataNegotiator.addVariant("text/graphml;q=0.95");
		dataNegotiator.addVariant("text/gexf;q=0.95");
		dataNegotiator.addVariant("text/gxl;q=0.95");
		dataNegotiator.addVariant("text/tlp;q=0.95");
		dataNegotiator.addVariant("application/prs.coverage+json;q=0.95");
		dataNegotiator.addVariant("text/rdf+n3;charset=utf-8")
				.addAliasMediaType("text/n3;q=0.5")
				.addAliasMediaType("application/n3;q=0.5");
		dataNegotiator.addVariant("application/x-trix;q=0.95")
				.addAliasMediaType("application/trix;q=0.8")
				.addAliasMediaType("text/trix;q=0.5");
		dataNegotiator.addVariant("text/olc;q=0.95").addAliasMediaType("application/olc;q=0.8");
		dataNegotiator.addVariant("application/x-trig;q=0.95")
				.addAliasMediaType("application/trig;q=0.8")
				.addAliasMediaType("text/trig;q=0.5");
		dataNegotiator.addVariant("application/json;q=0.76").addAliasMediaType("text/json;q=0.76")
					  .addAliasMediaType("application/json-ld;q=0.76").addAliasMediaType("text/json-ld;q=0.76");
		dataNegotiator.addVariant("application/geojson;q=0.95").addAliasMediaType("text/geojson;q=0.8");
		dataNegotiator.addVariant("text/cipher;q=0.95");
		dataNegotiator.addVariant("text/tgf;q=0.95");
		dataNegotiator.addVariant("text/twkb;q=0.95");
		dataNegotiator.addVariant("application/topojson;q=0.95").addAliasMediaType("text/topojson;q=0.8");
		dataNegotiator.addVariant("application/kml;q=0.95").addAliasMediaType("text/kml;q=0.8");
		dataNegotiator.addVariant("text/csv;q=0.95").addAliasMediaType("application/csv;q=0.8");
		dataNegotiator.addVariant("text/gpx;q=0.95").addAliasMediaType("application/gpx;q=0.8");
		dataNegotiator.addVariant("application/geojson-ld;q=0.95");
		dataNegotiator.addVariant("image/svg+xml;q=0.95");
		dataNegotiator.addVariant("application/hex+x-ndjson;q=0.95");
		dataNegotiator.addVariant("text/latlon;q=0.95");
		dataNegotiator.addVariant("text/googlemapslink;q=0.95");
		dataNegotiator.addVariant("text/osmlink;q=0.95");
		dataNegotiator.addVariant("application/gml2;q=0.95").addAliasMediaType("text/gml2;q=0.8");
		dataNegotiator.addVariant("application/gml;q=0.95").addAliasMediaType("text/gml;q=0.8");
		dataNegotiator.addVariant("text/mapml;q=0.95");
		dataNegotiator.addVariant("application/javascript;q=0.95");
		dataNegotiator.addVariant("text/vnd.yaml;q=0.95");
		dataNegotiator.addVariant("text/wkt;q=0.95");
		dataNegotiator.addVariant("text/ewkt;q=0.95");
		dataNegotiator.addVariant("text/wkb;q=0.95");
		dataNegotiator.addVariant("application/osm+xml;q=0.95");
		dataNegotiator.addVariant("application/rt;q=0.95");
		dataNegotiator.addVariant("application/geouri;q=0.95").addAliasMediaType("text/geouri;q=0.8");
		dataNegotiator.addVariant("application/x-turtle;q=0.99").addAliasMediaType("application/turtle;q=0.8").addAliasMediaType("text/turtle;q=0.5");
		dataNegotiator.addVariant("text/plain;q=0.2");
	}
	
	public static ContentTypeNegotiator getPubbyNegotiator() {
		return pubbyNegotiator;
	}
	
	public static ContentTypeNegotiator getDataNegotiator() {
		return dataNegotiator;
	}
}
