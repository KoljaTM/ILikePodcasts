package de.vanmar.android.ilikepodcasts.library.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;

public abstract class AbstractEpisodeContentProvider extends ContentProvider {

	public static final int EPISODES = 100;
	public static final int FEED = 110;
	public static final int EPISODE_ID = 120;
	public static final int PLAYLIST = 130;
	private static final String EPISODES_BASE_PATH = "episodes";
	private static final String FEED_PATH = "/feed";
	private static final String PLAYLIST_PATH = "/playlist";
	public static final String CONTENT_FEED_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ FEED_PATH;
	public static final String CONTENT_PLAYLIST_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ PLAYLIST_PATH;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/episodes";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/episodes";

	private DatabaseManager dbManager;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	public AbstractEpisodeContentProvider() {
		sURIMatcher.addURI(getAuthority(), EPISODES_BASE_PATH, EPISODES);
		sURIMatcher
				.addURI(getAuthority(), EPISODES_BASE_PATH + "/feed/#", FEED);
		sURIMatcher.addURI(getAuthority(), EPISODES_BASE_PATH + "/playlist",
				PLAYLIST);
		sURIMatcher.addURI(getAuthority(), EPISODES_BASE_PATH + "/#",
				EPISODE_ID);
	}

	protected abstract String getAuthority();

	@Override
	public boolean onCreate() {
		DatabaseManager.init(getContext());
		dbManager = DatabaseManager.getInstance();
		return true;
	}

	@Override
	public int delete(final Uri uri, final String selection,
			final String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(final Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {

		try {
			final int uriType = sURIMatcher.match(uri);
			switch (uriType) {
			case EPISODE_ID:
				return null;
			case EPISODES:
				// no filter
				return dbManager.getItemsAsCursor(projection);
			case FEED:
				// by feed
				final int feedId = Integer.parseInt(uri.getLastPathSegment());
				final Cursor itemsAsCursor = dbManager.getItemsAsCursor(
						projection, feedId);
				itemsAsCursor.setNotificationUri(getContext()
						.getContentResolver(), uri);
				return itemsAsCursor;
			case PLAYLIST:
				// playlist
				final Cursor playlistAsCursor = dbManager
						.getPlaylistAsCursor(projection);
				playlistAsCursor.setNotificationUri(getContext()
						.getContentResolver(), uri);
				return playlistAsCursor;
			default:
				throw new IllegalArgumentException("Unknown URI");
			}
		} catch (final Exception e) {
			Log.e("EpisodeContentProvider", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public int update(final Uri uri, final ContentValues values,
			final String selection, final String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
