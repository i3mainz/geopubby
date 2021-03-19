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

public class GeoBrowserServlet extends BaseServlet {

	public boolean doGet(String relativeURI,
			HttpServletRequest request,
			HttpServletResponse response,
			final Configuration config) throws ServletException, IOException {
				
		VelocityHelper template = new VelocityHelper(getServletContext(), response);
		Context context = template.getVelocityContext();
		context.put("project_name", config.getProjectName());
		context.put("project_link", config.getProjectLink());
		List<String> sources=new LinkedList<String>();
		List<String> sourceURLs=new LinkedList<String>();
		for(Dataset ds:config.getDatasets()) {
			sources.add(ds.sparqlEndpoint);
			sourceURLs.add(ds.datasetBase);
		}
		context.put("endpoint", sources);
		context.put("labelprops", config.getLabelProperties());
		context.put("typeprops", config.getTypeProperties());
		context.put("geoprops", config.getGeoProperties());
		context.put("crsprops", config.getCrsProperties());
		context.put("sourceURLs", sourceURLs);
		context.put("server_base", config.getWebApplicationBaseURI());
		template.renderXHTML("geobrowser.vm");
		return true;
	}

}
