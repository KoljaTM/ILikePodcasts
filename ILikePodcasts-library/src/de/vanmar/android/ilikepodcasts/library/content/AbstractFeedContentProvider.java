package de.vanmar.android.ilikepodcasts.library.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseManager_;

public abstract class AbstractFeedContentProvider extends ContentProvider {

	private static final String MESSAGE_NOT_YET_IMPLEMENTED = "Not yet implemented!";
	public static final int FEEDS = 100;
	public static final int FEED_ID = 110;
	private static final String FEEDS_BASE_PATH = "feeds";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/feeds";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/feeds";

	private DatabaseManager dbManager;

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	public AbstractFeedContentProvider() {
		sURIMatcher.addURI(getAuthority(), FEEDS_BASE_PATH, FEEDS);
		sURIMatcher.addURI(getAuthority(), FEEDS_BASE_PATH + "/#", FEED_ID);
	}

	protected abstract String getAuthority();

	@Override
	public boolean onCreate() {
		dbManager = DatabaseManager_.getInstance_(getContext());
		return true;
	}

	@Override
	public int delete(final Uri uri, final String selection,
			final String[] selectionArgs) {
		throw new IllegalArgumentException(MESSAGE_NOT_YET_IMPLEMENTED);
	}

	@Override
	public String getType(final Uri uri) {
		throw new IllegalArgumentException(MESSAGE_NOT_YET_IMPLEMENTED);
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		throw new IllegalArgumentException(MESSAGE_NOT_YET_IMPLEMENTED);
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {

		try {
			final Cursor cursor;
			final int uriType = sURIMatcher.match(uri);
			switch (uriType) {
			case FEED_ID:
				final int feedId = Integer.parseInt(uri.getLastPathSegment());
				cursor = dbManager.getFeedAsCursor(feedId);
				break;
			case FEEDS:
				// no filter
				cursor = dbManager.getFeedsAsCursor(projection);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI");
			}
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		} catch (final Exception e) {
			Log.e("FeedContentProvider", e.getMessage(), e);
			return null;
		}
	}

	@Override
	public int update(final Uri uri, final ContentValues values,
			final String selection, final String[] selectionArgs) {
		throw new IllegalArgumentException(MESSAGE_NOT_YET_IMPLEMENTED);
	}

}
