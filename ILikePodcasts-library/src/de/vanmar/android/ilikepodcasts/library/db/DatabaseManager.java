package de.vanmar.android.ilikepodcasts.library.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;

public class DatabaseManager {

	private static DatabaseManager instance;

	public static synchronized void init(final Context ctx) {
		if (null == instance) {
			instance = new DatabaseManager(ctx);
		}
	}

	protected static void forceInit(final Context ctx) {
		instance = new DatabaseManager(ctx);
	}

	public static DatabaseManager getInstance() {
		return instance;
	}

	private final DatabaseHelper helper;

	protected DatabaseManager(final Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	protected DatabaseHelper getHelper() {
		return helper;
	}

	public List<Feed> getAllFeeds() throws SQLException {
		return getHelper().getFeedDao().queryForAll();
	}

	public void saveFeed(final Feed feed, final Collection<Item> items)
			throws SQLException {
		Log.i("DatabaseManager", "saveFeed: " + feed.getTitle());
		final Dao<Feed, Integer> feedDao = getHelper().getFeedDao();
		final List<Feed> existingFeed = feedDao.queryBuilder().limit(1L)
				.where().eq(Feed.URL, feed.getUrl()).query();
		final Feed feedToSave;
		if (existingFeed.isEmpty()) {
			feedToSave = new Feed();
		} else {
			feedToSave = existingFeed.get(0);
		}

		feedToSave.setDescription(feed.getDescription());
		feedToSave.setLastUpdate(feed.getLastUpdate());
		feedToSave.setTitle(feed.getTitle());
		feedToSave.setUrl(feed.getUrl());

		feedDao.createOrUpdate(feedToSave);
		for (final Item item : items) {
			item.setFeed(feedToSave);
			saveItem(item);
		}
	}

	public List<Item> getAllItemsOrdered(final int feedId) throws SQLException {
		return getHelper().getItemDao().queryBuilder()
				.orderBy(Item.PUBLISHED, false).where().eq(Item.FEED, feedId)
				.query();
	}

	public void saveItem(final Item item) throws SQLException {
		final IItemDao itemDao = getHelper().getItemDao();
		final Item itemToSave = itemDao.getExistingItem(item);

		itemToSave.setFeed(item.getFeed());
		itemToSave.setDescription(item.getDescription());
		itemToSave.setTitle(item.getTitle());
		itemToSave.setUrl(item.getUrl());
		itemToSave.setMediaUrl(item.getMediaUrl());
		itemToSave.setMediaType(item.getMediaType());
		itemToSave.setMediaLength(item.getMediaLength());
		if (item.getMediaPath() != null) {
			itemToSave.setMediaPath(item.getMediaPath());
		}
		itemToSave.setPublished(item.getPublished());

		itemDao.createOrUpdate(itemToSave);
	}

	public void saveItemPlayPosition(final Item item, final int position)
			throws SQLException {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		item.setPosition(position);

		itemDao.createOrUpdate(item);
	}

	public void saveItemPlaylistIndex(final Item item,
			final Integer playlistIndex) throws SQLException {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		item.setPlaylistIndex(playlistIndex);

		itemDao.createOrUpdate(item);
	}

	public Item getItem(final int itemId) throws SQLException {
		return getHelper().getItemDao().queryForId(itemId);
	}

	public Cursor getFeedsAsCursor(final String[] projection) {
		final Dao<Feed, Integer> feedDao = getHelper().getFeedDao();
		final QueryBuilder<Feed, Integer> queryBuilder = feedDao.queryBuilder()
				.selectColumns(projection);
		queryBuilder.orderBy(Feed.TITLE, false);
		return getCursor(feedDao, queryBuilder);
	}

	private <T> Cursor getCursor(final Dao<T, Integer> dao,
			final QueryBuilder<T, Integer> queryBuilder) {
		CloseableIterator<T> iterator = null;
		try {
			iterator = dao.iterator(queryBuilder.prepare());
			return ((AndroidDatabaseResults) iterator.getRawResults())
					.getRawCursor();
		} catch (final SQLException e) {
			Log.e("DatabaseManager", e.getMessage(), e);
			return null;
		}
	}

	public Feed getFeed(final int feedId) throws SQLException {
		return getHelper().getFeedDao().queryForId(feedId);
	}

	public Cursor getFeedAsCursor(final int feedId) throws SQLException {
		final Dao<Feed, Integer> feedDao = getHelper().getFeedDao();
		final QueryBuilder<Feed, Integer> queryBuilder = feedDao.queryBuilder();
		queryBuilder.where().eq(Feed.ID, feedId);
		return getCursor(feedDao, queryBuilder);
	}

	public Cursor getItemsAsCursor(final String[] projection) {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		final QueryBuilder<Item, Integer> queryBuilder = itemDao.queryBuilder()
				.selectColumns(projection);
		queryBuilder.orderBy(Item.PUBLISHED, false);
		return getCursor(itemDao, queryBuilder);
	}

	public Cursor getItemsAsCursor(final String[] projection, final int feedId)
			throws SQLException {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		final QueryBuilder<Item, Integer> queryBuilder = itemDao.queryBuilder()
				.selectColumns(projection);
		queryBuilder.where().eq(Item.FEED, feedId);
		queryBuilder.orderBy(Item.PUBLISHED, false);
		return getCursor(itemDao, queryBuilder);
	}

	public Cursor getPlaylistAsCursor(final String[] projection)
			throws SQLException {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		final QueryBuilder<Item, Integer> queryBuilder = itemDao.queryBuilder()
				.selectColumns(projection);
		queryBuilder.where().isNotNull(Item.PLAYLIST_INDEX);
		queryBuilder.orderBy(Item.PLAYLIST_INDEX, true);
		return getCursor(itemDao, queryBuilder);
	}

	public Item getPreviousItemInPlaylist(final int beforePosition)
			throws SQLException {
		final QueryBuilder<Item, Integer> queryBuilder = getHelper()
				.getItemDao().queryBuilder();
		queryBuilder.where().lt(Item.PLAYLIST_INDEX, beforePosition);
		queryBuilder.orderBy(Item.PLAYLIST_INDEX, true);

		return queryBuilder.queryForFirst();
	}

	public Item getNextItemInPlaylist(final int afterPosition)
			throws SQLException {
		final QueryBuilder<Item, Integer> queryBuilder = getHelper()
				.getItemDao().queryBuilder();
		queryBuilder.where().gt(Item.PLAYLIST_INDEX, afterPosition);
		queryBuilder.orderBy(Item.PLAYLIST_INDEX, true);

		return queryBuilder.queryForFirst();
	}

	public void enqueueItem(final Item item) throws SQLException {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		final String newIndex = itemDao.queryRaw(
				"select max(playlist_index)+1 from item").getFirstResult()[0];
		if (newIndex == null) {
			item.setPlaylistIndex(1);
		} else {
			item.setPlaylistIndex(Integer.parseInt(newIndex));
		}
		itemDao.update(item);
	}
}
