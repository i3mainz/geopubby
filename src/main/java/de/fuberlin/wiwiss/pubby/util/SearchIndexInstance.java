package de.fuberlin.wiwiss.pubby.util;

import com.miguelfonseca.completely.text.analyze.tokenize.WordTokenizer;
import com.miguelfonseca.completely.text.analyze.transform.LowerCaseTransformer;

public class SearchIndexInstance {

	private static AutocompleteEngine<SearchRecord> globalIndex=null; 
	
	private SearchIndexInstance() {
		globalIndex= new AutocompleteEngine.Builder<SearchRecord>()
	            .setIndex(new SearchAdapter())
	            .setAnalyzers(new LowerCaseTransformer(), new WordTokenizer())
	            .build();
	}
	
	public static AutocompleteEngine<SearchRecord> getInstance() {
		if(globalIndex!=null) {
			return globalIndex;
		}
		globalIndex= new AutocompleteEngine.Builder<SearchRecord>()
	            .setIndex(new SearchAdapter())
	            .setAnalyzers(new LowerCaseTransformer(), new WordTokenizer())
	            .build();
		return globalIndex;
	}
	
}
