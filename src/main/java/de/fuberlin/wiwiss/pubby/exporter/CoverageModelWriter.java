package de.fuberlin.wiwiss.pubby.exporter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.sis.coverage.grid.GridCoverage;

import de.fuberlin.wiwiss.pubby.util.Tuple;
import de.fuberlin.wiwiss.pubby.vocab.COV;

public class CoverageModelWriter extends ModelWriter {

	GridCoverage cov;
	
	public Tuple<Boolean,String> handleCoverage(Statement curst,Resource ind,Model model) {
		boolean handled=false;
		String type="coverage";
		if(COV.ASCOVERAGEJSON.getURI().equals(curst.getPredicate().getURI().toString())) {
			handled=true;
		}else if(COV.ASGMLCOV.getURI().equals(curst.getPredicate().getURI().toString())) {
			handled=true;
		}else if(COV.ASRASTWKB.getURI().equals(curst.getPredicate().getURI().toString())) {
			handled=true;
		}else if(COV.ASHEXRASTWKB.getURI().equals(curst.getPredicate().getURI().toString())) {
			handled=true;
		}
		return new Tuple<Boolean,String>(handled,type);
	}
	
}
