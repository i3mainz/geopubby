package de.fuberlin.wiwiss.pubby.util;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.Resource;

import com.miguelfonseca.completely.data.Indexable;

/**
 * Implements a search record which may be indexed in the search index.
 */
public class SearchRecord implements Indexable{
	    private final String label;
	    
	    private final Resource res;

	    public SearchRecord(String label,Resource res)
	    {
	        this.label = label;
	        this.res=res;
	    }

	    @Override
	    public List<String> getFields()
	    {
	        return Arrays.asList(label);
	    }

	    public String getLabel()
	    {
	        return label;
	    }

	    public Resource getResource()
	    {
	        return res;
	    }
	
	}
