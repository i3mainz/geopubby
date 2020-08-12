package de.fuberlin.wiwiss.pubby.sources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;

import com.miguelfonseca.completely.text.analyze.tokenize.WordTokenizer;
import com.miguelfonseca.completely.text.analyze.transform.LowerCaseTransformer;

import de.fuberlin.wiwiss.pubby.ModelUtil;
import de.fuberlin.wiwiss.pubby.util.AutocompleteEngine;
import de.fuberlin.wiwiss.pubby.util.SearchAdapter;
import de.fuberlin.wiwiss.pubby.util.SearchIndexInstance;
import de.fuberlin.wiwiss.pubby.util.SearchRecord;

/**
 * A data source backed by a Jena model.
 */
public class ModelDataSource implements DataSource {
	private Model model;
	
	private AutocompleteEngine<SearchRecord> engine;

	public ModelDataSource(Model model) {
		this.model = model;
	}

	@Override
	public boolean canDescribe(String absoluteIRI) {
		return true;
	}

	@Override
	public Model describeResource(String resourceURI) {
		Resource r = ResourceFactory.createResource(resourceURI);
		if (model.contains(r, null, (RDFNode) null) || model.contains(null, null, r)) {
			return model;
		}
		return ModelUtil.EMPTY_MODEL;
	}
	
	@Override
	public Map<Property, Integer> getHighIndegreeProperties(String resourceURI) {
		return null;
	}

	@Override
	public Map<Property, Integer> getHighOutdegreeProperties(String resourceURI) {
		return null;
	}

	@Override
	public Model listPropertyValues(String resourceURI, Property property,
			boolean isInverse) {
		return model;
	}
	
	@Override
	public List<Resource> getIndex() {
		List<Resource> result = new ArrayList<Resource>();
		ResIterator subjects = model.listSubjects();
		while (subjects.hasNext() && result.size() < DataSource.MAX_INDEX_SIZE) {
			Resource r = subjects.next();
			if (r.isAnon()) continue;
			result.add(r);
		}
		NodeIterator objects = model.listObjects();
		while (objects.hasNext() && result.size() < DataSource.MAX_INDEX_SIZE) {
			RDFNode o = objects.next();
			if (!o.isURIResource()) continue;
			result.add(o.asResource());
		}
		return result;
	}

	@Override
	public de.fuberlin.wiwiss.pubby.util.AutocompleteEngine<SearchRecord> getLabelIndex() {
		System.out.println("ModelDataSource: Get Label Index!!!!");
		if(engine==null) {
			engine=SearchIndexInstance.getInstance();
			System.out.println("Building Label Index....");
			List<Resource> result = new ArrayList<Resource>();
			ResIterator subjects = model.listSubjects();
			int i=0;
			while (subjects.hasNext() && result.size() < DataSource.MAX_INDEX_SIZE) {
				Resource r = subjects.next();
				if (r.isAnon()) continue;
				StmtIterator st = r.listProperties(RDFS.label);
				while(st.hasNext()) {
					Literal lit=st.next().getObject().asLiteral();
					String label=lit.getString();
					engine.add(new SearchRecord(label,r));
				}
				i++;
			}
			System.out.println("Added "+i+" Labels to Index");
		}
		return engine;
	}

	@Override
	public Model describeResource(String absoluteIRI, String language) {
		// TODO Auto-generated method stub
		return null;
	}
}
