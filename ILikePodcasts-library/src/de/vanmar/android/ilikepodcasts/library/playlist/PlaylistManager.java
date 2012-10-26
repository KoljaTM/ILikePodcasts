package de.vanmar.android.ilikepodcasts.library.playlist;

import java.sql.SQLException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

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

	private DatabaseManager dbManager;

	public PlayPosition getPlayPosition() throws SQLException {
		final PlayPosition playPosition = getPlayPositionFromPrefs();

		if (playPosition != null) {
			return playPosition;
		}
		final Item nextItemInPlaylist = getDbManager().getNextItemInPlaylist(0);
		return new PlayPosition(nextItemInPlaylist,
				nextItemInPlaylist.getPosition());
	}

	private PlayPosition getPlayPositionFromPrefs() throws SQLException {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final int playedItemId = preferences.getInt(PLAYED_ITEM, 0);
		final int position = preferences.getInt(PLAY_POSITION, 0);

		if (playedItemId != 0) {
			final Item item = getDbManager().getItem(playedItemId);
			return new PlayPosition(item, position);
		}
		return null;
	}

	private DatabaseManager getDbManager() {
		if (dbManager == null) {
			DatabaseManager.init(context);
			dbManager = DatabaseManager.getInstance();
		}
		return dbManager;
	}

	/**
	 * Enqueues the item into the playlist. Does not start play.
	 */
	public void enqueueItem(final Item item) throws SQLException {
		getDbManager().enqueueItem(item);
		refreshItems();
	}

	private void refreshItems() {
		context.getContentResolver()
				.notifyChange(
						Uri.parse(context
								.getString(R.string.episodeContentProviderUri)),
						null);
	}

	public Item getNextItem(final Item item) throws SQLException {
		final int lastPlaylistIndex = item.getPlaylistIndex();
		final Item nextItemInPlaylist = getDbManager().getNextItemInPlaylist(
				lastPlaylistIndex);
		// item.setPlaylistIndex(null);
		// getDbManager().saveItem(item);
		refreshItems();
		return nextItemInPlaylist;
	}

	public void setLastPlayPosition(final PlayPosition playPosition) {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PLAYED_ITEM, playPosition.getItem().getId());
		editor.putInt(PLAY_POSITION, playPosition.getPosition());
		editor.commit();
	}

	public void savePlayPosition(final PlayPosition playPosition)
			throws SQLException {
		final Item item = playPosition.getItem();
		item.setPosition(playPosition.getPosition());
		getDbManager().saveItem(item);
	}
}
