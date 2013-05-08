package de.vanmar.android.ilikepodcasts.library.rss;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.unittest.TestUtil;

@RunWith(RobolectricTestRunner.class)
public class RssLoaderTest {

	private RssLoader rssLoader;

	private final Context context = new Activity();

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		rssLoader = RssLoader_.getInstance_(context);
	}

	@Test
	public void shouldReadRssFeed() throws IOException, XmlPullParserException,
			ParseException {
		// given
		final URL url = TestUtil.getMockUrl("minimal.rss2");
		final DateFormat dateFormat = new SimpleDateFormat(
				"dd MM yyyy HH:mm:ss Z");

		// when
		final Feed feed = rssLoader.readRssFeed(url);

		// then
		assertThat(feed, is(notNullValue()));
		assertThat(feed.getItems().size(), is(1));
		assertThat(feed.getTitle(), is("Example Channel"));
		assertThat(feed.getDescription(), is("My example channel"));
		assertThat(feed.getLastUpdate(),
				is(dateFormat.parse("25 03 2006 16:30:00 UTC")));
		final Item item = feed.getItems().iterator().next();
		assertThat(item.getTitle(), is("Example Item"));
		assertThat(item.getDescription(), is("other things happened today"));
		assertThat(item.getMediaUrl(), is("http://minimal.com/rss2.mp3"));
		assertThat(item.getMediaLength(), is(12345));
		assertThat(item.getPublished(),
				is(dateFormat.parse("26 03 2006 17:30:00 UTC")));
	}
}
