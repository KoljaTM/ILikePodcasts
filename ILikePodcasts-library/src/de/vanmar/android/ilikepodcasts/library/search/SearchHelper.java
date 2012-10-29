package de.vanmar.android.ilikepodcasts.library.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class SearchHelper {

	public List<SearchResultItem> searchPodcasts(final Context context,
			final String searchText) {

		try {
			final URL url = new URL("https://itunes.apple.com/search?term="
					+ URLEncoder.encode(searchText.trim(), "UTF-8")
					+ "&media=podcast");
			final InputStream openStream = url.openStream();
			final InputStreamReader inputStreamReader = new InputStreamReader(
					openStream);
			final SearchResult searchResult = new Gson().fromJson(
					inputStreamReader, SearchResult.class);
			return searchResult.getResults();
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new LinkedList<SearchResultItem>();
	}
}
