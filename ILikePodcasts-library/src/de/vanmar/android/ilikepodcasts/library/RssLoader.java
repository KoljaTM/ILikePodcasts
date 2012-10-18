package de.vanmar.android.ilikepodcasts.library;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.googlecode.androidannotations.annotations.EBean;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;

@EBean
public class RssLoader {

	private static final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

	public void loadRss(final Feed feed) throws Exception {
		final URL url = new URL(feed.getUrl());
		final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		final XmlPullParser xpp = factory.newPullParser();

		// We will get the XML from an input stream
		xpp.setInput(getInputStream(url), "UTF_8");

		boolean insideItem = false;
		final Map<String, Item> items = new HashMap<String, Item>();
		for (final Item item : feed.getItems()) {
			items.put(item.getUrl(), item);
		}

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
								item.setMediaLength(Long
										.parseLong(attributeValue));
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
				items.put(item.getUrl(), item);
				item = null;
				insideItem = false;
			}
			eventType = xpp.next(); // move to next element
		}
		DatabaseManager.getInstance().saveFeed(feed, items.values());
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
			loadRss(feed);
		}
	}
}
