package de.fuberlin.wiwiss.pubby;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.locationtech.jts.io.WKBWriter;

import de.fuberlin.wiwiss.pubby.exporter.CSVWriter;
import de.fuberlin.wiwiss.pubby.exporter.CoverageJSONWriter;
import de.fuberlin.wiwiss.pubby.exporter.GMLWriter;
import de.fuberlin.wiwiss.pubby.exporter.GPXWriter;
import de.fuberlin.wiwiss.pubby.exporter.GeoJSONWriterr;
import de.fuberlin.wiwiss.pubby.exporter.GeoURIWriter;
import de.fuberlin.wiwiss.pubby.exporter.GoogleMapsLinkWriter;
import de.fuberlin.wiwiss.pubby.exporter.HexTuplesWriter;
import de.fuberlin.wiwiss.pubby.exporter.JSONPWriter;
import de.fuberlin.wiwiss.pubby.exporter.KMLWriter;
import de.fuberlin.wiwiss.pubby.exporter.LDWriter;
import de.fuberlin.wiwiss.pubby.exporter.LatLonTextWriter;
import de.fuberlin.wiwiss.pubby.exporter.MapMLWriter;
import de.fuberlin.wiwiss.pubby.exporter.ModelWriter;
import de.fuberlin.wiwiss.pubby.exporter.OSMLinkWriter;
import de.fuberlin.wiwiss.pubby.exporter.OSMWriter;
import de.fuberlin.wiwiss.pubby.exporter.SVGWriter;
import de.fuberlin.wiwiss.pubby.exporter.TopoJSONWriter;
import de.fuberlin.wiwiss.pubby.exporter.WKBWriterr;
import de.fuberlin.wiwiss.pubby.exporter.WKTWriter;
import de.fuberlin.wiwiss.pubby.exporter.X3DWriter;
import de.fuberlin.wiwiss.pubby.exporter.XLSWriter;
import de.fuberlin.wiwiss.pubby.exporter.XLSXWriter;
import de.fuberlin.wiwiss.pubby.exporter.XYZASCIIWriter;
import de.fuberlin.wiwiss.pubby.exporter.YAMLWriter;
import de.fuberlin.wiwiss.pubby.negotiation.ContentTypeNegotiator;
import de.fuberlin.wiwiss.pubby.negotiation.MediaRangeSpec;
import de.fuberlin.wiwiss.pubby.negotiation.PubbyNegotiator;
import de.fuberlin.wiwiss.pubby.servlets.RequestParamHandler;

/**
 * Calls into Joseki to send a Jena model over HTTP. This gives us
 * content negotiation and all the other tricks supported by Joseki
 * for free. This has to be in the Joseki package because some
 * required methods are not visible.
 */
public class ModelResponse {
	private final Model model;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	
	public ModelResponse(Model model, HttpServletRequest request, 
			HttpServletResponse response) {

		// Handle ?output=format request parameter
		RequestParamHandler handler = new RequestParamHandler(request);
		if (handler.isMatchingRequest()) {
			request = handler.getModifiedRequest();
		}
		
		this.model = model;
		this.request = request;
		this.response = response;
	}
	
