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

	@RootContext
	Context context;

	private DatabaseManager dbManager;

	public PlayPosition getPlayPosition() throws SQLException {
		final PlayPosition playPosition = getPlayPositionFromPrefs();

		if (playPosition != null) {
			return playPosition;
		}
		final Item nextItemInPlaylist = getDbManager().getNextItemInPlaylist(0);
		return new PlayPosition(nextItemInPlaylist, 0L);
	}

	private PlayPosition getPlayPositionFromPrefs() throws SQLException {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final int playedItemId = preferences.getInt("playedItem", 0);
		final long position = preferences.getLong("playPosition", 0);

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
		context.getContentResolver()
				.notifyChange(
						Uri.parse(context
								.getString(R.string.episodeContentProviderUri)),
						null);
	}

	public void playItem(final Item item) {
		// TODO Auto-generated method stub

	}

}
