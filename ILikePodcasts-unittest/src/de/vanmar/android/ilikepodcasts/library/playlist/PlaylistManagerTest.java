package de.vanmar.android.ilikepodcasts.library.playlist;

import static de.vanmar.android.ilikepodcasts.library.bo.ItemBuilder.anItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.vanmar.android.ilikepodcasts.library.ILikePodcastsApplication;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;

@RunWith(RobolectricTestRunner.class)
public class PlaylistManagerTest {

	private PlaylistManager_ playlistManager;

	private Context context;

	@Mock
	private DatabaseManager dbManager;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		context = new Activity();
		playlistManager = PlaylistManager_.getInstance_(context);
		playlistManager.dbManager = dbManager;
	}

	@Test
	public void shouldGetPlayPositionFromPrefs() throws SQLException {
		// assume
		final int playedItemId = 17;
		final int playPosition = 1000;
		final Item itemFromDb = anItem().build();

		// given
		final Editor edit = PreferenceManager.getDefaultSharedPreferences(
				context).edit();
		edit.putInt("playedItem", playedItemId);
		edit.putInt("playPosition", playPosition);
		edit.commit();
		given(dbManager.getItem(playedItemId)).willReturn(itemFromDb);

		// when
		final Item item = playlistManager.getPlayPosition();

		// then
		assertNotNull(item);
		assertThat(item, sameInstance(itemFromDb));
		assertThat(item.getPosition(), is(playPosition));
	}

	@Test
	public void shouldEnqueueItem() throws SQLException {
		// assume
		final Item item = anItem().build();

		// when
		playlistManager.enqueueItem(item);

		// then
		verify(dbManager).enqueueItem(item);
	}

	@Test
	public void shouldMarkItemAsListenedAndDelete() throws SQLException,
			IOException {
		// assume
		final String mediaPath = ILikePodcastsApplication.FILE_DIR
				+ File.separator + "Podcast_test.mp3";
		final Item item = anItem().withMediaPath(mediaPath).build();
		final File expectedFile = new File(
				Environment.getExternalStorageDirectory(), mediaPath);
		// given
		expectedFile.mkdirs();
		expectedFile.createNewFile();
		assertTrue(expectedFile.exists());

		// when
		playlistManager.getNextItem(item, true);

		// then
		verify(dbManager).markItemListened(item);
		assertFalse(expectedFile.exists());
		assertThat(item.getMediaPath(), is(nullValue()));
	}

	@Test
	public void shouldNotMarkItemAsListenedAndDelete() throws SQLException,
			IOException {
		// assume
		final String mediaPath = ILikePodcastsApplication.FILE_DIR
				+ File.separator + "Podcast_test.mp3";
		final Item item = anItem().withMediaPath(mediaPath).build();
		final File expectedFile = new File(
				Environment.getExternalStorageDirectory(), mediaPath);
		// given
		expectedFile.mkdirs();
		expectedFile.createNewFile();
		expectedFile.deleteOnExit();
		assertTrue(expectedFile.exists());

		// when
		playlistManager.getNextItem(item, false);

		// then
		verify(dbManager, never()).markItemListened(item);
		assertTrue(expectedFile.exists());
	}
}
