package de.vanmar.android.ilikepodcasts.library.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;

public class RssHandler {

	private final DateFormat RSS_DATE_FORMAT = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

	private final URL url;

	private final Feed feed;
	private Item item;

	private boolean insideItem;

	public RssHandler(final URL url) {
		this.url = url;
		feed = new Feed();
		feed.setItems(new LinkedList<Item>());
		feed.setUrl(url.toString());
	}

	public Feed getFeed() throws XmlPullParserException, IOException,
			ParseException {
		final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		final XmlPullParser xpp = factory.newPullParser();

		// We will get the XML from an input stream
		xpp.setInput(getInputStream(url), "UTF-8");

		insideItem = false;

		Item item = null;
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			final String tag = xpp.getName();
			if (eventType == XmlPullParser.START_TAG) {
				item = handleTagStart(tag, xpp);
			} else if (eventType == XmlPullParser.END_TAG
					&& tag.equalsIgnoreCase("item")) {
				if (item.getMediaUrl() != null) {
					feed.getItems().add(item);
				}
				item = null;
				insideItem = false;
			}
			eventType = xpp.next(); // move to next element
		}
		return feed;
	}

	private InputStream getInputStream(final URL url) {
		try {
			return url.openConnection().getInputStream();
		} catch (final IOException e) {
			return null;
		}
	}

	private Item handleTagStart(final String tag, final XmlPullParser xpp)
			throws XmlPullParserException, IOException, ParseException {
		if (tag.equalsIgnoreCase("item")) {
			insideItem = true;
			item = new Item();
		} else if (tag.equalsIgnoreCase("title")) {
			if (insideItem) {
				item.setTitle(xpp.nextText());
			} else {
				feed.setTitle(xpp.nextText());
			}
		} else if (tag.equalsIgnoreCase("description")) {
			if (insideItem) {
				item.setDescription(xpp.nextText());
			} else {
				feed.setDescription(xpp.nextText());
			}
		} else if (tag.equalsIgnoreCase("enclosure")) {
			if (insideItem) {
				handleEnclosureTag(xpp);
			}
		} else if (tag.equalsIgnoreCase("link")) {
			if (insideItem) {
				item.setUrl(xpp.nextText());
			}
		} else if (tag.equalsIgnoreCase("pubDate")) {
			final Date pubDate = RSS_DATE_FORMAT.parse(xpp.nextText());
			if (insideItem) {
				item.setPublished(pubDate);
			} else {
				feed.setLastUpdate(pubDate);
			}
		}
		return item;
	}

	private void handleEnclosureTag(final XmlPullParser xpp) {
		final int attributeCount = xpp.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			final String attributeName = xpp.getAttributeName(i);
			final String attributeValue = xpp.getAttributeValue(i);
			if ("url".equalsIgnoreCase(attributeName)) {
				item.setMediaUrl(attributeValue);
			} else if ("length".equalsIgnoreCase(attributeName)) {
				item.setMediaLength(Integer.parseInt(attributeValue.trim()));
			} else if ("type".equalsIgnoreCase(attributeName)) {
				item.setMediaType(attributeValue);
			}
		}
	}
}
