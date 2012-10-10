package de.vanmar.android.ilikepodcasts.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
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
				.orderBy("published", false).where().eq("feed_id", feedId)
				.query();
	}
}