package de.vanmar.android.ilikepodcasts.library.db;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public interface IItemDao extends Dao<Item, Integer> {
	Item getExistingItem(final Item item) throws SQLException;
}
