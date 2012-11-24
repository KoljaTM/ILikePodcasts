package de.vanmar.android.ilikepodcasts.library.db;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import de.vanmar.android.ilikepodcasts.library.bo.Item;

public class ItemDao extends BaseDaoImpl<Item, Integer> implements IItemDao {
	// this constructor must be defined
	public ItemDao(final ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Item.class);
	}

	@Override
	public Item getExistingItem(final Item item) throws SQLException {
		final List<Item> existingItems = queryBuilder().limit(1L).where()
				.eq(Item.URL, item.getMediaUrl()).query();
		final Item existingItem;
		if (existingItems.isEmpty()) {
			existingItem = new Item();
		} else {
			existingItem = existingItems.get(0);
		}
		return existingItem;
	}

}
