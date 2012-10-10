package de.vanmar.android.ilikepodcasts.test;

import android.test.ActivityInstrumentationTestCase2;
import de.vanmar.android.ilikepodcasts.MainActivity_;
import de.vanmar.android.ilikepodcasts.R;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity_> {

	public MainActivityTest() {
		super(MainActivity_.class);
	}

	public void testViewsAvailable() {
		final MainActivity_ activity = getActivity();

		assertNotNull(activity.findViewById(R.id.fragment_container));
		assertNotNull(activity.findViewById(R.id.feedlist));
	}
}
