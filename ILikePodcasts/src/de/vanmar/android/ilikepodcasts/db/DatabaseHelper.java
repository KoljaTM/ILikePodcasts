package de.vanmar.android.ilikepodcasts.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import de.vanmar.android.ilikepodcasts.bo.Feed;
import de.vanmar.android.ilikepodcasts.bo.Item;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	// name of the database file
	private static final String DATABASE_NAME = "ILikePodcasts.sqlite";

	// Version history: DB:1 App Version:1
	private static final int DATABASE_VERSION = 2;

	// the DAO object we use to access the SimpleData table
	private Dao<Feed, Integer> feedDao = null;
	private Dao<Item, Integer> itemDao = null;

	public DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase database,
			final ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Feed.class);
			TableUtils.createTable(connectionSource, Item.class);
		} catch (final SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		} catch (final java.sql.SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(final SQLiteDatabase db,
			final ConnectionSource connectionSource, final int oldVersion,
			final int newVersion) {
		try {
			final List<String> allSql = new ArrayList<String>();
			switch (oldVersion) {
			case 1:
				// allSql.add("alter table Feed add column `url` VARCHAR");
				// allSql.add("update Feed set url='http://www.weeklyweinersmith.com/?feed=rss2'");
			}
			for (final String sql : allSql) {
				db.execSQL(sql);
			}
		} catch (final SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade",
					e);
			throw new RuntimeException(e);
		}

	}

	public Dao<Feed, Integer> getFeedDao() {
		if (null == feedDao) {
			try {
				feedDao = getDao(Feed.class);
			} catch (final java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return feedDao;
	}

	public Dao<Item, Integer> getItemDao() {
		if (null == itemDao) {
			try {
				itemDao = getDao(Item.class);
			} catch (final java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return itemDao;
	}

}