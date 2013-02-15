package de.vanmar.android.ilikepodcasts.library.playlist;

import java.io.File;
import java.sql.SQLException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

import de.vanmar.android.ilikepodcasts.library.R;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;

@EBean
public class PlaylistManager {

	private static final String PLAY_POSITION = "playPosition";

	private static final String PLAYED_ITEM = "playedItem";

	@RootContext
	Context context;

	@Bean
	DatabaseManager dbManager;

	public Item getPlayPosition() throws SQLException {
		final Item playing = getPlayPositionFromPrefs();

		if (playing != null) {
			return playing;
		}
		return dbManager.getNextItemInPlaylist(0);
	}

	private Item getPlayPositionFromPrefs() throws SQLException {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final int playedItemId = preferences.getInt(PLAYED_ITEM, 0);
		final int position = preferences.getInt(PLAY_POSITION, 0);

		if (playedItemId != 0) {
			final Item item = dbManager.getItem(playedItemId);
			if (item != null) {
				item.setPosition(position);
			}
			return item;
		}
		return null;
	}

	/**
	 * Enqueues the item into the playlist. Does not start play.
	 */
	public void enqueueItem(final Item item) throws SQLException {
		dbManager.enqueueItem(item);
		refreshItems();
	}

	private void refreshItems() {
		context.getContentResolver()
				.notifyChange(
						Uri.parse(context
								.getString(R.string.episodeContentProviderUri)),
						null);
	}

	public Item getPreviousItem(final Item item) throws SQLException {
		final int lastPlaylistIndex;
		if (item == null) {
			lastPlaylistIndex = Integer.MAX_VALUE;
		} else {
			lastPlaylistIndex = item.getPlaylistIndex();
		}
		final Item previousItemInPlaylist = dbManager
				.getPreviousItemInPlaylist(lastPlaylistIndex);
		return previousItemInPlaylist;
	}

	public Item getNextItem(final Item item, final boolean removeFromPlaylist)
			throws SQLException {
		final int lastPlaylistIndex;
		if (item != null) {
			lastPlaylistIndex = item.getPlaylistIndex() == null ? 0 : item
					.getPlaylistIndex();
			if (removeFromPlaylist) {
				dbManager.markItemListened(item);
				deleteMediaFile(item.getMediaPath());
				refreshItems();
			}
		} else {
			lastPlaylistIndex = 0;
		}
		final Item nextItemInPlaylist = dbManager
				.getNextItemInPlaylist(lastPlaylistIndex);

		return nextItemInPlaylist;
	}

	private void deleteMediaFile(final String mediaPath) {
		final File SDCardRoot = Environment.getExternalStorageDirectory();
		final File fileToDelete = new File(SDCardRoot, mediaPath);
		fileToDelete.delete();
	}

	public void setLastPlayPosition(final Item item, final int position) {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PLAYED_ITEM, item == null ? 0 : item.getId());
		editor.putInt(PLAY_POSITION, position);
		editor.commit();
	}

	public void savePlayPosition(final Item item, final int position)
			throws SQLException {
		dbManager.saveItemPlayPosition(item, position);
	}
}
