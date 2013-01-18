package de.vanmar.android.ilikepodcasts.library.download;

import static de.vanmar.android.ilikepodcasts.library.bo.ItemBuilder.anItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.net.Uri;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver;
import com.xtremelabs.robolectric.shadows.ShadowContentResolver.NotifiedUri;

import de.vanmar.android.ilikepodcasts.library.IDownloadService.Callback;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.download.DownloadService.DownloadServiceBinder;
import de.vanmar.android.ilikepodcasts.unittest.TestUtil;

@RunWith(RobolectricTestRunner.class)
public class DownloadServiceTest {

	private DownloadService service;
	private DownloadServiceBinder binder;
	private ShadowContentResolver contentResolver;

	@Mock
	private Downloader downloader;
	@Mock
	private Executor background;
	@Mock
	private Callback callback;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		TestUtil.mockBackgroundExecutor(background);
		service = new DownloadService_();
		service.onCreate();
		service.downloader = downloader;
		binder = (DownloadServiceBinder) service.onBind(null);
		contentResolver = Robolectric.shadowOf(service.getContentResolver());
	}

	@Test
	public void shouldDownloadFileInBackgroundThread() throws IOException,
			SQLException {
		// assume
		final Item item = anItem().build();
		final HashSet<Callback> callbacks = new HashSet<Callback>();
		callbacks.add(callback);

		// given
		binder.registerCallback(callback);

		// when
		binder.startDownload(item);

		// then
		verify(downloader).download(item, new URL(item.getMediaUrl()),
				callbacks);
		verify(background).execute(any(Runnable.class));
		final Uri uri = Uri
				.parse("content://de.vanmar.android.ilikepodcasts.EpisodeContentProvider/episodes");
		final List<NotifiedUri> notifiedUris = contentResolver
				.getNotifiedUris();
		assertThat(notifiedUris.get(0).uri, is(uri));
	}
}
