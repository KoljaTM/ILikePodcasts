package de.vanmar.android.ilikepodcasts.library.rss;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.net.Uri;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;

@EBean
public class RssLoader {

	@RootContext
	Context context;

	@Bean
	DatabaseManager dbManager;

	public void updateFeed(final Feed feed) throws Exception {
		final Feed updatedFeed = readRssFeed(new URL(feed.getUrl()));
		dbManager.saveFeed(updatedFeed, updatedFeed.getItems());
	}

	public Feed readRssFeed(final URL url) throws XmlPullParserException,
			IOException, ParseException {
		return new RssHandler(url).getFeed();
	}

	public void addFeed(final Feed feed) throws SQLException {
		dbManager.saveFeed(feed, feed.getItems());
		refreshContentProvider();
	}

	private void refreshContentProvider() {
		context.getContentResolver().notifyChange(
				Uri.parse(context.getString(R.string.feedContentProviderUri)),
				null);
	}
}
