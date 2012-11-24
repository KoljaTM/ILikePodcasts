package de.vanmar.android.ilikepodcasts.library;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;

@EBean
public class RssLoader {

	@RootContext
	Context context;

	private final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

	public void updateFeed(final Feed feed) throws Exception {
		final Feed updatedFeed = readRssFeed(new URL(feed.getUrl()));
		DatabaseManager.getInstance().saveFeed(updatedFeed,
				updatedFeed.getItems());
	}

	public Feed readRssFeed(final URL url) throws XmlPullParserException,
			IOException, ParseException {
		final Feed feed = new Feed();
		feed.setItems(new LinkedList<Item>());
		feed.setUrl(url.toString());
		final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		final XmlPullParser xpp = factory.newPullParser();

		Log.i("RssLoader", "start: " + url);
		// We will get the XML from an input stream
		xpp.setInput(getInputStream(url), "UTF_8");
		Log.i("RssLoader", "gotInput: " + url);

		boolean insideItem = false;

		Item item = null;
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equalsIgnoreCase("item")) {
					insideItem = true;
					item = new Item();
				} else if (xpp.getName().equalsIgnoreCase("title")) {
					if (insideItem) {
						item.setTitle(xpp.nextText());
					} else {
						feed.setTitle(xpp.nextText());
					}
				} else if (xpp.getName().equalsIgnoreCase("description")) {
					if (insideItem) {
						item.setDescription(xpp.nextText());
					} else {
						feed.setDescription(xpp.nextText());
					}
				} else if (xpp.getName().equalsIgnoreCase("enclosure")) {
					if (insideItem) {
						final int attributeCount = xpp.getAttributeCount();
						for (int i = 0; i < attributeCount; i++) {
							final String attributeName = xpp
									.getAttributeName(i);
							final String attributeValue = xpp
									.getAttributeValue(i);
							if ("url".equalsIgnoreCase(attributeName)) {
								item.setMediaUrl(attributeValue);
							} else if ("length".equalsIgnoreCase(attributeName)) {
								item.setMediaLength(Integer
										.parseInt(attributeValue.trim()));
							} else if ("type".equalsIgnoreCase(attributeName)) {
								item.setMediaType(attributeValue);
							}
						}
					}
				} else if (xpp.getName().equalsIgnoreCase("link")) {
					if (insideItem) {
						item.setUrl(xpp.nextText());
					}
				} else if (xpp.getName().equalsIgnoreCase("pubDate")) {
					final Date pubDate = RSS_DATE_FORMAT.parse(xpp.nextText());
					if (insideItem) {
						item.setPublished(pubDate);
					} else {
						feed.setLastUpdate(pubDate);
					}
				}
			} else if (eventType == XmlPullParser.END_TAG
					&& xpp.getName().equalsIgnoreCase("item")) {
				if (item.getMediaUrl() != null) {
					feed.getItems().add(item);
				}
				item = null;
				insideItem = false;
			}
			eventType = xpp.next(); // move to next element
		}
		Log.i("RssLoader", String.format("finished parsing: %s; %s items",
				feed.getTitle(), feed.getItems().size()));

		return feed;
	}

	public InputStream getInputStream(final URL url) {
		try {
			return url.openConnection().getInputStream();
		} catch (final IOException e) {
			return null;
		}
	}

	public void refreshFeeds() throws Exception {
		final List<Feed> feeds = DatabaseManager.getInstance().getAllFeeds();
		for (final Feed feed : feeds) {
			updateFeed(feed);
		}
		refreshContentProvider();
	}

	public void addFeed(final Feed feed) throws SQLException {
		DatabaseManager.getInstance().saveFeed(feed, feed.getItems());
		refreshContentProvider();
	}

	private void refreshContentProvider() {
		context.getContentResolver().notifyChange(
				Uri.parse(context.getString(R.string.feedContentProviderUri)),
				null);
	}
}
