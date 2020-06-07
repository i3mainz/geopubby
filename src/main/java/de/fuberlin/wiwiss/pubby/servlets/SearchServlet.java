package de.fuberlin.wiwiss.pubby.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import de.fuberlin.wiwiss.pubby.Configuration;
import de.fuberlin.wiwiss.pubby.util.SearchRecord;

public class SearchServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected boolean doGet(String relativeURI, HttpServletRequest request, HttpServletResponse response,
			Configuration config) throws IOException, ServletException {
		String query=request.getAttribute("search").toString();
		Object limitt=request.getAttribute("limit");
		Integer limit=null;
		if(limitt!=null) {
			try {
				limit=Integer.valueOf(limitt.toString());
			}catch(Exception e) {
			
			}
		}
		List<SearchRecord> res;
		if(limit!=null) {
			res=config.getDataSource().getLabelIndex().search(query,limit);			
		}else {
			res=config.getDataSource().getLabelIndex().search(query);
		}
		JSONObject result=new JSONObject();
		for(SearchRecord rec:res) {
			result.put(rec.getLabel(), rec.getResource().getURI());			
		}
		response.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
	    response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
	    response.getWriter().write(result.toString(2));
		return true;
	}

}
