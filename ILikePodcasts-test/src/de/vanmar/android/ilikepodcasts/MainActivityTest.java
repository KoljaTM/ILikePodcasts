package de.vanmar.android.ilikepodcasts;

import android.test.ActivityInstrumentationTestCase2;
import de.vanmar.android.ilikepodcasts.library.MainActivity_;
import de.vanmar.android.ilikepodcasts.library.R;

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
