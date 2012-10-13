package de.vanmar.android.ilikepodcasts.db;

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

import de.vanmar.android.ilikepodcasts.bo.Feed;
import de.vanmar.android.ilikepodcasts.bo.Item;

public class DatabaseManager {

	static private DatabaseManager instance;

	static public void init(final Context ctx) {
		if (null == instance) {
			instance = new DatabaseManager(ctx);
		}
	}

	static public DatabaseManager getInstance() {
		return instance;
	}

	private DatabaseHelper helper;

	private DatabaseManager(final Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	private DatabaseHelper getHelper() {
		return helper;
	}

	public List<Feed> getAllFeeds() throws SQLException {
		return getHelper().getFeedDao().queryForAll();
	}

	public void saveFeed(final Feed feed, final Collection<Item> items)
			throws SQLException {
		getHelper().getFeedDao().createOrUpdate(feed);
		for (final Item item : items) {
			item.setFeed(feed);
			getHelper().getItemDao().createOrUpdate(item);
		}
	}

	public List<Item> getAllItemsOrdered(final int feedId) throws SQLException {
		return getHelper().getItemDao().queryBuilder()
				.orderBy(Item.PUBLISHED, false).where().eq(Item.FEED, feedId)
				.query();
	}

	public void saveItem(final Item item) throws SQLException {
		getHelper().getItemDao().createOrUpdate(item);
	}

	public Item getItem(final int itemId) throws SQLException {
		return getHelper().getItemDao().queryForId(itemId);
	}

	public Cursor getFeedsAsCursor(final String[] projection) {
		final Dao<Feed, Integer> feedDao = getHelper().getFeedDao();
		final QueryBuilder<Feed, Integer> queryBuilder = feedDao.queryBuilder()
				.selectColumns(projection);
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
		return getCursor(itemDao, queryBuilder);
	}

	public Cursor getItemsAsCursor(final String[] projection, final int feedId)
			throws SQLException {
		final Dao<Item, Integer> itemDao = getHelper().getItemDao();
		final QueryBuilder<Item, Integer> queryBuilder = itemDao.queryBuilder()
				.selectColumns(projection);
		queryBuilder.where().eq(Item.FEED, feedId);
		return getCursor(itemDao, queryBuilder);
	}
}