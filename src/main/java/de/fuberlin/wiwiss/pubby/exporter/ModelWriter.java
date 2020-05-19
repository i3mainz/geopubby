package de.fuberlin.wiwiss.pubby.exporter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;

public interface ModelWriter {
	void write(Model model, HttpServletResponse response) throws IOException;
}