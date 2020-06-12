package de.fuberlin.wiwiss.pubby.servlets;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.Dataset;
import de.fuberlin.wiwiss.pubby.util.SearchRecord;

public class SearchServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected boolean doGet(String relativeURI, HttpServletRequest request, HttpServletResponse response,
			Configuration config) throws IOException, ServletException {
		System.out.println("Search Servlet");
		String query=null;
		if(request.getParameter("search")!=null) {
			query=request.getParameter("search").toString();
		}
		System.out.println("Query: "+query);
		Object limitt=request.getParameter("limit");
		Integer limit=null;
		if(limitt!=null) {
			try {
				limit=Integer.valueOf(limitt.toString());
			}catch(Exception e) {
			
			}
		}
		System.out.println("Limit: "+limit);
		if(query!=null) {
		List<SearchRecord> res=new LinkedList<SearchRecord>();
		if(limit!=null) {
			for(de.fuberlin.wiwiss.pubby.util.AutocompleteEngine<SearchRecord> rec:config.getDataSource().getLabelIndex()) {
				res.addAll(rec.search(query,limit));
			}		
		}else {
			for(de.fuberlin.wiwiss.pubby.util.AutocompleteEngine<SearchRecord> rec:config.getDataSource().getLabelIndex()) {
				res.addAll(rec.search(query));
			}
			System.out.println(config.getDataSource());
			System.out.println(config.getDataSource().getLabelIndex());
		}
		System.out.println("Results: "+res.size());
		JSONArray result=new JSONArray();
		
		for(SearchRecord rec:res) {
			JSONObject instance=new JSONObject();
			instance.put("label",rec.getLabel());
			String val=rec.getResource().getURI();
			for(Dataset ds:config.getDatasets()) {	
				System.out.println(ds.datasetBase+" - "+config.getWebApplicationBaseURI());
				val=val.replace(ds.datasetBase,config.getWebApplicationBaseURI());
			}
			instance.put("value",val);
			instance.put("id",rec.getResource().getURI());
			result.put(instance);			
		}
		response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
	    response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
	    response.getWriter().write(result.toString(2));
		}
		return true;
	}

}
