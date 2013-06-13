package de.vanmar.android.ilikepodcasts.library.rss;

import static de.vanmar.android.ilikepodcasts.library.bo.FeedBuilder.aFeed;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@RunWith(RobolectricTestRunner.class)
public class RssServiceTest {

	private RssService rssService;

	@Mock
	private DatabaseManager dbManager;

	@Mock
	private RssLoader rssLoader;

	@Mock
	private UiHelper uiHelper;

	final Feed feed = aFeed().withUrl("http://foo.bar").build();
	final Feed feed2 = aFeed().withUrl("http://blabla.xyz").build();

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		rssService = new RssService_();
		rssService.dbManager = dbManager;
		rssService.rssLoader = rssLoader;
		rssService.uiHelper = uiHelper;
	}

	@Test
	public void shouldRefreshFeeds() throws Exception {
		// given
		given(dbManager.getAllFeeds()).willReturn(Arrays.asList(feed, feed2));

		// when
		rssService.refreshFeeds();

		// then
		verify(rssLoader).updateFeed(feed);
		verify(rssLoader).updateFeed(feed2);
		verifyNoMoreInteractions(rssLoader);
	}

	@Test
	public void shouldContinueAfterException() throws Exception {
		// given
		given(dbManager.getAllFeeds()).willReturn(Arrays.asList(feed, feed2));
		final Exception toBeThrown = new RuntimeException("expected");
		willThrow(toBeThrown).given(rssLoader).updateFeed(feed);

		// when
		rssService.refreshFeeds();

		// then
		verify(rssLoader).updateFeed(feed);
		verify(rssLoader).updateFeed(feed2);
		verifyNoMoreInteractions(rssLoader);
		verify(uiHelper).displayError(toBeThrown);
	}

}
