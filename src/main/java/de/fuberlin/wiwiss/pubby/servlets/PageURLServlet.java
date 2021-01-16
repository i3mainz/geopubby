package de.fuberlin.wiwiss.pubby.servlets;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.Dataset;
import de.fuberlin.wiwiss.pubby.HypermediaControls;
import de.fuberlin.wiwiss.pubby.ResourceDescription;
import de.fuberlin.wiwiss.pubby.exporter.style.GeoJSONCSSFormatter;

/**
 * A servlet for serving the HTML page describing a resource.
 * Invokes a Velocity template.
 */
public class PageURLServlet extends BaseServlet {

	public boolean doGet(String relativeURI,
			HttpServletRequest request,
			HttpServletResponse response,
			final Configuration config) throws ServletException, IOException {
		
		final HypermediaControls controller = config.getControls(relativeURI, false);
		if (controller == null) return false;
		ResourceDescription description = controller.getResourceDescription();
		if (description == null) return false;
		
		VelocityHelper template = new VelocityHelper(getServletContext(), response);
		Context context = template.getVelocityContext();
		context.put("project_name", config.getProjectName());
		context.put("project_link", config.getProjectLink());
		context.put("uri", description.getURI());
		List<String> sources=new LinkedList<String>();
		List<String> sourceURLs=new LinkedList<String>();
		for(Dataset ds:config.getDatasets()) {
			sources.add(ds.sparqlEndpoint);
			sourceURLs.add(ds.datasetBase);
		}
		context.put("endpoint", sources);
		context.put("sourceURLs", sourceURLs);
		context.put("server_base", config.getWebApplicationBaseURI());
		context.put("rdf_link", controller.getDataURL());
		context.put("title", description.getTitle());
		context.put("comment", description.getComment());
		context.put("image", description.getImageURL());
		context.put("properties", description.getProperties());
		context.put("showLabels", config.showLabels());
		context.put("geoms",description.getGeoms());
		context.put("epsg",description.getEPSG());
		if(description.getStyle()!=null)
			context.put("style", description.getStyle().toHTML());
		context.put("styleWithHtml",new GeoJSONCSSFormatter().formatForWebView(description.getStyle()).toString());
		addPageMetadata(context, controller, description.getModel());
	
		template.renderXHTML("page.vm");
		return true;
	}

	private static final long serialVersionUID = 3363621132360159793L;
}
