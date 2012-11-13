package de.vanmar.android.ilikepodcasts.library.db;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import de.vanmar.android.ilikepodcasts.library.bo.FeedBuilder;
import de.vanmar.android.ilikepodcasts.library.bo.Item;

public class DatabaseManagerTest extends AndroidTestCase {

	private static final String TEST_FILE_PREFIX = "/mnt/sdcard/test";

	private static final String TESTDATA_DIR = TEST_FILE_PREFIX
			+ "/mnt/sdcard/ILikePodcasts/";

	RenamingDelegatingContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final File basePath = new File(TESTDATA_DIR);
		basePath.mkdirs();
		context = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
		DatabaseManager.forceInit(context);
	}

	public void testDefaultData() throws SQLException {
		final DatabaseManager dbManager = DatabaseManager.getInstance();

		assertEquals(0, dbManager.getAllFeeds().size());
		assertTrue(dbManager.getItemsAsCursor(new String[] {}).isAfterLast());
	}

	public void testSaveFeed() throws SQLException {
		final DatabaseManager dbManager = DatabaseManager.getInstance();

		final int feedsBefore = dbManager.getAllFeeds().size();
		dbManager.saveFeed(FeedBuilder.aFeed().build(), new LinkedList<Item>());
		final int feedsAfter = dbManager.getAllFeeds().size();

		assertEquals(feedsBefore + 1, feedsAfter);
	}
}
