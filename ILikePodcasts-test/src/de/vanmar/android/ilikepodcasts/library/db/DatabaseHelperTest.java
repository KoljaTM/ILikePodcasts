package de.vanmar.android.ilikepodcasts.library.db;

import java.io.File;
import java.sql.SQLException;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import de.vanmar.android.ilikepodcasts.library.db.DatabaseHelper;

public class DatabaseHelperTest extends AndroidTestCase {

	private static final String TEST_FILE_PREFIX = "/mnt/sdcard/test";

	RenamingDelegatingContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		final File basePath = new File(TEST_FILE_PREFIX
				+ "/mnt/sdcard/ILikePodcasts/");
		basePath.mkdirs();
		context = new RenamingDelegatingContext(getContext(), TEST_FILE_PREFIX);
	}

	public void testCreateDB() throws SQLException {
		final DatabaseHelper dbHelper = new DatabaseHelper(context);

		assertEquals(0, dbHelper.getFeedDao().countOf());
		assertEquals(0, dbHelper.getItemDao().countOf());
	}
}
