package de.vanmar.android.ilikepodcasts.library;

import android.support.v4.app.FragmentActivity;

import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;

import de.vanmar.android.ilikepodcasts.library.util.UiHelper;

@EActivity(resName = "feeddetail")
public class FeedDetailActivity extends FragmentActivity {

	public static final String FEED_URL = "de.vanmar.android.ilikepodcasts.library.FeedDetailActivity.feedUrl";

	@Bean
	protected UiHelper uiHelper;

}
