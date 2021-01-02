package de.fuberlin.wiwiss.pubby.servlets;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Analyzes an HttpServletRequest to check for the presence
 * of an ?output=n3 or =ttl or =rdfxml request parameter in
 * the URI. If present, returns a modified HttpServletRequest
 * that has the appropriate MIME type in the Accept: header.
 * This request can then be fed into the rest of our content
 * negotiation based tooling.
 */
public class RequestParamHandler {
	private static final String ATTRIBUTE_NAME_IS_HANDLED =
		"OutputRequestParamHandler.isHandled";
	private final static Map<String,String> mimeTypes = new HashMap<String,String>();
	static {
		mimeTypes.put("rdfxml", "application/rdf+xml");
		mimeTypes.put("rdfjson", "application/rdf+json");
		mimeTypes.put("xml", "application/rdf+xml");

		// Explicitly asking for output=turtle will still return
		// the N3 media type because browsers tend to *display*
		// text/* media types, while they tend to *download*
		// application/* media types. Displaying serves users better,
		// so we report the content as N3 even though it actually is
		// Turtle.
		mimeTypes.put("ttl", "text/rdf+n3;charset=utf-8");
		mimeTypes.put("turtle", "text/rdf+n3;charset=utf-8");
		mimeTypes.put("json", "application/json");
		mimeTypes.put("json", "text/json");
		mimeTypes.put("latlontext", "text/latlon");
		mimeTypes.put("geojson", "application/geojson");
		mimeTypes.put("geojson", "text/geojson");
		mimeTypes.put("esrijson", "text/esrijson");
		mimeTypes.put("geojsonld", "application/geojson-ld");
		mimeTypes.put("topojson", "application/topojson");
		mimeTypes.put("hextuples", "application/hex+x-ndjson");
		mimeTypes.put("geouri", "application/geouri");
		mimeTypes.put("geouri", "text/geouri");
		mimeTypes.put("kml", "application/kml");
		mimeTypes.put("kml", "text/kml");
		mimeTypes.put("covjson", "application/prs.coverage+json");
		mimeTypes.put("rdfexi", "application/rdf+xml+exi");
		mimeTypes.put("exijson", "application/json+exi");
		mimeTypes.put("grass", "text/grass");
		mimeTypes.put("xls", "application/msexcel");
		mimeTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		mimeTypes.put("gml", "application/gml");
		mimeTypes.put("yaml","text/vnd.yaml");
		mimeTypes.put("olc","text/olc");
		mimeTypes.put("graphml","text/graphml");
		mimeTypes.put("tgf","text/tgf");
		mimeTypes.put("gdf","text/gdf");
		mimeTypes.put("geohash","text/geohash");
		mimeTypes.put("x3d", "model/x3d+xml");
		mimeTypes.put("jsonp", "application/javascript");
		mimeTypes.put("gml", "text/gml");
		mimeTypes.put("mapml", "text/mapml");
		mimeTypes.put("cipher", "text/cipher");
		mimeTypes.put("rt", "application/rt");
		mimeTypes.put("csv", "text/csv");
		mimeTypes.put("ewkt", "text/ewkt");
		mimeTypes.put("wkt", "text/wkt");
		mimeTypes.put("twkb", "text/twkb");
		mimeTypes.put("wkb", "text/wkb");
		mimeTypes.put("xyz", "text/xyz");
		mimeTypes.put("svg", "image/svg+xml");
		mimeTypes.put("gpx", "text/gpx");
		mimeTypes.put("osmlink", "text/osmlink");
		mimeTypes.put("googlemapslink", "text/googlemapslink");
		mimeTypes.put("osm", "application/osm+xml");
		mimeTypes.put("trig", "application/trig");
		mimeTypes.put("trix", "application/trix");
		mimeTypes.put("n3", "text/rdf+n3;charset=utf-8");
		mimeTypes.put("nq", "text/nq");
		mimeTypes.put("nt", "text/nt");
		mimeTypes.put("text", "text/plain");
	}
	
	/**
	 * Removes the "output=foobar" part of a URI if present.
	 */
	public static String removeOutputRequestParam(String uri) {
		// Remove the param: output=[a-z0-9]*
		// There are two cases. The param can occur at the end of the URI,
		// this is matched by the first part: [?&]param$
		// Or it can occur elsewhere, then it's matched by param& with a
		// lookbehind that requires [?&] to occur before the pattern.
		return uri.replaceFirst("([?&]output=[a-z0-9]*$)|((?<=[?&])output=[a-z0-9]*&)", "");
	}
	
	private final HttpServletRequest request;
	private final String requestedType;

	public RequestParamHandler(HttpServletRequest request) {
		this.request = request;
		requestedType = identifyRequestedType(request.getParameter("output"));
	}
	
	public boolean isMatchingRequest() {
		if ("true".equals(request.getAttribute(ATTRIBUTE_NAME_IS_HANDLED))) {
			return false;
		}
		return requestedType != null;
	}

	public HttpServletRequest getModifiedRequest() {
		return new WrappedRequest();
	}
	
	private String identifyRequestedType(String parameterValue) {
		if (mimeTypes.containsKey(parameterValue)) {
			return parameterValue;
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")	// The API uses raw types
	private class WrappedRequest extends HttpServletRequestWrapper {
		WrappedRequest() {
			super(request);
			setAttribute(ATTRIBUTE_NAME_IS_HANDLED, "true");
		}
		@Override
		public String getHeader(String name) {
			if ("accept".equals(name.toLowerCase())) {
				return (String) mimeTypes.get(requestedType);
			}
			return super.getHeader(name);
		}
		@Override
		public Enumeration getHeaderNames() {
			final Enumeration realHeaders = super.getHeaderNames();
			return new Enumeration() {
				private String prefetched = null;
				public boolean hasMoreElements() {
					while (prefetched == null && realHeaders.hasMoreElements()) {
						String next = (String) realHeaders.nextElement();
						if (!"accept".equals(next.toLowerCase())) {
							prefetched = next;
						}
					}
					return (prefetched != null);
				}
				public Object nextElement() {
					return prefetched;
				}
			};
		}
		@Override
		public Enumeration getHeaders(String name) {
			if ("accept".equals(name.toLowerCase())) {
				Vector<Object> v = new Vector<Object>();
				v.add(getHeader(name));
				return v.elements();
			}
			return super.getHeaders(name);
		}
	}
}
