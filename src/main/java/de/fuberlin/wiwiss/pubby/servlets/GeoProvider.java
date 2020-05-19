package de.fuberlin.wiwiss.pubby.servlets;

import org.apache.jena.rdf.model.Resource;

public interface GeoProvider {
	 public Resource get(String url);
}
