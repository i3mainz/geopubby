package de.fuberlin.wiwiss.pubby.servlets;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.HypermediaControls;
import de.fuberlin.wiwiss.pubby.ResourceDescription;
import de.fuberlin.wiwiss.pubby.sources.DataSource;
import de.fuberlin.wiwiss.pubby.sources.IndexDataSource;
import de.fuberlin.wiwiss.pubby.sources.MergeDataSource;
import de.fuberlin.wiwiss.pubby.sources.RemoteSPARQLDataSource;
import de.fuberlin.wiwiss.pubby.sources.RewrittenDataSource;

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
		if(config.getDataSource() instanceof RemoteSPARQLDataSource) {
			context.put("endpoint", ((RemoteSPARQLDataSource)config.getDataSource()).endpointURL);
		}else if(config.getDataSource() instanceof MergeDataSource) {
			String sources="";
			for(DataSource source:((MergeDataSource)config.getDataSource()).sources) {
				if(source instanceof RemoteSPARQLDataSource) {
					sources+=((RemoteSPARQLDataSource) source).endpointURL+";";
				}else if(source instanceof RewrittenDataSource && ((RewrittenDataSource) source).original instanceof RemoteSPARQLDataSource) {
					sources+=((RemoteSPARQLDataSource)((RewrittenDataSource) source).original).endpointURL+";";
				}else if(source instanceof IndexDataSource && ((IndexDataSource) source).wrapped instanceof RemoteSPARQLDataSource) {
					sources+=((RemoteSPARQLDataSource)((IndexDataSource) source).wrapped).endpointURL+";";
				}
			}
			if(!sources.isEmpty())
				context.put("endpoint", sources.substring(0,sources.length()-1));
			else {
				context.put("endpoint", "#");
			}
		}else if(config.getDataSource() instanceof RewrittenDataSource && ((RewrittenDataSource) config.getDataSource()).original instanceof RemoteSPARQLDataSource){
			context.put("endpoint", ((RemoteSPARQLDataSource)((RewrittenDataSource) config.getDataSource()).original).endpointURL);
		}else if(config.getDataSource() instanceof IndexDataSource && ((IndexDataSource) config.getDataSource()).wrapped instanceof RemoteSPARQLDataSource){
			context.put("endpoint", ((RemoteSPARQLDataSource)((IndexDataSource) config.getDataSource()).wrapped).endpointURL);
		}else {
			context.put("endpoint", "#");
		}
		context.put("server_base", config.getWebApplicationBaseURI());
		context.put("rdf_link", controller.getDataURL());
		context.put("title", description.getTitle());
		context.put("comment", description.getComment());
		context.put("image", description.getImageURL());
		context.put("properties", description.getProperties());
		context.put("showLabels", config.showLabels());
		context.put("geoms",description.getGeoms());
		context.put("epsg",description.getEPSG());
		addPageMetadata(context, controller, description.getModel());
	
		template.renderXHTML("page.vm");
		return true;
	}

	private static final long serialVersionUID = 3363621132360159793L;
}