	public void serve() {
		// Error hendling is still quite a mess here.
		try {
			doResponseModel();
		} catch (IOException ioEx) {
			throw new RuntimeException(ioEx);
		} catch (JenaException jEx) {
			try {
				response.sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"JenaException: " + jEx.getMessage());
				jEx.printStackTrace();
            } catch (IOException e) {
            	throw new RuntimeException(e);
            }
		}
	}
	
	private void doResponseModel() throws IOException {
		response.addHeader("Vary", "Accept");
		ContentTypeNegotiator negotiator = PubbyNegotiator.getDataNegotiator();
		MediaRangeSpec bestMatch = negotiator.getBestMatch(
				request.getHeader("Accept"), request.getHeader("User-Agent"));
		if (bestMatch == null) {
			response.setStatus(406);
			response.setContentType("text/plain");
			ServletOutputStream out = response.getOutputStream();
			out.println("406 Not Acceptable: The requested data format is not supported.");
			out.println("Supported formats are RDF/XML, JSON-LD, GeoJSON, GeoURI, TriX, WKT, GML, KML, SVG, GPX, TriG, Turtle, N3, and N-Triples.");
			return;
		}
		String crs=null;
		if(request.getParameter("crs")!=null) {
			crs=request.getParameter("crs").toString();
		}
		response.setContentType(bestMatch.getMediaType());
		getWriter(bestMatch.getMediaType(),crs).write(model, response);
		response.getOutputStream().flush();
    }
	
	private ModelWriter getWriter(String mediaType, String crs) {
		if ("application/rdf+xml".equals(mediaType)) {
			return new LDWriter(crs,"RDF/XML");
		}
		if ("application/rdf+json".equals(mediaType)) {
			return new LDWriter(crs,"RDF/JSON");
		}
		if ("application/json".equals(mediaType)) {
			return new LDWriter(crs,"JSON-LD");
		}
		if ("application/geojson".equals(mediaType)) {
			return new GeoJSONWriterr(crs);
		}
		if ("application/topojson".equals(mediaType)) {
			return new TopoJSONWriter(crs);
		}
		if ("image/svg+xml".equals(mediaType)) {
			return new SVGWriter(crs);
		}
		if ("text/latlon".equals(mediaType)) {
			return new LatLonTextWriter(crs);
		}
		if("text/csv".equals(mediaType)){
			return new CSVWriter(crs);
		}
		if("text/gpx".equals(mediaType)){
			return new GPXWriter(crs);
		}
		if ("application/osm+xml".equals(mediaType)) {
			return new OSMWriter(crs);
		}
		if ("text/osmlink".equals(mediaType)) {
			return new OSMLinkWriter(crs);
		}
		if ("text/googlemapslink".equals(mediaType)) {
			return new GoogleMapsLinkWriter(crs);
		}
		if ("application/geouri".equals(mediaType)) {
			return new GeoURIWriter(crs);
		}
		if ("application/gml".equals(mediaType)) {
			return new GMLWriter(crs);
		}
		if ("application/prs.coverage+json".equals(mediaType)) {
			return new CoverageJSONWriter(crs);
		}
		if ("application/kml".equals(mediaType)) {
			return new KMLWriter(crs);
		}
		if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mediaType)) {
			return new XLSXWriter(crs);
		}
		if ("application/msexcel".equals(mediaType)) {
			return new XLSWriter(crs);
		}
		if ("application/trig".equals(mediaType)) {
			return new LDWriter(crs,"TriG");
		}
		if ("text/vnd.yaml".equals(mediaType)) {
			return new YAMLWriter(crs);
		}
		if ("application/javascript".equals(mediaType)) {
			return new JSONPWriter(crs);
		}	
		if("model/x3d+xml".equals(mediaType)) {
			return new X3DWriter(crs);
		}
		if ("text/wkt".equals(mediaType)) {
			return new WKTWriter(crs);
		}
		if ("text/wkb".equals(mediaType)) {
			return new WKBWriterr(crs);
		}
		if ("text/mapml".equals(mediaType)) {
			return new MapMLWriter(crs);
		}
		if ("text/xyz".equals(mediaType)) {
			return new XYZASCIIWriter(crs);
		}
		if ("application/trix".equals(mediaType)) {
			return new LDWriter(crs,"TriX");
		}
		if ("application/x-turtle".equals(mediaType)) {
			return new LDWriter(crs,"TURTLE");
		}
		if ("text/rdf+n3;charset=utf-8".equals(mediaType)) {
			return new LDWriter(crs,"N3");
		}
		if("application/rt".equals(mediaType)) {
			return new LDWriter(crs,"RDFTHRIFT");
		}
		if("text/nq".equals(mediaType)) {
			return new LDWriter(crs,"NQUADS");
		}
		if("application/hex+x-ndjson".equals(mediaType)) {
			return new HexTuplesWriter(crs);
		}
		return new LDWriter(crs,"N-TRIPLES");
	}
	
}
