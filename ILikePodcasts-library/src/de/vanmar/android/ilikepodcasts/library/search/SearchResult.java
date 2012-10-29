package de.vanmar.android.ilikepodcasts.library.search;

import java.util.List;

public class SearchResult {

	private int resultCount;
	private List<SearchResultItem> results;

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(final int resultCount) {
		this.resultCount = resultCount;
	}

	public List<SearchResultItem> getResults() {
		return results;
	}

	public void setResults(final List<SearchResultItem> results) {
		this.results = results;
	}
}
