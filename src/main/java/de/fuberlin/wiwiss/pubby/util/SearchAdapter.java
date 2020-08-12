package de.fuberlin.wiwiss.pubby.util;

import java.util.Collection;

import com.miguelfonseca.completely.IndexAdapter;
import com.miguelfonseca.completely.data.ScoredObject;
import com.miguelfonseca.completely.text.index.FuzzyIndex;
import com.miguelfonseca.completely.text.index.PatriciaTrie;
import com.miguelfonseca.completely.text.match.EditDistanceAutomaton;

/**
 * Search adapter defining a String distance metric between search records.
 */
public class SearchAdapter implements IndexAdapter<SearchRecord> {
	
	private FuzzyIndex<SearchRecord> index = new PatriciaTrie<>();

	@Override
	public Collection<ScoredObject<SearchRecord>> get(String token) {
		 // Set threshold according to the token length
        double threshold = Math.log(Math.max(token.length() - 1, 1));
        return index.getAny(new EditDistanceAutomaton(token, threshold));
	}

	@Override
	public boolean put(String token, SearchRecord value) {
		return index.put(token, value);
	}

	@Override
	public boolean remove(SearchRecord value) {
		return index.remove(value);
	}

}
