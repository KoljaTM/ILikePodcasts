package de.vanmar.android.ilikepodcasts.library.download;

import static de.vanmar.android.ilikepodcasts.library.bo.ItemBuilder.anItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.vanmar.android.ilikepodcasts.library.IDownloadService.Callback;
import de.vanmar.android.ilikepodcasts.library.ILikePodcastsApplication;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;
import de.vanmar.android.ilikepodcasts.unittest.TestUtil;

@RunWith(RobolectricTestRunner.class)
public class DownloaderTest {

	private Downloader downloader;
	private final Context context = new Activity();

	@Mock
	private PlaylistManager playlistManager;
	@Mock
	private DatabaseManager dbManager;
	@Mock
	private Callback callback;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		downloader = Downloader_.getInstance_(context);
		downloader.playlistManager = playlistManager;
		downloader.dbManager = dbManager;
	}

	@Test
	public void shouldDownloadSaveAndEnqueueFile() throws IOException,
			SQLException {
		// assume
		final URL mediaUrl = TestUtil.getMockUrl("test.mp3");
		final Item item = anItem().withMediaPath(null).build();
		final Set<Callback> callbacks = new HashSet<Callback>();
		callbacks.add(callback);

		final String mediaPath = ILikePodcastsApplication.FILE_DIR
				+ File.separator + "Podcast_" + mediaUrl.hashCode() + ".mp3";
		final File expectedFile = new File(
				Environment.getExternalStorageDirectory(), mediaPath);

		// when
		downloader.download(item, mediaUrl, callbacks);

		// then
		assertThat(item.getMediaPath(), is(mediaPath));
		assertTrue(expectedFile.exists());
		verify(callback).onDownloadProgress(item, 1024, 1342);
		verify(callback).onDownloadProgress(item, 1342, 1342);
		verify(callback).onDownloadCompleted(item);
		verify(dbManager).saveItem(item);
		verify(playlistManager).enqueueItem(item);
	}
}
