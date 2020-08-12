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

import de.fuberlin.wiwiss.pubby.exporter.CSVWriter;
import de.fuberlin.wiwiss.pubby.exporter.GMLWriter;
import de.fuberlin.wiwiss.pubby.exporter.GPXWriter;
import de.fuberlin.wiwiss.pubby.exporter.GeoJSONWriterr;
import de.fuberlin.wiwiss.pubby.exporter.GeoURIWriter;
import de.fuberlin.wiwiss.pubby.exporter.KMLWriter;
import de.fuberlin.wiwiss.pubby.exporter.ModelWriter;
import de.fuberlin.wiwiss.pubby.exporter.OSMLinkWriter;
import de.fuberlin.wiwiss.pubby.exporter.OSMWriter;
import de.fuberlin.wiwiss.pubby.exporter.SVGWriter;
import de.fuberlin.wiwiss.pubby.exporter.WKTWriter;
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
		response.setContentType(bestMatch.getMediaType());
		getWriter(bestMatch.getMediaType()).write(model, response);
		response.getOutputStream().flush();
    }
	
	private ModelWriter getWriter(String mediaType) {
		if ("application/rdf+xml".equals(mediaType)) {
			return new RDFXMLWriter();
		}
		if ("application/json".equals(mediaType)) {
			return new JSONWriter();
		}
		if ("application/geojson".equals(mediaType)) {
			return new GeoJSONWriterr();
		}
		if ("image/svg+xml".equals(mediaType)) {
			return new SVGWriter();
		}
		if("text/csv".equals(mediaType)){
			return new CSVWriter();
		}
		if("text/gpx".equals(mediaType)){
			return new GPXWriter();
		}
		if ("application/osm+xml".equals(mediaType)) {
			return new OSMWriter();
		}
		if ("text/osmlink".equals(mediaType)) {
			return new OSMLinkWriter();
		}
		if ("application/geouri".equals(mediaType)) {
			return new GeoURIWriter();
		}
		if ("application/gml".equals(mediaType)) {
			return new GMLWriter();
		}
		if ("application/kml".equals(mediaType)) {
			return new KMLWriter();
		}
		if ("application/trig".equals(mediaType)) {
			return new TrigWriter();
		}
		if ("text/wkt".equals(mediaType)) {
			return new WKTWriter();
		}
		if ("application/trix".equals(mediaType)) {
			return new TrixWriter();
		}
		if ("application/x-turtle".equals(mediaType)) {
			return new TurtleWriter();
		}
		if ("text/rdf+n3;charset=utf-8".equals(mediaType)) {
			return new TurtleWriter();
		}
		return new NTriplesWriter();
	}
	
	
	private class NTriplesWriter extends ModelWriter {
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
			model.getWriter("N-TRIPLES").write(model, response.getOutputStream(), null);
			return null;
		}
	}
	
	private class JSONWriter extends ModelWriter {
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
			model.getWriter("JSON-LD").write(model, response.getOutputStream(), null);
			return null;
		}
	}
	
	private class TrigWriter extends ModelWriter {
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
			model.getWriter("TriG").write(model, response.getOutputStream(), null);
			return null;
		}
	}
	
	private class TrixWriter extends ModelWriter {
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
			model.getWriter("TriX").write(model, response.getOutputStream(), null);
			return null;
		}
	}
	
	private class TurtleWriter extends ModelWriter {
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
			model.getWriter("TURTLE").write(model, response.getOutputStream(), null);
			return null;
		}
	}

	private class RDFXMLWriter extends ModelWriter {
		public ExtendedIterator<Resource> write(Model model, HttpServletResponse response) throws IOException {
			RDFWriter writer = model.getWriter("RDF/XML-ABBREV");
			writer.setProperty("showXmlDeclaration", "true");
			// From Joseki -- workaround for the j.cook.up bug.
			writer.setProperty("blockRules", "propertyAttr");
			writer.write(model, 
					new OutputStreamWriter(response.getOutputStream(), "utf-8"), null);
			return null;
		}
	}
}
