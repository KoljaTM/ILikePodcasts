package de.vanmar.android.ilikepodcasts.library.db;

import static de.vanmar.android.ilikepodcasts.library.bo.FeedBuilder.aFeed;
import static de.vanmar.android.ilikepodcasts.library.bo.ItemBuilder.anItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.vanmar.android.ilikepodcasts.library.bo.Feed;
import de.vanmar.android.ilikepodcasts.library.bo.Item;

@RunWith(RobolectricTestRunner.class)
public class DatabaseManagerUnitTest {

	private DatabaseManager databaseManager;

	@Mock
	private Context context;

	@Mock
	private IItemDao itemDao;

	@Mock
	private DatabaseHelper dbHelper;

	@Before
	public void setUp() throws SQLException {
		MockitoAnnotations.initMocks(this);

		DatabaseManager.init(context);
		databaseManager = spy(DatabaseManager.getInstance());
		BDDMockito.willReturn(dbHelper).given(databaseManager).getHelper();
		given(dbHelper.getItemDao()).willReturn(itemDao);
	}

	@Test
	public void shouldSetFeedInSavedItems() throws SQLException {
		// assume
		final Feed feed = aFeed().withId(1).build();
		final Item item = anItem().withFeed(feed).build();

		// given
		given(itemDao.getExistingItem(item)).willReturn(new Item());

		// when
		databaseManager.saveItem(item);

		// then
		final ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(itemDao).createOrUpdate(captor.capture());
		assertThat(captor.getValue().getFeed(), is(feed));
	}
}
